package com.zhouxiuya.adapter;


import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.daoshengwanwu.math_util.calculator.Calculator;
import com.graduation_project.android.algebrablade.R;
import com.graduation_project.android.algebrablade.views.custom_keyboard.CustomEditText;
import com.zhouxiuya.util.Calculation;

import java.util.ArrayList;


public class Calc_ListViewAdapter extends BaseAdapter{
    private Context context;
    private Calculator mCalculator = new Calculator();
    private ArrayList<Calculation> calculations;
    private int mFocusPosition;


    public Calc_ListViewAdapter(Context mcontext, ArrayList<Calculation> mcalculations) {
        context=mcontext;
        calculations=mcalculations;
    }

    @Override
    public int getCount() {
        return calculations.size();
    }

    @Override
    public Object getItem(int i) {
        return calculations.get(i);
    }

    @Override
    public long getItemId(int i) {
        return calculations.get(i).getNo();
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.item_calc, null);
        final CustomEditText calc_in = view.findViewById(R.id.et_calcin);
        final TextView calc_out = view.findViewById(R.id.tv_calcout);

        final Calculation data = (Calculation)getItem(i);
        calc_in.setText(data.getCalc_in());
        calc_out.setText(data.getCalc_out());

        calc_in.addTextChangedListener(new TextWatcher() {
            private String beforeText = null;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                beforeText = charSequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                data.setCalc_in(charSequence.toString());
                try {
                    double result = mCalculator.calculate(charSequence.toString());
                    calc_out.setText(result + "");
                    data.setCalc_out(result + "");
                } catch (RuntimeException e) {
                    calc_out.setText("NaN");
                    data.setCalc_out("NaN");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        calc_in.setExtraOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    mFocusPosition = i;
                }
            }
        });
        calc_in.requestFocus();

        return view;
    }
    //添加item数据
    public void addItem(){
        if (calculations!=null){
            calculations.add(new Calculation(getCount(),"",""));
            notifyDataSetChanged();
        }
    }
    //删除item数据
    public void deleItem(){
        if(calculations!=null&&calculations.size()>1){
            calculations.remove(calculations.size()-1);
        }
    }
    //清空listview
    public void clearListview(){
        while (calculations!=null&&calculations.size()>1){
            calculations.remove(calculations.size()-1);
        }
        calculations.get(0).setCalc_in("");
        calculations.get(0).setCalc_out("");
    }

    public void removeFocusItem() {
        if (mFocusPosition > 0) {
            calculations.remove(mFocusPosition);
            notifyDataSetChanged();
        }
    }
}
