package com.graduation_project.android.algebrablade.views;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class CanvasView extends View {
    private static final int AXIS_WIDTH = 5; //坐标轴宽度
    private static final int AXIS_MARGIN = 35; //坐标轴距离本View边缘最近距离
    private static final int AXIS_COLOR = 0xff000000; //坐标轴的颜色
    private static final int GUIDE_LINE_WIDTH = 2; //辅助线的宽度
    private static final int GUIDE_LINE_COLOR = 0xffdcdcdc; //辅助线的颜色
    private static final int INDEX_SIZE = 30; //坐标轴下标的文字大小
    private static final int INDEX_COLOR = 0Xff000000; //坐标轴下标的颜色
    private static final int INDEX_MARGIN = 12; //坐标轴下标与坐标轴的距离

    private int mCurWidth = -1; //当前View的宽度
    private int mCurHeight = -1; //当前View的高度
    private int mPixelOfLattice = 100; //默认坐标轴一格为50像素
    private int mUnitOfLattice = 1; //默认坐标轴一格代表一个单位
    private Point mOriginPoint = new Point(-300, 500); //坐标原点相对于Canvas坐标轴的坐标

    private Paint mPaint = new Paint();
    private Rect mTextBounds = new Rect();


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
        //算出X轴的纵坐标与Y轴的横坐标
        int yCoordinateOfXAxis = mOriginPoint.y;
        if (yCoordinateOfXAxis > mCurHeight / 2 - AXIS_MARGIN) {
            yCoordinateOfXAxis = mCurHeight / 2 - AXIS_MARGIN;
        }
        if (yCoordinateOfXAxis < mCurHeight / 2 - mCurHeight + AXIS_MARGIN) {
            yCoordinateOfXAxis = mCurHeight / 2 - mCurHeight + AXIS_MARGIN;
        }

        int xCoordinateOfYAxis = mOriginPoint.x;
        if (xCoordinateOfYAxis > mCurWidth - mCurWidth / 2 - AXIS_MARGIN) {
            xCoordinateOfYAxis = mCurWidth - mCurWidth / 2 - AXIS_MARGIN;
        }
        if (xCoordinateOfYAxis < AXIS_MARGIN - mCurWidth / 2) {
            xCoordinateOfYAxis = AXIS_MARGIN - mCurWidth / 2;
        }

        // 绘制X坐标轴上的格子及下标
        int xCoordinateOfFirstLattice;
        int unitsOfFirstLattice;
        int origin_leftEdge_len = Math.abs(mOriginPoint.x + mCurWidth / 2);
        int latticeNum = origin_leftEdge_len / mPixelOfLattice;
        if (mOriginPoint.x > -mCurWidth / 2) {
            origin_leftEdge_len = latticeNum * mPixelOfLattice;
            xCoordinateOfFirstLattice = mOriginPoint.x - origin_leftEdge_len;
            unitsOfFirstLattice = -latticeNum * mUnitOfLattice;
        } else {
            origin_leftEdge_len = (latticeNum + 1) * mPixelOfLattice;
            xCoordinateOfFirstLattice = mOriginPoint.x + origin_leftEdge_len;
            unitsOfFirstLattice = (latticeNum + 1) * mUnitOfLattice;
        }

        mPaint.reset();
        mPaint.setStrokeWidth(GUIDE_LINE_WIDTH);
        mPaint.setColor(GUIDE_LINE_COLOR);
        for (int x = xCoordinateOfFirstLattice; x < mCurWidth - mCurWidth / 2; x += mPixelOfLattice) {
            canvas.drawLine(x, mCurHeight / 2, x, mCurHeight / 2 - mCurHeight, mPaint);
        }

        canvas.save();
        canvas.scale(1, -1);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(INDEX_COLOR);
        mPaint.setTextSize(INDEX_SIZE);
        for (int x = xCoordinateOfFirstLattice, u = unitsOfFirstLattice;
             x < mCurWidth - mCurWidth / 2; x += mPixelOfLattice, u += mUnitOfLattice) {

            String indexStr = String.valueOf(u);
            mPaint.getTextBounds(indexStr, 0, indexStr.length(), mTextBounds);
            canvas.drawText(indexStr, x - mTextBounds.width() / 2,
                    -yCoordinateOfXAxis + mTextBounds.height() + INDEX_MARGIN, mPaint);
        }
        canvas.restore();

        // 绘制Y坐标轴上的格子及下标
        int yCoordinateOfFirstLattice;
        int origin_bottomEdge_len = Math.abs(mOriginPoint.y + mCurHeight - mCurHeight / 2);
        latticeNum = origin_bottomEdge_len / mPixelOfLattice;
        if (mOriginPoint.y > mCurHeight / 2 - mCurHeight) {
            origin_bottomEdge_len = latticeNum * mPixelOfLattice;
            yCoordinateOfFirstLattice = mOriginPoint.y - origin_bottomEdge_len;
            unitsOfFirstLattice = -latticeNum * mUnitOfLattice;
        } else {
            origin_bottomEdge_len = (latticeNum + 1) * mPixelOfLattice;
            yCoordinateOfFirstLattice = mOriginPoint.y + origin_bottomEdge_len;
            unitsOfFirstLattice = (latticeNum + 1) * mUnitOfLattice;
        }

        mPaint.setStrokeWidth(GUIDE_LINE_WIDTH);
        mPaint.setColor(GUIDE_LINE_COLOR);
        for (int y = yCoordinateOfFirstLattice; y < mCurHeight / 2; y += mPixelOfLattice) {
            canvas.drawLine(-mCurWidth / 2, y, mCurWidth - mCurWidth / 2, y, mPaint);
        }

        canvas.save();
        canvas.scale(1, -1);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(INDEX_COLOR);
        mPaint.setTextSize(INDEX_SIZE);
        for (int y = yCoordinateOfFirstLattice, u = unitsOfFirstLattice;
             y < mCurHeight / 2; y += mPixelOfLattice, u += mUnitOfLattice) {

            String indexStr = String.valueOf(u);
            mPaint.getTextBounds(indexStr, 0, indexStr.length(), mTextBounds);
            canvas.drawText(indexStr,
                    xCoordinateOfYAxis - mTextBounds.width() - INDEX_MARGIN,
                    -y + mTextBounds.height() / 2, mPaint);
        }
        canvas.restore();


        // 绘制X与Y坐标轴
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(AXIS_WIDTH);
        mPaint.setColor(AXIS_COLOR);

        canvas.drawLine(-mCurWidth / 2, yCoordinateOfXAxis,
                mCurWidth - mCurWidth / 2, yCoordinateOfXAxis, mPaint);

        canvas.drawLine(xCoordinateOfYAxis, mCurHeight / 2,
                xCoordinateOfYAxis, mCurHeight / 2 - mCurHeight, mPaint);
    }
}
