package com.keeper.components.uicomponents;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class DynamicChipLayout extends ViewGroup {

    private int lineSpacing = 16;
    private int itemSpacing = 16;

    public DynamicChipLayout(Context context) {
        super(context);
    }

    public DynamicChipLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int height = 0;
        int currentLineWidth = 0;
        int currentLineHeight = 0;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            int childW = child.getMeasuredWidth();
            int childH = child.getMeasuredHeight();

            if (currentLineWidth + childW > width) {
                height += currentLineHeight + lineSpacing;
                currentLineWidth = childW + itemSpacing;
                currentLineHeight = childH;
            } else {
                currentLineWidth += childW + itemSpacing;
                currentLineHeight = Math.max(currentLineHeight, childH);
            }
        }
        height += currentLineHeight;
        setMeasuredDimension(resolveSize(width, widthMeasureSpec), resolveSize(height, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l - getPaddingLeft() - getPaddingRight();
        int curLeft = getPaddingLeft();
        int curTop = getPaddingTop();
        int curLineHeight = 0;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;

            int childW = child.getMeasuredWidth();
            int childH = child.getMeasuredHeight();

            if (curLeft + childW > getPaddingLeft() + width) {
                curLeft = getPaddingLeft();
                curTop += curLineHeight + lineSpacing;
                curLineHeight = childH;
            } else {
                curLineHeight = Math.max(curLineHeight, childH);
            }

            child.layout(curLeft, curTop, curLeft + childW, curTop + childH);
            curLeft += childW + itemSpacing;
        }
    }
}
