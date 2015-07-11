title: Google Developing for Android 系列5
date: 2015-06-11
tags: [翻译,性能优化]
categories: [翻译,连载]
---

## 使用Android自身适当的数据结构

出于对内存分配的考虑，传统的集合类在Android上可能不是最优选。Android特地提供的一些集合类型在很多情况下更合适，比如ArrayMap（好过HashMap），SparseArray。当集合非常大的时候，那些一般的集合仍然是合适的。而较小的集合则受益于自动装箱和内存分配的减少。
<!--more-->
## Serialization

### Parcelable

Parcelable是Andorid的IPC序列化格式，或者说它是一种可以通过Binder传递数据的接口，它有以下限制

*   将Parcels写入到硬盘中是不安全的

*   你可以实现自己的Parcelables，但是如果在unparceling（Parcel反序列化）时不能访问到相同到类，那么就会unparcel就失败（对于向framework传递Parcels也适用）。

*   一些对象被存入到Parcels而不是共享内存中的情况，比如文件描述器，也许是很好的性能优化，但是隐藏了该Parcel对象真实的内存耗费（直到该对象被unparceling反序列化后才会占用真实的内存）。

### 持久化的Bundles

从API21开始，有一个新的PersistableBundle类，该类型的Bundle持有一个数据表支持XML格式数据的序列化。它接收的数据类型只能是Bundle所支持的子类。特别的，它不支持Parcelable对象。

当处理一些需要通过Binder IPC传递的数据时，PersistableBundle类非常有用。

### 避免Java序列化

Serializable，ObjectOutputStream以及相关的类一直是可以用的，但是这些方式总体来说很重，会增加原始序列化的字段。比如，对于一些较长期的硬盘的序列化来说，Serializable类型的序列化导致了一些额外的信息，而这些信息又是不必要的的。下面有些更好的选择：

*   使用Parcelable去处理所有在运行期间的数据交换
*   SharedPreferences也是不错的选择，快速的key－value存储对于少量的数据来说非常合适

*   使用SQLite来处理更复杂，row-oriented的数据。

有一种特殊的情况可能需要java 序列化。如果你的app需要和一些需要特定协议的老的服务器交互。这种情况下应该考虑系统升级以便更高效。

### XML 和 JSON

基于文本的格式通常比较慢也比较冗长，因此它们不适合大量的，复杂的，IPC（使用Parcelable代替），或者需要查询的数据（使用SQLite）。集成基于JSON或者XML的web service是比较好的。也可以使用一些XML去存储少量的较少修改的数据（虽然SharedPreferences更简单）

XML数据在Android资源文件种会被压缩成一种运行期更高效解析的格式。这并不是说XML是运行期间解析XML的特定格式。

## 避免JNI

JNI的问题有很多原因。首先，native代码要求JNI必须为所有平台编译（ARM，ARM64，MIPS等）。不像java代码一样可以跨平台运行。在JNI间的来回调用是相当耗资源的，远远超过native层的一些无关紧要的方法调用。最终还为查找bug带来了困难，因为native层的内存访问是不明显的。

如果确实要使用JNI，可以参考以下建议：

*   使用long类型的指针确保64位的兼容性
*   native方法应该总是是静态的，本地对象的指针作为第一个参数传递
*   java语言的对象应该决定native的生命周期，而不是其它方式
*   注意全局的对象引用可能会导致内存泄漏
*   在调用JNI方法前进行参数检查而不是到迷乱的JNI中
*   最小化JNI的交互次数，每一次JNI调用尽量做更多工作
*   通过值传递native指针而不是来自native的成员查询。这种方式只对非静态对方法有效，
*   考虑使用RenderScript 执一些计算敏感的操作

## 优先选择原始类型

在内存优化中已经提及，但在这里重复一次也是必要的。当有选择的时候，尽量使用原始类型（int，float，boolean）替代对象类型（Integer，Float，Boolean）。在涉及内存（对象实例会开销更多内存）和性能（对象需要更多的时间去访问值）的时候，在Android上，你应该一直优先选择原始类型。

泛型和数据结构需要对象类型，但是注意在Android上有一些优化的集合(ArrayMap, SimpleArrayMap, SparseArray, SparseBooleanArray, SparseIntArray, SparseLongArray, and LongSparseArray)可以满足特定的环境。
> 译文链接：[http://www.lightskystreet.com/2015/06/07/google-for-android-5-language-libraries/](http://www.lightskystreet.com/2015/06/07/google-for-android-5-language-libraries/)   
> 译文作者：[lightSky](http://www.lightskystreet.com/)  
> 原文链接：[Developing for Android V:The Rules: Language and Libraries](https://medium.com/google-developers/developing-for-android-v-f6b8038b42f5)
