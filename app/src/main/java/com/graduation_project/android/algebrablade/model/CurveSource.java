package com.graduation_project.android.algebrablade.model;


import com.daoshengwanwu.math_util.calculator.VarAriExp;


public class CurveSource {
    public VarAriExp varAriExp;
    public boolean isDomainSet;


    public CurveSource(VarAriExp varAriExp, boolean isDomainSet) {
        this.varAriExp = varAriExp;
        this.isDomainSet = isDomainSet;
    }
}
