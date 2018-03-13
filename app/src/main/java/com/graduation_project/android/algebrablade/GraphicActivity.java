package com.graduation_project.android.algebrablade;


import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;

import com.daoshengwanwu.math_util.calculator.Calculator;
import com.daoshengwanwu.math_util.calculator.VarAriExp;
import com.daoshengwanwu.math_util.calculator.Variable;
import com.daoshengwanwu.math_util.calculator.exception.VariableNotSetException;
import com.graduation_project.android.algebrablade.model.CurveSource;
import com.graduation_project.android.algebrablade.model.CurveSourceLab;
import com.graduation_project.android.algebrablade.model.ResultSource;
import com.graduation_project.android.algebrablade.views.CanvasView;

import butterknife.BindView;
import butterknife.ButterKnife;


public class GraphicActivity extends AppCompatActivity {
    @BindView(R.id.canvas_view) CanvasView mCanvasView;

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
                                       float start, float end, SparseArray<float[]> curves) {

                int key = -1;
                ResultSource resultSource = null;
                float[] pointsContainer = null;
                Variable variable = null;

                view.clearLines();
                for (int i = 0; i < mResultSourceSparseArray.size(); i++) {
                    key = mResultSourceSparseArray.keyAt(i);
                    resultSource = mResultSourceSparseArray.valueAt(i);
                    pointsContainer = curves.get(key);
                    variable = resultSource.resultGenerator.getVarAriExp().
                            getVariableAssistant().getVariable("x");

                    if (!resultSource.isDomainSet) {
                        variable.set(start, false,
                                end, false, (end - start) / 200);
                    } else if (!variable.isSet()) {
                        throw new VariableNotSetException();
                    }

                    view.addLine(key, getCurvePointsFromResultGenerator(resultSource.resultGenerator, pointsContainer));
                }
            }
        });
    }

    private float[] getCurvePointsFromResultGenerator(
            Calculator.ResultGenerator resultGenerator, float[] pointsContainer) {

        VarAriExp varAriExp = resultGenerator.getVarAriExp();
        Variable x = varAriExp.getVariableAssistant().getVariable("x");
        x.reset();

        int i = 0;
        float[] points = pointsContainer != null && pointsContainer.length == x.size() * 2 ?
                pointsContainer : new float[x.size() * 2];
        points[i++] = (float)x.curValue();
        points[i++] = (float)resultGenerator.curValue();

        while (x.hasNext()) {
            points[i++] = (float)x.nextValue();
            points[i++] = (float)resultGenerator.curValue();
        }

        return points;
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
            mResultSourceSparseArray.put(key, new ResultSource(resultGenerator, curveSource.isDomainSet));
        }
    }
}
