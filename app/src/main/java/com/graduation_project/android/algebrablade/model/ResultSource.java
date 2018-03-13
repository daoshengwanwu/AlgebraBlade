package com.graduation_project.android.algebrablade.model;


import com.daoshengwanwu.math_util.calculator.Calculator;


public class ResultSource {
    public Calculator.ResultGenerator resultGenerator;
    public boolean isDomainSet;


    public ResultSource(Calculator.ResultGenerator resultGenerator, boolean isDomainSet) {
        this.resultGenerator = resultGenerator;
        this.isDomainSet = isDomainSet;
    }
}
