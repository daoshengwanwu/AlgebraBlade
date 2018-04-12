package com.graduation_project.android.algebrablade.model;


import android.graphics.Color;
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

    //添加测试曲线
    private void addTestData() {
        String expression = "x";
        VariableAssistant variableAssistant = new VariableAssistant().addVariable("x");
        putCurveSource(999, new CurveSource(
                new VarAriExp(expression, variableAssistant), false, getRandomColor()));

        expression = "x^2";
        variableAssistant = new VariableAssistant().addVariable("x");
        putCurveSource(1, new CurveSource(
                new VarAriExp(expression, variableAssistant), false, getRandomColor()));

        expression = "x^3";
        variableAssistant = new VariableAssistant().addVariable("x");
        putCurveSource(2, new CurveSource(
                new VarAriExp(expression, variableAssistant), false, getRandomColor()));

        expression = "sin(x)";
        variableAssistant = new VariableAssistant().addVariable("x");
        putCurveSource(666, new CurveSource(
                new VarAriExp(expression, variableAssistant), false, getRandomColor()));

        expression = "cos(x)";
        variableAssistant = new VariableAssistant().addVariable("x");
        putCurveSource(4, new CurveSource(
                new VarAriExp(expression, variableAssistant), false, getRandomColor()));

        expression = "|x|";
        variableAssistant = new VariableAssistant().addVariable("x");
        putCurveSource(5, new CurveSource(
                new VarAriExp(expression, variableAssistant), false, getRandomColor()));

        expression = "sqrt(x)";
        variableAssistant = new VariableAssistant().addVariable("x");
        putCurveSource(6, new CurveSource(
                new VarAriExp(expression, variableAssistant), false, getRandomColor()));

        expression = "log(2)~x";
        variableAssistant = new VariableAssistant().addVariable("x");
        putCurveSource(7, new CurveSource(
                new VarAriExp(expression, variableAssistant), false, getRandomColor()));

        expression = "6*sin(sin(x/3))";
        variableAssistant = new VariableAssistant().addVariable("x");
        putCurveSource(8, new CurveSource(
                new VarAriExp(expression, variableAssistant), false, getRandomColor()));

        expression = "3*cos(cos(x*2))";
        variableAssistant = new VariableAssistant().addVariable("x");
        putCurveSource(9, new CurveSource(
                new VarAriExp(expression, variableAssistant), false, getRandomColor()));
    }

    private int getRandomColor() {
        int r = (int)(Math.random() * 255);
        int g = (int)(Math.random() * 255);
        int b = (int)(Math.random() * 255);

        return Color.rgb(r, g, b);
    }
}
