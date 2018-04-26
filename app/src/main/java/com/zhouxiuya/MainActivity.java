package com.zhouxiuya;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.graduation_project.android.algebrablade.GraphicEditActivity;
import com.graduation_project.android.algebrablade.R;
import com.graduation_project.android.algebrablade.views.custom_keyboard.CustomKeyboard;
import com.graduation_project.android.algebrablade.views.custom_keyboard.KeyboardPage1Fragment;
import com.zhouxiuya.adapter.Calc_ListViewAdapter;
import com.zhouxiuya.util.Calculation;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_LOGIN_ACTIVITY = 0;

    @BindView(R.id.lv_calc)
    ListView lv_calc;

    @BindView(R.id.custom_keyboard)
    CustomKeyboard mCustomKeyboard;

    private ArrayList<Calculation> cal_clist = new ArrayList<Calculation>();
    private Calc_ListViewAdapter calc_adapter;

    private Button btn_login;
    private TextView tv_username;


    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, MainActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //按home键之后退回到桌面，在次点击程序图标避免再次重新启动程序
        if (!isTaskRoot()) {
            finish();
            return;
        }

        ButterKnife.bind(this);

        initCustomKeyboard(); //初始化自定义键盘
        initCalcList(); //初始化数据
        initDrawerlayout(); //初始化侧滑菜单

        //禁用系统软键盘
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        updateHeaderView();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.collection) {
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
                updateHeaderView();
            }else {
                Toast.makeText(MainActivity.this, "请登录！", Toast.LENGTH_SHORT).show();
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

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

    private void updateHeaderView(){
        NavigationView navigationView = findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);
        if (headerView != null) {
            navigationView.removeHeaderView(headerView);
        }

        View headerLayout = null;
        if (AVUser.getCurrentUser() != null) {
            headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main_login);
            tv_username = headerLayout.findViewById(R.id.tv_username);
            tv_username.setText(AVUser.getCurrentUser().getUsername());

        }else{
            headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main_logout);
            btn_login = headerLayout.findViewById(R.id.btn_login);
            btn_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivityForResult(LoginActivity.newIntent(MainActivity.this),
                            REQUEST_LOGIN_ACTIVITY);
                }
            });
        }
    }

    private void initDrawerlayout() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        updateHeaderView();
    }

    private void initCalcList() {
        calc_adapter = new Calc_ListViewAdapter(this, cal_clist);
        lv_calc.setAdapter(calc_adapter);
        calc_adapter.addItem();
    }

    private void initCustomKeyboard() {
        mCustomKeyboard.init(getSupportFragmentManager(),
                CustomKeyboard.KeyboardType.TYPE_CALCULATOR);
        mCustomKeyboard.setOnEnterClickListener(new KeyboardPage1Fragment.OnEnterClickListener() {
            @Override
            public void onClick(String expStr) {
                calc_adapter.addItem();
            }
        });
        mCustomKeyboard.setOnDelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calc_adapter.removeFocusItem();
            }
        });
    }
}
