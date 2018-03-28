package com.zhouxiuya;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.graduation_project.android.algebrablade.R;
import com.zhouxiuya.adapter.Calc_ListViewAdapter;
import com.zhouxiuya.adapter.KeyboardpagerAdapter;
import com.zhouxiuya.util.Calculation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouxiuay on 2018/3/18.
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private List<String> func_list;
    private ArrayAdapter<String> arr_adapter;
    //listview 满足多个式子计算
    private ListView lv_calc;
    private ArrayList<Calculation> cal_clist = new ArrayList<Calculation>();
    private Calc_ListViewAdapter calc_adapter;
    //viewpager
    private ViewPager vpager_kb;
    private ArrayList<View> vpagers;
    private KeyboardpagerAdapter myAdapter;
    private Button btn_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //listview

        initCalcList();//初始化数据
//        enter_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                calc_adapter.addItem();
//                calc_adapter.notifyDataSetChanged();
//            }
//        });

        initViewpager();
        if (!isTaskRoot()) {
            finish();
            return;
        }

        //侧滑菜单
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    //初始化键盘viewpager
    private void initViewpager() {
        vpager_kb = (ViewPager) findViewById(R.id.vpager_kb);
        vpagers = new ArrayList<View>();
        View view1 = getLayoutInflater().inflate(R.layout.view1, null, false);
        View view2 = getLayoutInflater().inflate(R.layout.view2, null, false);
        View view3 = getLayoutInflater().inflate(R.layout.view3, null, false);
        vpagers.add(view1);
        vpagers.add(view2);
        vpagers.add(view3);
        btn_ok = (Button) view2.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calc_adapter.addItem();
                calc_adapter.notifyDataSetChanged();
            }
        });
        myAdapter = new KeyboardpagerAdapter(vpagers);
        vpager_kb.setAdapter(myAdapter);
        //设置view2位默认页
        setView2();
    }
    //设置view2位默认页
    public void setView2(){
        //先强制设定跳转到指定页面
        try {
            Field field = vpager_kb.getClass().getField("mCurItem");//参数mCurItem是系统自带的
            field.setAccessible(true);
            field.setInt(vpager_kb,1);
        }catch (Exception e){
            e.printStackTrace();
        }

        //然后调用下面的函数刷新数据
        myAdapter.notifyDataSetChanged();
        //再调用setCurrentItem()函数设置一次
        vpager_kb.setCurrentItem(1);
    }

    private void initCalcList() {
        lv_calc = (ListView) findViewById(R.id.lv_calc);
        //enter_btn = (Button) findViewById(R.id.enter_btn);
        calc_adapter = new Calc_ListViewAdapter(this, cal_clist);
        lv_calc = (ListView) findViewById(R.id.lv_calc);
        lv_calc.setAdapter(calc_adapter);
        calc_adapter.addItem();
        calc_adapter.notifyDataSetChanged();
    }


    //左侧菜单
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    //右侧菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //设置右上角弹框id
        if (id == R.id.action_clear) {
            calc_adapter.clearListview();
            calc_adapter.notifyDataSetChanged();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
