package com.graduation_project.android.algebrablade.views;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class CanvasView extends View {
    private static final int AXIS_WIDTH = 2; //坐标轴宽度
    private static final int AXIS_MARGIN = 20; //坐标轴距离本View边缘最近距离
    private static final int AXIS_COLOR = 0xff00ffff; //坐标轴的颜色

    private int mCurWidth = -1; //当前View的宽度
    private int mCurHeight = -1; //当前View的高度
    private Point mOriginPoint = new Point(0, 0); //坐标原点相对于Canvas坐标轴的坐标

    private Paint mPaint = new Paint();


    public CanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCurWidth = w;
        mCurHeight = h;

        if (mCurWidth < 2 * AXIS_MARGIN + 1 ||
                mCurHeight < 2 * AXIS_MARGIN + 1) {
            throw new RuntimeException("组件尺寸过小，请保证长宽均大于" + (2 * AXIS_MARGIN + 1));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        adjustCanvas(canvas);
        drawCoordinate(canvas);
    }

    /**
     * 调整canvas的坐标原点为View的中心，并且翻转y轴
     */
    private void adjustCanvas(Canvas canvas) {
        canvas.translate(mCurWidth / 2, mCurHeight / 2);
        canvas.scale(1, -1);
    }

    /**
     * 根据mOriginPoint, 绘制坐标轴
     */
    private void drawCoordinate(Canvas canvas) {
        int yCoordinateOfXAxis = mOriginPoint.y;
        if (mCurHeight / 2 - yCoordinateOfXAxis < AXIS_MARGIN) {
            yCoordinateOfXAxis = mCurHeight / 2 - AXIS_MARGIN;
        }
        if (mCurHeight / 2 - yCoordinateOfXAxis > mCurHeight - AXIS_MARGIN) {
            yCoordinateOfXAxis = AXIS_MARGIN - mCurHeight / 2;
        }

        int xCoordinateOfYAxis = mOriginPoint.x;
        if (mCurWidth / 2 + xCoordinateOfYAxis < AXIS_MARGIN) {
            xCoordinateOfYAxis = AXIS_MARGIN - mCurWidth / 2;
        }
        if (mCurWidth / 2 + xCoordinateOfYAxis > mCurWidth - AXIS_MARGIN) {
            xCoordinateOfYAxis = mCurWidth / 2 - AXIS_MARGIN;
        }

        mPaint.reset();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(AXIS_WIDTH);
        mPaint.setColor(AXIS_COLOR);

        canvas.drawLine(-mCurWidth / 2, yCoordinateOfXAxis,
                mCurWidth - mCurWidth / 2, yCoordinateOfXAxis, mPaint);

        canvas.drawLine(xCoordinateOfYAxis, mCurHeight / 2,
                xCoordinateOfYAxis, mCurHeight / 2 - mCurHeight, mPaint);
    }
}
