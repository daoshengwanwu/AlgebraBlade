package com.zhouxiuya;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVAnalytics;
import com.graduation_project.android.algebrablade.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResetpwdActivity extends AppCompatActivity {
    @BindView(R.id.btn_commit)
    Button btn_commit;
    @BindView(R.id.et_oripwd)
    EditText et_oripwd;
    @BindView(R.id.tv_oripwd)
    TextView tv_oripwd;
    @BindView(R.id.et_newpwd)
    EditText et_newpwd;
    @BindView(R.id.tv_newpwd)
    TextView tv_newpwd;

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, ResetpwdActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpwd);
        ButterKnife.bind(this);
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
