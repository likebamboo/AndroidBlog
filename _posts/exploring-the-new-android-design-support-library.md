title: 探索新的Android Material Design支持库
date: 2015-06-24
tags: [材料设计,SupportLibrary]
categories: [翻译,材料设计]
---

Android Material Design支持库提供了一些新组件，我们在这里简要的介绍一下这些组件，以及如何使用这些组件。

我是Material Design的粉丝，它使应用程序更具有一致性和整体性,而且看起来更美观，更容易使用。  
Google I / O大会2015年引进一些很棒的新Android特性，包括新的Material Design支持库。  
Material Design的介绍： [Material Design Guidelines](http://www.google.com/design/spec/material-design/introduction.html#) (译注：请自备梯子)  
让我们一起来看看这些我们现在能用的新组件。
<!--more-->
## Snackbar

<video loop="" video="" autoplay="" class="graf-image" data-image-id="1*xwgvwuq2GD426XDp_zeZBw.gif" data-width="388" data-height="690"><source src="https://d262ilb51hltx0.cloudfront.net/max/800/1*xwgvwuq2GD426XDp_zeZBw.ogv" type="video/ogg"><source src="https://d262ilb51hltx0.cloudfront.net/max/800/1*xwgvwuq2GD426XDp_zeZBw.mp4" type="video/mp4"></video>

Snackbar是带有动画效果的快速提示条，它只会出现在屏幕底部。  
它基本上继承了Toast的方法和属性，但与Toast不同的是，Snackbar上可以带有按钮，  
当Snackbar出现时，用户可以点击按钮做相应的处理；  
Snackbar支持滑动消失，类似通知栏的消息；  
如果用户没做任何操作，Snackbar在到达设定的时间后也会自动消失。  
![](https://d262ilb51hltx0.cloudfront.net/max/800/1*O3WtW1Xi7SpMS2tk9ihzHQ.png)

对开发者来说，我们只要简单的几行代码就可以实现Snackbar
```java
Snackbar.make(mDrawerLayout, "Your message", Snackbar.LENGTH_SHORT)
    .setAction(getString(R.string.text_undo), this)
    .show();	
```
_注意:_ 不能同时显示多个Snackbar

## Floating Action Button

[Floating Action Button](http://www.google.co.uk/design/spec/components/buttons-floating-action-button.html) (FAB)是一个悬浮按钮, 它主要用于一些重要的操作，比如在列表界面上新增按钮。  
现在我们可以在程序里很容易实现Floating Action Button，不再需要其他三方库的支持。

这个按钮我们使用时一般用以下2中尺寸:  
**Normal** (56dp) — 大部分情况下使用  
**Mini** (40dp) — 只有在与屏幕上其他组件保持一致性的时候使用

![](https://d262ilb51hltx0.cloudfront.net/max/800/1*htO3x82bq7HWSZab-70v4A.png)  
Normal (left) 和 Mini (right) FAB 按钮

FAB按钮默认会使用主题中定义的背景色，但我们也可以很容易的修改背景色，以下是一些我们一般会修改的属性：

*   fabSize - 设定FAB的大小 (‘normal’ or ‘mini’)
*   backgroundTint - 设置边框大小
*   rippleColor - 设定按下时的颜色
*   src - 设定在FAB中显示的图标
*   layout_anchor - 设置显示坐标的锚点
*   layout_anchorGravity - 设置锚点的对齐方式

我们只要简单加入以下代码，就可以实现FAB:
```xml
<android.support.design.widget.FloatingActionButton
     android:id=”@+id/fab_normal”
     android:layout_width=”wrap_content”
     android:layout_height=”wrap_content”
     android:src=”@drawable/ic_plus”
     app:fabSize=”normal” />
```

## EditText Floating Labels

TextInputLayout主要用于包含EditText，会默认生成一个浮动的Label, 当我们选择EditText时, EditText中设置的hint会上浮到EditText的左上角.  
这对提交用户数据很有用。

<video loop="" video="" autoplay="" class="graf-image" data-image-id="1*cHpl5ROayZZjEh_7ZExEPw.gif" data-width="388" data-height="175"><source src="https://d262ilb51hltx0.cloudfront.net/max/800/1*cHpl5ROayZZjEh_7ZExEPw.ogv" type="video/ogg"><source src="https://d262ilb51hltx0.cloudfront.net/max/800/1*cHpl5ROayZZjEh_7ZExEPw.mp4" type="video/mp4">Your browser does not support the video tag.</video>

实现很简单，只要包含EditText就可以了:
```xml
<android.support.design.widget.TextInputLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content">

    <EditText
        android:id="@+id/edit_text_email"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="textEmailAddress"
        android:hint="@string/hint_email" />

</android.support.design.widget.TextInputLayout>
```

它同时支持显示错误信息, 我们主要加入如下代码即可:
```java
setErrorEnabled(true);
setError(getString(R.string.text_error_message));
```

<video loop="" video="" autoplay="" class="graf-image" data-image-id="1*qnatfYsUcN_4s5ar1KZAnA.gif" data-width="388" data-height="199"><source src="https://d262ilb51hltx0.cloudfront.net/max/800/1*qnatfYsUcN_4s5ar1KZAnA.ogv" type="video/ogg"><source src="https://d262ilb51hltx0.cloudfront.net/max/800/1*qnatfYsUcN_4s5ar1KZAnA.mp4" type="video/mp4">Your browser does not support the video tag.</video>

_注意:_ 设置错误信息需要在setErrorEnabled标志之后，这样可以保证在错误出现时layout大小不发生变化

## Navigation View

Navigation Drawer在现在的APP中很常见, 以前实现一直不怎么容易，  
现在提供的NavigationView组件可以直接放在DrawerLayout中，  
通过设置menu resource就能显示菜单项了。

![](https://d262ilb51hltx0.cloudfront.net/max/800/1*AsV0mMvJ21ni3aoSQqQ5LA.png)
```xml
<android.support.v4.widget.DrawerLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/drawer_layout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true">

    <FrameLayout
        android:id="@+id/main_content_frame"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

    <android.support.design.widget.NavigationView
        android:id="@+id/navigation_view"
        android:layout_width="wrap_content"
        android:layout_height="match_parent"
        android:layout_gravity="start"
        app:headerLayout="@layout/navigation_header"
        app:menu="@menu/drawer" />

</android.support.v4.widget.DrawerLayout>
```

这个View有两个主要属性:

### Header Layout

headerLayout是一个可选得属性，通过设置它我们可以在导航栏上面增加一个Header,通用的做法我们在上面显示用户信息。

### Menu

menu属性用来定义需要引用的menu resource。

![](https://d262ilb51hltx0.cloudfront.net/max/2000/1*mBoN5QvKq6mZE5tLNo0Khg.png)  
如下所示, NavigationView menus我们一般有两张用法，第一种是使用标准的单选模式：  
```xml
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"    
    tools:context=".MainActivity">
    <group android:checkableBehavior="single">
        <item
            android:id="@+id/navigation_item_1"
            android:checked="true"
            android:icon="@drawable/ic_android"
            android:title="@string/navigation_item_1" />
        <item
            android:id="@+id/navigation_item_2"
            android:icon="@drawable/ic_android"
            android:title="@string/navigation_item_2" />
    </group>
</menu>
```

这样菜单项只是简单的罗列，所有的菜单项都属于同一个分组。

第二种用法也是相似的，不过我们可以进行分组，给每一个分组定义标题，如下所示：
```xml
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"                       
    tools:context=".MainActivity">
    <group android:checkableBehavior="single">
        <item
            android:id="@+id/navigation_subheader"
            android:title="@string/nav_header">
            <menu>
                <!-- Menu items go here -->
            </menu>
         </item>
    </group>
</menu>
```

这样我们就可以把我们的菜单项进行分组，这还是很有用得，我们可以按功能把菜单项进行分组。

还有一些重要属性的属性我们可以设置，如下:

*   itemBackground — 设置菜单项的背景
*   itemIconTint — 设置菜单项的图标
*   itemTextColor — 这只菜单项目的文本颜色

我们可以通过实现OnNavigationItemSelectedListener方法，处理菜单项的点击事件。

_注意_: For API21+, the NavigationView automatically takes care of scrim protection for the status bar.

## TabLayout

TabLayout可以很容易地在APP中添加Tab分组功能

我们有好几种方式来使用它:

*   固定Tabs，根据View的宽度适配  
    ![](https://d262ilb51hltx0.cloudfront.net/max/800/1*pmTCUt3WtAEXWh0vUX1Tsw.png)

*   固定Tabs, 在View中居中显示  
    ![](https://d262ilb51hltx0.cloudfront.net/max/800/1*xGCDe6ARHeHk2v9pnXS_bA.png)

*   可滑动的Tabs

    <video loop="" video="" autoplay="" class="graf-image" data-image-id="1*CiuC-A6TxcOJvn_hMVr0jA.gif" data-width="388" data-height="57"><source src="https://d262ilb51hltx0.cloudfront.net/max/800/1*CiuC-A6TxcOJvn_hMVr0jA.ogv" type="video/ogg"><source src="https://d262ilb51hltx0.cloudfront.net/max/800/1*CiuC-A6TxcOJvn_hMVr0jA.mp4" type="video/mp4"></video>

要实现上述效果，首先我们需要加入TabLayout:  
```xml
<android.support.design.widget.TabLayout
    android:id="@+id/sliding_tabs"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:tabMode="fixed"
    app:tabGravity="fill" />
```

然后, 我们可以通过以下这些属性调整TabLayout的外观:

*   tabMode - TabLayoutd模式，可以选择 **fixed** 或 **scrollable**
*   tabGravity - Tab的对齐方式, 可以选择 **fill** 或 **centre**
*   setText() - 设置Tab上的文字
*   setIcon() - 设置Tab上的图标

我们还可以给TabLayout设置一些Listener:

*   OnTabSelectedListener - Tab被选中时，触发的Listener
*   TabLayoutOnPageChangeListener
*   ViewPagerOnTabSelectedListener

我们添加好TabLayout后, 我们只需要通过setupWithViewPager方法加入viewpager:
```java
ViewPager pager = (ViewPager)rootView.findViewById(R.id.viewPager);
pager.setAdapter(new MyPagerAdapter(getActivity().getSupportFragmentManager()));

TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.sliding_tabs);
tabLayout.addTab(tabLayout.newTab().setText("Tab One"));
tabLayout.addTab(tabLayout.newTab().setText("Tab Two"));
tabLayout.addTab(tabLayout.newTab().setText("Tab Three"));
tabLayout.setupWithViewPager(pager);
```

## Coordinator Layout

CoordinatorLayout是组织它的子views之间协作的一个Layout，它可以给子View切换提供动画效果。  
要使用这个组件，请升级其他support library中的组件到最新版本，比如我需要把RecyclerView升级到22.2.0。

*   **Floating Action Button**

我们刚才已经知道Snackbar可以显示在其他UI组件的上面，不过我们可以让FloatingActionButton不被Snackbar覆盖，  
当Snackbar出现时，FAB上移，Snackbar消失时，FAB下移。

<video loop="" video="" autoplay="" class="graf-image" data-image-id="1*aquWj6YbdiBpURPkmor_ww.gif" data-width="388" data-height="144"><source src="https://d262ilb51hltx0.cloudfront.net/max/800/1*aquWj6YbdiBpURPkmor_ww.ogv" type="video/ogg"><source src="https://d262ilb51hltx0.cloudfront.net/max/800/1*aquWj6YbdiBpURPkmor_ww.mp4" type="video/mp4">Your browser does not support the video tag.</video>

要实现如上的效果，FloatingActionBar必须包含在CoordinatorLayout中，  
接着我们需要设置**layout_anchor** 和 **layout_anchorGravity**属性
```xml
<android.support.design.widget.CoordinatorLayout
    android:id="@+id/main_content">
<!-- Your other views -->
    <android.support.design.widget.FloatingActionButton
        android:id=”@+id/fab_normal”
        android:layout_width=”wrap_content”
        android:layout_height=”wrap_content”
        android:src=”@drawable/ic_plus”
        app:layout_anchor="@id/main_content"
        app:layout_anchorGravity="bottom|right"
        app:fabSize=”normal” />
</android.support.design.widget.CoordinatorLayout>
```

最后, 在我们构造Snackbar时, 我们需要把CoordinatorLayout作为View参数传递过去, 如下所示:  
```java
Snackbar.make(mCoordinator, "Your message", Snackbar.LENGTH_SHORT).show();
```

### App Bar

CoordinatorLayout我们让我们根据滚动事件来调整子View的布局，比如在滚动内容时，我们可以隐藏Toolbar。

要实现这个效果，首先我们需要设置**layout_scrollFlags**属性，这个属性用来控制跟随View滚动还是固定在最上面，  
这个属性可以设置为以下几种值：

*   enterAlways - 实现quick return效果, 当向下移动时，显示View（比如Toolbar)

<video loop="" video="" autoplay="" class="graf-image" data-image-id="1*HJUrvQWhZLHpIGd6s8pw2g.gif" data-width="388" data-height="690"><source src="https://d262ilb51hltx0.cloudfront.net/max/800/1*HJUrvQWhZLHpIGd6s8pw2g.ogv" type="video/ogg"><source src="https://d262ilb51hltx0.cloudfront.net/max/800/1*HJUrvQWhZLHpIGd6s8pw2g.mp4" type="video/mp4">Your browser does not support the video tag.</video>

*   enterAlwaysCollapsed - 当你的View已经设置minHeight属性又使用此标志时，你的View只能以最小高度进入，只有当滚动视图到达顶部时才扩大到完整高度。

<video loop="" video="" autoplay="" class="graf-image" data-image-id="1*p3LUCtXT7Zxyqja6Ildi7Q.gif" data-width="388" data-height="690"><source src="https://d262ilb51hltx0.cloudfront.net/max/800/1*p3LUCtXT7Zxyqja6Ildi7Q.ogv" type="video/ogg"><source src="https://d262ilb51hltx0.cloudfront.net/max/800/1*p3LUCtXT7Zxyqja6Ildi7Q.mp4" type="video/mp4">Your browser does not support the video tag.</video>

*   exitUntilCollapsed - 向上滚动时收缩View，但可以固件Toolbar一直在上面

<video loop="" video="" autoplay="" class="graf-image" data-image-id="1*B978QTrWe-bNLcdnNRgGxw.gif" data-width="388" data-height="690"><source src="https://d262ilb51hltx0.cloudfront.net/max/800/1*B978QTrWe-bNLcdnNRgGxw.ogv" type="video/ogg"><source src="https://d262ilb51hltx0.cloudfront.net/max/800/1*B978QTrWe-bNLcdnNRgGxw.mp4" type="video/mp4">Your browser does not support the video tag.</video>

_注意_: 设置_scroll_标志的View必须在没有设置的之前定义，这样可以确保设置过的View都从上面移出, 只留下那些固定的View在下面。

如下代码所示, 我们的recycler view设置了**layout_behavior**属性，  
当我们的recycler view滑动时，就会触发设置了layout_scrollFlags的控件发生状态的改变。  
不过我们没有设置TabLayout的layout_scrollFlags属性, 所以TabLayout会固定在屏幕最上方。
```xml
<android.support.design.widget.CoordinatorLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
     
    <android.support.v7.widget.RecyclerView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_behavior=
        "@string/appbar_scrolling_view_behavior" />

    <android.support.design.widget.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">
        
        <android.support.v7.widget.Toolbar
            ...
            app:layout_scrollFlags="scroll|enterAlways" />

        <android.support.design.widget.TabLayout
            ...
            />
    </android.support.design.widget.AppBarLayout>
</android.support.design.widget.CoordinatorLayout>
```

### ToolBars

现在我们可以使用CollapsingToolbarLayout，它可以实现当屏幕内容滚动时，收缩Toolbar
```xml
<android.support.design.widget.AppBarLayout
        android:layout_height="192dp"
        android:layout_width="match_parent">
    <android.support.design.widget.CollapsingToolbarLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:layout_scrollFlags="scroll|exitUntilCollapsed">
        <android.support.v7.widget.Toolbar
                android:layout_height="?attr/actionBarSize"
                android:layout_width="match_parent"
                app:layout_collapseMode="pin" />
        </android.support.design.widget.CollapsingToolbarLayout>
</android.support.design.widget.AppBarLayout>
```

当我们使用这个组件时, **layout_collapseMode**必须设置, 它有两个选项：

*   **Pin -** 设置为这个模式时，当CollapsingToolbarLayout完全收缩后，Toolbar还可以保留在屏幕上

    <video loop="" video="" autoplay="" class="graf-image" data-image-id="1*rDsxrspi35eM6yRgqnHZig.gif" data-width="388" data-height="462"><source src="https://d262ilb51hltx0.cloudfront.net/max/800/1*rDsxrspi35eM6yRgqnHZig.ogv" type="video/ogg"><source src="https://d262ilb51hltx0.cloudfront.net/max/800/1*rDsxrspi35eM6yRgqnHZig.mp4" type="video/mp4">Your browser does not support the video tag.</video>
*   **Parallax** - 设置为这个模式时，在内容滚动时，CollapsingToolbarLayout中的View（比如ImageView)也可以同时滚动，实现视差滚动效果.  
    可以通过_layout_collapseParallaxMultiplier*_设置视差因子。

<video loop="" video="" autoplay="" class="graf-image" data-image-id="1*dHI4V_uRApsDJidhvUTwvg.gif" data-width="388" data-height="482"><source src="https://d262ilb51hltx0.cloudfront.net/max/800/1*dHI4V_uRApsDJidhvUTwvg.ogv" type="video/ogg"><source src="https://d262ilb51hltx0.cloudfront.net/max/800/1*dHI4V_uRApsDJidhvUTwvg.mp4" type="video/mp4">Your browser does not support the video tag.</video>

通过CollapsingToolbarLayout的setText()方法，我们就可以实现让文字大小随着缩放慢慢变小。

### Custom Views

我们还可以给自定义View定义Behaviour, 在onDependentViewChanged()方法中做相应的回调处理，这可以更好处理touch事件, 手势操作和子View之间的依赖关系。

那么你还在等什么呢? 加入这个Material Design支持库，赶紧试试吧！
```
compile 'com.android.support:design:22.2.0'
```

本文译自：[Exploring the new Android Design Support Library](https://medium.com/ribot-labs/exploring-the-new-android-design-support-library-b7cda56d2c32)

> 英文原文：[Exploring the new Android Design Support Library](https://medium.com/ribot-labs/exploring-the-new-android-design-support-library-b7cda56d2c32)
> 译文原文：[探索新的Android Material Design支持库](http://www.aswifter.com/2015/06/21/andorid-material-design-support-library/)
