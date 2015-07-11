title: Google Developing for Android 系列3
date: 2015-06-11
tags: [翻译,性能优化]
categories: [翻译,连载]
---

在Android中，性能和内存的关系很密切，因为系统的整体内存大小会影响所有进程的性能，因为垃圾回收器会对运行期间的性能产生很大的影响。下面的重点是运行期间的性能问题而不是内存。
<!--more-->
## 避免在动画和交互期间繁重的操作

正如在第一篇文章中提到过的，在UI Thread做繁重的操作会影响到渲染的处理。同样会导致动画的问题，因为它依赖于每一帧的渲染。这就意味着在动画期间避免在UI进行繁重的操作就更加重要。以下是一些可以避免的常见情况：

*   **Layout**  
    Measurement 和 layout是比较繁重的操作，view的层级越复杂，操作就会越繁重。Measurement和layout是在UI Thred发发生的。因此当系统需要运行一个动画的时候紧接着还需进行layout，而它们都是在同一个线程，因此动画的流畅度可能就会受到影响。  
    假设你的动画在13ms内就可以完成所有的渲染，在16帧率之内。然后某一请求导致了layout，花费了5ms的时间。该layout在下一帧绘制前会发生，那么总的绘制时间就会达到18ms，最终你的动画就明显的跳过一帧。当动画过程中需要进行layout的时候，为了避免这种情况，可以在动画启动前进行layout或者延迟layout到动画完成。当然，尽量为那些不会触发layout的属性添加动画。比如，View的`translationX`和`tanshlationY`属性影响到post-layout属性。LayoutParams 属性也会需要请求layout操作，因此对这些属性进行动画的时候在相对复杂的UI上会导致卡顿。

*   **Inflation**  
    View 填充也只会发生在UI Thread，也是比较繁重的操作（View的层级越大，工作越繁重）。填充工作会在手动填充一个View 或者启动一个activity的时候发生。这些都是在相同的UI线程进行的，当新的activity被填充的时候将会导致动画暂停。为了避免这种情况，可以在动画完成的时候再启动Activity。或者避免滚动列表时填充不同类型的View导致的卡顿，可以考虑预填充。比如，RecyclerView支持使用RecycledViewPool来装载不同的View类型。

## 加快启动速度

View的填充比较耗资源。不仅要解析资源数据，还要实例化潜在的View以及它们所需要的数据，包括第一次需要的decode bitmap，layout和draw。UI越复杂，填充操作就会越繁重。

上述所有都会降低启动速度。当用户启动一个应用时时，希望等得到一个及时的反馈说明应用已经在运行了。Android通过使用了一个“Starting Window”来弱化这种问题，该window通过应用主题和一些指定背景的图片构成。这样可以很好的让系统进程在后台去进行加载和填充工作。当activity准备好展示的时候，starting window就会过渡到真实的内容紧接着用户就可以使用应用了。

然而，这种starting window应该给用户更多的反馈以表明应用正在进行一些处理，当然这种策略不足以满足那种需要2秒甚至跟多时间去启动的应用，用户会被动的坐在那里一直等到完全加载完毕。

为了启动更快，一些不需要立即展示的UI可以延迟加载。通过使用ViewStub可以搞定。任何时候都尽可能的避免繁重的操作，比如进行大bitmap的decoding，避免由于内存分配和回收产生的内存搅动。可以使用工具监视启动时间去解决瓶颈问题。

避免在Application对象中初始化代码。Application在每一次进程启动的时候会被创建，会导致更多的工作而占用了实际需要展示给用户的UI的初始化时间。比如用户正在浏览一张图片，决定share，选中了你的app，那么你的app需要做的就是展示给用户分享的UI，其它都是多余的。Application的子类更倾向在某些情况下需要做一些耗时的操作，建议你选择使用singletons去持有公共全局的状态，这样就会在它第一次被访问的时候进行初始化。有一点相关的注意事项，不要在Application对象中进行网络有关的操作。That object may be created when one of the app’s Services or BroadcastReceivers is started; hitting the network will turn code that does a local update at a specific frequency into a regular DDoS.

还要注意，应用的不同状态对于启动时间有一个很大的区别。如果应用第一次启动，那么就会做大量的工作：启动进程，初始化所有的状态，必要的填充，布局和绘制。如果应用已经启动了并且在后台存活，重新启动就就很简单。这两种极端的例子会有另外两种情况出现，一：应用在用户退出后还存在，但是任务需要重新创建（通过调用`Activity.onCreaate()`），二：进程被系统干掉了，需要重新启动该进程，但是任务可以在Activity.onCreate()方法中通过保存的bundle恢复状态。你在进行应用启动时间测试的时候，确保优化最糟糕的情况：进程被干掉，需要重新启动。你可以通过从任务列表中移除你的应用来模拟这种情况。

## 避免复杂的View层级

布局越复杂，操作的时间就会越长：填充，布局和渲染（一些潜在的无用内容的内存开销，自定义View中多余数据的引入）。寻找最节省资源的方式去展示嵌套的内容。一种方法就是使用自定义View或者自定义布局，在自定义布局中去避免复杂的嵌套，对于一个单独的View来说绘制一些text和icons，相对于一个嵌套的ViewGroup就更简单。如何在一个交互模块中绑定多个元素呢？如果用户可以通过一个元素就可以完成交互，那么该元素应该是一个独立的View，而不是和其它元素绑定在一起。

## 避免在View层级的顶层使用RelativeLayout

RelativeLayout使用起来很方便，因为可以任意指定View的相对位置。在很多时候，可能是最好的选择，但是相对布局是消耗资源的一种方案，因为它需要两次measurement去确保自己处理了所有的布局关系。而且这个问题会伴随着View层级中的ReativeLayout的增多，而变得跟严重。想象一下，一个顶部是RelativeLayout的布局，本来就进行两次的measurement工作，如果它的第一个child也是RelativeLayout，那么该chilld RelativeLayout下面的布局也要进行两次measurement，整个布局就要进行4次measurement。

在不需要RelativeLayout的一些属性的时候，可以选择使用其它的布局类型。比如LinearLayout或者自定义的布局。确实需要对child进行相对布局的时候，可以考虑更优化的GridLayout，它已经预处理了Child View的关系，可以避免double－measurement的问题。

## 避免在UI Thread 进行繁重的操作

在UI Thread中复杂的操作会导致动画和绘制的延迟，最终会导致明显的卡顿。一些已知的应该避免耗时操作的方法：`onDraw`，`onLayout`，以及任何与View相关的在UI thread调用的相关方法。还有一些其它的操作，比如webservice的调用，网络操作以及数据库的操作。可以考虑使用Loaders或者其它执行在其它线程的工具去操作，完成后再填充到UI上。一个可以追踪卡顿原因的工具是`StrictMode`。

另一个在UI Thread中避免访问文件系统和数据库的原因是：Android设备的存储在处理多个并发的读写操作时支持的不够好。即使你的app处理空闲状态，但是其它的app可能正在执行繁重的I/O操作（Play Store更新apps）也可能会导致你的应用产生ANR或者一些比较大的延迟。

总的来说，所有的事情都应该是异步的，UI Thread应该只操作那些核心的UI 操作，比如处理View的属性和绘制。

## 最小化 Wakeups

BroadcastReceivers可以用于从其它应用接收那些期望响应的信息和事件。但是过多的响应以至于超过了本身所需的话，这些事件就会导致app经常被唤醒，最终导致整个设备的性能和资源的耗费。当你的应用不需要关心这些结果时，考虑关闭BroadcastReceivers，并且慎重选择那些要响应的Intent。

## 为低端手机考虑

大多数用户的手机比开发者手机的配置要低。因此去为这个市场的用户开发就很重要。在关注性能问题的时候，不要以自己的手机水平作为衡量标准，使用不同档次的手机进行测试，确保你的应用可以满足不同水平的设备。

低端手机的一些关注点还包括一些RAM的大小，屏幕的大小，比如512M的RAM或者768*480的屏幕分辨率的配置在低端手机中很常见。

## 性能检测

使用Android提供的一些测试工具去追踪重要的性能相关的信息：渲染性能（是否达到60的帧率？），内存分配（内存分配是否导致垃圾回收最终导致动画的卡顿？），启动性能（在第一启动的时候是否做了太多的工作，导致用户等太久？）找到这些问题，解决它们！

> 译文链接：[http://www.lightskystreet.com/2015/06/07/google-for-android-3-performance/](http://www.lightskystreet.com/2015/06/07/google-for-android-3-performance/)   
> 译文作者：[lightSky](http://www.lightskystreet.com/)  
> 原文链接：[Developing for Android, III:The Rules: Performance](https://medium.com/google-developers/developing-for-android-iii-2efc140167fd)
