package com.zhouxiuya;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.graduation_project.android.algebrablade.GraphicEditActivity;
import com.graduation_project.android.algebrablade.R;
import com.zhouxiuya.adapter.Calc_ListViewAdapter;
import com.zhouxiuya.adapter.KeyboardpagerAdapter;
import com.zhouxiuya.util.Calculation;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhouxiuya on 2018/3/18.
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private List<String> func_list;
    private ArrayAdapter<String> arr_adapter;
    //listview 满足多个式子计算
    @BindView(R.id.lv_calc)
    ListView lv_calc;
    private ArrayList<Calculation> cal_clist = new ArrayList<Calculation>();
    private Calc_ListViewAdapter calc_adapter;
    //viewpager
    @BindView(R.id.vpager_kb)
    ViewPager vpager_kb;
    private ArrayList<View> vpagers;
    private KeyboardpagerAdapter myAdapter;
    private List<ImageView> mDots;//定义一个集合存储三个dot
    private int oldPosition;//记录当前点的位置。
    //view1
    private Button btn_a;
    private Button btn_xgen;
    private Button btn_gcd;
    private Button btn_solve;
    private Button btn_b;
    private Button btn_ln;
    private Button btn_mod;
    private Button btn_taylor;
    private Button btn_c;
    private Button btn_log;
    private Button btn_jiecheng;
    private Button btn_int;
    private Button btn_equal;
    private Button btn_exp;
    private Button btn_e;
    private Button btn_diff;
    private Button btn_dou;
    private Button btn_X;
    private Button btn_nCr;
    private Button btn_nPr;
    //view2
    private Button btn_gen;
    private Button btn_X2;
    private Button btn_7;
    private Button btn_4;
    private Button btn_1;
    private Button btn_0;
    private Button btn_left;
    private Button btn_sin1;
    private Button btn_8;
    private Button btn_5;
    private Button btn_2;
    private Button btn_dian;
    private Button btn_right;
    private Button btn_cos1;
    private Button btn_9;
    private Button btn_6;
    private Button btn_3;
    private Button btn_E;
    private Button btn_zuokuo;
    private Button btn_cifang;
    private Button btn_chu;
    private Button btn_cheng;
    private Button btn_jia;
    private Button btn_jian;
    private Button btn_youkuo;
    private Button btn_pi;
    private Button btn_ans;
    private Button btn_del;
    private Button btn_ok;
    //view3
    private Button btn_sin2;
    private Button arcsin;
    private Button sinh;
    private Button arcsinh;
    private Button btn_cos2;
    private Button btn_arccos;
    private Button btn_cosh;
    private Button btn_arccosh;
    private Button btn_tan;
    private Button btn_arctan;
    private Button btn_tanh;
    private Button btn_arctanh;
    private Button btn_abs;
    private Button btn_g;
    private Button btn_floor;
    private Button btn_frac;

    private Button btn_login;

    private TextView tv_username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
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

        //禁用系统软键盘
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);


    }

    @SuppressLint("MissingSuperCall")
    public void onResume(){
        super.onResume();
    }
    //初始化侧滑菜单
    public void initDrawerlayout() {
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

        //设置headerlayout
        setHeaderView(navigationView);
    }

    //设置headerlayout
    public void setHeaderView(NavigationView navigationView){
        //左侧菜单设置headerLayout
        //首先判断是否登录，未登录将headerLayout设置成nav_header_main_logout，设置按钮监听事件
        //已登录将headerLayout设置成nav_header_main_login，获取头像，昵称
        View headerLayout = null;
        if (AVUser.getCurrentUser() != null) {
            headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main_login);
            tv_username = (TextView)headerLayout.findViewById(R.id.tv_username);
            tv_username.setText(AVUser.getCurrentUser().getUsername());

        }else{
            headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main_logout);
            btn_login = (Button)headerLayout.findViewById(R.id.btn_login);
            btn_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(LoginActivity.newIntent(MainActivity.this));
                }
            });
        }
    }

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, MainActivity.class);
        return intent;
    }

    //初始化键盘viewpager
    private void initViewpager() {
//        vpager_kb = (ViewPager) findViewById(R.id.vpager_kb);
        vpagers = new ArrayList<View>();
        View view1 = getLayoutInflater().inflate(R.layout.view1, null, false);
        View view2 = getLayoutInflater().inflate(R.layout.view2, null, false);
        View view3 = getLayoutInflater().inflate(R.layout.view3, null, false);
        vpagers.add(view1);
        vpagers.add(view2);
        vpagers.add(view3);

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
        //设置键盘点击
        btn_ok = (Button) view2.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calc_adapter.addItem();
                calc_adapter.notifyDataSetChanged();
            }
        });
    }


    //设置viewpager默认页
    public void setView(int position) {
        //先强制设定跳转到指定页面
        myAdapter.setView(vpager_kb, position);

        //然后调用下面的函数刷新数据
        myAdapter.notifyDataSetChanged();
        //再调用setCurrentItem()函数设置一次
        vpager_kb.setCurrentItem(1);
    }

    public void initDots() {
        //初始化三个dot
        mDots = new ArrayList<ImageView>();
        ImageView dotFirst = (ImageView) findViewById(R.id.dot_first);
        ImageView dotFSecond = (ImageView) findViewById(R.id.dot_second);
        ImageView dotThird = (ImageView) findViewById(R.id.dot_third);
        mDots.add(dotFirst);
        mDots.add(dotFSecond);
        mDots.add(dotThird);
        oldPosition = 1;
        mDots.get(oldPosition).setImageResource(R.drawable.dot_focused);
    }

    private void initCalcList() {
        calc_adapter = new Calc_ListViewAdapter(this, cal_clist);
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
            if(AVUser.getCurrentUser()!=null){
                startActivity(CollectionActivity.newIntent(MainActivity.this));
            }else{
                Toast.makeText(MainActivity.this, "请登录！", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.reset_pwd) {
            if(AVUser.getCurrentUser()!=null){
                startActivity(ResetpwdActivity.newIntent(MainActivity.this));
            }else{
                Toast.makeText(MainActivity.this, "请登录！", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.exit) {
            if (AVUser.getCurrentUser() != null){
                AVUser.logOut();
            }else {
                Toast.makeText(MainActivity.this, "请登录！", Toast.LENGTH_SHORT).show();
            }
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
        switch (id) {
            case R.id.action_bai: {
                startActivity(GraphicEditActivity.newIntent(this));
            }break;
            case R.id.action_clear:{
                calc_adapter.clearListview();
                calc_adapter.notifyDataSetChanged();
            }break;

            default: break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getId() {

    }
}
