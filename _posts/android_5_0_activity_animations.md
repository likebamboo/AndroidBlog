title: Android 5.0 Activity切换动画
date: 2015-06-08
tags: [翻译,特效]
categories: [翻译]
---

![](http://images0.cnblogs.com/blog2015/354225/201506/071858146918959.gif)  
<!--more-->
在Androiod5.0中,Google定义了Material Design的规范.而动画切换,能给用户直观的连贯性的体验,也是Google推崇的.  
为此,在Android5.0中,Android新支持了不少炫酷的动画效果.这里是Android官方对于Material Design的动画的介绍文档.  
下面要介绍的其中一种Activity的切换效果(如上图).当前后两个Activity有共同的UI元素时候,适合用这种动画效果,给用户连贯性的体验.  

###实现步骤

####1. 新建一个Android应用Project,里面有两个Activity;
####2. 下面是MainActivity的代码和XML布局;
代码和布局都非常简单.解释一下`onClick()`方法的代码.  
`makeSceneTransactionAnimation()`方法第二个参数是用来告诉指定共同的UI元素是哪一个,这里是shareView.  
`makeSceneTransactionAnimation()`方法第三个参数是`"robot"`,和XML布局里面的`android:transactionName="robot"`的值是一一对应的.  

```java
package com.example.garena.myapplication;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final View shareView = findViewById(R.id.share_element_image_view);
        shareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(MainActivity.this, shareView, "robot");
                startActivity(intent, options.toBundle());
            }
        });
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.first_activity);
        }
    }
}
```

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:paddingBottom="@dimen/activity_vertical_margin"
    android:paddingLeft="@dimen/activity_horizontal_margin"
    android:paddingRight="@dimen/activity_horizontal_margin"
    android:paddingTop="@dimen/activity_vertical_margin"
    tools:context=".MainActivity">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/hello_world" />

    <ImageView
        android:id="@+id/share_element_image_view"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@drawable/ic_launcher"
        android:transitionName="robot" />
</LinearLayout>
```

####3.下面是SecondActivity的代码和布局;
在onClick()方法里面,调用finishAfterTransaction()来finish activity.  
在XML布局里面,同样是需要用android:transactionName="roboto"来标识共同的UI元素.  

```java
package com.example.garena.myapplication;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class SecondActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);
        ActionBar actionBar = getActionBar();
        if (actionBar !=  null) {
            actionBar.setTitle(R.string.second_activity);
        }
        View btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAfterTransition();
            }
        });
    }
}
```
 
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:gravity="center"
    android:orientation="vertical">

    <ImageView
        android:id="@+id/title_icon_image_view"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@drawable/ic_launcher"
        android:transitionName="robot" />

    <Button
        android:id="@+id/btn_back"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/back" />
</LinearLayout>
```

####4. 运行效果(如上图);
从最终运行的效果图可以看到,点击小机器人的图标进入SecondActivity时,能看到小机器人图标移动到SecondActivity这个小机器人图标的位置.  
点击SecondActivity的Back按钮,小机器人图标会移动到MainActivity中小机器人图标的位置.  

> 原文链接 :[http://www.cnblogs.com/wingyip/p/4558923.html](http://www.cnblogs.com/wingyip/p/4558923.html)
> 作者 : [wingyip](http://www.cnblogs.com/wingyip/)