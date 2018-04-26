package com.graduation_project.android.algebrablade;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.graduation_project.android.algebrablade.views.custom_keyboard.CustomKeyboard;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LauncherActivity extends AppCompatActivity {
    @BindView(R.id.custom_keyboard) CustomKeyboard mCustomKeyboard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        ButterKnife.bind(this);

        mCustomKeyboard.init(getSupportFragmentManager(),
                CustomKeyboard.KeyboardType.TYPE_CALCULATOR);
    }
}
