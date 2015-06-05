package com.likebamboo.osa.android.ui.view.fa;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.util.LruCache;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * ButtonAwesome
 *
 * @see <a herf="https://github.com/bperin/FontAwesomeAndroid">FontAwesomeAndroid</a>
 */
public class ButtonAwesome extends Button {
    private final static String NAME = "FONTAWESOME";
    private static LruCache<String, Typeface> sTypefaceCache = new LruCache<String, Typeface>(12);

    public ButtonAwesome(Context context) {
        super(context);
        init();

    }

    public ButtonAwesome(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ButtonAwesome(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public synchronized void init() {
        Typeface typeface = sTypefaceCache.get(NAME);
        if (typeface == null) {
            typeface = Typeface.createFromAsset(getContext().getAssets(), "fontawesome-webfont.ttf");
            sTypefaceCache.put(NAME, typeface);
        }

        setTypeface(typeface);
    }
}


