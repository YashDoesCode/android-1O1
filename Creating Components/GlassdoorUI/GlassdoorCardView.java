package com.keeper.components.glassdoor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class GlassdoorCardView extends FrameLayout {

    private final Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF bounds = new RectF();
    private float cornerRadius = 24f;

    public GlassdoorCardView(Context context) {
        super(context);
        init();
    }

    public GlassdoorCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        backgroundPaint.setColor(Color.argb(40, 255, 255, 255));
        backgroundPaint.setStyle(Paint.Style.FILL);

        strokePaint.setColor(Color.argb(70, 255, 255, 255));
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(2f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bounds.set(2, 2, w - 2, h - 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRoundRect(bounds, cornerRadius, cornerRadius, backgroundPaint);
        canvas.drawRoundRect(bounds, cornerRadius, cornerRadius, strokePaint);
        super.onDraw(canvas);
    }
}
