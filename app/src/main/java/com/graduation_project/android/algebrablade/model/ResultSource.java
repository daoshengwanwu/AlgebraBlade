package com.graduation_project.android.algebrablade.model;


import com.daoshengwanwu.math_util.calculator.Calculator;


public class ResultSource {
    public Calculator.ResultGenerator resultGenerator;
    public boolean isDomainSet;
    public int color;


    public ResultSource(Calculator.ResultGenerator resultGenerator, boolean isDomainSet, int color) {
        this.resultGenerator = resultGenerator;
        this.isDomainSet = isDomainSet;
        this.color = color;
    }
}
