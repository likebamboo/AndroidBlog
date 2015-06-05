package com.likebamboo.osa.android.ui.nav;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;

import com.likebamboo.osa.android.R;
import com.likebamboo.osa.android.ui.NavigationActivity;
import com.likebamboo.osa.android.ui.WebViewActivity;

import java.util.List;

/**
 * Activity导航
 * Created by wentaoli on 2015/5/11.
 */
public class ActivityNavigator {

    public static final ActivityNavigator nav = new ActivityNavigator();

    private static final String NAV_ANIM_EXIT = "NAV_ANIM_EXIT";
    private static final String NAV_ANIM_IN = "NAV_ANIM_EXIT";


    /**
     * 动画模式
     */
    public enum AnimationMode {
        SLIDE_RIGHT,
        SLIDE_BOTTOM,
        FADE_SLOW,
        FADE_IN_OUT,
        POP,
        DEFAULT,
        DEFAULT_OUT,
        ZOOM_IN_OUT,
        NONE
    }


    /**
     * 添加动画效果
     *
     * @param intent
     * @param animationMode
     * @return
     */
    public static ActivityNavigator withAnim(Intent intent, AnimationMode animationMode) {
        if (intent == null) {
            return nav;
        }
        int anim = 0;
        switch (animationMode) {
            case SLIDE_RIGHT:
                break;
            case SLIDE_BOTTOM:
                break;
            case FADE_SLOW:
                break;
            case FADE_IN_OUT:
                intent.putExtra(NAV_ANIM_IN, R.anim.fade_in);
                //intent.putExtra(NAV_ANIM_EXIT, R.anim.fade_out);
                break;
            case POP:
                break;
            case DEFAULT:
                break;
            case DEFAULT_OUT:
                break;
            case ZOOM_IN_OUT:
                break;
            case NONE:
            default:
                break;
        }
        return nav;
    }

    /**
     * 将Activity带到最顶层[有BUG]
     *
     * @param i
     * @return
     */
    public static ActivityNavigator reorderToTop(Intent i) {
        if (i == null) {
            return nav;
        }
        i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        return nav;
    }

    /**
     * 将Activity清除某个Activity上边的Activity
     *
     * @param i
     * @return
     */
    public static ActivityNavigator clearTop(Intent i) {
        if (i == null) {
            return nav;
        }
        // 清除目标Activity上方的Activity（同时会清掉目标activity，并重新建立一个目标activity）
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // 加SingleTop标志，防止同时清除目标Activity
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return nav;
    }

    /**
     * 打开webview界面
     *
     * @param activity
     * @param intent
     * @param url
     */
    public static void openWebView(Activity activity, Intent intent, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (intent == null) {
            intent = new Intent();
        }
        // 跳转到Web页面
        intent.setClass(activity, WebViewActivity.class);
        intent.putExtra(WebViewActivity.EXTRA_URL, url);
        intent.putExtra(NavigationActivity.EXTRA_SHOULD_DISABLE_DRAWER, true);
        ActivityNavigator.startActivity(activity, intent);
    }

    /**
     * 启动Activity
     *
     * @param activity
     * @param intent
     */
    public static void startActivity(Activity activity, Intent intent) {
        if (intent == null || !isIntentAvailable(activity, intent)) {
            return;
        }
        int animIn = 0, animOut = 0;
        if (intent.hasExtra(NAV_ANIM_EXIT)) {
            animOut = intent.getIntExtra(NAV_ANIM_EXIT, 0);
        }
        if (intent.hasExtra(NAV_ANIM_IN)) {
            animIn = intent.getIntExtra(NAV_ANIM_IN, 0);
        }
        activity.startActivity(intent);
        activity.overridePendingTransition(animIn, animOut);
    }

    /**
     * 检验Intent是否有效
     *
     * @param context
     * @param i
     * @return
     */
    public static boolean isIntentAvailable(Context context, Intent i) {
        if (i == null) {
            return false;
        }
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
}
