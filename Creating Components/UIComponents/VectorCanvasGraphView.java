package com.keeper.components.uicomponents;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class VectorCanvasGraphView extends View {

    private final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();
    private float[] points = new float[]{10, 50, 40, 90, 70, 30, 100, 80};

    public VectorCanvasGraphView(Context context) {
        super(context);
        init();
    }

    public VectorCanvasGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        linePaint.setColor(Color.parseColor("#00E676"));
        linePaint.setStrokeWidth(4f);
        linePaint.setStyle(Paint.Style.STROKE);
    }

    public void setPoints(float[] newPoints) {
        this.points = newPoints;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (points == null || points.length < 2) return;

        path.reset();
        path.moveTo(points[0], points[1]);
        for (int i = 2; i < points.length; i += 2) {
            path.lineTo(points[i], points[i + 1]);
        }
        canvas.drawPath(path, linePaint);
    }
}
