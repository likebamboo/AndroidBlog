title: 使用Android studio分析内存泄露 
date: 2015-06-28
tags: [内存泄露]
categories: [其他]
---

Android使用java作为平台开发，帮助了我们解决了很多底层问题，比如内存管理，平台依赖等等。然而，我们也经常遇到`OutOfMemoey`问题，垃圾回收到底去哪了？
<!--more-->
接下来是一个`Handler Leak`的例子，它一般会在编译器中被警告提示。

### 所需要的工具

*   Android Studio 1.1 or higher
*   Eclipse MemoryAnalyzer

### 示例代码

```java
public class NonStaticNestedClassLeakActivity extends ActionBarActivity {

  TextView textView;

  public static final String TAG = NonStaticNestedClassLeakActivity.class.getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_non_static_nested_class_leak);
    textView = (TextView)findViewById(R.id.textview);
    Handler handler = new Handler();

    handler.postDelayed(new Runnable() {
      @Override public void 
        textView.setText("Done");
      }//a mock for long time work
    }, 800000L);

  }
}
```

这是一个非常基础的Activity.注意这个匿名的`Runnable`被送到了Handler中，而且延迟非常的长。现在我们运行这个Activity,反复旋转屏幕，然后导出内存并分析。

### 导入 Memory 到Eclipse MemoryAnalyzer

#### 使用Androidstudio导出 heap dump

![Android Studio dump Memory Analyze](http://upload-images.jianshu.io/upload_images/98641-56b9fde4965b0a22.png?imageView2/2/w/1240/q/100)  

*   点击左下角的Android
*   选中你的程序的包名
*   点击 initiates garbage collection on selected vm
*   点击 dump java heap for selected client

#### 打开MAT，进行分析

MAT是对java heap中变量分析的一个工具，它可以用于分析内存泄露。

*   点击`OQL`图标
*   在窗口输入`select * from instanceof android.app.Activity`并按`Ctrl + F5`或者`!`按钮
*   奇迹出现了，现在你发现泄露了许多的activity
*   这个真是相当的不容乐观，我们来分析一下为什么GC没有回收它

![EMA](http://upload-images.jianshu.io/upload_images/98641-5bdf6f3320b3be57.png?imageView2/2/w/1240/q/100)  


> 在OQL（Object Query Language）窗口下输入的查询命令可以获得所有在内存中的Activities，这段查询代码是不是非常简单高效呢？

点击一个activity对象，右键选中`Path to GC roots`

![GC root](http://upload-images.jianshu.io/upload_images/98641-15b85219d213fc76.png?imageView2/2/w/1240/q/100)  

![Message in looper hold a reference to Activity](http://upload-images.jianshu.io/upload_images/98641-f061475dc9b7aad6.png?imageView2/2/w/1240/q/100)  


在打开的新窗口中，你可以发现，你的Activity是被`this$0`所引用的，它实际上是匿名类对当前类的引用。`this$0`又被`callback`所引用，接着它又被`Message`中一串的`next`所引用，最后到主线程才结束。

> 任何情况下你在class中创建非静态内部类，内部类会（自动）拥有对当前类的一个强引用。

一旦你把`Runnable`或者`Message`发送到`Handler`中，它就会被放入`LooperThread`的消息队列，并且被保持引用，直到`Message`被处理。发送postDelayed这样的消息，你输入延迟多少秒，它就会泄露至少多少秒。而发送没有延迟的消息的话，当队列中的消息过多时，也会照成一个临时的泄露。

#### 尝试使用static inner class来解决

现在把`Runnable`变成静态的class

![StaticClass](http://upload-images.jianshu.io/upload_images/98641-3ade66cfdcbe579f.png?imageView2/2/w/1240/q/100)  

现在，摇一摇手机，导出内存

![StaticClass_memory_analyze](http://upload-images.jianshu.io/upload_images/98641-64aca564b14181a2.png?imageView2/2/w/1240/q/100)  


为什么又出现了泄露呢？我们看一看`Activities`的引用.

![StaticClass_memory_analyze_explained](http://upload-images.jianshu.io/upload_images/98641-75627c00a4afe269.png?imageView2/2/w/1240/q/100)  


看到下面的`mContext`的引用了吗，它被`mTextView`引用，这样说明，使用静态内部类还远远不够，我们仍然需要修改。

#### 使用弱引用 + static Runnable

现在我们把刚刚内存泄露的罪魁祸首 - TextView改成弱引用。

![StaticClassWithWeakRef_code](http://upload-images.jianshu.io/upload_images/98641-123083d4deb8bc3f.png?imageView2/2/w/1240/q/100)  


再次注意我们对TextView保持的是**弱引用**，现在让它运行，摇晃手机

> 小心地操作WeakReferences，它们随时可以为空，在使用前要判断是否为空.

![StaticClassWithWeakRef_memory_analyze](http://upload-images.jianshu.io/upload_images/98641-8ba97b98f57bef50.png?imageView2/2/w/1240/q/100)  

哇！现在只有一个Activity的实例了，这回终于解决了我们的问题。

所以，我们应该记住：

*   使用静态内部类
*   Handler/Runnable的依赖要使用弱引用。

如果你把现在的代码与开始的代码相比，你会发现它们大不相同，开始的代码易懂简介，你甚至可以脑补出运行结果。

而现在的代码更加复杂，有很多的模板代码，当把`postDelayed`设置为一个短时间，比如`50ms`的情况下，写这么多代码就有点亏了。其实，还有一个更简单的方法。

### onDestroy中手动控制声明周期

Handler可以使用`removeCallbacksAndMessages(null)`，它将移除这个Handler所拥有的`Runnable`与`Message`。

    //Fixed by manually control lifecycle
      @Override protected void onDestroy() {
        super.onDestroy();
        myHandler.removeCallbacksAndMessages(null);
      }

现在运行，旋转手机，导出内存

![removeCallbacks_memory_analyze](http://upload-images.jianshu.io/upload_images/98641-1ae8a9f10acc9d1f.png?imageView2/2/w/1240/q/100)  

Good！只有一个实例。

这样写可以让你的代码更加简洁与可读。唯一要记住的就是就是要记得在生命周期onDestory的时候手动移除所有的消息。

### 使用WeakHander

（这个是第三方库，我就不翻译了，大家去[Github](https://github.com/badoo/android-weak-handler)上去学习吧）

### 结论

在Handler中使用`postDelayed`需要额外的注意，为了解决问题，我们有三种方法

*   使用静态内部Handler/Runnable + 弱引用
*   在onDestory的时候，手动清除Message
*   使用Badoo开发的第三方的 [WeakHandler](https://github.com/badoo/android-weak-handler)

这三种你可以任意选用，第二种看起来更加合理，但是需要额外的工作。第三种方法是我最喜欢的，当然你也要注意WeakHandler不能与外部的强引用共同使用。

### 最后

本博客将长期保持原创性，翻译文章费时费力，如果你认为我的**免费劳动**有价值的话，不妨帮忙`点赞`或者`关注我`吧！

> This post is a permitted translation of [badoo Tech Blog](https://techblog.badoo.com/blog/2014/08/28/android-handler-memory-leaks/) and I add some text and screenshots for android studio users.  
> **Origin Author**: [Dmytro Voronkevych](https://techblog.badoo.com/authors/dmytro-voronkevych/)  
> **follow badoo on [Tweet](https://twitter.com/badootech)**   
> **Translator**: Miao1007
