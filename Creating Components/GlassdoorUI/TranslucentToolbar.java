package com.keeper.components.glassdoor;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import androidx.appcompat.widget.Toolbar;

public class TranslucentToolbar extends Toolbar {

    public TranslucentToolbar(Context context) {
        super(context);
        init();
    }

    public TranslucentToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setBackgroundColor(Color.argb(180, 10, 10, 10));
    }

    public void updateAlphaByScroll(float scrollFraction) {
        int alpha = (int) (Math.min(1.0f, Math.max(0.0f, scrollFraction)) * 255);
        setBackgroundColor(Color.argb(alpha, 10, 10, 10));
    }
}
