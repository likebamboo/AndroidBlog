title: Google Developing for Android 系列8
date: 2015-06-15
tags: [翻译,性能优化]
categories: [翻译,连载]
---

## 避免过度绘制

正如在第一篇Context介绍种关于GPU讨论的部分所说，很多手机的性能有限，如果应用中有严重的过度绘制可能会导致比较糟糕的渲染性能。不透明的View会完全遮盖其它的View的情况下会导致渲染引擎进行多次绘制。你可以通过开启设置中的开发者选项的GPU overdraw来修复相应的问题。
<!--more-->
## 避免无用的背景

避免过度绘制的一种方式是：移除已经拥有非透明背景View的背景。适用于View的背景不透明而且完全遮住了window背景的情况。

移除window backgroud的方式是一种很有效的技术，但可能是比较复杂的方式去解决过度绘制，并且在有些情况下还会导致一些奇怪的渲染问题。在manifest中为应用设置一个null backgroud虽然可行，但由于系统不能够正确的绘制starting window，可能会导致一些图形化的问题。更好的方式是在manifest中保留启动时的backgroud，而在Activity的onCreate方法中通过getWindow().setBackgroud(null)将backgroud设置为null。即便是这种方式还是会导致一些问题。比如，键盘或者IME被设置为自适应大小模式，然后以动画的方式进入到一个backgroud设置为null的activity中，因为window manager不会对window的背景进行任何绘制，从而导致一些问题。另外，全屏的ListView在由于滑动时的回弹产生的间隙也会有一些问题（可以通过ListView.setOverscrollFooter/Header来解决）。

这种情况下正确定位过度绘制的方式是使用starting window。放置想要的背景图片并通过window自身的windowBackgroud主题属性让这些介于容器之间的View保持默认的透明背景，而不是在window 背景和Views之间使用一些带有不透明背景色的容器。

## 避免关闭Starting Window功能

正如在[性能](https://medium.com/google-developers/developing-for-android-iii-2efc140167fd)篇中提到的如何加快启动速度的问题一样，当应用被加载的时候，通过starting window可以提供一种快速启动的体验。请求系统关闭starting window会失去这种体验（通过windowDisabl  
ePreview主题属性设置）。一些应用这样做的原因时他们希望启动一个自定义的splash screen或者其它品牌性质的体验。或者因为starting window看上去与启动的activity的初始化内容看上去不搭。这种处理方式就会导致那些app会花费更长的时间启动，并且由于activity没有任何可见反馈，用户就得干等着。

为了提供最好的体验，你应该开启默认的starting window。如果需要优化，可以选择activity的主题，该主题会被starting window使用，从而更好的适应你的activity。当然你也可以为starting window制定自定义的drawable（通过windowBackgroud主题属性）以便更好的品牌化或者定制启动体验。

## 沉浸模式下允许方便的退出

应用可以开启全屏幕模式，这种模式下只有滑动屏幕的边缘才会展开导航栏。这种模式只有在沉浸式的游戏中是可以被接受的，因为并不希望用户点击某块屏幕的时候会退出游戏。对于其他类型的app来说，特别是内容浏览的app，比如media player，当用户点击屏幕时应该更容易的退出，而不是滑动方式再去展开导航。更多关于如何正确的使用沉浸模式请参考 (immersive mode develper guide)[[https://developer.android.com/training/system-ui/immersive.html](https://developer.android.com/training/system-ui/immersive.html)]

## 为starting window设置正确的状态/导航栏的颜色

如果你的应用拥有带颜色的状态栏或者导航栏，那么你的主题（用于应用启动时的starting window）应该拥有相同的颜色。为了避免从starting window 到应用内容窗口的切换比较突兀，可以通过android:statusBarColor和android:navigationBarColor属性为starting window设置状态栏或者导航栏的颜色。如果你的activity window需要不同的颜色可以通过getWindow().setStatusBarColor()和getWindow.setNavigationbarColor()方法设置。

## 使用适当的Context

你可以从Application获得Context，但是将它用于创建UI的Context来说是不合适的，因为Application Context没有正确的主题信息，因此你应该使用Activity的Context对象。比如，你正在为activity获取资源，那么你应该使用activity的context而不是Application的。

## 避免在异步回调中出现View的相关引用

避免在网络操作或者长时间的异步交互中引用一个View，Activity，或者Fragment，比如：  

```java
// ...
// some activity code
// 
void onClick(View view) {
    webservice.fetchPosts(new Callback() {
        public void onResult(Response repsonse) {
            // View may not be valid, Activity may be gone
        }
    });
}
```

View的引用可能导致占用过多资源的Context泄漏（也有可能是对View的间接引用），或者导致应用crash，因为的那个回调时，旧的引用可能已经不存在了。因此，考虑使用事件总线或者依赖性低的 回调并小心的处理attachment和detachement。

## 为RTL设计

在API 17中，应用应该使用布局的start/end 而不是left/right属性。比如，你应该使用paddingStart和paddingEnd，而不是paddingLeft和paddingRignt。这样可以保证在从右到左的情况下正确的布局。

测试应用布局的正确性：开启开发者选项下的`Force RTL layout direction`设置。

## 对数据进行本地缓存

“本地缓存，全局同步”  
本地缓存数据对于提高启动速度是很重要的事情之一。除了一些像图片的大文件，也应该保存从服务器获取的数据。如果可以的话，你应该创建一个本地数据库来以一种更有意义的方式来保存这些（不是简单的对服务返回结果的序列化）。

在Google I/O 2010中有一个[Android Rest Client Application](https://www.youtube.com/watch?v=xHXn3Kg2IQE)的演讲。这里介绍了一些组件，相比多去，它们可以更好的帮助我们开发（ORM，JobQueue，EventBus），但演讲中谈论的思想和方式仍然是有效的。

## 对用户的输入进行本地缓存

当用户的输入需要转发到服务器时，应该在转发到服务器之前缓存它。可以想象得到，在很多累积的响应情况下，网络交互可能是异步机制的（比如一个网络请求池）。对用户输入进行缓存的主要原因是避免网络失败情况下，用户的输入丢失。缓存输入让应用可以让你在网络状态正常时进行重试。

在UI中你应该提供一个提示，能够让用户知道自己的请求已经完成了还是延迟了。

## 将网络和后台硬盘操作分开

当你使用一个线程池维护后台所有的操作时，因为长时间的后台操作（比如网络请求），某些快速任务可能要被迫等待。对于本地的存储操作，考虑使用独立的线程池。这样可以确保本地变化可以立即更新到用户的界面而不是受到长时间的网络请求或者IPC调用的影响。

> 译文链接：[http://www.lightskystreet.com/2015/06/13/google-for-android-8-user-interface/](http://www.lightskystreet.com/2015/06/13/google-for-android-8-user-interface/)   
> 译文作者：[lightSky](http://www.lightskystreet.com/)  
> 原文链接：[Developing for Android VIII The Rules: User Interface](https://medium.com/google-developers/developing-for-android-viii-e91ced595fac)
