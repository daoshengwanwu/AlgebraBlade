package com.graduation_project.android.algebrablade.views;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;


public class CanvasView extends View {
    private static final int AXIS_WIDTH = 5; //坐标轴宽度
    private static final int AXIS_MARGIN = 8; //坐标轴距离本View边缘最近距离
    private static final int AXIS_COLOR = 0xff000000; //坐标轴的颜色
    private static final int X_AXIS_LATTICE_INDEX_MARGIN = 10;
    private static final int GUIDE_LINE_WIDTH = 2; //辅助线的宽度
    private static final int GUIDE_LINE_COLOR = 0xffdcdcdc; //辅助线的颜色
    private static final int INDEX_SIZE = 30; //坐标轴下标的文字大小
    private static final int INDEX_COLOR = 0Xff000000; //坐标轴下标的颜色
    private static final int INDEX_MARGIN = 38; //坐标轴下标与坐标轴的距离
    private static final int CURVE_WIDTH = 3; //曲线宽度
    private static final float LATTICE_MAX_PIXEL = 150; //格子的最大像素值
    private static final float LATTICE_MIN_PIXEL = LATTICE_MAX_PIXEL / 2; //格子的最小像素值
    private static final float LATTICE_MAX_UNIT = 1024.1f;//一个格子最大代表的单位
    private static final float LATTICE_MIN_UNIT = 0.0009999f;//一个格子最小代表的单位
    private static final float SCALE_SENSITIVITY = 5;//缩放敏感度，越小越敏感
    private static final float INTERSECTION_SIZE = 5;//交点的大小
    private static final float ALIGNMENT_WIDTH = 2;
    private static final int ALIGNMENT_COLOR = 0xffaf6f1f;

    private int mCurWidth = -1; //当前View的宽度
    private int mCurHeight = -1; //当前View的高度
    private float mPixelOfLattice = (LATTICE_MAX_PIXEL + LATTICE_MIN_PIXEL) / 2; //默认坐标轴一格的像素
    private float mUnitOfLattice = 1f; //默认坐标轴一格代表的单位数
    private PointF mOriginPoint = new PointF(0, 0); //坐标原点相对于Canvas坐标轴的坐标

    private Path mPath = new Path();
    private Paint mPaint = new Paint();
    private Rect mTextBounds = new Rect();

    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;

    private ArrayList<Float> mSelectedPoints = new ArrayList<>();
    private OnSelectPointsFinishedListener mOnSelectPointsFinishedListener;
    private Curve mFittingCurve;

    private SparseArray<Curve> mCurves = new SparseArray<>();
    private OnDomainChangeListener mOnDomainChangeListener;

    private PointF mSelectLogicPoint = new PointF(0, 0);
    private PointF mSelectRealPoint = new PointF(0, 0);
    private SparseArray<PointF> mIntersections = new SparseArray<>();
    private OnPointSelectListener mOnPointSelectListener;

    private int mCurrentState = State.FREE;
    private boolean mIsScaleMotion = false;
    private boolean mIsScrolling = false;


    public CanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mGestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                if (mCurrentState == State.FREE) {
                    float preDomainStart = getCurrentDomainStart();
                    float preDomainEnd = getCurrentDomainEnd();

                    mOriginPoint.x -= v;
                    mOriginPoint.y += v1;

                    if (mOnDomainChangeListener != null) {
                        mOnDomainChangeListener.onDomainChange(
                                CanvasView.this,
                                preDomainStart,
                                preDomainEnd,
                                getCurrentDomainStart(),
                                getCurrentDomainEnd(),
                                mCurves
                        );
                    }
                } else if (mCurrentState == State.GET_POINTS) {
                    if (!mIsScrolling) {
                        mSelectedPoints.clear();
                        mFittingCurve = null;
                    }

                    mIsScrolling = true;
                    PointF logicPoint = motionPoint2LogicPoint(
                            motionEvent1.getX(0), motionEvent1.getY(0));

                    mSelectedPoints.add(logicPoint.x);
                    mSelectedPoints.add(logicPoint.y);
                }

                invalidate();
                return false;
            }
        });

        mScaleGestureDetector = new ScaleGestureDetector(context,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {

            private float mStartSpan;
            private float mStartPixelOfLattice;
            private float mStartUnitOfLattice;
            private float mStartOriginPointX;
            private float mStartOriginPointY;
            private float mStartFocusX;
            private float mStartFocusY;

            @Override
            public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
                float preDomainStart = getCurrentDomainStart();
                float preDomainEnd = getCurrentDomainEnd();
                float scaleFocusPixelX = mStartFocusX - mCurWidth / 2 - mStartOriginPointX;
                float scaleFocusPixelY = mCurHeight / 2 - mStartFocusY - mStartOriginPointY;
                float scaleFocusUnitX = scaleFocusPixelX / mStartPixelOfLattice * mStartUnitOfLattice;
                float scaleFocusUnitY = scaleFocusPixelY / mStartPixelOfLattice * mStartUnitOfLattice;
                float fullDelta = (scaleGestureDetector.getCurrentSpan() - mStartSpan) / SCALE_SENSITIVITY;
                float delta = fullDelta % (LATTICE_MAX_PIXEL - LATTICE_MIN_PIXEL);
                float unitDelta = (float)Math.pow(2, (int)fullDelta / (int)(LATTICE_MAX_PIXEL - LATTICE_MIN_PIXEL));
                float tmpUnitOfLattice;
                boolean change = false;

                if (mStartPixelOfLattice + delta < LATTICE_MIN_PIXEL) {
                    unitDelta /= 2;
                    if (mStartUnitOfLattice / unitDelta <= LATTICE_MAX_UNIT) {
                        mPixelOfLattice = LATTICE_MAX_PIXEL - (LATTICE_MIN_PIXEL - mStartPixelOfLattice - delta);
                        mUnitOfLattice = mStartUnitOfLattice / unitDelta;

                        change = true;
                    }
                } else if (mStartPixelOfLattice + delta > LATTICE_MAX_PIXEL) {
                    unitDelta *= 2;
                    if (mStartUnitOfLattice / unitDelta >= LATTICE_MIN_UNIT) {
                        mPixelOfLattice = LATTICE_MIN_PIXEL + (mStartPixelOfLattice + delta - LATTICE_MAX_PIXEL);
                        mUnitOfLattice = mStartUnitOfLattice / unitDelta;

                        change = true;
                    }
                } else {
                    tmpUnitOfLattice = mStartUnitOfLattice / unitDelta;
                    if (tmpUnitOfLattice >= LATTICE_MIN_UNIT && tmpUnitOfLattice <= LATTICE_MAX_UNIT) {
                        mPixelOfLattice = mStartPixelOfLattice + delta;
                        mUnitOfLattice = mStartUnitOfLattice / unitDelta;

                        change = true;
                    }
                }

                if (change) {
                    mOriginPoint.x = mStartOriginPointX - scaleFocusUnitX / mUnitOfLattice * mPixelOfLattice + scaleFocusPixelX;
                    mOriginPoint.y = mStartOriginPointY - scaleFocusUnitY / mUnitOfLattice * mPixelOfLattice + scaleFocusPixelY;

                    if (mOnDomainChangeListener != null) {
                        mOnDomainChangeListener.onDomainChange(
                                CanvasView.this,
                                preDomainStart,
                                preDomainEnd,
                                getCurrentDomainStart(),
                                getCurrentDomainEnd(),
                                mCurves
                        );
                    }
                    invalidate();
                }

                return false;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                mIsScaleMotion = true;

                mStartFocusX = detector.getFocusX();
                mStartFocusY = detector.getFocusY();
                mStartSpan = detector.getCurrentSpan();
                mStartPixelOfLattice = mPixelOfLattice;
                mStartUnitOfLattice = mUnitOfLattice;
                mStartOriginPointX = mOriginPoint.x;
                mStartOriginPointY = mOriginPoint.y;

                return super.onScaleBegin(detector);
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                mIsScaleMotion = false;
                super.onScaleEnd(detector);
            }
        });

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public void addLine(int key, Curve curve) {
        mCurves.put(key, curve);
    }

    public void setFittingCurve(Curve curve) {
        mFittingCurve = curve;
    }

    public void clearLines() {
        mCurves.clear();
    }

    public void setOnDomainChangeListener(OnDomainChangeListener listener) {
        mOnDomainChangeListener = listener;
    }

    public void setOnSelectPointsFinishedListener(OnSelectPointsFinishedListener listener) {
        mOnSelectPointsFinishedListener = listener;
    }

    public void addIntersection(int key, float x, float y) {
        PointF point = mIntersections.get(key);
        if (point == null) {
            point = new PointF(x, y);
            mIntersections.put(key, point);
        }

        point.x = x;
        point.y = y;
    }

    public void clearIntersections() {
        mIntersections.clear();
    }

    public void setOnPointSelectListener(OnPointSelectListener listener) {
        mOnPointSelectListener = listener;
    }

    public void setState(int state) {
        mCurrentState = state;

        if (state == State.FREE) {
            invalidate();
        }
    }

    public int getState() {
        return mCurrentState;
    }

    public void refresh() {
        invalidate();
    }

    public void reset() {
        float preDomainStart = getCurrentDomainStart();
        float preDomainEnd = getCurrentDomainEnd();

        mPixelOfLattice = (LATTICE_MAX_PIXEL + LATTICE_MIN_PIXEL) / 2; //默认坐标轴一格的像素
        mUnitOfLattice = 1f; //默认坐标轴一格代表的单位数
        mOriginPoint.x = mOriginPoint.y = 0;

        if (mOnDomainChangeListener != null) {
            mOnDomainChangeListener.onDomainChange(this, preDomainStart, preDomainEnd,
                    getCurrentDomainStart(), getCurrentDomainEnd(), mCurves);
        }
        invalidate();
    }

    public Bitmap screenshot() {
        setDrawingCacheEnabled(true);
        buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(getDrawingCache());
        setDrawingCacheEnabled(false);

        return bitmap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (mCurrentState) {
            case State.GET_POINTS:
            case State.FREE: {
                if (event.getPointerCount() > 1 || event.getAction() == MotionEvent.ACTION_UP) {
                    if (mIsScrolling && mOnSelectPointsFinishedListener != null) {
                        mOnSelectPointsFinishedListener.
                                onPointsSelected(this, mSelectedPoints);
                    }

                    mIsScrolling = false;
                }

                mScaleGestureDetector.onTouchEvent(event);
                if (!mIsScaleMotion) {
                    mGestureDetector.onTouchEvent(event);
                }

            } break;

            case State.GET_VALUE: {
                mSelectRealPoint.x = event.getX(0);
                mSelectRealPoint.y = event.getY(0);

                motionPoint2LogicPoint(event.getX(0), event.getY(0), mSelectLogicPoint);

                if (mOnPointSelectListener != null) {
                    mOnPointSelectListener.onPointSelect(this, mSelectLogicPoint, mSelectRealPoint);
                }

                invalidate();
            } break;

            default: break;
        }

        return true;
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

        if (mOnDomainChangeListener != null) {
            mOnDomainChangeListener.onDomainChange(this, 1, -1,
                    getCurrentDomainStart(), getCurrentDomainEnd(), mCurves);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        adjustCanvas(canvas);
        drawCoordinate(canvas);
        if (mCurrentState != State.GET_POINTS) {
            drawCurves(canvas);
            if (mCurrentState == State.GET_VALUE) {
                drawIntersections(canvas);
            }
        } else {
            drawSelectedPoints(canvas);
            drawCurve(canvas, mFittingCurve);
        }
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
        int yCoordinateOfXAxis = (int)mOriginPoint.y;
        if (yCoordinateOfXAxis > mCurHeight / 2 - AXIS_MARGIN) {
            yCoordinateOfXAxis = mCurHeight / 2 - AXIS_MARGIN;
        }
        if (yCoordinateOfXAxis < mCurHeight / 2 - mCurHeight + AXIS_MARGIN) {
            yCoordinateOfXAxis = mCurHeight / 2 - mCurHeight + AXIS_MARGIN;
        }

        int xCoordinateOfYAxis = (int)mOriginPoint.x;
        if (xCoordinateOfYAxis > mCurWidth - mCurWidth / 2 - AXIS_MARGIN) {
            xCoordinateOfYAxis = mCurWidth - mCurWidth / 2 - AXIS_MARGIN;
        }
        if (xCoordinateOfYAxis < AXIS_MARGIN - mCurWidth / 2) {
            xCoordinateOfYAxis = AXIS_MARGIN - mCurWidth / 2;
        }

        // 绘制X坐标轴上的格子及下标
        int xCoordinateOfFirstLattice;
        float unitsOfFirstLattice;
        int origin_leftEdge_len = Math.abs((int)mOriginPoint.x + mCurWidth / 2);
        int latticeNum = origin_leftEdge_len / (int)mPixelOfLattice;
        if ((int)mOriginPoint.x > -mCurWidth / 2) {
            origin_leftEdge_len = latticeNum * (int)mPixelOfLattice;
            xCoordinateOfFirstLattice = (int)mOriginPoint.x - origin_leftEdge_len;
            unitsOfFirstLattice = -latticeNum * mUnitOfLattice;
        } else {
            origin_leftEdge_len = (latticeNum + 1) * (int)mPixelOfLattice;
            xCoordinateOfFirstLattice = (int)mOriginPoint.x + origin_leftEdge_len;
            unitsOfFirstLattice = (latticeNum + 1) * mUnitOfLattice;
        }

        mPaint.reset();
        mPaint.setStrokeWidth(GUIDE_LINE_WIDTH);
        mPaint.setColor(GUIDE_LINE_COLOR);
        for (int x = xCoordinateOfFirstLattice; x < mCurWidth - mCurWidth / 2; x += (int)mPixelOfLattice) {
            canvas.drawLine(x, mCurHeight / 2,
                    x, mCurHeight / 2 - mCurHeight + INDEX_MARGIN + X_AXIS_LATTICE_INDEX_MARGIN,
                    mPaint);
        }

        canvas.save();
        canvas.scale(1, -1);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(INDEX_COLOR);
        mPaint.setTextSize(INDEX_SIZE);
        float u = unitsOfFirstLattice;
        for (int x = xCoordinateOfFirstLattice;
             x < mCurWidth - mCurWidth / 2; x += (int)mPixelOfLattice, u += mUnitOfLattice) {

            String indexStr = String.valueOf(Math.round(u * 1000) / 1000f);
            mPaint.getTextBounds(indexStr, 0, indexStr.length(), mTextBounds);
            canvas.drawText(indexStr, x - mTextBounds.width() / 2,
                    mCurHeight - mCurHeight / 2 - INDEX_MARGIN + mTextBounds.height(), mPaint);
        }
        canvas.restore();

        // 绘制Y坐标轴上的格子及下标
        int yCoordinateOfFirstLattice;
        int bottomYCoordinate = mCurHeight / 2 - mCurHeight + INDEX_MARGIN + X_AXIS_LATTICE_INDEX_MARGIN;
        int origin_bottomEdge_len = Math.abs((int)mOriginPoint.y - bottomYCoordinate);
        latticeNum = origin_bottomEdge_len / (int)mPixelOfLattice;
        if ((int)mOriginPoint.y > bottomYCoordinate) {
            origin_bottomEdge_len = latticeNum * (int)mPixelOfLattice;
            yCoordinateOfFirstLattice = (int)mOriginPoint.y - origin_bottomEdge_len;
            unitsOfFirstLattice = -latticeNum * mUnitOfLattice;
        } else {
            origin_bottomEdge_len = (latticeNum + 1) * (int)mPixelOfLattice;
            yCoordinateOfFirstLattice = (int)mOriginPoint.y + origin_bottomEdge_len;
            unitsOfFirstLattice = (latticeNum + 1) * mUnitOfLattice;
        }

        mPaint.setStrokeWidth(GUIDE_LINE_WIDTH);
        mPaint.setColor(GUIDE_LINE_COLOR);
        for (int y = yCoordinateOfFirstLattice; y < mCurHeight / 2; y += (int)mPixelOfLattice) {
            canvas.drawLine(-mCurWidth / 2, y, mCurWidth - mCurWidth / 2, y, mPaint);
        }

        canvas.save();
        canvas.scale(1, -1);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(INDEX_COLOR);
        mPaint.setTextSize(INDEX_SIZE);
        u = unitsOfFirstLattice;
        for (int y = yCoordinateOfFirstLattice;
             y < mCurHeight / 2; y += (int)mPixelOfLattice, u += mUnitOfLattice) {

            String indexStr = String.valueOf(Math.round(u * 1000) / 1000f);
            mPaint.getTextBounds(indexStr, 0, indexStr.length(), mTextBounds);
            canvas.drawText(indexStr,
                    -mCurWidth / 2 + INDEX_MARGIN - mTextBounds.height(),
                    -y, mPaint);
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

    /**
     * 将mCurves里的曲线绘制出来
     */
    private void drawCurves(Canvas canvas) {
        mPaint.reset();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(CURVE_WIDTH);

        for (int i = 0; i < mCurves.size(); i++) {
            drawCurve(canvas, mCurves.valueAt(i));
        }
    }

    private void drawCurve(Canvas canvas, Curve curve) {
        if (curve == null) {
            return;
        }

        float[] points = curve.points;
        int color = curve.color;
        int size = curve.size;

        if (size < 4 || size % 2 != 0) {
            return;
        }

        mPath.reset();
        mPath.rewind();
        mPath.moveTo(logicXCoordinate2CanvasXCoordinate(points[0]),
                logicYCoordinate2CanvasYCoordinate(points[1]));
        for (int j = 2; j < size; j += 2) {
            mPath.lineTo(logicXCoordinate2CanvasXCoordinate(points[j]),
                    logicYCoordinate2CanvasYCoordinate(points[j + 1]));
        }
        mPaint.setColor(color);
        canvas.drawPath(mPath, mPaint);
    }

    private void drawIntersections(Canvas canvas) {
        mPaint.reset();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(ALIGNMENT_WIDTH);
        mPaint.setColor(ALIGNMENT_COLOR);

        canvas.drawLine(
                logicXCoordinate2CanvasXCoordinate(mSelectLogicPoint.x),
                mCurHeight / 2,
                logicXCoordinate2CanvasXCoordinate(mSelectLogicPoint.x),
                mCurHeight / 2 - mCurHeight,
                mPaint
        );

        int key;
        int color;
        int x, y;
        PointF point;
        String disStr;
        mPaint.reset();
        for (int i = 0; i < mIntersections.size(); i++) {
            key = mIntersections.keyAt(i);
            point = mIntersections.valueAt(i);
            color = mCurves.get(key).color;
            mPaint.setColor(color);

            x = logicXCoordinate2CanvasXCoordinate(point.x);
            y = logicYCoordinate2CanvasYCoordinate(point.y);

            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(10);
            canvas.drawPoint(x, y, mPaint);

            disStr = "#" + key;
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setTextSize(36);
            mPaint.getTextBounds(disStr, 0, disStr.length(), mTextBounds);

            canvas.save();
            canvas.scale(1, -1);
            canvas.drawText(disStr, x + 10, mTextBounds.height() / 2 - y, mPaint);
            canvas.restore();
        }
    }

    private void drawSelectedPoints(Canvas canvas) {
        int size = mSelectedPoints.size();
        if (size < 2 || size % 2 != 0) {
            return;
        }

        mPaint.reset();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(CURVE_WIDTH);

        mPath.reset();
        mPath.rewind();
        mPath.moveTo(logicXCoordinate2CanvasXCoordinate(mSelectedPoints.get(0)),
                logicYCoordinate2CanvasYCoordinate(mSelectedPoints.get(1)));
        for (int j = 2; j < size; j += 2) {
            mPath.lineTo(logicXCoordinate2CanvasXCoordinate(mSelectedPoints.get(j)),
                    logicYCoordinate2CanvasYCoordinate(mSelectedPoints.get(j + 1)));
        }
        mPaint.setColor(Color.BLUE);
        canvas.drawPath(mPath, mPaint);
    }

    private int logicXCoordinate2CanvasXCoordinate(double logicXCoordinate) {
        int logicPixelXCoordinate = (int)(logicXCoordinate / mUnitOfLattice * (int)mPixelOfLattice);
        return logicPixelXCoordinate + (int)mOriginPoint.x;
    }

    private int logicYCoordinate2CanvasYCoordinate(double logicYCoordinate) {
        int logicPixelYCoordinate = (int)(logicYCoordinate / mUnitOfLattice * (int)mPixelOfLattice);
        return logicPixelYCoordinate + (int)mOriginPoint.y;
    }

    private PointF motionPoint2LogicPoint(float motionX, float motionY) {
        PointF rst = new PointF();
        motionPoint2LogicPoint(motionX, motionY, rst);
        return rst;
    }

    private void motionPoint2LogicPoint(float motionX, float motionY, PointF outPoint) {
        if (outPoint == null) {
            return;
        }

        outPoint.x = (motionX - mCurWidth / 2 - mOriginPoint.x) / mPixelOfLattice * mUnitOfLattice;
        outPoint.y = (mCurHeight / 2 - motionY - mOriginPoint.y) / mPixelOfLattice * mUnitOfLattice;
    }

    private float getCurrentDomainStart() {
        return (float)(-mCurWidth / 2 - (int)mOriginPoint.x) / (int)mPixelOfLattice * mUnitOfLattice;
    }

    private float getCurrentDomainEnd() {
        return (float)(mCurWidth - mCurWidth / 2 - (int)mOriginPoint.x) / (int)mPixelOfLattice * mUnitOfLattice;
    }


    public static class Curve {
        public float[] points;
        public int color;
        public int size;


        public Curve() { }

        public Curve(float[] points, int color, int size) {
            this.points = points;
            this.color = color;
            this.size = size;
        }
    }

    public static class State {
        public static final int FREE = 0x00000000;
        public static final int GET_VALUE = 0x00000001;
        public static final int GET_POINTS = 0x00000002;
    }

    public interface OnDomainChangeListener {
        void onDomainChange(CanvasView view, float preStart, float preEnd,
                            float start, float end, SparseArray<Curve> curves);
    }

    public interface OnPointSelectListener {
        void onPointSelect(CanvasView view, PointF selectLogicPoint, PointF selectRealPoint);
    }

    public interface OnSelectPointsFinishedListener {
        void onPointsSelected(CanvasView view, ArrayList<Float> points);
    }
}
