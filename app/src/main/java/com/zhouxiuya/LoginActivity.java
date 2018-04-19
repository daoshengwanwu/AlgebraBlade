package com.zhouxiuya;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.graduation_project.android.algebrablade.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.btn_login)
    Button btn_login;
    @BindView(R.id.et_username)
    EditText et_username;
    @BindView(R.id.tv_username)
    TextView tv_username;
    @BindView(R.id.et_pwd)
    EditText et_pwd;
    @BindView(R.id.tv_pwd)
    TextView tv_pwd;
    @BindView(R.id.tv_register)
    TextView tv_register;
    @BindView(R.id.tv_forgot)
    TextView tv_forgot;

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, LoginActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        if (AVUser.getCurrentUser() != null) {
            startActivity(MainActivity.newIntent(this));
            LoginActivity.this.finish();
        }
        btn_login.setOnClickListener(this);
        tv_register.setOnClickListener(this);
        tv_forgot.setOnClickListener(this);

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                attemptLogin();
                break;
            case R.id.tv_register:
                startActivity(RegisterActivity.newIntent(LoginActivity.this));
                break;
            case R.id.tv_forgot:
                startActivity(FindpwdActivity.newIntent(LoginActivity.this));
            default:
                break;

        }
    }

    private void attemptLogin() {
        tv_username.setText("");
        tv_pwd.setText("");

        final String username = et_username.getText().toString();
        final String password = et_pwd.getText().toString();

        //密码长度不够
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            tv_pwd.setText(R.string.error_invalid_password);
        }


        //用户名判空
        if (TextUtils.isEmpty(username)) {
            tv_username.setText(getString(R.string.error_field_required));
        }

        AVUser.logInInBackground(username, password, new LogInCallback<AVUser>() {

            @Override
            public void done(AVUser avUser, AVException e) {
                if (e == null) {
                    LoginActivity.this.finish();
                    startActivity(MainActivity.newIntent(LoginActivity.this));
                } else {
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isPasswordValid(String password) {
        //判断密码长度是否大于等于6位
        return password.length() > 5;
    }


    @Override
    protected void onPause() {
        super.onPause();
        AVAnalytics.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AVAnalytics.onResume(this);
    }
}
