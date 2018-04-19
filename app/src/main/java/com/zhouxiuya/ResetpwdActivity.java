package com.zhouxiuya;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.graduation_project.android.algebrablade.R;

public class ResetpwdActivity extends AppCompatActivity {

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, ResetpwdActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpwd);
    }

    public static class CollectionActivity extends AppCompatActivity {
        public static Intent newIntent(Context packageContext) {
            Intent intent = new Intent(packageContext, CollectionActivity.class);
            return intent;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_collection);
        }
    }
}
