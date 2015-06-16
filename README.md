#Android博客客户端
---
这是一个简单的android博客阅读应用，遵循 Android 设计风格，UI借鉴我个人非常喜欢的一款应用[Etsy](https://www.etsy.com/mobile/?ref=ftr).

##构建与支持
项目使用 Android Studio (v1.2) + Gradle (v2.2.1) 构建  
该项目支持 Android 2.3+ ( api level 9)

## 效果图
<img src="https://raw.github.com/likebamboo/AndroidBlog/master/art/home.png" width=360 height=640 alt="image1"> <img src="https://raw.github.com/likebamboo/AndroidBlog/master/art/author.png" width=360 height=640 alt="image2">
<img src="https://raw.github.com/likebamboo/AndroidBlog/master/art/blog.png" width=360 height=640 alt="image3"><img src="https://raw.github.com/likebamboo/AndroidBlog/master/art/info.png" width=360 height=640 alt="image4">
<img src="https://raw.github.com/likebamboo/AndroidBlog/master/art/menu.png" width=720 height=405 alt="image5">

##使用到的开源项目
[AndroidStaggeredGrid](https://github.com/etsy/AndroidStaggeredGrid) Esty 开源的瀑布流布局库  
[Sugar](https://github.com/satyan/sugar) 一个简单的ORM框架  
[Volley](https://android.googlesource.com/platform/frameworks/volley) Google官方发布的网络请求库  
[ButterKnife](http://jakewharton.github.io/butterknife/)  一个专注于Android系统的View注入框架  
[NineOldAndroid](http://nineoldandroids.com/) 动画支持库  
[Jackson](https://github.com/FasterXML/jackson)  json解析库  
...

## 意见与建议
欢迎通过Pull Requests 方式向本项目贡献代码  
如果发现程序的任何BUG，或对本项目有任何意见，可以直接创建 [Issue](https://github.com/likebamboo/AndroidBlog/issues) 告知我。

## TODO
- [x] 博客收藏(本地)(完成时间2015-06-13).
- [x] 博客排序(完成时间2015-06-16).
- [ ] 下拉刷新.
- [ ] 博客错误反馈.
- [ ] 博客图片大图浏览与保存.
- [ ] 搜索优化.
- [ ] 博客推荐优化.


## 关于
本项目的前端和后端均由我一人开发，后端代码暂不开源，主要是因为本人不太会写后端，后端代码太烂了(其实前端也不怎么样)。  
本项目的Apk安装包暂时不会发布到任何应用市场，请直接在[release](https://github.com/likebamboo/AndroidBlog/releases)下载。
