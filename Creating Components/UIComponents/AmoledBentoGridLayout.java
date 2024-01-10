package com.keeper.components.uicomponents;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.GridLayout;

public class AmoledBentoGridLayout extends GridLayout {

    public AmoledBentoGridLayout(Context context) {
        super(context);
        init();
    }

    public AmoledBentoGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setBackgroundColor(Color.BLACK);
        setUseDefaultMargins(true);
        setAlignmentMode(ALIGN_MARGINS);
        setColumnCount(2);
    }
}
