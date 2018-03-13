package com.graduation_project.android.algebrablade.model;


import android.util.SparseArray;

import com.daoshengwanwu.math_util.calculator.VarAriExp;
import com.daoshengwanwu.math_util.calculator.VariableAssistant;


public class CurveSourceLab {
    private static CurveSourceLab sInstance = new CurveSourceLab();

    private SparseArray<CurveSource> mCurveSources = new SparseArray<>();


    public static CurveSourceLab getInstance() {
        return sInstance;
    }

    private CurveSourceLab() {
        addTestData();
    }

    public void clear() {
        mCurveSources.clear();
    }

    public int size() {
        return mCurveSources.size();
    }

    public void putCurveSource(int key, CurveSource curveSource) {
        mCurveSources.put(key, curveSource);
    }

    public void removeCurveSource(int key) {
        mCurveSources.remove(key);
    }

    public CurveSource getCurveSource(int key) {
        return mCurveSources.get(key);
    }

    public SparseArray<CurveSource> getCurveSources() {
        return mCurveSources;
    }

    private void addTestData() {
        String expression = "x";
        VariableAssistant variableAssistant = new VariableAssistant().addVariable("x");
        putCurveSource(0, new CurveSource(new VarAriExp(expression, variableAssistant), false));

        expression = "x^2";
        variableAssistant = new VariableAssistant().addVariable("x");
        putCurveSource(1, new CurveSource(new VarAriExp(expression, variableAssistant), false));

        expression = "x^3";
        variableAssistant = new VariableAssistant().addVariable("x");
        putCurveSource(2, new CurveSource(new VarAriExp(expression, variableAssistant), false));

        expression = "sin(x)";
        variableAssistant = new VariableAssistant().addVariable("x");
        putCurveSource(3, new CurveSource(new VarAriExp(expression, variableAssistant), false));

        expression = "cos(x)";
        variableAssistant = new VariableAssistant().addVariable("x");
        putCurveSource(4, new CurveSource(new VarAriExp(expression, variableAssistant), false));

        expression = "|x|";
        variableAssistant = new VariableAssistant().addVariable("x");
        putCurveSource(5, new CurveSource(new VarAriExp(expression, variableAssistant), false));

        expression = "sqrt(x)";
        variableAssistant = new VariableAssistant().addVariable("x",
                0, true, 10, false, 0.01);
        putCurveSource(6, new CurveSource(new VarAriExp(expression, variableAssistant), true));

        expression = "log(2)~x";
        variableAssistant = new VariableAssistant().addVariable("x",
                0, true, 10, false, 0.01);
        putCurveSource(7, new CurveSource(new VarAriExp(expression, variableAssistant), true));

        expression = "6*sin(sin(x/3))";
        variableAssistant = new VariableAssistant().addVariable("x");
        putCurveSource(8, new CurveSource(new VarAriExp(expression, variableAssistant), false));

        expression = "3*cos(cos(x*2))";
        variableAssistant = new VariableAssistant().addVariable("x");
        putCurveSource(9, new CurveSource(new VarAriExp(expression, variableAssistant), false));
    }
}
