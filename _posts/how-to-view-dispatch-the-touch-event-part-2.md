title: Android触摸屏事件派发机制详解与源码分析二(ViewGroup篇)
date: 2015-07-04
tags: [基础]
categories: [其他]
---

## <a name="t0"></a>**1 背景**

还记得前一篇[《Android触摸屏事件派发机制详解与源码分析一(View篇)》](http://blog.csdn.net/yanbober/article/details/45887547)中关于透过源码继续进阶实例验证模块中存在的点击Button却触发了LinearLayout的事件疑惑吗？当时说了，在那一篇咱们只讨论View的触摸事件派发机制，这个疑惑留在了这一篇解释，也就是ViewGroup的事件派发机制。

关于View与ViewGroup的区别在前一篇的Android 5.1.1(API 22) View触摸屏事件传递源码分析部分的写在前面的话里面有详细介绍。其实你只要记住类似Button这种控件都是View的子类，类似布局这种控件都是ViewGroup的子类，而ViewGroup又是View的子类而已。具体查阅[《Android触摸屏事件派发机制详解与源码分析一(View篇)》](http://blog.csdn.net/yanbober/article/details/45887547)。
<!--more-->
## <a name="t1"></a>**2 基础实例现象**

### <a name="t2"></a>**2-1 例子**

这个例子布局等还和上一篇的例子相似，只是重写了Button和LinearLayout而已，所以效果图不在提供，具体参见上一篇。

首先我们简单的自定义一个Button（View的子类），再自定义一个LinearLayout（ViewGroup的子类），其实没有自定义任何属性，只是重写部分方法（添加了打印，方便查看）而已，如下：

```java
public class TestButton extends Button {

    public TestButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.i(null, "TestButton dispatchTouchEvent-- action=" + event.getAction());
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(null, "TestButton onTouchEvent-- action=" + event.getAction());
        return super.onTouchEvent(event);
    }
}

public class TestLinearLayout extends LinearLayout {
    public TestLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.i(null, "TestLinearLayout onInterceptTouchEvent-- action=" + ev.getAction());
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.i(null, "TestLinearLayout dispatchTouchEvent-- action=" + event.getAction());
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(null, "TestLinearLayout onTouchEvent-- action=" + event.getAction());
        return super.onTouchEvent(event);
    }
}
```

如上两个控件很简单吧，不解释，继续看其他代码：

```xml
<?xml version="1.0" encoding="utf-8"?>
<com.zzci.light.TestLinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
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
</com.zzci.light.TestLinearLayout>

public class ListenerActivity extends Activity implements View.OnTouchListener, View.OnClickListener {
    private TestLinearLayout mLayout;
    private TestButton mButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        mLayout = (TestLinearLayout) this.findViewById(R.id.mylayout);
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

到此基础示例的代码编写完成。没有啥难度，很简单易懂，不多解释了。

### <a name="t3"></a>**2-2 运行现象**

当直接点击Button时打印现象如下：

```
TestLinearLayout dispatchTouchEvent-- action=0
TestLinearLayout onInterceptTouchEvent-- action=0
TestButton dispatchTouchEvent-- action=0
OnTouchListener--onTouch-- action=0 --com.zzci.light.TestButton
TestButton onTouchEvent-- action=0
TestLinearLayout dispatchTouchEvent-- action=1
TestLinearLayout onInterceptTouchEvent-- action=1
TestButton dispatchTouchEvent-- action=1
OnTouchListener--onTouch-- action=1 --com.zzci.light.TestButton
TestButton onTouchEvent-- action=1
OnClickListener--onClick--com.zzci.light.TestButton
```

分析：你会发现这个结果好惊讶吧，点击了Button却先执行了TestLinearLayout（ViewGroup）的dispatchTouchEvent，接着执行TestLinearLayout（ViewGroup）的onInterceptTouchEvent，接着执行TestButton（TestLinearLayout包含的成员View）的dispatchTouchEvent，接着就是View触摸事件的分发流程，上一篇已经讲过了。也就是说当点击View时事件派发每一个down，up的action顺序是先触发最父级控件（这里为LinearLayout）的dispatchTouchEvent->onInterceptTouchEvent->然后向前一级传递（这里就是传递到Button View）。

那么继续看，当直接点击除Button以外的其他部分时打印如下：

```
TestLinearLayout dispatchTouchEvent-- action=0
TestLinearLayout onInterceptTouchEvent-- action=0
OnTouchListener--onTouch-- action=0 --com.zzci.light.TestLinearLayout
TestLinearLayout onTouchEvent-- action=0
TestLinearLayout dispatchTouchEvent-- action=1
OnTouchListener--onTouch-- action=1 --com.zzci.light.TestLinearLayout
TestLinearLayout onTouchEvent-- action=1
OnClickListener--onClick--com.zzci.light.TestLinearLayout
```

分析：你会发现一个奇怪的现象，派发ACTION_DOWN（action=0）事件时顺序为dispatchTouchEvent->onInterceptTouchEvent->onTouch->onTouchEvent，而接着派发ACTION_UP（action=1）事件时与上面顺序不同的时竟然没触发onInterceptTouchEvent方法。这是为啥呢？我也纳闷，那就留着下面分析源码再找答案吧，先记住这个问题。

有了上面这个例子你是不是发现包含ViewGroup与View的事件触发有些相似又有很大差异吧（PS：在Android中继承View实现的控件已经是最小单位了，也即在XML布局等操作中不能再包含子项了，而继承ViewGroup实现的控件通常不是最小单位，可以包含不确定数目的子项）。具体差异是啥呢？咱们类似上篇一样，带着这个实例疑惑去看源码找答案吧。

## <a name="t4"></a>**3 Android 5.1.1(API 22) ViewGroup触摸屏事件传递源码分析**

通过上面例子的打印我们可以确定分析源码的顺序，那就开始分析呗。

### <a name="t5"></a>**3-1 从ViewGroup的dispatchTouchEvent方法说起**

前一篇的3-2小节说在Android中你只要触摸控件首先都会触发控件的dispatchTouchEvent方法（其实这个方法一般都没在具体的控件类中，而在他的父类View中）。这其实是思维单单局限在View的角度去看待的，这里通过上面的例子你是否发现触摸控件会先从他的父级dispatchTouchEvent方法开始派发呢？是的，所以咱们先从ViewGroup的dispatchTouchEvent方法说起，如下：

```java
public boolean dispatchTouchEvent(MotionEvent ev) {
    if (mInputEventConsistencyVerifier != null) {
        mInputEventConsistencyVerifier.onTouchEvent(ev, 1);
    }

    // If the event targets the accessibility focused view and this is it, start
    // normal event dispatch. Maybe a descendant is what will handle the click.
    if (ev.isTargetAccessibilityFocus() && isAccessibilityFocusedViewOrHost()) {
        ev.setTargetAccessibilityFocus(false);
    }

    boolean handled = false;
    if (onFilterTouchEventForSecurity(ev)) {
        final int action = ev.getAction();
        final int actionMasked = action & MotionEvent.ACTION_MASK;

        // Handle an initial down.
        if (actionMasked == MotionEvent.ACTION_DOWN) {
            // Throw away all previous state when starting a new touch gesture.
            // The framework may have dropped the up or cancel event for the previous gesture
            // due to an app switch, ANR, or some other state change.
            cancelAndClearTouchTargets(ev);
            resetTouchState();
        }

        // Check for interception.
        final boolean intercepted;
        if (actionMasked == MotionEvent.ACTION_DOWN
                || mFirstTouchTarget != null) {
            final boolean disallowIntercept = (mGroupFlags & FLAG_DISALLOW_INTERCEPT) != 0;
            if (!disallowIntercept) {
                intercepted = onInterceptTouchEvent(ev);
                ev.setAction(action); // restore action in case it was changed
            } else {
                intercepted = false;
            }
        } else {
            // There are no touch targets and this action is not an initial down
            // so this view group continues to intercept touches.
            intercepted = true;
        }

        // If intercepted, start normal event dispatch. Also if there is already
        // a view that is handling the gesture, do normal event dispatch.
        if (intercepted || mFirstTouchTarget != null) {
            ev.setTargetAccessibilityFocus(false);
        }

        // Check for cancelation.
        final boolean canceled = resetCancelNextUpFlag(this)
                || actionMasked == MotionEvent.ACTION_CANCEL;

        // Update list of touch targets for pointer down, if needed.
        final boolean split = (mGroupFlags & FLAG_SPLIT_MOTION_EVENTS) != 0;
        TouchTarget newTouchTarget = null;
        boolean alreadyDispatchedToNewTouchTarget = false;
        if (!canceled && !intercepted) {

            // If the event is targeting accessiiblity focus we give it to the
            // view that has accessibility focus and if it does not handle it
            // we clear the flag and dispatch the event to all children as usual.
            // We are looking up the accessibility focused host to avoid keeping
            // state since these events are very rare.
            View childWithAccessibilityFocus = ev.isTargetAccessibilityFocus()
                    ? findChildWithAccessibilityFocus() : null;

            if (actionMasked == MotionEvent.ACTION_DOWN
                    || (split && actionMasked == MotionEvent.ACTION_POINTER_DOWN)
                    || actionMasked == MotionEvent.ACTION_HOVER_MOVE) {
                final int actionIndex = ev.getActionIndex(); // always 0 for down
                final int idBitsToAssign = split ? 1 << ev.getPointerId(actionIndex)
                        : TouchTarget.ALL_POINTER_IDS;

                // Clean up earlier touch targets for this pointer id in case they
                // have become out of sync.
                removePointersFromTouchTargets(idBitsToAssign);

                final int childrenCount = mChildrenCount;
                if (newTouchTarget == null && childrenCount != 0) {
                    final float x = ev.getX(actionIndex);
                    final float y = ev.getY(actionIndex);
                    // Find a child that can receive the event.
                    // Scan children from front to back.
                    final ArrayList<View> preorderedList = buildOrderedChildList();
                    final boolean customOrder = preorderedList == null
                            && isChildrenDrawingOrderEnabled();
                    final View[] children = mChildren;
                    for (int i = childrenCount - 1; i >= 0; i--) {
                        final int childIndex = customOrder
                                ? getChildDrawingOrder(childrenCount, i) : i;
                        final View child = (preorderedList == null)
                                ? children[childIndex] : preorderedList.get(childIndex);

                        // If there is a view that has accessibility focus we want it
                        // to get the event first and if not handled we will perform a
                        // normal dispatch. We may do a double iteration but this is
                        // safer given the timeframe.
                        if (childWithAccessibilityFocus != null) {
                            if (childWithAccessibilityFocus != child) {
                                continue;
                            }
                            childWithAccessibilityFocus = null;
                            i = childrenCount - 1;
                        }

                        if (!canViewReceivePointerEvents(child)
                                || !isTransformedTouchPointInView(x, y, child, null)) {
                            ev.setTargetAccessibilityFocus(false);
                            continue;
                        }

                        newTouchTarget = getTouchTarget(child);
                        if (newTouchTarget != null) {
                            // Child is already receiving touch within its bounds.
                            // Give it the new pointer in addition to the ones it is handling.
                            newTouchTarget.pointerIdBits |= idBitsToAssign;
                            break;
                        }

                        resetCancelNextUpFlag(child);
                        if (dispatchTransformedTouchEvent(ev, false, child, idBitsToAssign)) {
                            // Child wants to receive touch within its bounds.
                            mLastTouchDownTime = ev.getDownTime();
                            if (preorderedList != null) {
                                // childIndex points into presorted list, find original index
                                for (int j = 0; j < childrenCount; j++) {
                                    if (children[childIndex] == mChildren[j]) {
                                        mLastTouchDownIndex = j;
                                        break;
                                    }
                                }
                            } else {
                                mLastTouchDownIndex = childIndex;
                            }
                            mLastTouchDownX = ev.getX();
                            mLastTouchDownY = ev.getY();
                            newTouchTarget = addTouchTarget(child, idBitsToAssign);
                            alreadyDispatchedToNewTouchTarget = true;
                            break;
                        }

                        // The accessibility focus didn't handle the event, so clear
                        // the flag and do a normal dispatch to all children.
                        ev.setTargetAccessibilityFocus(false);
                    }
                    if (preorderedList != null) preorderedList.clear();
                }

                if (newTouchTarget == null && mFirstTouchTarget != null) {
                    // Did not find a child to receive the event.
                    // Assign the pointer to the least recently added target.
                    newTouchTarget = mFirstTouchTarget;
                    while (newTouchTarget.next != null) {
                        newTouchTarget = newTouchTarget.next;
                    }
                    newTouchTarget.pointerIdBits |= idBitsToAssign;
                }
            }
        }

        // Dispatch to touch targets.
        if (mFirstTouchTarget == null) {
            // No touch targets so treat this as an ordinary view.
            handled = dispatchTransformedTouchEvent(ev, canceled, null,
                    TouchTarget.ALL_POINTER_IDS);
        } else {
            // Dispatch to touch targets, excluding the new touch target if we already
            // dispatched to it.  Cancel touch targets if necessary.
            TouchTarget predecessor = null;
            TouchTarget target = mFirstTouchTarget;
            while (target != null) {
                final TouchTarget next = target.next;
                if (alreadyDispatchedToNewTouchTarget && target == newTouchTarget) {
                    handled = true;
                } else {
                    final boolean cancelChild = resetCancelNextUpFlag(target.child)
                            || intercepted;
                    if (dispatchTransformedTouchEvent(ev, cancelChild,
                            target.child, target.pointerIdBits)) {
                        handled = true;
                    }
                    if (cancelChild) {
                        if (predecessor == null) {
                            mFirstTouchTarget = next;
                        } else {
                            predecessor.next = next;
                        }
                        target.recycle();
                        target = next;
                        continue;
                    }
                }
                predecessor = target;
                target = next;
            }
        }

        // Update list of touch targets for pointer up or cancel, if needed.
        if (canceled
                || actionMasked == MotionEvent.ACTION_UP
                || actionMasked == MotionEvent.ACTION_HOVER_MOVE) {
            resetTouchState();
        } else if (split && actionMasked == MotionEvent.ACTION_POINTER_UP) {
            final int actionIndex = ev.getActionIndex();
            final int idBitsToRemove = 1 << ev.getPointerId(actionIndex);
            removePointersFromTouchTargets(idBitsToRemove);
        }
    }

    if (!handled && mInputEventConsistencyVerifier != null) {
        mInputEventConsistencyVerifier.onUnhandledEvent(ev, 1);
    }
    return handled;
}
```

我勒个去！！！这比View的dispatchTouchEvent方法长很多啊，那就只关注重点分析吧。

**第一步，17-24行，对ACTION_DOWN进行处理。**

因为ACTION_DOWN是一系列事件的开端，当是ACTION_DOWN时进行一些初始化操作，从上面源码中注释也可以看出来，清除以往的Touch状态然后开始新的手势。在这里你会发现cancelAndClearTouchTargets(ev)方法中有一个非常重要的操作就是将mFirstTouchTarget设置为了null（刚开始分析大眼瞄一眼没留意，结果越往下看越迷糊，所以这个是分析ViewGroup的dispatchTouchEvent方法第一步中重点要记住的一个地方），接着在resetTouchState()方法中重置Touch状态标识。

**第二步，26-47行，检查是否要拦截。**

在dispatchTouchEvent(MotionEvent ev)这段代码中使用变量intercepted来标记ViewGroup是否拦截Touch事件的传递，该变量类似第一步的mFirstTouchTarget变量，在后续代码中起着很重要的作用。`if (actionMasked == MotionEvent.ACTION_DOWN || mFirstTouchTarget != null)`这一条判断语句说明当事件为ACTION_DOWN或者mFirstTouchTarget不为null(即已经找到能够接收touch事件的目标组件)时if成立，否则if不成立，然后将intercepted设置为true，也即拦截事件。当当事件为ACTION_DOWN或者mFirstTouchTarget不为null时判断disallowIntercept(禁止拦截)标志位，而这个标记在ViewGroup中提供了public的设置方法，如下：

```java
public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    if (disallowIntercept == ((mGroupFlags & FLAG_DISALLOW_INTERCEPT) != 0)) {
        // We're already in this state, assume our ancestors are too
        return;
    }

    if (disallowIntercept) {
        mGroupFlags |= FLAG_DISALLOW_INTERCEPT;
    } else {
        mGroupFlags &= ~FLAG_DISALLOW_INTERCEPT;
    }

    // Pass it up to our parent
    if (mParent != null) {
        mParent.requestDisallowInterceptTouchEvent(disallowIntercept);
    }
}
```

所以你可以在其他地方调用requestDisallowInterceptTouchEvent(boolean disallowIntercept)方法，从而禁止执行是否需要拦截的判断。当disallowIntercept为true（禁止拦截判断）时则intercepted直接设置为false，否则调用onInterceptTouchEvent(ev)方法，然后将结果赋值给intercepted。那就来看下ViewGroup与众不同与View特有的onInterceptTouchEvent方法，如下：

```java
public boolean onInterceptTouchEvent(MotionEvent ev) {
    return false;
}
```

看见了吧，默认的onInterceptTouchEvent方法只是返回了一个false，也即intercepted=false。所以可以说明上面例子的部分打印（dispatchTouchEvent->onInterceptTouchEvent->onTouchEvent），这里很明显表明在ViewGroup的dispatchTouchEvent()中默认（不在其他地方调运requestDisallowInterceptTouchEvent方法设置禁止拦截标记）首先调用了onInterceptTouchEvent()方法。

**第三步，49-51行，检查cancel。**

通过标记和action检查cancel，然后将结果赋值给局部boolean变量canceled。

**第四步，53-函数结束，事件分发。**

54行首先可以看见获取一个boolean变量标记split来标记，默认是true，作用是是否把事件分发给多个子View，这个同样在ViewGroup中提供了public的方法设置，如下：

```java
public void setMotionEventSplittingEnabled(boolean split) {
    // TODO Applications really shouldn't change this setting mid-touch event,
    // but perhaps this should handle that case and send ACTION_CANCELs to any child views
    // with gestures in progress when this is changed.
    if (split) {
        mGroupFlags |= FLAG_SPLIT_MOTION_EVENTS;
    } else {
        mGroupFlags &= ~FLAG_SPLIT_MOTION_EVENTS;
    }
}
```

接着57行`if (!canceled && !intercepted)`判断表明，事件不是ACTION_CANCEL并且ViewGroup的拦截标志位intercepted为false(不拦截)则会进入其中。

**事件分发步骤中关于ACTION_DOWN的特殊处理**

接着67行这个很大的if语句`if (actionMasked == MotionEvent.ACTION_DOWN || (split && actionMasked == MotionEvent.ACTION_POINTER_DOWN) || actionMasked == MotionEvent.ACTION_HOVER_MOVE)`处理ACTION_DOWN事件，这个环节比较繁琐，也比较重要，如下具体分析。

在79行判断了childrenCount个数是否不为0，然后接着在84行拿到了子View的list集合preorderedList；接着在88行通过一个for循环i从childrenCount - 1开始遍历到0，倒序遍历所有的子view，这是因为preorderedList中的顺序是按照addView或者XML布局文件中的顺序来的，后addView添加的子View，会因为Android的UI后刷新机制显示在上层；假如点击的地方有两个子View都包含的点击的坐标，那么后被添加到布局中的那个子view会先响应事件；这样其实也是符合人的思维方式的，因为后被添加的子view会浮在上层，所以我们去点击的时候一般都会希望点击最上层的那个组件先去响应事件。

接着在106到112行通过getTouchTarget去查找当前子View是否在mFirstTouchTarget.next这条target链中的某一个targe中，如果在则返回这个target，否则返回null。在这段代码的if判断通过说明找到了接收Touch事件的子View，即newTouchTarget，那么，既然已经找到了，所以执行break跳出for循环。如果没有break则继续向下执行走到115行开始到134行，这里你可以看见一段if判断的代码`if (dispatchTransformedTouchEvent(ev, false, child, idBitsToAssign))`，这个被if的大括弧括起来的一段代码很重要，具体解释如下：

调用方法dispatchTransformedTouchEvent()将Touch事件传递给特定的子View。该方法十分重要，在该方法中为一个递归调用，会递归调用dispatchTouchEvent()方法。在dispatchTouchEvent()中如果子View为ViewGroup并且Touch没有被拦截那么递归调用dispatchTouchEvent()，如果子View为View那么就会调用其onTouchEvent()。dispatchTransformedTouchEvent方法如果返回true则表示子View消费掉该事件，同时进入该if判断。满足if语句后重要的操作有：

*   给newTouchTarget赋值；
*   给alreadyDispatchedToNewTouchTarget赋值为true；
*   执行break，因为该for循环遍历子View判断哪个子View接受Touch事件，既然已经找到了就跳出该外层for循环；

如果115行if判断中的dispatchTransformedTouchEvent()方法返回false，即子View的onTouchEvent返回false(即Touch事件未被消费)，那么就不满足该if条件，也就无法执行addTouchTarget()，从而导致mFirstTouchTarget为null（没法对mFirstTouchTarget赋值，因为上面分析了mFirstTouchTarget一进来是ACTION_DOWN就置位为null了），那么该子View就无法继续处理ACTION_MOVE事件和ACTION_UP事件（28行的判断为false，也即intercepted=true了，所以之后一系列判断无法通过）。

如果115行if判断中的dispatchTransformedTouchEvent()方法返回true，即子View的onTouchEvent返回true(即Touch事件被消费)，那么就满足该if条件，从而mFirstTouchTarget不为null。

继续看143行的判断`if (newTouchTarget == null && mFirstTouchTarget != null)`。该if表示经过前面的for循环没有找到子View接收Touch事件并且之前的mFirstTouchTarget不为空则为真，然后newTouchTarget指向了最初的TouchTarget。

通过上面67到157行关于事件分发步骤中ACTION_DOWN的特殊处理可以发现，对于此处ACTION_DOWN的处理具体体现在dispatchTransformedTouchEvent()方法，该方法返回值具备如下特征：

return | description | set
-------|-------------|--------
true | 事件被消费 | mFirstTouchTarget!=null
false | 事件未被消费 | mFirstTouchTarget==null
 
因为在dispatchTransformedTouchEvent()会调用递归调用dispatchTouchEvent()和onTouchEvent()，所以dispatchTransformedTouchEvent()的返回值实际上是由onTouchEvent()决定的。简单地说onTouchEvent()是否消费了Touch事件的返回值决定了dispatchTransformedTouchEvent()的返回值，从而决定mFirstTouchTarget是否为null，进一步决定了ViewGroup是否处理Touch事件，这一点在160行开始的代码中有体现。如下分析事件分发步骤中关于ACTION_DOWN处理之后的其他处理逻辑，也即160行开始剩余的逻辑。

**事件分发步骤中关于ACTION_DOWN处理之后的其他处理逻辑**

可以看到，如果派发的事件不是ACTION_DOWN就不会经过上面的流程，而是直接从此处开始执行。上面说了，经过上面对于ACTION_DOWN的处理后mFirstTouchTarget可能为null或者不为null。所以可以看见161行代码`if (mFirstTouchTarget == null)与else`判断了mFirstTouchTarget值是否为null的情况，完全符合如上分析。那我们分情况继续分析一下：

当161行if判断的mFirstTouchTarget为null时，也就是说Touch事件未被消费，即没有找到能够消费touch事件的子组件或Touch事件被拦截了，则调用ViewGroup的dispatchTransformedTouchEvent()方法处理Touch事件（和普通View一样），即子View没有消费Touch事件，那么子View的上层ViewGroup才会调用其onTouchEvent()处理Touch事件。具体就是在调用dispatchTransformedTouchEvent()时第三个参数为null，关于dispatchTransformedTouchEvent方法下面会分析，暂时先记住就行。

这下再回想上面例子，点击Button时为啥触发了Button的一系列touch方法而没有触发父级LinearLayout的touch方法的疑惑？明白了吧？

子view对于Touch事件处理返回true那么其上层的ViewGroup就无法处理Touch事件了，子view对于Touch事件处理返回false那么其上层的ViewGroup才可以处理Touch事件。

当161行if判断的mFirstTouchTarget不为null时，也就是说找到了可以消费Touch事件的子View且后续Touch事件可以传递到该子View。可以看见在源码的else中对于非ACTION_DOWN事件继续传递给目标子组件进行处理，依然是递归调用dispatchTransformedTouchEvent()方法来实现的处理。

到此ViewGroup的dispatchTouchEvent方法分析完毕。

上面说了ViewGroup的dispatchTouchEvent方法详细情况，也知道在其中可能会执行onInterceptTouchEvent方法，所以接下来咱们先简单分析一下这个方法。

### <a name="t6"></a>**3-2 说说ViewGroup的dispatchTouchEvent中可能执行的onInterceptTouchEvent方法**

如下系统源码：

```java
public boolean onInterceptTouchEvent(MotionEvent ev) {
    return false;
}
```

看到了吧，这个方法算是ViewGroup不同于View特有的一个事件派发调运方法。在源码中可以看到这个方法实现很简单，但是有一堆注释。其实上面分析了，如果ViewGroup的onInterceptTouchEvent返回false就不阻止事件继续传递派发，否则阻止传递派发。

对了，还记得在dispatchTouchEvent方法中除过可能执行的onInterceptTouchEvent以外在后面派发事件时执行的dispatchTransformedTouchEvent方法吗？上面分析dispatchTouchEvent时说了下面会仔细分析，那么现在就来继续看看这个方法吧。

### <a name="t7"></a>**3-3 继续说说ViewGroup的dispatchTouchEvent中执行的dispatchTransformedTouchEvent方法**

ViewGroup的dispatchTransformedTouchEvent方法系统源码如下：

```java
private boolean dispatchTransformedTouchEvent(MotionEvent event, boolean cancel,
        View child, int desiredPointerIdBits) {
    final boolean handled;

    // Canceling motions is a special case.  We don't need to perform any transformations
    // or filtering.  The important part is the action, not the contents.
    final int oldAction = event.getAction();
    if (cancel || oldAction == MotionEvent.ACTION_CANCEL) {
        event.setAction(MotionEvent.ACTION_CANCEL);
        if (child == null) {
            handled = super.dispatchTouchEvent(event);
        } else {
            handled = child.dispatchTouchEvent(event);
        }
        event.setAction(oldAction);
        return handled;
    }

    // Calculate the number of pointers to deliver.
    final int oldPointerIdBits = event.getPointerIdBits();
    final int newPointerIdBits = oldPointerIdBits & desiredPointerIdBits;

    // If for some reason we ended up in an inconsistent state where it looks like we
    // might produce a motion event with no pointers in it, then drop the event.
    if (newPointerIdBits == 0) {
        return false;
    }

    // If the number of pointers is the same and we don't need to perform any fancy
    // irreversible transformations, then we can reuse the motion event for this
    // dispatch as long as we are careful to revert any changes we make.
    // Otherwise we need to make a copy.
    final MotionEvent transformedEvent;
    if (newPointerIdBits == oldPointerIdBits) {
        if (child == null || child.hasIdentityMatrix()) {
            if (child == null) {
                handled = super.dispatchTouchEvent(event);
            } else {
                final float offsetX = mScrollX - child.mLeft;
                final float offsetY = mScrollY - child.mTop;
                event.offsetLocation(offsetX, offsetY);

                handled = child.dispatchTouchEvent(event);

                event.offsetLocation(-offsetX, -offsetY);
            }
            return handled;
        }
        transformedEvent = MotionEvent.obtain(event);
    } else {
        transformedEvent = event.split(newPointerIdBits);
    }

    // Perform any necessary transformations and dispatch.
    if (child == null) {
        handled = super.dispatchTouchEvent(transformedEvent);
    } else {
        final float offsetX = mScrollX - child.mLeft;
        final float offsetY = mScrollY - child.mTop;
        transformedEvent.offsetLocation(offsetX, offsetY);
        if (! child.hasIdentityMatrix()) {
            transformedEvent.transform(child.getInverseMatrix());
        }

        handled = child.dispatchTouchEvent(transformedEvent);
    }

    // Done.
    transformedEvent.recycle();
    return handled;
}
```

看到了吧，这个方法也算是ViewGroup不同于View特有的一个事件派发调运方法，而且奇葩的就是这个方法也很长。那也继续分析吧。。。

上面分析了，在dispatchTouchEvent()中调用dispatchTransformedTouchEvent()将事件分发给子View处理。在此我们需要重点分析该方法的第三个参数（View child）。在dispatchTouchEvent()中多次调用了dispatchTransformedTouchEvent()方法，而且有时候第三个参数为null，有时又不是，他们到底有啥区别呢？这段源码中很明显展示了结果。在dispatchTransformedTouchEvent()源码中可以发现多次对于child是否为null的判断，并且均做出如下类似的操作。其中，当child == null时会将Touch事件传递给该ViewGroup自身的dispatchTouchEvent()处理，即super.dispatchTouchEvent(event)（也就是View的这个方法，因为ViewGroup的父类是View）；当child != null时会调用该子view(当然该view可能是一个View也可能是一个ViewGroup)的dispatchTouchEvent(event)处理，即child.dispatchTouchEvent(event)。别的代码几乎没啥需要具体注意分析的。

所以，到此你也会发现ViewGroup没有重写View的onTouchEvent(MotionEvent event) 方法，也就是说接下来的调运关系就是上一篇分析的流程了，这里不在多说。

好了，到此你是不是即明白了上面实例演示的代码结果，也明白了上一篇最后升级实例验证模块留下的点击Button触发了LinearLayout的一些疑惑呢？答案自然是必须的！

## <a name="t8"></a>**4 Android 5.1.1(API 22) ViewGroup触摸屏事件传递总结**

如上就是所有ViewGroup关于触摸屏事件的传递机制源码分析与实例演示。具体总结如下：

1.  Android事件派发是先传递到最顶级的ViewGroup，再由ViewGroup递归传递到View的。
2.  在ViewGroup中可以通过onInterceptTouchEvent方法对事件传递进行拦截，onInterceptTouchEvent方法返回true代表不允许事件继续向子View传递，返回false代表不对事件进行拦截，默认返回false。
3.  子View中如果将传递的事件消费掉，ViewGroup中将无法接收到任何事件。

【工匠若水 [http://blog.csdn.net/yanbober](http://blog.csdn.net/yanbober)】

好了，至此整个View与ViewGroup的触摸屏事件派发机制分析完毕。关于他们的事件是哪派发来的可以继续进阶的阅读下一篇[《Android触摸屏事件派发机制详解与源码分析三(Activity篇)》](http://blog.csdn.net/yanbober/article/details/45932123)
