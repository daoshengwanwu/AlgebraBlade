package com.zhouxiuya;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.UpdatePasswordCallback;
import com.graduation_project.android.algebrablade.R;

import javax.security.auth.callback.PasswordCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResetpwdActivity extends AppCompatActivity implements View.OnClickListener{
    @BindView(R.id.btn_commit)
    Button btn_commit;
    @BindView(R.id.et_oldpwd)
    EditText et_oldpwd;
    @BindView(R.id.tv_oldpwd)
    TextView tv_oldpwd;
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
        btn_commit.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_commit:
                attemptResetpwd();
                break;
            default:
                break;
        }

    }

    private void attemptResetpwd() {
        tv_oldpwd.setText("");
        tv_newpwd.setText("");
        final String oldpwd = et_oldpwd.getText().toString();
        final String newpwd = et_newpwd.getText().toString();
        final AVUser user = AVUser.getCurrentUser();
        final String username = user.getUsername();

        //新密码与原密码相同
        if(oldpwd == newpwd){
            tv_newpwd.setText("新密码与原密码相同");
        }
        //新密码长度不够
        if (!TextUtils.isEmpty(newpwd) && !isPasswordValid(newpwd)) {
            tv_newpwd.setText(R.string.error_invalid_password);
        }
        else{
            user.updatePasswordInBackground(oldpwd, newpwd, new UpdatePasswordCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        ResetpwdActivity.this.finish();
                    } else {
                        tv_oldpwd.setText("Incorrect password");
                    }
                }
            });
        }

    }
    private boolean isPasswordValid(String password) {
        //密码大于6位
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
