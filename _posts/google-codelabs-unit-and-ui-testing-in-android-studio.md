title: 在Android Studio中进行单元测试和UI测试
date: 2015-06-25
tags: [测试]
categories: [翻译,单元测试]
---

## 1.概述

在这个codelab中，你将学习如何在Android Studio中配置工程用于测试，在开发机器上编写并运行单元测试，以及如何在手机上做功能UI测试。

**你会学到什么**

*   更新包含JUnit和Android Testing Support Library的Gradle构建文件
*   编写运行在本机Java虚拟机上的单元测试
*   编写运行在手机或者虚拟机上的Espresso测试

**你需要什么**

*   [Android Studio](https://developer.android.com/sdk/installing/studio.html) v1.2+
*   Android 4.0+的测试设备
<!--more-->
* * *

## 2.创建新的Android Studio工程

如果是第一次启动Android Studio，从欢迎页选择“**Start a new Android Studio project**”。如果已经打开了一个工程，选择**File>New>New Project...**

“_Create new project_”向导会指导整个过程，在第一页输入如下内容：

Setting   |    Value
----------|-----------
Application Name | TestingExample
Company demain | testing.example.com

这样会保证你的代码同codelab讲解的内容具有一致的命名。其他的选项都设置为默认，一路点击**Next**直到工程创建完毕。

点击**Run**按钮检查app是否运行正常，要么从模拟器列表中选择一个启动，要么确认开启了debug模式的设备通过USB同电脑正确连接。

app目前没有做任何事情，但是屏幕上应该显示“Hello world!”和app的名字。

![](http://upload-images.jianshu.io/upload_images/580359-839b775d39f912f6.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  

**经常被问及的问题**

*   [如何安装Android Studio？](https://developer.android.com/sdk/index.html)
*   [如何开启USB调试？](http://developer.android.com/tools/device.html)
*   [为什么Android Studio找不到我的设备？](http://stackoverflow.com/questions/16596877/android-studio-doesnt-see-device)
*   [Android错误：无法将*.apk安装到设备上：超时？](http://stackoverflow.com/questions/4775603/android-error-failed-to-install-apk-on-device-timeout/4786299#4786299)

* * *

## 3.配置支持单元测试的工程

在写测试之前，让我们做下简单的检查，确保工程配置正确。

首先，确认在**Build Variants**窗口内的**Test Artifact**中选择了"Unit Tests"。

![](http://upload-images.jianshu.io/upload_images/580359-ab4402443ad7dc5f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  

然后，在工程的`src`文件夹内创建`test`和`test/java`文件夹。需要注意的是，你不能在**Android**视图下进行这些操作，要么在系统的文件管理器内创建，要么在工程窗口左上方点击下拉菜单选择**Project**视图。最终的工程结构应该是这样的：

![](http://upload-images.jianshu.io/upload_images/580359-9e098817f6fcca44.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  

（在codelab的剩余部分，你可以返回继续使用**Android**工程视图）

最后，打开工程的`build.gradle（Module:app）`文件，添加JUnit4依赖，点击**Gradle sync**按钮。

**build.gradle**
```
    dependencies {
        compile fileTree(dir: 'libs', include: ['*.jar'])
        compile 'com.android.support:appcompat-v7:22.1.1'
        testCompile 'junit:junit:4.12'
    }
```
	
> 当你同步Gradle配置时，可能需要联网下载JUnit依赖。

* * *

## 4.创建第一个单元测试

现在，万事俱备，让我们开始写第一个测试吧。首先，创建一个非常简单的被测类：Calculator类。

![](http://upload-images.jianshu.io/upload_images/580359-20cce1345b5076de.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  

然后，向类中添加一些基本的算术运算方法，比如加法和减法。将下列代码复制到编辑器中。不用担心实际的实现，暂时让所有的方法返回0。

**Calculator.java**
```java
    package com.example.testing.testingexample;

    public class Calculator {

        public double sum(double a, double b){
            return 0;
        }

        public double substract(double a, double b){
            return 0;
        }

        public double divide(double a, double b){
            return 0;
        }

        public double multiply(double a, double b){
            return 0;
        }
    }
```

Android Studio提供了一个快速创建测试类的方法。只需在编辑器内右键点击Calculator类的声明，选择**Go to > Test**，然后**"Create a new test…"**

![](http://upload-images.jianshu.io/upload_images/580359-729c021ff61b0dc7.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  

在打开的对话窗口中，选择**JUnit4**和"**setUp/@Before**"，同时为所有的计算器运算生成测试方法。

![](http://upload-images.jianshu.io/upload_images/580359-19f96a03d2fa811a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)   

这样，就会在正确的文件夹内`(app/src/test/java/com/example/testing/testingexample)`生成测试类框架，在框架内填入测试方法即可。下面是一个示例：

**Calculator.java**
```java
    package com.example.testing.testingexample;

    import org.junit.Before;
    import org.junit.Test;

    import static org.junit.Assert.*;

    public class CalculatorTest {

        private Calculator mCalculator;

        @Before
        public void setUp() throws Exception {
            mCalculator = new Calculator();
        }

        @Test
        public void testSum() throws Exception {
            //expected: 6, sum of 1 and 5
            assertEquals(6d, mCalculator.sum(1d, 5d), 0);
        }

        @Test
        public void testSubstract() throws Exception {
            assertEquals(1d, mCalculator.substract(5d, 4d), 0);
        }

        @Test
        public void testDivide() throws Exception {
            assertEquals(4d, mCalculator.divide(20d, 5d), 0);
        }

        @Test
        public void testMultiply() throws Exception {
            assertEquals(10d, mCalculator.multiply(2d, 5d), 0);
        }
    }
```

请将代码复制到编辑器或者使用JUnit框架提供的断言来编写自己的测试。

* * *

## 5.运行单元测试

终于到运行测试的时候了！右键点击`CalculatorTest`类，选择**Run > CalculatorTest**。也可以通过命令行运行测试，在工程目录内输入：
```
    ./gradlew test
```

无论如何运行测试，都应该看到输出显示4个测试都失败了。这是预期的结果，因为我们还没有实现运算操作。
![](http://upload-images.jianshu.io/upload_images/580359-00a07e968baebccc.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  

让我们修改Calculator类中的`sum(double a, double b)`方法返回一个正确的结果，重新运行测试。你应该看到4个测试中的3个失败了。

**Calculator.java**
```java
    public double sum(double a, double b){
        return a + b;
    }
```

作为练习，你可以实现剩余的方法使所有的测试通过。

> 可能你已经注意到了Android Studio从来没有让你连接设备或者启动模拟器来运行测试。那是因为，位于`src/tests`目录下的测试是运行在本地电脑Java虚拟机上的单元测试。编写测试，实现功能使测试通过，然后再添加更多的测试...这种工作方式使快速迭代成为可能，我们称之为**测试驱动开发**。  
> 值得注意的是，当在本地运行测试时，Gradle为你在环境变量中提供了包含Android框架的android.jar包。但是它们功能不完整（所以，打个比方，你不能单纯调用`Activity`的方法并指望它们生效）。推荐使用[Mockito](http://mockito.org/)等mocking框架来mock你需要使用的任何Android方法。对于运行在设备上，并充分利用Android框架的测试，请继续阅读本篇教程的下个部分。

* * *

## 6.配置支持Instrumentation测试的工程

虽然在Android框架内支持运行instrumentation测试，但是目前开发重心主要集中在刚刚发布的作为**Android Testing Support Library**一部分的新的`AndroidJUnitRunner`。测试库包含_Espresso_，用于运行功能UI测试的框架。让我们通过编辑`build.gradle`的相关部分来把它们添加进我们的工程。

**build.gradle**
```
    apply plugin: 'com.android.application'

    android {
        compileSdkVersion 22
        buildToolsVersion "22.0.1"

        defaultConfig {
            applicationId "com.example.testing.testingexample"
            minSdkVersion 15
            targetSdkVersion 22
            versionCode 1
            versionName "1.0"

            //ADD THIS LINE:
            testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
        }

        buildTypes {
            release {
                minifyEnabled false
                proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
            }
        }

        //ADD THESE LINES:
        packagingOptions {
            exclude 'LICENSE.txt'
        }
    }

    dependencies {
        compile fileTree(dir: 'libs', include: ['*.jar'])
        compile 'com.android.support:appcompat-v7:22.0.0' //← MAKE SURE IT’S 22.0.0
        testCompile 'junit:junit:4.12'

        //ADD THESE LINES:
        androidTestCompile 'com.android.support.test:runner:0.2'
        androidTestCompile 'com.android.support.test:rules:0.2'
        androidTestCompile 'com.android.support.test.espresso:espresso-core:2.1'
    }
```

> **重要**：由于一些依赖版本冲突，你需要确认`com.android.support:appcompat-v7`库的版本号是`22.0.0`，像上面的代码片段一样。  
> 另外，Android Studio可能会提醒你`Build Tools 22.0.1`没有安装。你应该接受修复建议，Studio会为你安装Build Tools或者在build.gradle中把这行修改成已经安装在你电脑的版本。

上面的工作完成后，在**Build Variants**窗口内切换成**Android Instrumentation Tests**，你的工程应该自动同步。如果没有，点击**Gradle sync**按钮。

* * *

## 7.为app添加简单的交互
![](http://upload-images.jianshu.io/upload_images/580359-40a6436d81203a3d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  

在使用Espresso进行UI测试前，让我们为app添加一些Views和简单的交互。我们使用一个用户可以输入名字的EditText，欢迎用户的Button和用于输出的TextView。打开`res/layout/activity_main.xml`，粘贴如下代码：  
**activity_main.xml**
```xml
    <RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:tools="http://schemas.android.com/tools" android:layout_width="match_parent"
        android:layout_height="match_parent" android:paddingLeft="@dimen/activity_horizontal_margin"
        android:paddingRight="@dimen/activity_horizontal_margin"
        android:paddingTop="@dimen/activity_vertical_margin"
        android:paddingBottom="@dimen/activity_vertical_margin" tools:context=".MainActivity">

        <TextView
            android:id="@+id/textView"
            android:text="@string/hello_world" android:layout_width="wrap_content"
            android:layout_height="wrap_content" />
        <EditText
            android:hint="Enter your name here"
            android:id="@+id/editText"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_below="@+id/textView"/>
        <Button
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Say hello!"
            android:layout_below="@+id/editText"
            android:onClick="sayHello"/>
    </RelativeLayout>
```

还需要在`MainActivity.java`中添加onClick handler：

**MainActivity.java**
```java
    public void sayHello(View v){
        TextView textView = (TextView) findViewById(R.id.textView);
        EditText editText = (EditText) findViewById(R.id.editText);
        textView.setText("Hello, " + editText.getText().toString() + "!");
    }
```

现在可以运行app并确认一切工作正常。在点击**Run**按钮之前，确认你的_Run Configuration_没有设置为运行测试。如需更改，点击下拉选项，选择**app**。

* * *

## 8.创建并运行Espresso测试

![](http://upload-images.jianshu.io/upload_images/580359-182d42c3cc27596a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  

在工程的整体视图上，找到以（`androidTest`）后缀结尾的包名并创建一个新的Java类。可以将它命名为`MainActivityInstrumentationTest`，将如下代码粘贴过去。

_** MainActivityInstrumentationTest.java_
```java
    package com.example.testing.testingexample;

    import android.support.test.InstrumentationRegistry;
    import android.support.test.espresso.action.ViewActions;
    import android.support.test.rule.ActivityTestRule;
    import android.support.test.runner.AndroidJUnit4;
    import android.test.ActivityInstrumentationTestCase2;
    import android.test.suitebuilder.annotation.LargeTest;

    import org.junit.After;
    import org.junit.Before;
    import org.junit.Rule;
    import org.junit.Test;
    import org.junit.runner.RunWith;

    import static android.support.test.espresso.Espresso.onView;
    import static android.support.test.espresso.action.ViewActions.click;
    import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
    import static android.support.test.espresso.action.ViewActions.typeText;
    import static android.support.test.espresso.assertion.ViewAssertions.matches;
    import static android.support.test.espresso.matcher.ViewMatchers.withId;
    import static android.support.test.espresso.matcher.ViewMatchers.withText;

    @RunWith(AndroidJUnit4.class)
    @LargeTest
    public class MainActivityInstrumentationTest {

        private static final String STRING_TO_BE_TYPED = "Peter";

        @Rule
        public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

        @Test
        public void sayHello(){
            onView(withId(R.id.editText)).perform(typeText(STRING_TO_BE_TYPED), closeSoftKeyboard()); //line 1

            onView(withText("Say hello!")).perform(click()); //line 2

            String expectedText = "Hello, " + STRING_TO_BE_TYPED + "!";
            onView(withId(R.id.textView)).check(matches(withText(expectedText))); //line 3
        }

    }
```

测试类通过**AndroidJUnitRunner**运行，并执行`sayHello()`方法。下面将逐行解释都做了什么：

*   1.首先，找到ID为`editText`的view，输入`Peter`，然后关闭键盘；
*   2.接下来，点击`Say hello!`的View，我们没有在布局的XML中为这个Button设置id，因此，通过搜索它上面的文字来找到它；
*   3.最后，将`TextView`上的文本同预期结果对比，如果一致则测试通过；

你也可以右键点击域名运行测试，选择**Run>MainActivityInstrume...**（第二个带Android图标的）

![](http://upload-images.jianshu.io/upload_images/580359-86da68654bd41cb1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  

这样就会在模拟器或者连接的设备上运行测试，你可以在手机屏幕上看到被执行的动作（比如在`EditText`上打字）。最后会在Android Studio输出通过和失败的测试结果。

[Github下载测试源码](https://github.com/dongdaqing/TestingExample)

* * *

## 9.祝贺

我们希望你能喜欢本篇教程，并且开始着手测试你的应用程序。接着你可以学习如下内容：

*   了解更多关于[单元测试和instrumentation测试的区别](https://developer.android.com/training/testing/unit-testing/index.html)；
*   了解更多关于设置[Android Testing Support Library](https://developer.android.com/tools/testing-support-library/index.html)；
*   观看下面非常棒的有关Android Studio的视频：

    *   [Introduction to Android Studio](https://www.youtube.com/watch?v=K2dodTXARqc)
    *   [Introducing Gradle (Ep 2, Android Studio)](https://www.youtube.com/watch?v=cD7NPxuuXYY)
    *   [Layout Editor (Ep 3, Android Studio)](http://www.youtube.com/watch?v=JLLnhwtDoHw)
    *   [Debugging and testing in Android Studio (Ep 4, Android Studio)](http://www.youtube.com/watch?v=2I6fuD20qlY)
*   在[Github下载Google测试示例代码](https://github.com/googlesamples/android-testing/)

> 译文链接：[在Android Studio中进行单元测试和UI测试](http://www.jianshu.com/p/03118c11c199)
> 本篇教程翻译自[Google I/O 2015](https://events.google.com/io2015/)中关于测试的codelab，掌握科学上网的同学请点击这里阅读：[Unit and UI Testing in Android Studio](https://io2015codelabs.appspot.com/codelabs/android-studio-testing#1)。能力有限，如有翻译错误，请批评指正。如需转载，请注明出处。  
> [Github下载测试源码](https://github.com/dongdaqing/TestingExample)
