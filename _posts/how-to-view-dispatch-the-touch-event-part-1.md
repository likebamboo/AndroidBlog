title: Android触摸屏事件派发机制详解与源码分析一(View篇)
date: 2015-07-04
tags: [基础]
categories: [其他]
---

## **1 背景**

最近在简书和微博还有Q群看见很多人说Android自定义控件（View/ViewGroup）如何学习？为啥那么难？其实答案很简单：“基础不牢，地动山摇。”

不扯蛋了，进入正题。就算你不自定义控件，你也必须要了解Android控件的触摸屏事件传递机制（之所以说触摸屏是因为该系列以触摸屏的事件机制分析为主，对于类似TV设备等的物理事件机制的分析雷同但有区别。哈哈，谁让我之前是做Android TV BOX的，悲催！），只有这样才能将你的控件事件运用的如鱼得水。接下来的控件触摸屏事件传递机制分析依据Android 5.1.1源码（API 22）。
<!--more-->
## **2 基础实例现象**

### **2-1 例子**

从一个例子分析说起吧。如下是一个很简单不过的Android实例：  
![这里写图片描述](http://img.blog.csdn.net/20150521095141196)

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:gravity="center"
    android:layout_width="fill_parent"
    android:layout_height="fill_parent"
    android:id="@+id/mylayout">
    <Button
        android:id="@+id/my_btn"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="click test"/>
</LinearLayout>

public class ListenerActivity extends Activity implements View.OnTouchListener, View.OnClickListener {
    private LinearLayout mLayout;
    private Button mButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        mLayout = (LinearLayout) this.findViewById(R.id.mylayout);
        mButton = (Button) this.findViewById(R.id.my_btn);

        mLayout.setOnTouchListener(this);
        mButton.setOnTouchListener(this);

        mLayout.setOnClickListener(this);
        mButton.setOnClickListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.i(null, "OnTouchListener--onTouch-- action="+event.getAction()+" --"+v);
        return false;
    }

    @Override
    public void onClick(View v) {
        Log.i(null, "OnClickListener--onClick--"+v);
    }
}
```

### **2-2 现象**

如上代码很简单，但凡学过几天Android的人都能看懂吧。Activity中有一个LinearLayout（ViewGroup的子类，ViewGroup是View的子类）布局，布局中包含一个按钮（View的子类）；然后分别对这两个控件设置了Touch与Click的监听事件，具体运行结果如下：

1.  当稳稳的点击Button时打印如下：  
    ![这里写图片描述](http://img.blog.csdn.net/20150521100045328)
2.  当稳稳的点击除过Button以外的其他地方时打印如下：  
    ![这里写图片描述](http://img.blog.csdn.net/20150521100226822)
3.  当收指点击Button时按在Button上晃动了一下松开后的打印如下：  
    ![这里写图片描述](http://img.blog.csdn.net/20150521100326301)

机智的你看完这个结果指定知道为啥吧？   
我们看下onTouch和onClick，从参数都能看出来onTouch比onClick强大灵活，毕竟多了一个event参数。这样onTouch里就可以处理ACTION_DOWN、ACTION_UP、ACTION_MOVE等等的各种触摸。现在来分析下上面的打印结果；在1中，当我们点击Button时会先触发onTouch事件（之所以打印action为0,1各一次是因为按下抬起两个触摸动作被触发）然后才触发onClick事件；在2中也同理类似1；在3中会发现onTouch被多次调运后才调运onClick，是因为手指晃动了，所以触发了ACTION_DOWN->ACTION_MOVE…->ACTION_UP。

如果你眼睛比较尖你会看见onTouch会有一个返回值，而且在上面返回了false。你可能会疑惑这个返回值有啥效果？那就验证一下吧，我们将上面的onTouch返回值改为ture。如下：

```java
@Override
public boolean onTouch(View v, MotionEvent event) {
    Log.i(null, "OnTouchListener--onTouch-- action="+event.getAction()+" --"+v);
    return true;
}
```

再次点击Button结果如下：   
![这里写图片描述](http://img.blog.csdn.net/20150521102011228)  
看见了吧，如果onTouch返回true则onClick不会被调运了。

### **2-3 总结结论**

好了，经过这个简单的实例验证你可以总结发现：

1.  Android控件的Listener事件触发顺序是先触发onTouch，其次onClick。
2.  如果控件的onTouch返回true将会阻止事件继续传递，返回false事件会继续传递。

对于伸手党码农来说其实到这足矣应付常规的App事件监听处理使用开发了，但是对于复杂的事件监听处理或者想自定义控件的码农来说这才是刚刚开始，只是个热身。既然这样那就继续喽。。。

## **3 Android 5.1.1(API 22) View触摸屏事件传递源码分析**

### **3-1 写在前面的话**

其实Android源码无论哪个版本对于触摸屏事件的传递机制都类似，这里只是选用了目前最新版本的源码来分析而已。分析Android View事件传递机制之前有必要先看下源码的一些关系，如下是几个继承关系图：  
![这里写图片描述](http://img.blog.csdn.net/20150521103430816)  
![这里写图片描述](http://img.blog.csdn.net/20150521103546948)

怎么样？看了官方这个继承图是不是明白了上面例子中说的LinearLayout是ViewGroup的子类，ViewGroup是View的子类，Button是View的子类关系呢？其实，在Android中所有的控件无非都是ViewGroup或者View的子类，说高尚点就是所有控件都是View的子类。

这里通过继承关系是说明一切控件都是View，同时View与ViewGroup又存在一些区别，所以该模块才只单单先分析View触摸屏事件传递机制。

### **3-2 从View的dispatchTouchEvent方法说起**

在Android中你只要触摸控件首先都会触发控件的dispatchTouchEvent方法（其实这个方法一般都没在具体的控件类中，而在他的父类View中），所以我们先来看下View的dispatchTouchEvent方法，如下：

```java
public boolean dispatchTouchEvent(MotionEvent event) {
    // If the event should be handled by accessibility focus first.
    if (event.isTargetAccessibilityFocus()) {
        // We don't have focus or no virtual descendant has it, do not handle the event.
        if (!isAccessibilityFocusedViewOrHost()) {
            return false;
        }
        // We have focus and got the event, then use normal event dispatch.
        event.setTargetAccessibilityFocus(false);
    }

    boolean result = false;

    if (mInputEventConsistencyVerifier != null) {
        mInputEventConsistencyVerifier.onTouchEvent(event, 0);
    }

    final int actionMasked = event.getActionMasked();
    if (actionMasked == MotionEvent.ACTION_DOWN) {
        // Defensive cleanup for new gesture
        stopNestedScroll();
    }

    if (onFilterTouchEventForSecurity(event)) {
        //noinspection SimplifiableIfStatement
        ListenerInfo li = mListenerInfo;
        if (li != null && li.mOnTouchListener != null
                && (mViewFlags & ENABLED_MASK) == ENABLED
                && li.mOnTouchListener.onTouch(this, event)) {
            result = true;
        }

        if (!result && onTouchEvent(event)) {
            result = true;
        }
    }

    if (!result && mInputEventConsistencyVerifier != null) {
        mInputEventConsistencyVerifier.onUnhandledEvent(event, 0);
    }

    // Clean up after nested scrolls if this is the end of a gesture;
    // also cancel it if we tried an ACTION_DOWN but we didn't want the rest
    // of the gesture.
    if (actionMasked == MotionEvent.ACTION_UP ||
            actionMasked == MotionEvent.ACTION_CANCEL ||
            (actionMasked == MotionEvent.ACTION_DOWN && !result)) {
        stopNestedScroll();
    }

    return result;
}
```

dispatchTouchEvent的代码有点长，咱们看重点就可以。前面都是设置一些标记和处理input与手势等传递，到24行的`if (onFilterTouchEventForSecurity(event))`语句判断当前View是否没被遮住等，接着26行定义ListenerInfo局部变量，ListenerInfo是View的静态内部类，用来定义一堆关于View的XXXListener等方法；接着`if (li != null && li.mOnTouchListener != null && (mViewFlags & ENABLED_MASK) == ENABLED && li.mOnTouchListener.onTouch(this, event))`语句就是重点，首先li对象自然不会为null，li.mOnTouchListener呢？你会发现ListenerInfo的mOnTouchListener成员是在哪儿赋值的呢？怎么确认他是不是null呢？通过在View类里搜索可以看到：

```java
/**
 * Register a callback to be invoked when a touch event is sent to this view.
 * @param l the touch listener to attach to this view
 */
public void setOnTouchListener(OnTouchListener l) {
    getListenerInfo().mOnTouchListener = l;
}
```

li.mOnTouchListener是不是null取决于控件（View）是否设置setOnTouchListener监听，在上面的实例中我们是设置过Button的setOnTouchListener方法的，所以也不为null；接着通过位与运算确定控件（View）是不是ENABLED 的，默认控件都是ENABLED 的；接着判断onTouch的返回值是不是true。通过如上判断之后如果都为true则设置默认为false的result为true，那么接下来的`if (!result && onTouchEvent(event))`就不会执行，最终dispatchTouchEvent也会返回true。而如果`if (li != null && li.mOnTouchListener != null && (mViewFlags & ENABLED_MASK) == ENABLED && li.mOnTouchListener.onTouch(this, event))`语句有一个为false则`if (!result && onTouchEvent(event))`就会执行，如果onTouchEvent(event)返回false则dispatchTouchEvent返回false，否则返回true。

这下再看前面的实例部分明白了吧？控件触摸就会调运dispatchTouchEvent方法，而在dispatchTouchEvent中先执行的是onTouch方法，所以验证了实例结论总结中的onTouch优先于onClick执行道理。如果控件是ENABLE且在onTouch方法里返回了true则dispatchTouchEvent方法也返回true，不会再继续往下执行；反之，onTouch返回false则会继续向下执行onTouchEvent方法，且dispatchTouchEvent的返回值与onTouchEvent返回值相同。

所以依据这个结论和上面实例打印结果你指定已经大胆猜测认为onClick一定与onTouchEvent有关系？是不是呢？先告诉你，是的。下面我们会分析。

#### **3-2-1 总结结论**

在View的触摸屏传递机制中通过分析dispatchTouchEvent方法源码我们会得出如下基本结论：

1.  触摸控件（View）首先执行dispatchTouchEvent方法。
2.  在dispatchTouchEvent方法中先执行onTouch方法，后执行onClick方法（onClick方法在onTouchEvent中执行，下面会分析）。
3.  如果控件（View）的onTouch返回false或者mOnTouchListener为null（控件没有设置setOnTouchListener方法）或者控件不是enable的情况下会调运onTouchEvent，dispatchTouchEvent返回值与onTouchEvent返回一样。
4.  如果控件不是enable的设置了onTouch方法也不会执行，只能通过重写控件的onTouchEvent方法处理（上面已经处理分析了），dispatchTouchEvent返回值与onTouchEvent返回一样。
5.  如果控件（View）是enable且onTouch返回true情况下，dispatchTouchEvent直接返回true，不会调用onTouchEvent方法。

上面说了onClick一定与onTouchEvent有关系，那么接下来就分析分析dispatchTouchEvent方法中的onTouchEvent方法。

### **3-3 继续说说View的dispatchTouchEvent方法中调运的onTouchEvent方法**

上面说了dispatchTouchEvent方法中如果onTouch返回false或者mOnTouchListener为null（控件没有设置setOnTouchListener方法）或者控件不是enable的情况下会调运onTouchEvent，所以接着看就知道了，如下：

```java
public boolean onTouchEvent(MotionEvent event) {
    final float x = event.getX();
    final float y = event.getY();
    final int viewFlags = mViewFlags;

    if ((viewFlags & ENABLED_MASK) == DISABLED) {
        if (event.getAction() == MotionEvent.ACTION_UP && (mPrivateFlags & PFLAG_PRESSED) != 0) {
            setPressed(false);
        }
        // A disabled view that is clickable still consumes the touch
        // events, it just doesn't respond to them.
        return (((viewFlags & CLICKABLE) == CLICKABLE ||
                (viewFlags & LONG_CLICKABLE) == LONG_CLICKABLE));
    }

    if (mTouchDelegate != null) {
        if (mTouchDelegate.onTouchEvent(event)) {
            return true;
        }
    }

    if (((viewFlags & CLICKABLE) == CLICKABLE ||
            (viewFlags & LONG_CLICKABLE) == LONG_CLICKABLE)) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                boolean prepressed = (mPrivateFlags & PFLAG_PREPRESSED) != 0;
                if ((mPrivateFlags & PFLAG_PRESSED) != 0 || prepressed) {
                    // take focus if we don't have it already and we should in
                    // touch mode.
                    boolean focusTaken = false;
                    if (isFocusable() && isFocusableInTouchMode() && !isFocused()) {
                        focusTaken = requestFocus();
                    }

                    if (prepressed) {
                        // The button is being released before we actually
                        // showed it as pressed.  Make it show the pressed
                        // state now (before scheduling the click) to ensure
                        // the user sees it.
                        setPressed(true, x, y);
                   }

                    if (!mHasPerformedLongPress) {
                        // This is a tap, so remove the longpress check
                        removeLongPressCallback();

                        // Only perform take click actions if we were in the pressed state
                        if (!focusTaken) {
                            // Use a Runnable and post this rather than calling
                            // performClick directly. This lets other visual state
                            // of the view update before click actions start.
                            if (mPerformClick == null) {
                                mPerformClick = new PerformClick();
                            }
                            if (!post(mPerformClick)) {
                                performClick();
                            }
                        }
                    }

                    if (mUnsetPressedState == null) {
                        mUnsetPressedState = new UnsetPressedState();
                    }

                    if (prepressed) {
                        postDelayed(mUnsetPressedState,
                                ViewConfiguration.getPressedStateDuration());
                    } else if (!post(mUnsetPressedState)) {
                        // If the post failed, unpress right now
                        mUnsetPressedState.run();
                    }

                    removeTapCallback();
                }
                break;

            case MotionEvent.ACTION_DOWN:
                mHasPerformedLongPress = false;

                if (performButtonActionOnTouchDown(event)) {
                    break;
                }

                // Walk up the hierarchy to determine if we're inside a scrolling container.
                boolean isInScrollingContainer = isInScrollingContainer();

                // For views inside a scrolling container, delay the pressed feedback for
                // a short period in case this is a scroll.
                if (isInScrollingContainer) {
                    mPrivateFlags |= PFLAG_PREPRESSED;
                    if (mPendingCheckForTap == null) {
                        mPendingCheckForTap = new CheckForTap();
                    }
                    mPendingCheckForTap.x = event.getX();
                    mPendingCheckForTap.y = event.getY();
                    postDelayed(mPendingCheckForTap, ViewConfiguration.getTapTimeout());
                } else {
                    // Not inside a scrolling container, so show the feedback right away
                    setPressed(true, x, y);
                    checkForLongClick(0);
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                setPressed(false);
                removeTapCallback();
                removeLongPressCallback();
                break;

            case MotionEvent.ACTION_MOVE:
                drawableHotspotChanged(x, y);

                // Be lenient about moving outside of buttons
                if (!pointInView(x, y, mTouchSlop)) {
                    // Outside button
                    removeTapCallback();
                    if ((mPrivateFlags & PFLAG_PRESSED) != 0) {
                        // Remove any future long press/tap checks
                        removeLongPressCallback();

                        setPressed(false);
                    }
                }
                break;
        }

        return true;
    }

    return false;
}
```

我勒个去！一个方法比一个方法代码多。好吧，那咱们继续只挑重点来说明呗。

首先地6到14行可以看出，如果控件（View）是disenable状态，同时是可以clickable的则onTouchEvent直接消费事件返回true，反之如果控件（View）是disenable状态，同时是disclickable的则onTouchEvent直接false。多说一句，关于控件的enable或者clickable属性可以通过java或者xml直接设置，每个view都有这些属性。

接着22行可以看见，如果一个控件是enable且disclickable则onTouchEvent直接返回false了；反之，如果一个控件是enable且clickable则继续进入过于一个event的switch判断中，然后最终onTouchEvent都返回了true。switch的ACTION_DOWN与ACTION_MOVE都进行了一些必要的设置与置位，接着到手抬起来ACTION_UP时你会发现，首先判断了是否按下过，同时是不是可以得到焦点，然后尝试获取焦点，然后判断如果不是longPressed则通过post在UI Thread中执行一个PerformClick的Runnable，也就是performClick方法。具体如下：

```java
public boolean performClick() {
    final boolean result;
    final ListenerInfo li = mListenerInfo;
    if (li != null && li.mOnClickListener != null) {
        playSoundEffect(SoundEffectConstants.CLICK);
        li.mOnClickListener.onClick(this);
        result = true;
    } else {
        result = false;
    }

    sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
    return result;
}
```

这个方法也是先定义一个ListenerInfo的变量然后赋值，接着判断li.mOnClickListener是不是为null，决定执行不执行onClick。你指定现在已经很机智了，和onTouch一样，搜一下mOnClickListener在哪赋值的呗，结果发现：

```java
public void setOnClickListener(OnClickListener l) {
    if (!isClickable()) {
        setClickable(true);
    }
    getListenerInfo().mOnClickListener = l;
}
```

看见了吧！控件只要监听了onClick方法则mOnClickListener就不为null，而且有意思的是如果调运setOnClickListener方法设置监听且控件是disclickable的情况下默认会帮设置为clickable。

我勒个去！！！惊讶吧！！！猜的没错onClick就在onTouchEvent中执行的，而且是在onTouchEvent的ACTION_UP事件中执行的。

#### **3-3-1 总结结论**

1.  onTouchEvent方法中会在ACTION_UP分支中触发onClick的监听。
2.  当dispatchTouchEvent在进行事件分发的时候，只有前一个action返回true，才会触发下一个action。

到此上面例子中关于Button点击的各种打印的真实原因都找到了可靠的证据，也就是说View的触摸屏事件传递机制其实也就这么回事。

## **4 透过源码继续进阶实例验证**

其实上面分析完View的触摸传递机制之后已经足够用了。如下的实例验证可以说是加深阅读源码的理解，还有一个主要作用就是为将来自定义控件打下坚实基础。因为自定义控件中时常会与这几个方法打交道。

### **4-1 例子**

我们自定义一个Button（Button实质继承自View），如下：

```java
public class TestButton extends Button {

    public TestButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.i(null, "dispatchTouchEvent-- action=" + event.getAction());
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(null, "onTouchEvent-- action="+event.getAction());
        return super.onTouchEvent(event);
    }
}
```

其他代码如下：

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:gravity="center"
    android:layout_width="fill_parent"
    android:layout_height="fill_parent"
    android:id="@+id/mylayout">
    <com.zzci.light.TestButton
        android:id="@+id/my_btn"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="click test"/>
</LinearLayout>

public class ListenerActivity extends Activity implements View.OnTouchListener, View.OnClickListener {
    private LinearLayout mLayout;
    private TestButton mButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        mLayout = (LinearLayout) this.findViewById(R.id.mylayout);
        mButton = (TestButton) this.findViewById(R.id.my_btn);

        mLayout.setOnTouchListener(this);
        mButton.setOnTouchListener(this);

        mLayout.setOnClickListener(this);
        mButton.setOnClickListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.i(null, "OnTouchListener--onTouch-- action="+event.getAction()+" --"+v);
        return false;
    }

    @Override
    public void onClick(View v) {
        Log.i(null, "OnClickListener--onClick--"+v);
    }
}
```

其实这段代码只是对上面例子中的Button换为了自定义Button而已。

### **4-2 现象分析**

#### **4-2-1 点击Button（手抽筋了一下）**

![这里写图片描述](http://img.blog.csdn.net/20150521153718515)

可以发现，如上打印完全符合源码分析结果，dispatchTouchEvent方法先派发down事件，完事调运onTouch，完事调运onTouchEvent返回true，同时dispatchTouchEvent返回true，然后dispatchTouchEvent继续派发move或者up事件，循环，直到onTouchEvent处理up事件时调运onClick事件，完事返回true，同时dispatchTouchEvent返回true；一次完整的View事件派发流程结束。

#### **4-2-2 简单修改onTouchEvent返回值为true**

将TestButton类的onTouchEvent方法修改如下，其他和基础代码保持不变：

```java
@Override
public boolean onTouchEvent(MotionEvent event) {
    Log.i(null, "onTouchEvent-- action="+event.getAction());
    return true;
}
```

点击Button打印如下：  
![这里写图片描述](http://img.blog.csdn.net/20150521160926822)

可以发现，当自定义了控件（View）的onTouchEvent直接返回true而不调运super方法时，事件派发机制如同4.2.1类似，只是最后up事件没有触发onClick而已（因为没有调用super）。

所以可想而知，如果TestButton类的onTouchEvent修改为如下：
```java
@Override
public boolean onTouchEvent(MotionEvent event) {
    Log.i(null, "onTouchEvent-- action="+event.getAction());
    super.onTouchEvent(event);
    return true;
}
```

点击Button如下：  
![这里写图片描述](http://img.blog.csdn.net/20150521161342663)

整个派发机制和4.2.1完全类似。

#### **4-2-3 简单修改onTouchEvent返回值为false**

将TestButton类的onTouchEvent方法修改如下，其他和基础代码保持不变：
```java
@Override
public boolean onTouchEvent(MotionEvent event) {
    Log.i(null, "onTouchEvent-- action="+event.getAction());
    return false;
}
```

点击Button如下：   
![这里写图片描述](http://img.blog.csdn.net/20150521162024516)  
你会发现如果onTouchEvent返回false（也即dispatchTouchEvent一旦返回false将不再继续派发其他action，立即停止派发），这里只派发了down事件。至于后面触发了LinearLayout的touch与click事件我们这里不做关注，下一篇博客会详细解释为啥（其实你可以想下的，LinearLayout是ViewGroup的子类，你懂的），这里你只用知道View的onTouchEvent返回false会阻止继续派发事件。

同理修改如下：
```java
@Override
public boolean onTouchEvent(MotionEvent event) {
    Log.i(null, "onTouchEvent-- action="+event.getAction());
    super.onTouchEvent(event);
    return false;
}
```

点击Button如下：  
![这里写图片描述](http://img.blog.csdn.net/20150521162525409)

#### **4-2-4 简单修改dispatchTouchEvent返回值为true**

将TestButton类的dispatchTouchEvent方法修改如下，其他和基础代码保持不变：

```java
@Override
public boolean dispatchTouchEvent(MotionEvent event) {
    Log.i(null, "dispatchTouchEvent-- action=" + event.getAction());
    return true;
}
```

点击Button如下：  
![这里写图片描述](http://img.blog.csdn.net/20150521163217862)

你会发现如果dispatchTouchEvent直接返回true且不调运super任何事件都得不到触发。

继续修改如下呢？   
将TestButton类的dispatchTouchEvent方法修改如下，其他和基础代码保持不变：
```java
@Override
public boolean dispatchTouchEvent(MotionEvent event) {
    Log.i(null, "dispatchTouchEvent-- action=" + event.getAction());
    super.dispatchTouchEvent(event);
    return true;
}
```

点击Button如下：  
![这里写图片描述](http://img.blog.csdn.net/20150521163417932)

可以发现所有事件都可以得到正常派发，和4.2.1类似。

#### **4-2-5 简单修改dispatchTouchEvent返回值为false**

将TestButton类的dispatchTouchEvent方法修改如下，其他和基础代码保持不变：
```java
@Override
public boolean dispatchTouchEvent(MotionEvent event) {
    Log.i(null, "dispatchTouchEvent-- action=" + event.getAction());
    return false;
}
```

点击Button如下：  
![这里写图片描述](http://img.blog.csdn.net/20150521163709111)

你会发现事件不进行任何继续触发，关于点击Button触发了LinearLayout的事件暂时不用关注，下篇详解。

继续修改如下呢？   
将TestButton类的dispatchTouchEvent方法修改如下，其他和基础代码保持不变：
```java
@Override
public boolean dispatchTouchEvent(MotionEvent event) {
    Log.i(null, "dispatchTouchEvent-- action=" + event.getAction());
    super.dispatchTouchEvent(event);
    return false;
}
```

点击Button如下：   
![这里写图片描述](http://img.blog.csdn.net/20150521164032664)  
你会发现结果和4.2.3的第二部分结果一样，也就是说如果dispatchTouchEvent返回false事件将不再继续派发下一次。

#### **4-2-6 简单修改dispatchTouchEvent与onTouchEvent返回值**

**修改dispatchTouchEvent返回值为true，onTouchEvent为false：**

将TestButton类的dispatchTouchEvent方法和onTouchEvent方法修改如下，其他和基础代码保持不变：
```java
@Override
public boolean dispatchTouchEvent(MotionEvent event) {
    Log.i(null, "dispatchTouchEvent-- action=" + event.getAction());
    super.dispatchTouchEvent(event);
    return true;
}

@Override
public boolean onTouchEvent(MotionEvent event) {
    Log.i(null, "onTouchEvent-- action=" + event.getAction());
    super.onTouchEvent(event);
    return false;
}
```

点击Button如下：  
![这里写图片描述](http://img.blog.csdn.net/20150521164403213)

**修改dispatchTouchEvent返回值为false，onTouchEvent为true：**

将TestButton类的dispatchTouchEvent方法和onTouchEvent方法修改如下，其他和基础代码保持不变：
```java
@Override
public boolean dispatchTouchEvent(MotionEvent event) {
    Log.i(null, "dispatchTouchEvent-- action=" + event.getAction());
    super.dispatchTouchEvent(event);
    return false;
}

@Override
public boolean onTouchEvent(MotionEvent event) {
    Log.i(null, "onTouchEvent-- action=" + event.getAction());
    super.onTouchEvent(event);
    return true;
}
```

点击Button如下：  
![这里写图片描述](http://img.blog.csdn.net/20150521164637264)

由此对比得出结论，dispatchTouchEvent事件派发是传递的，如果返回值为false将停止下次事件派发，如果返回true将继续下次派发。譬如，当前派发down事件，如果返回true则继续派发up，如果返回false派发完down就停止了。

### **4-1 总结**

这个例子组合了很多种情况的值去验证上面源码的分析，同时也为自定义控件打下了基础。仔细理解这个例子对于View的事件传递就差不多了。

## **5 总结View触摸屏事件传递机制**

上面例子也测试了，源码也分析了，总得有个最终结论方便平时写代码作为参考依据呀，不能每次都再去分析一遍源码，那得多蛋疼呢！

综合得出Android View的触摸屏事件传递机制有如下特征：

1.  触摸控件（View）首先执行dispatchTouchEvent方法。
2.  在dispatchTouchEvent方法中先执行onTouch方法，后执行onClick方法（onClick方法在onTouchEvent中执行，下面会分析）。
3.  如果控件（View）的onTouch返回false或者mOnTouchListener为null（控件没有设置setOnTouchListener方法）或者控件不是enable的情况下会调运onTouchEvent，dispatchTouchEvent返回值与onTouchEvent返回一样。
4.  如果控件不是enable的设置了onTouch方法也不会执行，只能通过重写控件的onTouchEvent方法处理（上面已经处理分析了），dispatchTouchEvent返回值与onTouchEvent返回一样。
5.  如果控件（View）是enable且onTouch返回true情况下，dispatchTouchEvent直接返回true，不会调用onTouchEvent方法。
6.  当dispatchTouchEvent在进行事件分发的时候，只有前一个action返回true，才会触发下一个action（也就是说dispatchTouchEvent返回true才会进行下一次action派发）。

> 【工匠若水 [http://blog.csdn.net/yanbober](http://blog.csdn.net/yanbober)】

> **关于上面的疑惑还有ViewGroup事件派发机制你可以继续阅读下一篇博客[《Android触摸屏事件派发机制详解与源码分析二(ViewGroup篇)》](http://blog.csdn.net/yanbober/article/details/45912661)**，以便继续分析View之外的ViewGroup事件传递机制。
