title: RecyclerView的拖动和滑动一 ：基本的ItemTouchHelper示例
date: 2015-06-30
tags: [翻译,SupportLibrary]
categories: [翻译]
---

英文原文：[Drag and Swipe with RecyclerView](https://medium.com/@ipaulpro/drag-and-swipe-with-recyclerview-b9456d2b1aaf) 

![1_4Cqfs-75ibhvC_3BMEIvVA.ogv_1435641250.gif](http://www.jcodecraeer.com/uploads/20150630/1435641365165672.gif "1435641365165672.gif")

在安卓中，有许多关于如何使用RecyclerView实现“drag & drop”与swipe-to-dismiss”的教程，库和例子。即使现在已经有了新的，更优的实现方式，大多数仍然是使用老旧的[View.OnDragListener](http://developer.android.com/guide/topics/ui/drag-drop.html)以及Roman Nurik在[SwipeToDismiss](https://github.com/romannurik/Android-SwipeToDismiss)中所使用的方法。很少有人使用新的api，反而要么经常依赖于GestureDetectors和onInterceptTouchEvent，要么实现方式很复杂。实际上，在RecyclerView上添加拖动特性有一个非常简单的方法。这个方法只需要一个类，并且它也是Android 兼容包的一部分，它就是：  
<!--more-->
## [ItemTouchHelper](https://developer.android.com/reference/android/support/v7/widget/helper/ItemTouchHelper.html)

ItemTouchHelper是一个强大的工具，它处理好了关于在RecyclerView上添加拖动排序与滑动删除的所有事情。它是[RecyclerView.ItemDecoration](https://developer.android.com/reference/android/support/v7/widget/RecyclerView.ItemDecoration.html)的子类，也就是说它可以轻易的添加到几乎所有的LayoutManager和Adapter中。它还可以和现有的item动画一起工作，提供受类型限制的拖放动画等等，

这篇文章中，我会演示一个ItemTouchHelper的简单实现，然后在这个系列文章的后面部分，我们将拓展范围，探索一些新的特性。

### 跳过本文

只对完整源码感兴趣？直接跳到github：[ Android-ItemTouchHelper-Demo](https://github.com/iPaulPro/Android-ItemTouchHelper-Demo)。[第一次提交的代码](https://github.com/iPaulPro/Android-ItemTouchHelper-Demo/tree/d8d85c32d579f19718b9bbb97f7a1bda0e616f1f)和本文的内容相对应。 [在这里](https://github.com/iPaulPro/Android-ItemTouchHelper-Demo/releases)下载demo apk 。

### 设置

第一件事是RecyclerView的基本设置，修改build.gradle，添加RecyclerView的依赖。

```
compile 'com.android.support:recyclerview-v7:22.2.0'
```

ItemTouchHelper可以与几乎任意的RecyclerView.Adapter 和 LayoutManager使用，但是本文建立了几个基本的文件，可以在Gist上找到：

[https://gist.github.com/iPaulPro/2216ea5e14818056cfcc](https://gist.github.com/iPaulPro/2216ea5e14818056cfcc)

### 使用 ItemTouchHelper 和 ItemTouchHelper.Callback

要使用ItemTouchHelper，你需要创建一个[ItemTouchHelper.Callback](https://developer.android.com/reference/android/support/v7/widget/helper/ItemTouchHelper.Callback.html)。这个接口可以让你监听“move”与 “swipe”事件。这里还是控制view被选中的状态以及重写默认动画的地方。如果你只是想要一个基本的实现，有一个帮助类可以使用：[SimpleCallback](https://developer.android.com/reference/android/support/v7/widget/helper/ItemTouchHelper.SimpleCallback.html),但是为了了解其工作机制，我们还是自己实现。

启用基本的拖动排序与滑动删除需要重写的主要回调方法是：

```java
getMovementFlags(RecyclerView, ViewHolder)
onMove(RecyclerView, ViewHolder, ViewHolder)
onSwiped(ViewHolder, int)
```

我也需要两个帮助方法：

```java
isLongPressDragEnabled()
isItemViewSwipeEnabled()
```

我们将一一解答上面的方法。

```java
@Override
public int getMovementFlags(RecyclerView recyclerView, 
        RecyclerView.ViewHolder viewHolder) {
    int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
    int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
    return makeMovementFlags(dragFlags, swipeFlags);
}
```

ItemTouchHelper可以让你轻易得到一个事件的方向。你需要重写getMovementFlags()方法来指定可以支持的拖放和滑动的方向。使用helperItemTouchHelper.makeMovementFlags(int, int)来构造返回的flag。这里我们启用了上下左右两种方向。注：上下为拖动（drag），左右为滑动（swipe）。

```java
@Override
public boolean isLongPressDragEnabled() {
    return true;
}
```

ItemTouchHelper可以用于没有滑动的拖动操作（或者反过来），你必须指明你到底要支持哪一种。要支持长按RecyclerView item进入拖动操作，你必须在isLongPressDragEnabled()方法中返回true。或者，也可以调用ItemTouchHelper.startDrag(RecyclerView.ViewHolder) 方法来开始一个拖动。这会在后面讲到。

```java
@Override
public boolean isItemViewSwipeEnabled() {
    return true;
}
```

而要在view任意位置触摸事件发生时启用滑动操作，则直接在sItemViewSwipeEnabled()中返回true就可以了。或者，你也主动调用ItemTouchHelper.startSwipe(RecyclerView.ViewHolder) 来开始滑动操作。

接下来的两个是onMove()和onSwiped()，用于通知底层数据的更新。首先我们创建一个可以将这些回调方法传递出去的接口。

```java
public interface ItemTouchHelperAdapter {
 
    void onItemMove(int fromPosition, int toPosition);
 
    void onItemDismiss(int position);
}
```

[ItemTouchHelperAdapter.java Gist](https://gist.github.com/iPaulPro/5d43325ac7ae579760a9)

以本例来说，最简单的方法就是在我们的[RecyclerListAdapter](https://gist.github.com/iPaulPro/2216ea5e14818056cfcc#file-recyclerlistadapter-java) 中实现这个listener。

```java
public class RecyclerListAdapter extends 
        RecyclerView.Adapter<ItemViewHolder> 
        implements ItemTouchHelperAdapter {// ... code from gist
@Override
public void onItemDismiss(int position) {
    mItems.remove(position);
    notifyItemRemoved(position);
}
 
@Override
public void onItemMove(int from, int to) {
    Collections.swap(mItems, from, to);
    notifyItemMoved(from, to);
}
```

notifyItemRemoved()和 notifyItemMoved()的调用非常重要，有了它们Adapter才能知道发生了改变。同时还需要注意的是每当一个view切换到了一个新的索引时，我们都需要改变item的位置，而不是在拖动事件结束的时候。

现在我们回来创建我们的SimpleItemTouchHelperCallback，我们仍然需要重写onMove() 和 onSwiped()。

首先我们添加一个构造函数以及一个引用adapter的成员变量：

```java
private final ItemTouchHelperAdapter mAdapter;
 
public SimpleItemTouchHelperCallback(
        ItemTouchHelperAdapter adapter) {
    mAdapter = adapter;
}
```

然后重写剩下的事件同时通知adapter：

```java
@Override
public boolean onMove(RecyclerView recyclerView, 
        RecyclerView.ViewHolder viewHolder, 
        RecyclerView.ViewHolder target) {
    mAdapter.onItemMove(viewHolder.getAdapterPosition(), 
            target.getAdapterPosition());
    return true;
}
@Override
public void onSwiped(RecyclerView.ViewHolder viewHolder, 
        int direction) {
    mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
}
```

这样Callback类大致如下：

```java
public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {
  
    private final ItemTouchHelperAdapter mAdapter;
  
    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
        mAdapter = adapter;
    }
     
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }
  
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }
  
    @Override
    public int getMovementFlags(RecyclerView recyclerView, ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }
  
    @Override
    public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, 
            ViewHolder target) {
        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }
  
    @Override
    public void onSwiped(ViewHolder viewHolder, int direction) {
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }
  
}
```

Callback准备好了之后，我们就可以创建我们的ItemTouchHelper并调用attachToRecyclerView(RecyclerView) 了（参见[MainFragment.java](https://gist.github.com/iPaulPro/2216ea5e14818056cfcc#file-mainfragment-java)）：

```java
ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
touchHelper.attachToRecyclerView(recyclerView);
```

运行之后，你可以看到如下的效果：

![](http://www.jcodecraeer.com/uploads/20150630/1435660711130575.gif)

### 总结

这是一个ItemTouchHelper的简单实现。但是已经阐明了在RecyclerView上实现拖动排序与滑动删除时根本不需要第三方库的。在下一部分中，我们将对被拖动或者滑动的item做更多外观上的控制。

### 源代码

我在github上创建了一个覆盖这个系列文章的项目：[Android-ItemTouchHelper-Demo](https://github.com/iPaulPro/Android-ItemTouchHelper-Demo)。[第一次提交的代码](https://github.com/iPaulPro/Android-ItemTouchHelper-Demo/tree/d8d85c32d579f19718b9bbb97f7a1bda0e616f1f)和这部分相对应的，也有点第二部分的内容。

> 译文链接：[RecyclerView的拖动和滑动 第一部分 ：基本的ItemTouchHelper示例](http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0630/3123.html)  