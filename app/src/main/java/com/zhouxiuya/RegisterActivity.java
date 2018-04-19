package com.zhouxiuya;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVSMS;
import com.avos.avoscloud.AVSMSOption;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.LogUtil;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.SignUpCallback;
import com.graduation_project.android.algebrablade.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.btn_register)
    Button btn_register;
    @BindView(R.id.et_username)
    EditText et_username;
    @BindView(R.id.tv_username)
    TextView tv_username;
    @BindView(R.id.et_pwd)
    EditText et_pwd;
    @BindView(R.id.tv_pwd)
    TextView tv_pwd;
    @BindView(R.id.et_conpwd)
    EditText et_conpwd;
    @BindView(R.id.tv_conpwd)
    TextView tv_conpwd;
    @BindView(R.id.et_phone)
    EditText et_phone;
    @BindView(R.id.tv_phone)
    TextView tv_phone;
    @BindView(R.id.et_code)
    EditText et_code;
    @BindView(R.id.btn_code)
    Button btn_code;
    //用户名是否存在
    private Boolean isExit = true;
    //验证码是否有效
    private Boolean isCode = false;


    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, RegisterActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        btn_register.setOnClickListener(this);
        btn_code.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                attemptRegister();
                break;
            case R.id.btn_code:
                sendSmscode();
                break;
            default:
                break;

        }
    }

    //调用接口发送验证短信
    private void sendSmscode() {
        AVSMSOption option = new AVSMSOption();
        option.setTtl(10);                     // 验证码有效时间为 10 分钟
        option.setApplicationName("AlgebraBlade");
        option.setOperation("Register");
        AVSMS.requestSMSCodeInBackground(et_phone.getText().toString(), option, new RequestMobileCodeCallback() {
            @Override
            public void done(AVException e) {
                if (null == e) {
                 /* 请求成功 */
                } else {
                 /* 请求失败 */
                 Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //验证用户输入的验证码是否有效
    private boolean isCodeVailid(){
        AVSMS.verifySMSCodeInBackground(et_code.getText().toString(), et_phone.getText().toString(), new AVMobilePhoneVerifyCallback() {
            @Override
            public void done(AVException e) {
                if (null == e) {
                /* 验证成功 */
                isCode=true;
                } else {
                /* 验证失败 */
                isCode=false;
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        return isCode;
    }

    private void attemptRegister() {
        tv_username.setText("");
        tv_pwd.setText("");
        tv_conpwd.setText("");
        tv_phone.setText("");

        String username = et_username.getText().toString();
        String password = et_pwd.getText().toString();
        String cpassword = et_conpwd.getText().toString();
        String phone = et_phone.getText().toString();
        String code = et_code.getText().toString();

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            tv_pwd.setText(getString(R.string.error_invalid_password));
        }

        if (TextUtils.isEmpty(username)) {
            tv_username.setText(getString(R.string.error_field_required));
        }
//        if (isusernameValid(username)) {
//            tv_username.setText("existed");
//        }
        if (!cpassword.equals(password)) {
            tv_conpwd.setText("error");
        }
        if (!TextUtils.isEmpty(phone) && !isMoiblephoneValid(phone)) {
            tv_phone.setText(getString(R.string.error_invalid_phone));
        }
//        if(!isCodeVailid()){
//            Toast.makeText(RegisterActivity.this,"验证码错误!", Toast.LENGTH_SHORT).show();
//        }
        else {
            AVUser user = new AVUser();// 新建 AVUser 对象实例
            user.setUsername(username);// 设置用户名
            user.setPassword(password);// 设置密码
            user.setMobilePhoneNumber(phone);// 设置手机
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        // 注册成功，把用户对象赋值给当前用户 AVUser.getCurrentUser()
                        RegisterActivity.this.finish();
                        startActivity(MainActivity.newIntent(RegisterActivity.this));
                        //finish LoginActivity
                    } else {
                        // 失败的原因可能有多种，常见的是用户名已经存在。
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    private boolean isMoiblephoneValid(String phone) {
        return phone.length() == 11;
    }

    private boolean isusernameValid(String username) {
        //用户名是否存在
        AVQuery<AVObject> queryName = new AVQuery<>("User");
        //用户名存在
        queryName.whereEqualTo("username", username);
        queryName.countInBackground(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                if (i==0) {
                    isExit = false;
                } else {
                    isExit = true;
                }
            }
        });

        return isExit;
    }

    private boolean isPasswordValid(String password) {
        //密码大于6位
        return password.length() > 5;
    }
}
