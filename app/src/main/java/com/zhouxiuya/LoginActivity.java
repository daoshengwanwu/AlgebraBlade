package com.zhouxiuya;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import com.graduation_project.android.algebrablade.GraphicEditActivity;
import com.graduation_project.android.algebrablade.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.btn_login) Button btn_login;
    @BindView(R.id.et_username) Button et_username;
    @BindView(R.id.et_pwd) Button et_pwd;


    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, LoginActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }
}
