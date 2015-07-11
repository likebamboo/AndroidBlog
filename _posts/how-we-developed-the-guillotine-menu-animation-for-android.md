title: 我们是如何实现一个Android铡刀菜单的
date: 2015-06-23
tags: [特效]
categories: [其他]
---

你可能已经阅读了关于设计师Vitaly Rubtsov 和ios开发者Maksym Lazebnyi 创建独特的top bar动画的[故事](http://yalantis.com/blog/how-we-created-guillotine-menu-animation/)，这个动画菜单被取了一个不吉利的名字 － 铡刀（断头台用的）菜单（你可以在[Dribbble](https://dribbble.com/shots/2018249-Guillotine-Menu?list=users&offset=11) 和 [GitHub](https://github.com/Yalantis/GuillotineMenu)上看到这个ios动画）。很快，我们的安卓开发工程师Dmytro Denysenko接受了在安卓上实现相同动画的挑战（可在[GitHub](https://github.com/Yalantis/GuillotineMenu-Android)上查看）。牛逼的是，他甚至都不知道自己会面临什么样的困难，也不知道自己要做多深的研究才能解决这个问题。
<!--more-->
![](http://www.jcodecraeer.com/uploads/20150618/1434565628830943.gif)

## 牛吃南瓜是如何开始的?

最开始，我想采用传统的方法来实现这个控件，毕竟，第一眼看上去是完全可能的。我打算使用ObjectAnimation来实现navigation view的旋转，还准备添加一个默认的BounceInterpolator来达到菜单触到屏幕左边沿时的来回反弹的效果。但是 BounceInterpolator似乎让反弹有点过头了，就像足球的反弹，不是我们要的金属铡刀的效果。

默认的BounceInterpolator没有提供任何自定义属性，因此我除了写一个自己的interpolator之外别无选择。除了反弹效果之外，还应该创建一个自由落体的加速效果来让动画更自然。

这个铡刀控件包含了铡刀的旋转，铡刀的反弹，以及actionbar的反弹。另外，我还用了两个自定义的interpolator来分别实现自由落体加速效果和反弹效果。

现在我们来讲讲开发过程。

如何实现铡刀菜单的旋转  
 关于旋转的动画，我需要做两件事：找到旋转的中心，然后实现一个ObjectAnimation来做旋转的实际工作。

在计算旋转中心之前，我们需要将布局放到屏幕中。
```java
private void setUpOpeningView(final View openingView) {

   if (mActionBarView != null) {
       mActionBarView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

           @Override

           public void onGlobalLayout() {

               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
mActionBarView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

               } else {
mActionBarView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
               }

               mActionBarView.setPivotX(calculatePivotX(openingView));
               mActionBarView.setPivotY(calculatePivotY(openingView));
           }
       });
   }

}

private float calculatePivotY(View burger) {
   return burger.getTop() + burger.getHeight() / 2
}

private float calculatePivotY(View burger) {
   return burger.getTop() + burger.getHeight() / 2;
}

```

完了之后，我们只需要添加几行代码：

```java
ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(mGuillotineView, "rotation", GUILLOTINE_OPENED_ANGLE, GUILLOTINE_CLOSED_ANGLE);
 
/* setting duration, listeners, interpolator, etc. */
 
rotationAnimator.start();
```

旋转的中心启示就是汉堡菜单（三根横线）的中心。这个动画需要两个汉堡图标：一个在主 action bar上，另一个在铡刀布局上。为了让动画看起来更自然，两个汉堡需要一致并且使用相同的坐标体系。

为此，我在toolbar上创建了一个汉堡图标（看不见），然后将它与铡刀菜单汉堡图标的中心重合。

## 如何实现自由落体和反弹

为了在iOS上实现铡刀菜单动画，我的同事Maksym Lazebnyi 使用了一个默认的UIDynamicItemBehavior类，该类自定义了弹力和阻力两个属性。但是，在安卓上可没那么简单。

ps 在安卓上似乎任何事情都没那么简单。

![](http://www.jcodecraeer.com/uploads/20150618/1434565654842777.jpg)

[标准的 Android 插值]

就如刚刚我提到的，我本可以使用默认的BounceInterpolator实现布局的旋转，但是好像这个东西的反弹效果看起来过于柔和了（就好像我们的铡 刀是个球样，－－哈哈哈）。所以我试图自定义一个插值器（interpolation）。我们应该在动画上添加一个加速器（acceleration）。

插值器（interpolation）的比率是从0到1。而我的情况中，旋转的角度是从0°到 90° （顺时针方向）。这就意味着在0°的时候interpolation的比率应该是”0″（起点） ，而在角度为90°的时候，interpolation的比率应该是”1″（终点）。

我们的插值器有一个二次方程，可以同时用于Vitaly动画的屏幕截图中的反弹和下降效果。

我得回忆一下高中数学课程中关于创建自定义插值器的知识。在经过一番思考之后，我画了一个演示对象属性随时间变化的函数曲线图。

![](http://www.jcodecraeer.com/uploads/20150618/1434566628217826.png)

[自定义的插值器]

我写了三个遵循图示的二次方程

![](http://www.jcodecraeer.com/uploads/allimg/150618/0S3251H3-0.png)
```java
public class GuillotineInterpolator implements TimeInterpolator {
 
   public static final float ROTATION_TIME = 0.46667f;
   public static final float FIRST_BOUNCE_TIME = 0.26666f;
   public static final float SECOND_BOUNCE_TIME = 0.26667f;
 
 
   public GuillotineInterpolator() {
   }
 
   public float getInterpolation(float t) {
       if (t < ROTATION_TIME) return rotation(t);
       else if (t < ROTATION_TIME + FIRST_BOUNCE_TIME) return firstBounce(t);
       else return secondBounce(t);
   }
 
   private float rotation(float t) {
       return 4.592f * t * t;
   }
 
   private float firstBounce(float t) {
       return 2.5f * t * t - 3f * t + 1.85556f;
   }
 
   private float secondBounce(float t) {
       return 0.625f * t * t - 1.08f * t + 1.458f;
   }
 
```

## 如何实现actionbar的反弹

现在我们的铡刀菜单可以下落并且在碰撞到屏幕左边缘的时候可以反弹了，但是我们还需要实现一个反弹。

当铡刀菜单回到初始状态的时候，碰到actionbar产生一个反弹效果。为此，我们还需要一个interpolator。

下面是初始和终点在0° 的曲线，不过二次方程是基于和前面相同规则的。
```java
public class ActionBarInterpolator implements TimeInterpolator {
 
   private static final float FIRST_BOUNCE_PART = 0.375f;
   private static final float SECOND_BOUNCE_PART = 0.625f;
 
   @Override
   public float getInterpolation(float t) {
       if (t < FIRST_BOUNCE_PART) {
           return (-28.4444f) * t * t + 10.66667f * t;
       } else if (t < SECOND_BOUNCE_PART) {
           return (21.33312f) * t * t - 21.33312f * t + 4.999950f;
       } else {
           return (-9.481481f) * t * t + 15.40741f * t - 5.925926f;
       }
   }
}
```

这 样我们就得到了三个ObjectAnimation实例：铡刀的打开和关闭，actionbar的旋转，以及两个插值器：铡刀的下落和actionbar 的反弹。我们需要做的只是为动画设置适当的插值器，在菜单关闭的时候立即开始actionbar的反弹，并且将动画和汉堡图的tap事件标绑定。
```java 
ObjectAnimator rotationAnimator = initAnimator(ObjectAnimator.ofFloat(mGuillotineView, ROTATION, GUILLOTINE_CLOSED_ANGLE, GUILLOTINE_OPENED_ANGLE));
rotationAnimator.setInterpolator(mInterpolator);
rotationAnimator.setDuration(mDuration);
rotationAnimator.addListener(new Animator.AnimatorListener() {...});
```

这就是整个过程。虽然创建一个动画是一个不小的挑战，但是值得。现在我们的铡刀菜单就可以在两个平台通用了。

你也可以看看这篇文章: [How we created FlipViewPager animation for Android](http://yalantis.com/blog/how-we-created-flip-view-pager-animation-on-android/)。

## 计划中的特性

我计划在铡刀菜单动画中添加一些新特性。包括swipe transition，支持从右到左的布局，以及横屏布局。请关注我们的更新。

你可以在这里找到项目的例子和设计图：

[GitHub](https://github.com/Yalantis/GuillotineMenu-Android) 、[Dribbble](https://dribbble.com/shots/2018249-Guillotine-Menu?list=users&offset=11)。

> *   原文链接：[How We Developed the Guillotine Menu Animation for Android][How We Developed the Guillotine Menu Animation for Android](http://yalantis.com/blog/how-we-developed-the-guillotine-menu-animation-for-android/?utm_source=github)
> *  译文链接：[我们是如何在Android上实现铡刀菜单(Guillotine Menu)动画的](http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0618/3086.html)
