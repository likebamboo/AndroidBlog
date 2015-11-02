package com.likebamboo.osa.android.ui.view.fa;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.util.LruCache;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * DrawableAwesome
 *
 * @see <a herf="https://github.com/bperin/FontAwesomeAndroid">FontAwesomeAndroid</a>
 */
public class TextAwesome extends TextView {

    private final static String NAME = "FONTAWESOME";
    private static LruCache<String, Typeface> sTypefaceCache = new LruCache<String, Typeface>(12);

    public TextAwesome(Context context) {
        super(context);
        init();
    }

    public TextAwesome(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public synchronized void init() {
        if (isInEditMode()) {
            return;
        }
        Typeface typeface = sTypefaceCache.get(NAME);
        if (typeface == null) {
            typeface = Typeface.createFromAsset(getContext().getAssets(), "fontawesome-webfont.ttf");
            sTypefaceCache.put(NAME, typeface);
        }
        setTypeface(typeface);
    }

    /**
     * 设置文字
     *
     * @param icon
     * @param text
     */
    public void setText(int icon, String text) {
        String iconStr = getResources().getString(icon);
        setText(iconStr + " " + text);
    }

}


