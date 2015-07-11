title: Google Developing for Android 系列7
date: 2015-06-11
tags: [翻译,性能优化]
categories: [翻译,连载]
---

## 避免选择应用组件搭建架构

应用组件（activities, services, providers, receivers)是你与操作系统交互的接口但是不要将它们看作是搭建整个应用架构的基础工具。每一个组件在系统中有自己特定语境，通常也只应该有需要的时候才使用：
<!--more-->
*   **Activity** 应用顶级的UI实体。它相当于传统操作系统中的main函数 （当用户点击应用icon的时候运行）。当你希望其它应用启动你应用的特定部分的时候才应该使用Activity来实现它。比如执行一个分享的操作或者打开应用中的一些内容时。如果你只是从自己的应用中去获取一个UI的时候，没有必要通过Activity来实现它，你也可以只转变当前UI的状态（比如通过fragments）。在fragments出现之前一个应用的架构可能通过多个activities来实现是比较常见的，但是现在已经是不必要的了，除非你需要特定的设计。

*   **Service** 一个用于在Activity UI 之外执行一个长时间的操作的服务。它可以自启动（通过Context.startService()）或者运行在另一个进程中（Context.bindService()），如果这些行为你都不需要，那么你就不应该使用Service。

比如你需要后台工作但是不需要自启动（下载UI需要的内容，可以在用户重新回到UI的时候resume），你应该使用本地线程的原始类，比如： AsyncTask, Loader, HandlerThread, 等等。service是资源敏感的（需要作为全局状态的一部分被一直追踪），而且当你的应用不需要service的时候它还在后台运行，可能导致一些bug（这是Android中常见的问题，而且对系统是有害的）。如果你需要将同一进程的代码关联起来，不要使用bindSerice()，使用简单的callbacks和其它工具就可以。因为它们更容易编码也更容易理解，并且资源耗费更少。当然你要理解AsyncTask的异步特性，当Activity finish掉之后结果才返回的情况也是有可能的。在使用结果时请check Activity的状态。

-Broadcast Receiver 关注感兴趣的特定事件，在事件发生的时候会自动唤起 。

## Services要么被绑定要么自启动

Service要么是作为一个被绑定的服务，要么是一个自发起的服务，避免两者同时出现。

如果有必要，一个绑定的service为了继续在后台执行可以自启动，但是当工作完成的时候也要finish掉自己。（额外的start-service产生的冲突可能会导致一些错误）

## 独立的事件优先选择Broadcast而不是Service

使用broadcasts去分发独立的事件，使用service处理那些生命周期较长的事件。

## 避免通过Binder传递大对象

为了通过Binder进行序列化，对象需要被复制，因此对象越大，传递过程的处理时间就越长。

## 将UI处理从后台service中区分开来

当系统资源受限的时候activity manager会将activities干掉。如果你的activity与后台的任务绑定的太近，那么其中一者挂掉，另外一者也同样不可用了。一个好的例子是music 应用将UI和播放内容的service分离开来，UI activity可能被干掉，但不会引起后台service继续音乐的播放。

> 译文链接：[http://www.lightskystreet.com/2015/06/07/google-for-android-7-framework/](http://www.lightskystreet.com/2015/06/07/google-for-android-7-framework/)   
> 译文作者：[lightSky](http://www.lightskystreet.com/)  
> 原文链接：[Developing for Android VII The Rules: Framework](https://medium.com/google-developers/developing-for-android-vii-the-rules-framework-concerns-d0210e52eee3)
