package com.zhouxiuya.util;

/**
 * 首页计算类
 * Created by zhouxiuya on 2018/3/14.
 */

public class Calculation {
    private int no;
    private String calc_in;
    private String calc_out;

    public Calculation(int no, String calc_in, String calc_out) {
        this.no = no;
        this.calc_in = calc_in;
        this.calc_out = calc_out;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getCalc_in() {
        return calc_in;
    }

    public void setCalc_in(String calc_in) {
        this.calc_in = calc_in;
    }

    public String getCalc_out() {
        return calc_out;
    }

    public void setCalc_out(String calc_out) {
        this.calc_out = calc_out;
    }
}
