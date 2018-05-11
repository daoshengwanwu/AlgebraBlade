package com.graduation_project.android.algebrablade;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.daoshengwanwu.math_util.calculator.Calculator;
import com.daoshengwanwu.math_util.calculator.VarAriExp;
import com.daoshengwanwu.math_util.calculator.Variable;
import com.daoshengwanwu.math_util.calculator.VariableAssistant;
import com.daoshengwanwu.math_util.calculator.exception.OperandOutOfBoundsException;
import com.daoshengwanwu.math_util.calculator.exception.VariableNotSetException;
import com.graduation_project.android.algebrablade.model.CurveSource;
import com.graduation_project.android.algebrablade.model.CurveSourceLab;
import com.graduation_project.android.algebrablade.model.ResultSource;
import com.graduation_project.android.algebrablade.utils.LeastSquareMethodFromApache;
import com.graduation_project.android.algebrablade.views.CanvasView;
import com.graduation_project.android.algebrablade.views.CanvasView.Curve;
import com.zhouxiuya.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class GraphicActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSION = 0;

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

        mCanvasView.setOnSelectPointsFinishedListener(
                new CanvasView.OnSelectPointsFinishedListener() {

            @Override
            public void onPointsSelected(CanvasView view, ArrayList<Float> points) {
                String fittingExp = LeastSquareMethodFromApache.
                        testLeastSquareMethodFromApache(points, 0.9);

                VariableAssistant assistant = new VariableAssistant()
                        .addVariable("x", points.get(0), false,
                                points.get(points.size() - 2), false,
                                points.get(2) - points.get(0));

                VarAriExp varAriExp = new VarAriExp(fittingExp, assistant);
                Calculator.ResultGenerator generator = mCalculator.calculate(varAriExp);
                ResultSource rs = new ResultSource(
                        generator, true, Color.rgb(255, 128, 128));

                mCanvasView.setFittingCurve(getCurveFromResultSource(rs, null));
                mCanvasView.refresh();
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

            case R.id.save_graphic: {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) !=
                            PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                                    PackageManager.PERMISSION_GRANTED) {

                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);

                        break;
                    }
                }

                FileUtil.saveScreenshotToLocal(this, mCanvasView.screenshot());
            } break;

            case R.id.save_and_upload_to_cloud: {
                FileUtil.saveScreenshotToCloud(this, mCanvasView.screenshot());
            } break;

            case R.id.fitting: {
                if (mCanvasView.getState() == CanvasView.State.FREE) {
                    mCanvasView.setState(CanvasView.State.GET_POINTS);
                    mCanvasView.refresh();
                } else if (mCanvasView.getState() == CanvasView.State.GET_POINTS) {
                    mCanvasView.setState(CanvasView.State.FREE);
                    mCanvasView.refresh();
                }
            } break;

            case android.R.id.home: {
                finish();
            } break;

            default: break;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CODE_PERMISSION: {
                boolean isGranted = true;

                for (int rst : grantResults) {
                    if (rst != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this,
                                "请允许存储权限，否则无法保存至本地", Toast.LENGTH_SHORT).show();
                        isGranted = false;
                    }
                }

                if (isGranted) {
                    FileUtil.saveScreenshotToLocal(this, mCanvasView.screenshot());
                }
            } break;

            default: break;
        }
    }

    private Curve getCurveFromResultSource(ResultSource resultSource, Curve recycleCurve) {
        Calculator.ResultGenerator resultGenerator = resultSource.resultGenerator;

        VarAriExp varAriExp = resultGenerator.getVarAriExp();
        Variable x = varAriExp.getVariableAssistant().getVariable("x");
        x.reset();

        if (x.size() <= 1) {
            return null;
        }

        if (recycleCurve == null) {
            recycleCurve = new Curve();
        }
        float[] points = recycleCurve.points != null && recycleCurve.points.length >= x.size() * 2 ?
                recycleCurve.points : new float[x.size() * 2 + 32];

        int i = 0;
        try {
            points[i + 1] = (float) resultGenerator.curValue();
            points[i] = (float)x.curValue();
            i += 2;
        } catch (OperandOutOfBoundsException e) {
            //do nothing
        }

        while (x.hasNext() && i < points.length - 1) {
            x.nextValue();

            try {
                points[i + 1] = (float) resultGenerator.curValue();
                points[i] = (float)x.curValue();
                i += 2;
            } catch (OperandOutOfBoundsException e) {
                //do nothing
            }
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
