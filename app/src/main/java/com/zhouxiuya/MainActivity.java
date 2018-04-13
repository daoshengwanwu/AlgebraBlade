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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.graduation_project.android.algebrablade.GraphicEditActivity;
import com.graduation_project.android.algebrablade.R;
import com.zhouxiuya.adapter.Calc_ListViewAdapter;
import com.zhouxiuya.adapter.KeyboardpagerAdapter;
import com.zhouxiuya.util.Calculation;
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
    private List<ImageView> mDots;//定义一个集合存储三个dot
    private int oldPosition;//记录当前点的位置。

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 测试 SDK 是否正常工作的代码
        AVObject testObject = new AVObject("TestObject");
        testObject.put("words","Hello World!");
        testObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if(e == null){
                    Log.d("saved","success!");
                }
            }
        });

        //listview
        initCalcList();//初始化数据
        //初始化viewpager
        initViewpager();
        //按home键之后退回到桌面，在次点击程序图标避免再次重新启动程序
        if (!isTaskRoot()) {
            finish();
            return;
        }
        //初始化侧滑菜单
        initDrawerlayout();


    }

    //初始化侧滑菜单
    public void initDrawerlayout(){
        //侧滑菜单
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //左侧菜单menu设置监听
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //左侧菜单设置headerLayout
        //首先判断是否登录，未登录将headerLayout设置成nav_header_main_logout，设置按钮监听事件
        //已登录将headerLayout设置成nav_header_main_login，获取头像，昵称
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main_logout);
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
        //初始化三个dot
        initDots();

        myAdapter = new KeyboardpagerAdapter(vpagers);
        vpager_kb.setAdapter(myAdapter);

        vpager_kb.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mDots.get(oldPosition).setImageResource(R.drawable.dot_normal);
                mDots.get(position).setImageResource(R.drawable.dot_focused);
                oldPosition = position;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //设置view2位默认页
        setView(1);
    }
    //设置viewpager默认页
    public void setView(int position){
        //先强制设定跳转到指定页面
        myAdapter.setView(vpager_kb,position);

        //然后调用下面的函数刷新数据
        myAdapter.notifyDataSetChanged();
        //再调用setCurrentItem()函数设置一次
        vpager_kb.setCurrentItem(1);
    }

    public void initDots(){
        //初始化三个dot
        mDots = new ArrayList<ImageView>();
        ImageView dotFirst = (ImageView) findViewById(R.id.dot_first);
        ImageView dotFSecond = (ImageView) findViewById(R.id.dot_second);
        ImageView dotThird = (ImageView) findViewById(R.id.dot_third);
        mDots.add(dotFirst);
        mDots.add(dotFSecond);
        mDots.add(dotThird);
        oldPosition =1;
        mDots.get(oldPosition).setImageResource(R.drawable.dot_focused);
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

        if (id == R.id.collection) {
            // Handle the collection action
            Toast.makeText(MainActivity.this, "请登录！",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.reset_pwd) {
            Toast.makeText(MainActivity.this, "请登录！",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.exit) {
            Toast.makeText(MainActivity.this, "请登录！",Toast.LENGTH_SHORT).show();
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

        switch (id) {
            case R.id.action_bai: {
                startActivity(GraphicEditActivity.newIntent(this));
            } break;

            default: break;
        }

        return super.onOptionsItemSelected(item);
    }
}
