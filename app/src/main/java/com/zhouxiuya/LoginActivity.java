package com.zhouxiuya;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.graduation_project.android.algebrablade.R;

import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }
}
