package com.zhouxiuya.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by lenovo on 2018/3/18.
 */

public class KeyboardpagerAdapter extends PagerAdapter {
    private ArrayList<View> viewLists;

    public KeyboardpagerAdapter(ArrayList<View> viewLists) {
        super();
        this.viewLists = viewLists;
    }

    @Override
    public int getCount() {
        return viewLists.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(viewLists.get(position));
        return viewLists.get(position);
    }
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewLists.get(position));
    }
    public void setView(ViewGroup container,int position){
        //先强制设定跳转到指定页面
        try {
            Field field = container.getClass().getField("mCurItem");//参数mCurItem是系统自带的
            field.setAccessible(true);
            field.setInt(container,position);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
