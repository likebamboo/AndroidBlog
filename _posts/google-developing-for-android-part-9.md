title: Google Developing for Android 系列9
date: 2015-06-15
tags: [翻译,性能优化]
categories: [翻译,连载]
---

本篇文章为Google Developing for Android 系列的最后一篇文章,文章结尾如是说：

> Fin  
> (The End)  
> (Last Chapter. Done. No more. Finished.)
<!--more-->
Andorid提供了很多可以帮助我们debug和分析问题的工具，它们可以让你的app拥有更好的性能。这些检测工具涵盖了内存分析，比如Allocation Tracker(在DDMS和Android Studio中都有)和设备性能。

知道这些工具很重要，但更重要的是要真正的使用这些工具，保证你的应用拥有期望的表现（60帧限制，避免垃圾回收器搅动）。通过尽可能的优化和提高整体体验可以帮助Android更好的整体平台性。

通常用于分析性能的工具有两种类型：host和on-device。Host工具是那些运行在计算机上的工具（命令行，DDMS，Android Studio等）。on-device工具是设备上的一些工具（比如开发者选项下的一些工具），可以展示一些时时的信息，通常涉及到性能的评测。

## Host Tools

### Systrace

[Systrace](http://developer.android.com/tools/help/systrace.html)是一个很强大的工具，可以展示系统中某一时间点各种事件的情况和持续时间。你可以通过运行该工具捕捉一些数据，然后在浏览器打开追踪文件去分析结果。

你可以在Android Studio或者命令行来打开它：  
$SDK_ROOT/platform-tools/systrace/systrace.py.

### AllocationTracker

可以从ddms或者Android Studio打开。允许你追踪在你开启到结束该功能期间的所有内存分配。对于查出在何处或者何时应该避免那些不必要的内存分配是非常有用的。比如，检测某一动画期间的对象分配，先启动动画，点击“Start Allocations”按钮，几帧之后但在动画结束前，再点击“Get Allocations”按钮（你可能需要将动画时长设置的比默认值更高些，以便有足够的时间完成该过程）。动画期间所有的内存分配将会被统计。更多信息参考文章 [Debugging Memory](https://developer.android.com/tools/debugging/debugging-memory.html)

### TraceView

同样可从DDMS或者Android Studio运行。一个方法分析器，可以运行在trace mode（追踪每一次的方法调用）或者sample mode（特定时间间隔内的方法调用）。Trace mode能够很好的帮助追踪间隔期间内应用所执行的完整的代码路径，并标明了不同方法耗费的时间。由于处理这些信息相当的麻烦，因此该View已经提供了方法的调用时间。Sample mode避免了大部分的工作，因为它并没有追踪每一次调用而是指定时间内，但也丢失了代码流程的细节。  
此外,重要的是要注意,sample模式的间隔并不总是均等的(因为运行期间只能在GC的安全点取样),它无法在内联方法中取样,而且取样中的JNI方法比例有可能超过Java方法。

### Hierarchyviewer

可以作为独立的工具也可以从AndroidStudio打开。展示了所选应用的View的整个层级关系，也可以查看每个View的属性值。对于查看View层级是否复杂很有用。是否大多数时间你维持了很多不可见的View。是否使用了独立关系的布局而导致View的层级是否比较深。

### MAT(内存分析工具)

有时你怀疑activity内部可能发生了内存泄漏，而内存的足迹又不太容易追踪。有一些内存泄漏可能是大对象（比如Activities）导致的，可能发生在屏幕方向变化时。MAT可以很好的追踪这些问题。

首先，通过ddms或者Android Studio进行一次heap dump。它保存了对象关系的状态。然后你可以通过命令行执行`hprof-conv`(运行`hprof-conv-z`可以抽取非app的heaps)将这些数据转化成MAT可视化的格式。最终，可以将这些数据倒入到MAT工具中（独立的工具或者Eclipse插件）得到可视化的内存图，从图中可以看到哪些对象常驻在内存中以及为什么常驻内存。

MAT教程视频Google I/O 2011 [Memory Management for Android pps](https://www.youtube.com/watch?v=_CruQY55HOk),也可以参考 [Debugging Memory](https://developer.android.com/tools/debugging/debugging-memory.html) 文章。

### Memory Monitor

Momory Monitor是AndroidStudio的新工具，能够可视化指定进程期间的heap内存使用情况。

### meminfo

运行`adb shell dumpsys meminfo` 可以输出系统总体或者指定进程的不同统计数据。更多信息参考文章 [Debugging Memory](https://developer.android.com/tools/debugging/debugging-memory.html)

## On－device 工具

有几种可以在设备上使用的工具或者模式，它们能够直接在屏幕上展示应用运行期间的系统的总体信息。大多数可以在开发者设置中找到。

### StrictMode

[Strict mode](http://developer.android.com/reference/android/os/StrictMode.html)可以通过“Strict mode enabled”开发者选项开启，当一些操作违背了strict mode时屏幕的边界会闪现红色，比如在UI 线程做了太多的工作。StrictMode的相关信息会输出在log中。

### Profile GPU rendering

通过开发者选项的“Profile GPU redering”开启，展示了每一帧渲染的时间以及4种主要处理过程的渲染时间：DisplayList的创建（处理绘制命令），同步渲染内容到RenderThread（大多涉及Bitmap纹理的加载），DisplayList（将本地渲染处理命令转化为OpenGL命令和参数并传送到GPU），缓冲区交换（包括缓冲区的返回和用于完成整个处理过程的GPU命令的延迟时间）

### Debug GPU overdraw

通过“Debug GPU overdraw”开发者选项开启，在屏幕上展示了所有像素的信息，同一帧内该像素被绘制了多少次，可以给你一些总体的参考：是否在同一区域是否向GPU创建了太多次的渲染操作（通常由于一些View被遮蔽导致）。

很多过度绘制是可以避免的。比如，text的底部不应该绘制背景，其阴影或者底部透明View都会导致过渡绘制。但是大块的不透明区域的过渡绘制应该被分析出来。

### Animator duration scale

通过“Animator duration scale”开发者选项开启，可以减慢或者加度动画的时间。在调试动画的时候很有用，可以观测加速或者减慢情况下的动画是否会有问题。还有两个相似的级别设置，other级别用于控制不同类型的window和系统动画，“animator”级别用于控制应用内部的动画使用了Animator对象。

### 录屏

通过adb shell命令(`adb shell screenrecord /sdcard/myscreenrecord.mp4`)开启，用于记录设备和mpeg vid的帧。Ctrl－C可以停止，然后执行`adb pull`命令可以将该文件导入到电脑上。录屏对于调试动画和其它很在绝对时间哪难分析的交互情况是非常有用的。

### Show hardware layer updates

通过开发者选项“Show hardware layer updates”开启。当layer更新的时候，屏幕上会闪现绿色。对于在动画期间不会变化的View来说，通过Hardware layers可以获取更好的性能。但是如果hardware层的View或者其子层级的View发生变化了，就可能会导致严重的性能问题。使用该工具允许你看到不期望的layer更新。

## 本系列完结！

> 译文链接：[http://www.lightskystreet.com/2015/06/13/google-for-android-9-tools/](http://www.lightskystreet.com/2015/06/13/google-for-android-9-tools/)   
> 译文作者：[lightSky](http://www.lightskystreet.com/)  
> 原文链接：[Developing for Android IX Tools](https://medium.com/google-developers/developing-for-android-ix-tools-375134af1098)  
