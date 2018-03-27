package com.zhouxiuya;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import com.graduation_project.android.algebrablade.R;
import com.zhouxiuya.adapter.Calc_ListViewAdapter;
import com.zhouxiuya.adapter.KeyboardpagerAdapter;
import com.zhouxiuya.util.Calculation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2018/3/18.
 */

public class MainActivity extends AppCompatActivity {
    private ArrayAdapter<String> arr_adapter;
    //listview 满足多个式子计算
    private ListView lv_calc;
    private ArrayList<Calculation> cal_clist = new ArrayList<Calculation>();
    private Calc_ListViewAdapter calc_adapter;
    private Button enter_btn;
    //viewpager
    private ViewPager vpager_kb;
    private ArrayList<View> vpagers;
    private KeyboardpagerAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化首页功能选项弹框
        //listview

        initCalcList();//初始化数据


        initViewpager();
    }

    //初始化键盘viewpager
    private void initViewpager() {
        vpager_kb=(ViewPager)findViewById(R.id.vpager_kb);
        vpagers=new ArrayList<View>();
        View view1=getLayoutInflater().inflate(R.layout.view1,null,false);
        View view2=getLayoutInflater().inflate(R.layout.view2,null,false);
        View view3=getLayoutInflater().inflate(R.layout.view3,null,false);
        vpagers.add(view1);
        vpagers.add(view2);
        vpagers.add(view3);
        enter_btn = (Button) view2.findViewById(R.id.enter_btn);
        enter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calc_adapter.addItem();
                calc_adapter.notifyDataSetChanged();
            }
        });
        myAdapter=new KeyboardpagerAdapter(vpagers);
        vpager_kb.setAdapter(myAdapter);

    }

    private void initCalcList() {
        calc_adapter = new Calc_ListViewAdapter(this, cal_clist);
        lv_calc = (ListView) findViewById(R.id.lv_calc);
        lv_calc.setAdapter(calc_adapter);
        calc_adapter.addItem();
        calc_adapter.notifyDataSetChanged();
    }


}
