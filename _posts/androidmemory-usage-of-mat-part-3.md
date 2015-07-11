title: Android内存优化之三：打开MAT中的Bitmap原图
date: 2015-06-22
tags: [性能优化]
categories: [连载,性能优化]
---

## 背景

在使用MAT查看应用程序内存使用情况的时候,我们经常会碰到Bitmap对象以及BitmapDrawable$BitmapState对象,如图:

![image](http://androidperformance.com/images/MAT_OpenBitmap/Image_1.png)
<!--more-->
而且在内存使用上,Bitmap所占用的内存占大多数.在这样的情况下, Bitmap所造成的内存泄露尤其严重, 需要及时发现并且及时处理.在这样的需求下, 当我们在MAT中发现和图片相关的内存泄露的时候, 如果能知道是那一张图片,对分析问题会有很大的帮助.

## 导出Bitmap原始数据

在MAT中打开Dominator Tree视图 , 选择一个Bitmap对象 , 查看此时右边的Inspector窗口,内容如下图:

![image](http://androidperformance.com/images/MAT_OpenBitmap/Image_2.png)

这个视图中,可以看到这个Bitmap的一些基本的信息: mBuffer, mHeight, mWidth , mNativeBitmap等, 宽和高的值我们一会需要用的到 .

mBuffer的定义在Bitmap.java中:
```java
/**
 * Backing buffer for the Bitmap.
 * Made public for quick access from drawing methods -- do NOT modify
 * from outside this class
 *
 * @hide
 */
@SuppressWarnings("UnusedDeclaration") // native code only
public byte[] mBuffer;
```

其值是保存在byte数组中的, 我们需要的就是这个byte数组中的内容. 在Inspector窗口的mBuffer这一栏或者Dominator Tree视图的Bitmap这一栏点开下一级,都可以看到这个byte数组的内容. 鼠标右键选择Copy —>Save Value To File. 弹出如下对话框:

![image](http://androidperformance.com/images/MAT_OpenBitmap/Image_3.png)

选择存储路径和文件名,这里需要注意的是,**文件名一定要以 .data为后缀**,否则无法正常使用,切记.

## 打开原始资源数据

### Linux

这时需要借助Linux上强大的图片应用:GIMP,没安装的可以去安装一下. 安装好之后, 打开GIMP,选择文件-打开.选择我们上一步导出的.data文件(比如image.data),然后会出现如下图的属性框:

![image](http://androidperformance.com/images/MAT_OpenBitmap/Image_4.png)

图像类型这一栏选择RGB Alpha, 宽度和高度必填, 其值可以在MAT中查看到,第一步的时候有说到这个值的位置, 其他的选择默认即可,然后点击打开. GIMP就会把这个文件打开.

### Mac && Windows

Mac和Windows可以选择使用PhotoShop作为打开的工具, 和Linux唯一不同的地方在于. 保存的文件的格式需要以.raw结尾 (比如image.raw),选择深度为32位. 其余的和Linux相同.

另外GIMP也有Mac、Windows版本，建议大家在各个平台都使用GIMP，这样学习成本比较低，而且GIMP为免费软件，使用起来功能也非常多。

> 原文链接：[Android内存优化之三：打开MAT中的Bitmap原图](http://androidperformance.com/2015/04/11/AndroidMemory-Open-Bitmap-Object-In-MAT/)  
> 原文作者：[Gracker](http://androidperformance.com/)  
