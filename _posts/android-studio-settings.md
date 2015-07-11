title: Android Studio-1.2版本设置教程
date: 2015-05-10
tags: [Android Studio]
categories: [AndroidIDE]
---

> 转载请注明原文出处 [http://licheetec.com/2015/05/02/android-studio-settings/](http://licheetec.com/2015/05/02/android-studio-settings/)

这两天Google更新了Android Studio 1.2正式版，新版本的设置界面大变面，设置条目较旧版本进行了归类，不像以前那样列表长长的了。

趁着安装新版本的机会，把常用的设置记录一下，放到博客里面，以作备忘。
<!--more-->
# Android Studio / Gradle学习资源

在说设置之前，先上点学习Android Studio和Gradle的学习资源。

## 官方教程

*   developer.android.com上的**Android Studio Overview**  
    [http://developer.android.com/tools/studio/index.html](http://developer.android.com/tools/studio/index.html)  

*   developer.android.com上的**Build System Overview**  
    [http://developer.android.com/sdk/installing/studio-build.html](http://developer.android.com/sdk/installing/studio-build.html)  

*   tools.android.com上的**Gradle Plugin User Guide***（Google出的Gradle指南）  
    [http://tools.android.com/tech-docs/new-build-system/user-guide](http://tools.android.com/tech-docs/new-build-system/user-guide)  

*   IntelliJ IDEA **Quick Start**（IDEA入门教程）  
    [https://www.jetbrains.com/idea/help/intellij-idea-quick-start-guide.html](https://www.jetbrains.com/idea/help/intellij-idea-quick-start-guide.html)  

*   IntelliJ IDEA的快捷键大全  
    [https://www.jetbrains.com/idea/docs/IntelliJIDEA_ReferenceCard_Mac.pdf](https://www.jetbrains.com/idea/docs/IntelliJIDEA_ReferenceCard_Mac.pdf)

## 非官方资源

*   **Developer Phil博客**的**Android Studio Tips系列**（里面提供快捷键效果的动态图，**强烈推荐**）  
    [http://www.developerphil.com/android-studio-tips-tricks-moving-around/#recent-posts-4](http://www.developerphil.com/android-studio-tips-tricks-moving-around/#recent-posts-4)  

*   Android-Studio-Tips-by-Philippe-Breault（貌似就是上面那个，还没细看，暂时不确定是不是一样，还是发出来好了）  
    [https://github.com/pavlospt/Android-Studio-Tips-by-Philippe-Breault/wiki](https://github.com/pavlospt/Android-Studio-Tips-by-Philippe-Breault/wiki)  

*   **stormzhang**大神的**Android Studio系列教程**（推荐）  
    [http://stormzhang.com/posts.html#AndroidStudio](http://stormzhang.com/posts.html#AndroidStudio)  

*   **Rinvay Tang**的博客  
    （《Gradle Plugin User Guide》翻译版，《使用Gradle构建Android程序》）（推荐）  
    [http://rinvay.github.io/archive.html](http://rinvay.github.io/archive.html)  

*   **Gradle Android插件用户指南翻译**（《Gradle Plugin User Guide》另一个翻译版）  
    [http://avatarqing.github.io/Gradle-Plugin-User-Guide-Chinese-Verision/](http://avatarqing.github.io/Gradle-Plugin-User-Guide-Chinese-Verision/)  

*   Github上面搜“Android Studio”（很多好东西都在里面）  
    [https://github.com/search?utf8=%E2%9C%93&q=Android+Studio](https://github.com/search?utf8=%E2%9C%93&q=Android+Studio)

# 环境搭建相关

## 下载

正式版的Android Studio和SDK可以在下面的链接进行下载  
[http://developer.android.com/sdk/index.html#Other](http://developer.android.com/sdk/index.html#Other)  
关于下载，我建议下载绿色版的Android Studio和SDK，别下安装包版捆绑版的，这样如果有哪个出了问题要重装，只要动一个就行了。

![下载](http://i4.tietuku.com/d1cdaa3469459a3f.jpg)  
其他版本可以在这里找到 [http://tools.android.com/download/studio/canary](http://tools.android.com/download/studio/canary)

## 禁用Windows中`Ctrl+Space`切换输入法的快捷键

Windows的Ctrl+Space快捷键（切换中文输入法）跟一堆IDE的冲突，所以必须禁用掉，按照下图那样修改注册表即可。  
或者下载我改好的注册表文件（[右键另存为](https://github.com/licheetec/filestore/raw/master/others/disable_ctrl_space.zip)），双击导入压缩包里面的文件，重启系统即可。

![禁用Windows+Space快捷键](http://i4.tietuku.com/4824ab2a935b548c.jpg)  
原理参考[这里](http://answers.microsoft.com/en-us/windows/forum/windows_vista-desktop/how-do-i-disable-the-changing-of-languages-when-i/f01de525-73b2-4c4e-969e-b5aa001c0eb7)。

## 环境变量

很多IDE依赖环境变量，正确设置好环境变量，可以确保软件能正常运行，和避免很多莫名奇妙的问题。

> 设置环境变量的方法请自行搜索。下面的环境变量值都是我自己电脑的，请按照`实际情况`进行修改。  
> 还有`PATH`的要特别注意，要加到原来的后面，别一脑门全部覆盖掉。

### JAVA

    JAVA_HOME=E:\DevTools\Others\Java\jdk1.7.0_67
    CLASSPATH=.;%JAVA_HOME%\lib;%JAVA_HOME%\lib\tools.jar
    # 注意，%PATH%为原来的环境变量值，添加";"和后面的内容到原来值的后面
    PATH=%PATH%;%JAVA_HOME%\bin;%JAVA_HOME%\jre\bin

### Android SDK

    ANDROID_HOME=E:\DevTools\Android\sdk
    PATH=%PATH%;%ANDROID_HOME%\tools;%ANDROID_HOME%\platform-tools
    # ADB端口，可以避免国内一堆软件，如酷狗音乐啥的把ADB端口抢占了，值随便改个不常用的就行
    ANDROID_ADB_SERVER_PORT=7123

### Gradle

    # Android Studio正式版后就内置了一个Gradle，当然你也可以另外去Gradle官网下载一个
    GRADLE_HOME=E:\DevTools\Android\android-studio\gradle\gradle-2.2.1\bin
    PATH=%PATH%;%GRADLE_HOME%\bin
    # 依赖仓库存放路径，平时构建工程时下载的依赖库都放在这里
    # 不配置的话，Windows中默认是在C:\Users\<username>\.gradle的
    # 重装系统时忘记备份，又不想重新下载依赖的话，就赶紧把这里改了
    GRADLE_USER_HOME=E:\DevWorks\.gradle

## 运行Android Studio前的配置

### 配置SDK Manager科学上网，升级SDK

最近红杏出了公益代理，简直是开发人员的福音，详情看：[http://blog.honx.in/dev-only/](http://blog.honx.in/dev-only/)  
按照红杏提供地址和端口（目前是`hx.gy:1080`）进行修改（别加`http://`），必要时可以按一下“Clear Cache”。

> 有条件的话，可以试试买个付费的shadowsocks，我现在shadowsocks.com的99包年套餐。  
> 电信8M，部分线路能达到900多KB/s的速度，几乎满速了，感觉还行。  
> 具体怎么购买和配置，不在本文介绍范围内，请自行找科普。  
> 如果要买的话，请务必用 [`我的推广链接`](https://portal.shadowsocks.com/aff.php?aff=470) ，我有提成的。（笑）

![SDK Manager配置](http://i1.tietuku.com/655456e2121590d9.jpg)

关于下载，`Tools`中`Android SDK Build-tools`建议`全部下载`，  
其他各个API版本，建议至少下载`SDK Platform`（必须，framework层的东东全在这里）和`Sources for Android SDK`（源码），  
`Extras`中**_必须_**下载`Android Support Repository`和`Android Support Library`（Support库的东东，现在开发离不开Support库了），  
上面提到的是开发必须用到的，其他东西就看情况了，你有时间又不在乎资源占用的话，全下载都行。  
顺便附上我自己下载的，[猛戳此连接查看](http://i1.tietuku.com/53c9ca6eee9d8ab6.jpg)。

### 修改idea.properties文件

找到`<android-studio>\bin\idea.properties`文件，打开，改成这样：

![idea.properties](http://i4.tietuku.com/6e2599351ee2b032.jpg)

    # 禁止第一次运行Android Studio时，自动检查和升级Android SDK
    disable.android.first.run=true

    # 下面两个是Android Studio的设置、插件和运行时产生的其他文件存放的目录
    # 不改的话，Windows中默认在 C:\Users\<username>\.AndroidStudio.2\ 里面
    # ${idea.home.path}表示Android Studio程序的主目录，注意目录分隔符要用正斜杠“/”
    idea.config.path=${idea.home.path}/.AndroidStudio.2/config
    idea.system.path=${idea.home.path}/.AndroidStudio.2/system

> 非常坑爹的是，每次Android Studio升级时，都会强制检测AS主目录里面的文件或文件夹是否被动过，  
> 当然也包含这个idea.properties，  
> 有些增强模板或插件，例如这个 [AndroidStudioTemplate](https://github.com/gabrielemariotti/AndroidStudioTemplate)，安装时要求覆盖`<android-studio>\plugins\android\lib\templates`。  
> 如果发现被改了，就会要求进行<del>处理</del>（恢复默认）操作，举例来说，idea.properties和那个templates会被还原为解压时的模样，各种修改都会失效。  
> 所以保险起见，这个`idea.properties`文件改完就备份一下，以后升级完AS，就手动改回去，各种插件/模板也存一个备份，别装完就删掉，免得被AS的升级程序删了后找不回来。

# 设置Android Studio

正题来了，运行后，进入欢迎界面，别急着建工程，先`Configure`→`Settings`去修改设置。

## IDE外观&行为

### 修改主题，修改全局字体

![主题&字体](http://i4.tietuku.com/a7467ac2bd7067bf.jpg)

1.  修改主题，想用炫酷的深色主题，就改成`Darcula`吧；  
    字体的话，选一个带中文的，要不然会有很多`口口`，我这里用`Microsoft YaHei UI`，很不错。  

2.  启用/禁用动画特效，禁用掉感觉可能会快点。

### 禁止自动打开上次的工程

我喜欢自己选打开哪个工程，果断禁用

![禁止自动打开最后打开的工程](http://i4.tietuku.com/ea4fb529175d61fc.jpg)

### 设置网络代理

跟上文一样，改为红杏公益代理吧

![代理](http://i4.tietuku.com/699907660e9f6148.jpg)

### 禁用自动检查更新

洁癖的选择，升级控请无视

![禁用更新](http://i4.tietuku.com/44821b256312b44c.jpg)

### 配置快捷键

不管你改不改，反正我没改，默认的好，免得以后查资料，操作对不上

![快捷键](http://i4.tietuku.com/a1e17305d06190a3.jpg)

## 编辑器

### 鼠标悬停显示文档，格式化&导包提示

![鼠标悬停显示文档，格式化&导包提示](http://i4.tietuku.com/9fd64f9122214d96.jpg)

1.  鼠标指针悬停若干时间，显示文档，时间自己改。  

2.  就是按格式化代码或者导包时，是否会显示个对话框，觉得烦人的话，都取消掉吧，反正我取消了。

### 显示行号，显示方法分隔线

勾上吧，你值得拥有的

![显示行号，显示方法分隔线](http://i4.tietuku.com/7022f5b4a39a5dbe.jpg)

### 代码折叠

Intellij IDEA有很多地方的代码都能自动折叠，不过我看不惯，所以取消了，这里见仁见智，看着办  
P.S. `Ctrl + .` 可以折叠和展开代码

![代码折叠](http://i4.tietuku.com/e156cf6e6bb500d3.jpg)

### 代码智能提示

![代码智能提示](http://i4.tietuku.com/afcfe37b9d6ebba5.jpg)

1.  敲什么字符会提示，All(大小写全部符合)，None（不管大小写，符合就提示），（First letter）（第一个字符符合就OK，其他随意）。我这种脑残没记性的当然选择None。  

2.  自动弹出文档，时间看着办。  

3.  自动弹出方法参数提示，时间看着办。

### 自动导包

![自动导包](http://i4.tietuku.com/a1bb4e23dde8936a.jpg)

*   Optimize imports on the fly：优化导包，格式化代码时会删掉多余的导包。  

*   Add unambiguous imports on the fly：敲代码时，敲简单类名就帮你把包导了。

### 创建个人代码样式配置

估计是为了保护默认的代码样式配置，让用户把配置搞坏了也能一键还原，IDEA不允许修改默认的配置，需要用户创建配置才能进行修改。  
选择基于哪个主题的，然后`Save As`一份即可。

![创建个人代码样式配置](http://i4.tietuku.com/2b866c9c4021e70a.jpg)

### 修改代码字体

强烈建议用`Consolas`字体，好看！！！

![修改代码字体](http://i4.tietuku.com/7e949854b0c76c50.jpg)

### 修改控制台字体

好吧，我就喜欢这个字体不行么？

![修改控制台字体](http://i4.tietuku.com/4188c779b2651289.jpg)

### Logcat字体

要改的话，得先把1那个地方的勾取消掉

![Logcat字体](http://i4.tietuku.com/a1d8f83d58306671.jpg)

### 修改注释位置，禁用“语句堆一行”

![修改注释位置，禁用“语句堆一行”](http://i4.tietuku.com/bfdcc07101de949c.jpg)

*   Comment at frist column：启用的话，注释符号就会在行首，否则就按照缩进来注释。我取消掉了。  

*   Control statement in one line：格式化代码的时候，会把些很短的语句合并成一行。我觉得这样影响代码可读性，故取消。

### 修改变量前后缀

静态成员是s，普通成员是m，有点意思

![变量前缀](http://i4.tietuku.com/f623911b2a24b3ae.jpg)

### 修改新建文件文件头

每次建新类的话，对下面这段注释肯定很熟悉。
```
    /**
     * Created by licheetec on 2015/5/2.
     */
```
![通用文件头](http://i4.tietuku.com/11df21d9d347afcd.jpg)

上图就是通用的文件头，框住的地方是你系统的用户名，想个性化的话，可以改这里，至于哪里引用这个文件头的呢，就在隔壁。

![引用文件头](http://i4.tietuku.com/17155e2a606bf083.jpg)

### 修改文件编码为UTF-8

别坑队友，小伙伴们都统一改为UTF-8吧。

![修改文件编码为UTF-8](http://i4.tietuku.com/5c6399212e1bea83.jpg)

## 体验增强

### 增强Live Templates（`Ctrl+J`的智能提示模板）

默认的Live Templates不够用，连foreach都没，幸好Github上的大神做了增强的模板，果断拿来用。

> idea-live-templates： [https://github.com/keyboardsurfer/idea-live-templates](https://github.com/keyboardsurfer/idea-live-templates)

文件下载后，找到下面这个目录：  
`<android-studio>\.AndroidStudio.2\config\templates`，  
至于为什么是这个目录，麻烦往前补补 [**修改idea.properties文件**](#idea-properties) ,

> 如果没有`templates`文件夹的话，自己手动建一个即可。

![只要xml文件](http://i4.tietuku.com/8ed6fb052db5d55e.jpg)

把所有`*.xml`文件弄进那个文件夹，重新打开Android Studio，就能看到效果了。

![修改foreach](http://i4.tietuku.com/3bfbf68635f3a38d.jpg)

`foreach`那里要改一个设置，否则`for ($i$ : $data$) {`会被自动格式化为多行。  
下面是`foreach`和`fbc`的动图演示

![foreach&fbc](http://i4.tietuku.com/6d3e735486ae8a9d.gif)

# おわり

![(:з」∠)](http://i4.tietuku.com/46bec46a702f7b52.gif)

搞定，Android Studio基本设置教程暂时这样了，以后有新东西再更新，其他编译系统相关的暂时不是摸得很明白，就不乱来误人子弟了。  
可累了。
