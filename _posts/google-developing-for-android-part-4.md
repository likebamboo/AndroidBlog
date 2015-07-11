title: Google Developing for Android 系列4
date: 2015-06-11
tags: [翻译,性能优化]
categories: [翻译,连载]
---

## 不要过度同步

向云端发送数据和获取数据是非常耗电的行为之一。不是网络传输会将设备搞挂，而是一定量的后台应用向服务发起的这些请求会导致设备不能进入睡眠状态（或者对于收音机的低电量模式），一定时间后会导致严重的电量的流失。如果你不需要立即获取数据，就不要获取。如果将来需要获取数据，那么可以使用`JobScheduler`或者`GCM Network Manager`将它们和一些系统的请求一起处理。  
下面是一些避免过度请求的一些建议：
<!--more-->
*   使用 GoogleCloudMessaging（GCM）。而不是使用新的持久化连接。

*   使用JobScheduler（API 21以及之后）或者GCM NetrowManager将一些异步请求绑在一起进行批处理，这些API可以保证操作只在设备恰当的闲暇状态下进行

*   不要轮询，任何情况都不要

*   只同步你需要的数据。数据同步已经被认为是电池和总体系统健康的罪魁祸首之一。因此开发者需要谨慎的选择哪些是真正需要同步的数据，以及多久同步一次,这些都可以让用户有更好的体验。

*   这里有一些参考文章可以查看更多信息:[Minimizing the Effect of Regular Updates](http://developer.android.com/training/efficient-downloads/regular_updates.html),[Transferring Data Using Sync Adapters](http://developer.android.com/training/sync-adapters/index.html),[Optimizing Downloads for Efficient Network Access](http://developer.android.com/training/efficient-downloads/efficient-network-access.html)

<a id="more"></a>

## 避免过度加载服务

当服务请求失败，应该使用备用的技术避免请求一直，重复的请求。另外,不要设置固定时间，不然服务器在设定的时间上会被过多的请求压垮。

## 不要对网络想当然

进行网络调用前，确保使用`NetworkInfo.isConnected()`进行检测。

网络请求不确定性很大，比较耗时，因此另一个建议是不要在UI 线程或者其它需要立即同步的行为中进行网络的请求。

## 考虑低端网络环境

类似于Performance篇里提到的要考虑低端手机的性能一样，网络方面也类似。如果你的应用的内容（Video或者music应用）依赖于一些网络性能，那么你就要考虑那些网络比较差的情况，你要理解一些市场不能够访问到较快的网速。如果你的应用可以从文本内容中获取大量信息（message或者社交app），那么在没有依赖于处理了较慢网络情况的媒体组件下，能够正常访问就很重要。2G网络可以用来测试很多市场上流行的较慢网络。

## 设计适合客户端使用模式的服务端API

为了向所有的客户提供相同的API，向后兼容就是必要的。尽管这种向后兼容的策略是合理的，但是并不是好的主意，在一些小的设备上，完成数据的大量传输或者处理都是不好的，可以考虑将它们放到服务端。

比如，应用需要展示混合内容，请确保客户端可以通过一个单独的请求获取到所需要的信息并且返回的数据适合直接缓存。你通常希望客户端能够识直接别实体以便持久化，也希望避免重复的对象出现在内存中（开发者处理过程产生的对象内存开销）。

然而，很多API返回的数据结构是非标准化的。这种数据结构可能对于服务端处理更合适，但是对于移动端的数据持久化和从硬盘的读取都不够好。

客户端从返回的信息中能够直接用于展示的信息越多，就越高效。应用要很仔细的去筛选那些需要缓存的，剔除一些无用的，以及新数据到来时如何刷新视图。如果对待客户端像一个简单的HTML渲染机制一样就会丢掉客户端自身的很多优势。

> 译文链接：[http://www.lightskystreet.com/2015/06/07/google-for-android-4-network/](http://www.lightskystreet.com/2015/06/07/google-for-android-4-network/)   
> 译文作者：[lightSky](http://www.lightskystreet.com/)  
> 原文链接：[Developing for Android, IV:The Rules: Networking](https://medium.com/google-developers/developing-for-android-iv-e7dc4ce0a59)
