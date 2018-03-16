package com.graduation_project.android.algebrablade.model;


import com.daoshengwanwu.math_util.calculator.VarAriExp;


public class CurveSource {
    public VarAriExp varAriExp;
    public boolean isDomainSet;
    public int color;


    public CurveSource(VarAriExp varAriExp, boolean isDomainSet, int color) {
        this.varAriExp = varAriExp;
        this.isDomainSet = isDomainSet;
        this.color = color;
    }
}
