package com.graduation_project.android.algebrablade;


import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.daoshengwanwu.math_util.calculator.Calculator;
import com.daoshengwanwu.math_util.calculator.VarAriExp;
import com.daoshengwanwu.math_util.calculator.Variable;
import com.daoshengwanwu.math_util.calculator.exception.VariableNotSetException;
import com.graduation_project.android.algebrablade.model.CurveSource;
import com.graduation_project.android.algebrablade.model.CurveSourceLab;
import com.graduation_project.android.algebrablade.model.ResultSource;
import com.graduation_project.android.algebrablade.views.CanvasView;
import com.graduation_project.android.algebrablade.views.CanvasView.Curve;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class GraphicActivity extends AppCompatActivity {
    @BindView(R.id.canvas_view)
    CanvasView mCanvasView;

    @BindView(R.id.left_value_display_text_view)
    TextView mLeftValueDisplayTextView;

    @BindView(R.id.right_value_display_text_view)
    TextView mRightValueDisplayTextView;

    private TextView mCurValDisTV;
    private Calculator mCalculator = new Calculator();
    private CurveSourceLab mCurveSourceLab = CurveSourceLab.getInstance();
    private SparseArray<ResultSource> mResultSourceSparseArray = new SparseArray<>();


    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, GraphicActivity.class);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphic);
        ButterKnife.bind(this);

        updateResultGeneratorsFromCurveSourceLab();
        mCanvasView.setOnDomainChangeListener(new CanvasView.OnDomainChangeListener() {
            @Override
            public void onDomainChange(CanvasView view, float preStart, float preEnd,
                                       float start, float end, SparseArray<Curve> curves) {

                int key = -1;
                ResultSource resultSource = null;
                Variable variable = null;
                Curve recycleCurve = null;

                for (int i = 0; i < mResultSourceSparseArray.size(); i++) {
                    key = mResultSourceSparseArray.keyAt(i);
                    resultSource = mResultSourceSparseArray.valueAt(i);
                    variable = resultSource.resultGenerator.getVarAriExp().
                            getVariableAssistant().getVariable("x");

                    if (!resultSource.isDomainSet) {
                        variable.set(start, false,
                                end, false, (end - start) / 200);
                    } else if (!variable.isSet()) {
                        throw new VariableNotSetException();
                    }

                    recycleCurve = curves.get(key);
                    view.addLine(key, getCurveFromResultSource(resultSource, recycleCurve));
                }
            }
        });

        mCanvasView.setOnPointSelectListener(new CanvasView.OnPointSelectListener() {
            @Override
            public void onPointSelect(CanvasView view, PointF selectLogicPoint, PointF selectRealPoint) {
                view.clearIntersections();

                int canvasViewWidth = view.getWidth();
                if (selectRealPoint.x > canvasViewWidth / 2) {
                    mCurValDisTV = mLeftValueDisplayTextView;
                    mRightValueDisplayTextView.setVisibility(View.GONE);
                } else {
                    mCurValDisTV = mRightValueDisplayTextView;
                    mLeftValueDisplayTextView.setVisibility(View.GONE);
                }

                mCurValDisTV.setVisibility(View.VISIBLE);
                StringBuilder displayStr = new StringBuilder();
                int key;
                ResultSource resultSource;
                Variable variable;
                float y;
                for (int i = 0; i < mResultSourceSparseArray.size(); i++) {
                    key = mResultSourceSparseArray.keyAt(i);
                    resultSource = mResultSourceSparseArray.valueAt(i);
                    variable = resultSource.resultGenerator.getVarAriExp().getVariableAssistant().getVariable("x");
                    variable.setCurValue(selectLogicPoint.x);
                    try {
                        y = (float) resultSource.resultGenerator.curValue();
                        view.addIntersection(key, selectLogicPoint.x, y);

                        displayStr.append(String.format(Locale.getDefault(),
                                "#%d: (%f, %f) \n", key, selectLogicPoint.x, y));
                    } catch (RuntimeException e) {
                        displayStr.append(String.format(Locale.getDefault(),
                                "#%d: (%f, NaN) \n", key, selectLogicPoint.x));
                    }

                }
                mCurValDisTV.setText(displayStr.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_graphic, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.get_value: {
                if (mCanvasView.getState() == CanvasView.State.FREE) {
                    mCanvasView.setState(CanvasView.State.GET_VALUE);
                    item.setIcon(R.drawable.ic_get_value_no_point);
                } else if (mCanvasView.getState() == CanvasView.State.GET_VALUE) {
                    mCanvasView.setState(CanvasView.State.FREE);
                    item.setIcon(R.drawable.ic_get_value);

                    if (mCurValDisTV != null) {
                        mCurValDisTV.setVisibility(View.GONE);
                        mCurValDisTV = null;
                    }
                }
            } break;

            case R.id.reset: {
                mCanvasView.reset();
            } break;

            case android.R.id.home: {
                finish();
            } break;

            default: break;
        }

        return true;
    }

    private Curve getCurveFromResultSource(ResultSource resultSource, Curve recycleCurve) {
        Calculator.ResultGenerator resultGenerator = resultSource.resultGenerator;

        VarAriExp varAriExp = resultGenerator.getVarAriExp();
        Variable x = varAriExp.getVariableAssistant().getVariable("x");
        x.reset();

        if (recycleCurve == null) {
            recycleCurve = new Curve();
        }
        float[] points = recycleCurve.points != null && recycleCurve.points.length >= x.size() * 2 ?
                recycleCurve.points : new float[x.size() * 2 + 32];

        int i = 0;
        points[i++] = (float)x.curValue();
        points[i++] = (float)resultGenerator.curValue();

        while (x.hasNext() && i < points.length - 1) {
            points[i++] = (float)x.nextValue();
            points[i++] = (float)resultGenerator.curValue();
        }

        recycleCurve.points = points;
        recycleCurve.color = resultSource.color;
        recycleCurve.size = i;

        return recycleCurve;
    }

    private void updateResultGeneratorsFromCurveSourceLab() {
        int key = -1;
        CurveSource curveSource = null;
        Calculator.ResultGenerator resultGenerator = null;
        SparseArray<CurveSource> curveSources = mCurveSourceLab.getCurveSources();

        for (int i = 0; i < curveSources.size(); i++) {
            key = curveSources.keyAt(i);
            curveSource = curveSources.valueAt(i);

            resultGenerator = mCalculator.calculate(curveSource.varAriExp);
            mResultSourceSparseArray.put(key,
                    new ResultSource(resultGenerator, curveSource.isDomainSet, curveSource.color));
        }
    }
}
