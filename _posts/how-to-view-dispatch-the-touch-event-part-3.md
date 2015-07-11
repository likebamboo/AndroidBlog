title: Android触摸屏事件派发机制详解与源码分析三(Activity篇)
date: 2015-07-04
tags: [基础]
categories: [其他]
---

## <a name="t0"></a>**1 背景**

还记得前面两篇从Android的基础最小元素控件（View）到ViewGroup控件的触摸屏事件分发机制分析吗？你可能看完会有疑惑，View的事件是ViewGroup派发的，那ViewGroup的事件呢？他包含在Activity上，是不是Activity也有类似的事件派发方法呢？带着这些疑惑咱们继续实例验证加源码分析吧。

PS：阅读本篇前建议先查看前一篇[《Android触摸屏事件派发机制详解与源码分析二(ViewGroup篇)》](http://blog.csdn.net/yanbober/article/details/45912661)与[《Android触摸屏事件派发机制详解与源码分析一(View篇)》](http://blog.csdn.net/yanbober/article/details/45887547)，这一篇承接上一篇。
<!--more-->
## <a name="t1"></a>**2 实例验证**

### <a name="t2"></a>**2-1 代码**

如下实例与前面实例相同，一个Button在LinearLayout里，只不过我们这次重写了Activity的一些方法而已。具体如下：

自定义的Button与LinearLayout：

```java
public class TestButton extends Button {
    public TestButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.i(null, "TestButton--dispatchTouchEvent--action="+event.getAction());
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(null, "TestButton--onTouchEvent--action="+event.getAction());
        return super.onTouchEvent(event);
    }
}

public class TestLinearLayout extends LinearLayout {
    public TestLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.i(null, "TestLinearLayout--onInterceptTouchEvent--action="+ev.getAction());
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.i(null, "TestLinearLayout--dispatchTouchEvent--action=" + event.getAction());
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(null, "TestLinearLayout--onTouchEvent--action="+event.getAction());
        return super.onTouchEvent(event);
    }
}
```

整个界面的布局文件：

```xml
<com.example.yanbo.myapplication.TestLinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:id="@+id/layout">
    <com.example.yanbo.myapplication.TestButton
        android:text="click test"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/button"/>
</com.example.yanbo.myapplication.TestLinearLayout>
```

整个界面Activity，重写了Activity的一些关于触摸派发的方法（三个）：

```java
public class MainActivity extends Activity implements View.OnClickListener, View.OnTouchListener {
    private TestButton mButton;
    private TestLinearLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = (TestButton) this.findViewById(R.id.button);
        mLayout = (TestLinearLayout) this.findViewById(R.id.layout);

        mButton.setOnClickListener(this);
        mLayout.setOnClickListener(this);

        mButton.setOnTouchListener(this);
        mLayout.setOnTouchListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.i(null, "onClick----v=" + v);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.i(null, "onTouch--action="+event.getAction()+"--v="+v);
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.i(null, "MainActivity--dispatchTouchEvent--action=" + ev.getAction());
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onUserInteraction() {
        Log.i(null, "MainActivity--onUserInteraction");
        super.onUserInteraction();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(null, "MainActivity--onTouchEvent--action="+event.getAction());
        return super.onTouchEvent(event);
    }
}
```

如上就是实例测试代码，非常简单，没必要分析，直接看结果吧。

### <a name="t3"></a>**2-2 结果分析**

直接点击Button按钮打印如下：

```
    MainActivity--dispatchTouchEvent--action=0
    MainActivity--onUserInteraction
    TestLinearLayout--dispatchTouchEvent--action=0
    TestLinearLayout--onInterceptTouchEvent--action=0
    TestButton--dispatchTouchEvent--action=0
    onTouch--action=0--v=com.example.yanbo.myapplication.TestButton
    TestButton--onTouchEvent--action=0
    MainActivity--dispatchTouchEvent--action=1
    TestLinearLayout--dispatchTouchEvent--action=1
    TestLinearLayout--onInterceptTouchEvent--action=1
    TestButton--dispatchTouchEvent--action=1
    onTouch--action=1--v=com.example.yanbo.myapplication.TestButton
    TestButton--onTouchEvent--action=1
    onClick----v=com.example.yanbo.myapplication.TestButton
```

分析可以发现，当点击Button时除过派发Activity的几个新方法之外其他完全符合前面两篇分析的View与ViewGroup的触摸事件派发机制。对于Activity来说，ACTION_DOWN事件首先触发dispatchTouchEvent，然后触发onUserInteraction，再次onTouchEvent，接着的ACTION_UP事件触发dispatchTouchEvent后触发了onTouchEvent，也就是说ACTION_UP事件时不会触发onUserInteraction（待会可查看源代码分析原因）。

直接点击Button以外的其他区域：

```
    MainActivity--dispatchTouchEvent--action=0
    MainActivity--onUserInteraction
    TestLinearLayout--dispatchTouchEvent--action=0
    TestLinearLayout--onInterceptTouchEvent--action=0
    onTouch--action=0--v=com.example.yanbo.myapplication.TestLinearLayout
    TestLinearLayout--onTouchEvent--action=0
    MainActivity--dispatchTouchEvent--action=1
    TestLinearLayout--dispatchTouchEvent--action=1
    onTouch--action=1--v=com.example.yanbo.myapplication.TestLinearLayout
    TestLinearLayout--onTouchEvent--action=1
    onClick----v=com.example.yanbo.myapplication.TestLinearLayout
```

怎么样？完全符合上面点击Button结果分析的猜想。

那接下来还是要看看Activity里关于这几个方法的源码了。

## <a name="t4"></a>**3 Android 5.1.1(API 22) Activity触摸屏事件传递源码分析**

通过上面例子的打印我们可以确定分析源码的顺序，那就开始分析呗。

### <a name="t5"></a>**3-1 从Activity的dispatchTouchEvent方法说起**

#### <a name="t6"></a>**3-1-1 开始分析**

先上源码，如下：
```java
/**
 * Called to process touch screen events.  You can override this to
 * intercept all touch screen events before they are dispatched to the
 * window.  Be sure to call this implementation for touch screen events
 * that should be handled normally.
 *
 * @param ev The touch screen event.
 *
 * @return boolean Return true if this event was consumed.
 */
public boolean dispatchTouchEvent(MotionEvent ev) {
    if (ev.getAction() == MotionEvent.ACTION_DOWN) {
        onUserInteraction();
    }
    if (getWindow().superDispatchTouchEvent(ev)) {
        return true;
    }
    return onTouchEvent(ev);
}
```

哎呦！这次看着代码好少的样子，不过别高兴，浓缩才是精华，这里代码虽少，涉及的问题点还是很多的，那么咱们就来一点一点分析吧。

12到14行看见了吧？上面例子咱们看见只有ACTION_DOWN事件派发时调运了onUserInteraction方法，当时还在疑惑呢，这下明白了吧，不多解释，咱们直接跳进去可以看见是一个空方法，具体下面会分析。

好了，自己分析15到17行，看着简单吧，我勒个去，我怎么有点懵，这是哪的方法？咱们分析分析吧。

首先分析Activity的attach方法可以发现getWindow()返回的就是PhoneWindow对象（PhoneWindow为抽象Window的实现子类），那就简单了，也就相当于PhoneWindow类的方法，而PhoneWindow类实现于Window抽象类，所以先看下Window类中抽象方法的定义，如下：

```java
 /**
  * Used by custom windows, such as Dialog, to pass the touch screen event
  * further down the view hierarchy. Application developers should
  * not need to implement or call this.
  *
  */
 public abstract boolean superDispatchTouchEvent(MotionEvent event);
```

看见注释没有？用户不需要重写实现的方法，实质也不能，在Activity中没有提供重写的机会，因为Window是以组合模式与Activity建立关系的。好了，看完了抽象的Window方法，那就去PhoneWindow里看下Window抽象方法的实现吧，如下：

```java
@Override
public boolean superDispatchTouchEvent(MotionEvent event) {
    return mDecor.superDispatchTouchEvent(event);
}
```

又是看着好简单的样子哦，实际又是一堆问题，继续分析。你会发现在PhoneWindow的superDispatchTouchEvent方法里又直接返回了另一个mDecor对象的superDispatchTouchEvent方法，mDecor是啥？继续分析吧。

在PhoneWindow类里发现，mDecor是DecorView类的实例，同时DecorView是PhoneWindow的内部类。最惊人的发现是DecorView extends FrameLayout implements RootViewSurfaceTaker，看见没有？它是一个真正Activity的root view,它继承了FrameLayout。怎么验证他一定是root view呢？很简单，不知道大家是不是熟悉Android App开发技巧中关于UI布局优化使用的SDK工具Hierarchy Viewer。咱们通过他来看下上面刚刚展示的那个例子的Hierarchy Viewer你就明白了，如下我在Ubuntu上截图的Hierarchy Viewer分析结果：

![这里写图片描述](http://img.blog.csdn.net/20150523104058889)

看见没有，我们上面例子中Activity中setContentView时放入的xml layout是一个LinearLayout，其中包含一个Button，上图展示了我们放置的LinearLayout被放置在一个id为content的FrameLayout的布局中，这也就是为啥Activity的setContentView方法叫set content view了，就是把我们的xml放入了这个id为content的FrameLayout中。

赶快回过头，你是不是发现上面PhoneWindow的superDispatchTouchEvent直接返回了DecorView的superDispatchTouchEvent，而DecorView又是FrameLayout的子类，FrameLayout又是ViewGroup的子类。机智的你想到了啥木有？

没想到就继续看下DecorView类的superDispatchTouchEvent方法吧，如下：

```java
public boolean superDispatchTouchEvent(MotionEvent event) {
    return super.dispatchTouchEvent(event);
}
```

这回你一定恍然大悟了吧，不然就得脑补前面两篇博客的内容了。。。

搞半天Activity的dispatchTouchEvent方法的15行`if (getWindow().superDispatchTouchEvent(ev))`本质执行的是一个ViewGroup的dispatchTouchEvent方法（这个ViewGroup是Activity特有的root view，也就是id为content的FrameLayout布局），接下来就不用多说了吧，完全是前面两篇分析的执行过程。

接下来依据派发事件返回值决定是否触发Activity的onTouchEvent方法。

#### <a name="t7"></a>**3-1-2 小总结一下**

在Activity的触摸屏事件派发中：

1.  首先会触发Activity的dispatchTouchEvent方法。
2.  dispatchTouchEvent方法中如果是ACTION_DOWN的情况下会接着触发onUserInteraction方法。
3.  接着在dispatchTouchEvent方法中会通过Activity的root View（id为content的FrameLayout），实质是ViewGroup，通过super.dispatchTouchEvent把touchevent派发给各个activity的子view，也就是我们再Activity.onCreat方法中setContentView时设置的view。
4.  若Activity下面的子view拦截了touchevent事件(返回true)则Activity.onTouchEvent方法就不会执行。

### <a name="t8"></a>**3-2 继续Activity的dispatchTouchEvent方法中调运的onUserInteraction方法**

如下源码：

```java
/**
 * Called whenever a key, touch, or trackball event is dispatched to the
 * activity.  Implement this method if you wish to know that the user has
 * interacted with the device in some way while your activity is running.
 * This callback and {@link #onUserLeaveHint} are intended to help
 * activities manage status bar notifications intelligently; specifically,
 * for helping activities determine the proper time to cancel a notfication.
 *
 * <p>All calls to your activity's {@link #onUserLeaveHint} callback will
 * be accompanied by calls to {@link #onUserInteraction}.  This
 * ensures that your activity will be told of relevant user activity such
 * as pulling down the notification pane and touching an item there.
 *
 * <p>Note that this callback will be invoked for the touch down action
 * that begins a touch gesture, but may not be invoked for the touch-moved
 * and touch-up actions that follow.
 *
 * @see #onUserLeaveHint()
 */
public void onUserInteraction() {
}
```

搞了半天就像上面说的，这是一个空方法，那它的作用是啥呢？

此方法是activity的方法，当此activity在栈顶时，触屏点击按home，back，menu键等都会触发此方法。下拉statubar、旋转屏幕、锁屏不会触发此方法。所以它会用在屏保应用上，因为当你触屏机器 就会立马触发一个事件，而这个事件又不太明确是什么，正好屏保满足此需求；或者对于一个Activity，控制多长时间没有用户点响应的时候，自己消失等。

这个方法也分析完了，那就剩下onTouchEvent方法了，如下继续分析。

### <a name="t9"></a>**3-3 继续Activity的dispatchTouchEvent方法中调运的onTouchEvent方法**

如下源码：

```java
/**
 * Called when a touch screen event was not handled by any of the views
 * under it.  This is most useful to process touch events that happen
 * outside of your window bounds, where there is no view to receive it.
 *
 * @param event The touch screen event being processed.
 *
 * @return Return true if you have consumed the event, false if you haven't.
 * The default implementation always returns false.
 */
public boolean onTouchEvent(MotionEvent event) {
    if (mWindow.shouldCloseOnTouch(this, event)) {
        finish();
        return true;
    }

    return false;
}
```

看见没有，这个方法看起来好简单的样子。

如果一个屏幕触摸事件没有被这个Activity下的任何View所处理，Activity的onTouchEvent将会调用。这对于处理window边界之外的Touch事件非常有用，因为通常是没有View会接收到它们的。返回值为true表明你已经消费了这个事件，false则表示没有消费，默认实现中返回false。

继续分析吧，重点就一句，mWindow.shouldCloseOnTouch(this, event)中的mWindow实际就是上面分析dispatchTouchEvent方法里的getWindow()对象，所以直接到Window抽象类和PhoneWindow子类查看吧，发现PhoneWindow没有重写Window的shouldCloseOnTouch方法，所以看下Window类的shouldCloseOnTouch实现吧，如下：

```java
/** @hide */
public boolean shouldCloseOnTouch(Context context, MotionEvent event) {
    if (mCloseOnTouchOutside && event.getAction() == MotionEvent.ACTION_DOWN
            && isOutOfBounds(context, event) && peekDecorView() != null) {
        return true;
    }
    return false;
}
```

这其实就是一个判断，判断mCloseOnTouchOutside标记及是否为ACTION_DOWN事件，同时判断event的x、y坐标是不是超出Bounds，然后检查FrameLayout的content的id的DecorView是否为空。其实没啥太重要的，这只是对于处理window边界之外的Touch事件有判断价值而已。

所以，到此Activity的onTouchEvent分析完毕。

## <a name="t10"></a>**4 Android触摸事件综合总结**

到此整个Android的Activity->ViewGroup->View的触摸屏事件分发机制完全分析完毕。这时候你可以回过头看这三篇文章的例子，你会完全明白那些打印的含义与原理。

当然，了解这些源码机制不仅对你写普通代码时有帮助，最重要的是对你想自定义装逼控件时有不可磨灭的基础性指导作用与技巧提示作用。
