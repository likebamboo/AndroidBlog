package com.likebamboo.osa.android.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.widget.Toast;

import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.entity.TagList;
import com.likebamboo.osa.android.ui.nav.ActivityNavigator;
import com.likebamboo.osa.android.ui.view.TagGroup;
import com.likebamboo.osa.android.ui.view.blur.BlurBehind;
import com.orm.StringUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 搜索界面
 */
public class SearchActivity extends BaseActivity {

    @InjectView(R.id.search_history_tags)
    TagGroup mHistoryTags;

    /**
     * ActionBar搜索布局
     */
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.inject(this);

        // 背景
        BlurBehind.getInstance().withFilterColor(getResources().getColor(R.color.bg_blur)).setBackground(this);

        // 初始化搜索布局
        initSearchView();
        // 设置历史数据
        setHistoryDatas();
        // 添加监听器
        addListener();
    }

    /**
     * 添加监听器
     */
    private void addListener() {
        // 监听标签点击事件
        mHistoryTags.setOnTagClickListener(new TagGroup.IOnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                // 执行搜索
                doSearch(tag);
            }

            @Override
            public void onTagLongClick(String tag) {
                // 删除历史记录
                mHistoryTags.deleteTag(tag);
                TagList.Tag.delete(tag);
                // 显示toast
                Toast.makeText(getApplicationContext(), tag + " 标签已删除", Toast.LENGTH_SHORT).show();
            }
        });

        // 设置搜索监听
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public boolean onQueryTextSubmit(String query) {
                return doSearch(query);
            }

            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
    }

    /**
     * 开始搜索
     *
     * @param key
     * @return
     */
    private boolean doSearch(String key) {
        if (TextUtils.isEmpty(key)) {
            return false;
        }

        // 保存历史记录
        mHistoryTags.addTag(0, key);
        TagList.Tag t = TagList.Tag.findTagByName(key);
        if (t == null) {
            t = new TagList.Tag();
            t.setName(key);
        }
        t.setAddTime(System.currentTimeMillis());
        t.save();

        // 清空输入框
        searchView.setQuery("", false);

        // 开始搜索
        Intent i = new Intent(SearchActivity.this, SearchResultActivity.class);
        // 搜索关键字
        i.putExtra(SearchResultActivity.EXTRA_SEARCH_KEY, key);
        // 设置不显示抽屉导航
        i.putExtra(NavigationActivity.EXTRA_SHOULD_DISABLE_DRAWER, true);
        // 设置标题
        i.putExtra(EXTRA_TITLE, key);
        ActivityNavigator.withAnim(i, ActivityNavigator.AnimationMode.DEFAULT).startActivity(SearchActivity.this, i);
        return true;
    }

    /**
     * 设置历史数据
     */
    private void setHistoryDatas() {
        List<String> tags = new ArrayList<>();
        List<TagList.Tag> datas = TagList.Tag.find(TagList.Tag.class, null, null, null, StringUtil.toSQLName("addTime") + " desc ", null);
        if (datas != null) {
            for (TagList.Tag tag : datas) {
                tags.add(tag.getName());
            }
        }
        mHistoryTags.setTags(tags);
    }

    /**
     * 初始化搜索布局
     */
    private void initSearchView() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_CUSTOM);
        searchView = new SearchView(this);
        searchView.onActionViewExpanded();
        searchView.setIconifiedByDefault(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_up);

        actionBar.setCustomView(searchView);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.fade_out);
    }
}
