title: Google Developing for Android 系列6
date: 2015-06-11
tags: [翻译,性能优化]
categories: [翻译,连载]
---

## 避免文件路径的硬编码

尽量从上context或者Environment中获取

*   不要硬编码全局的路径”/sdcard”，使用Environment.getExternalStorageDirectory() 或者相关的方法替代

*   不要硬编码应用路径： “/data/data/myapp/databases”, 使用 Context.getDatabasePath(), Context.getFilesDir()或者相关的方法替代
<!--more-->
## 只持久化相对路径

当你需要持久化某个路径的时候，为了防止路径的变化，你应该使用相对路径。比如你的应用的备份需要恢复到一个新的设备上，数据路径就可能有些不同。

比如通过Context.getFilesDir() 方法返回的路径在不同的设备，用户或者配置时发生变化。因此在运行期间只通过相对路径构造绝对路径是最安全的。

避免过重的标准化以免出现特定的安全情况。

## 对临时文件使用缓存存储

通过Context.getCacheDir(). 返回的缓存目录将应用的临时文件从其它的持久化数据中分离出来。下面是更高效的存储的建议：

*   缓存目录的文件在内存不足的情况下可能会被系统删除掉，不像在data目录下的文件只有在应用被卸载或者用户明确请求清除应用数据的时候才会被删除。

*   cache目录的文件从来不会被备份，不像data目录下的文件可能会自动备份

## 简单的需求避免使用SQLite

SQLite是一个完全的关系型数据引擎，对于不需要维护关系的简单的数据结构或者key/value 键值对来说是过度的使用。事务的完整性在每次I/O操作中很重要，但是如果你不需要它们，就会让你的应用更慢。

如果你的数据很简单，考虑以下替换：

*   简单的key values 使用SharedPreferences存储。首先它们在第一次读取之后会被静态的缓存。这样加速了访问速度，但是如果你使用过于复杂可能导致泄漏和内存的问题。第二，commit的变化会导致整个SharedPreferences结构的重写，因此小而频繁的更新所做的工作可能会比期望的更多。

*   当需要存储一些时序事件的数据时，使用只会在尾部添加的log文件，并定时处理它

*   如果你需要的只是NoSQL，并且能够正确的，最小化的使用JNI，你可以使用[LevelDB](https://github.com/google/leveldb)

## 避免使用太多的数据库

SQLite数据库在硬盘和内存中都是比较耗资源的。不要为每一个表单独创建一个独立的DB。大多数应用应该只有一个DB。

## 让用户选择内容的存储位置

设备通常有多个存储位置，包括多个SDCard，USB驱动以及云存储。通过<a href="">Storage Access Framework</a>让用户去选择打开或者存储数据的位置.

可以启动简单的intents去提示用户打开或者保存一个文件，接收一个content:// UIR准备数据存储。当需要一个传统样式的文件API时，[DocumentFile](https://developer.android.com/reference/android/support/v4/provider/DocumentFile.html)support library类可以更容易的适应现有的代码。

> 译文链接：[http://www.lightskystreet.com/2015/06/07/google-for-android-6-storage/](http://www.lightskystreet.com/2015/06/07/google-for-android-6-storage/)   
> 译文作者：[lightSky](http://www.lightskystreet.com/)  
> 原文链接：[Developing for Android VI The Rules: Storage](https://medium.com/google-developers/developing-for-android-vi-c0b1539f0e98)
