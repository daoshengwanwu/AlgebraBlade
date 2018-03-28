package com.zhouxiuya.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;


import com.graduation_project.android.algebrablade.R;
import com.zhouxiuya.util.Calculation;

import java.util.ArrayList;


/**
 * Created by zhouxiuya on 2018/3/14.
 */

public class Calc_ListViewAdapter extends BaseAdapter{
    private EditText editText;
    private Context context;
    private ArrayList<Calculation> calculations = new ArrayList<Calculation>();
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        view= LayoutInflater.from(context).inflate(R.layout.calc_item,null);

        final Calculation data = calculations.get(i);
        EditText calc_in=(EditText)view.findViewById(R.id.et_calcin);
        TextView calc_out=(TextView)view.findViewById(R.id.tv_calcout);
        calc_in.setHint(data.getCalc_in());
        calc_out.setText(data.getCalc_out());
        calc_in.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                data.setCalc_in(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }
    //添加item数据
    public void addItem(){
        if (calculations!=null){
            calculations.add(new Calculation(getCount(),"",""));
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
    //关闭软键盘
    public void closeKeyboard(){
        editText=(EditText)LayoutInflater.from(context).inflate(R.layout.calc_item,null).findViewById(R.id.et_calcin);
        editText.setInputType(InputType.TYPE_NULL);
    }
}
