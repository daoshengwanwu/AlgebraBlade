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

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVSMS;
import com.avos.avoscloud.AVSMSOption;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CloudQueryCallback;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.UpdatePasswordCallback;
import com.graduation_project.android.algebrablade.R;
import com.tencent.qc.stat.common.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FindpwdActivity extends AppCompatActivity implements View.OnClickListener{
    @BindView(R.id.btn_commit)
    Button btn_commit;
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
    private Boolean isnotExit = true;
    //验证码是否有效
    private Boolean isCode = false;

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, FindpwdActivity.class);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpwd);
        ButterKnife.bind(this);
        btn_commit.setOnClickListener(this);
        btn_code.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_commit:
                attemptResetpwd();
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
        option.setOperation("找回密码");
        AVSMS.requestSMSCodeInBackground(et_phone.getText().toString(), option, new RequestMobileCodeCallback() {
            @Override
            public void done(AVException e) {
                if (null == e) {
                 /* 请求成功 */
                } else {
                 /* 请求失败 */
                    Toast.makeText(FindpwdActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isusernamenotExit(String username) {

//        //用户名是否存在
//        AVQuery<AVUser> userQuery = AVUser.getQuery(AVUser.class);
//
//        //用户名存在
//        userQuery.whereEqualTo("username", et_username.getText());
//        userQuery.findInBackground(new FindCallback<AVUser>() {
//            @Override
//            public void done(List<AVUser> list, AVException e) {
//                if(e==null){//即使查不到数据，它返回的是[]这样的符号，所以用这样的符号进行判断
//                   list.size();
//
//                }else{
//                    e.printStackTrace();
//                }
//            }
//        });
        AVQuery<AVObject> queryuser = new AVQuery<>("Users");
        queryuser.whereEqualTo("username",username);
        queryuser.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(list.size()!=0){
                    isnotExit = false;
                }
                else
                    isnotExit = true;
            }
        });

        return isnotExit;
    }
    //设置密码
    private void attemptResetpwd() {
        tv_username.setText("");
        tv_pwd.setText("");
        tv_conpwd.setText("");
        tv_phone.setText("");
        String username = et_username.getText().toString();
        String password = et_pwd.getText().toString();
        String cpassword = et_conpwd.getText().toString();
        String phone = et_phone.getText().toString();
        if (!TextUtils.isEmpty(username)) {
            tv_pwd.setText(getString(R.string.error_field_required));
        }
        if(isusernamenotExit(username)){
            tv_username.setText("username does not exit");
        }
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            tv_pwd.setText(getString(R.string.error_invalid_password));
        }
        if (!cpassword.equals(password)) {
            tv_conpwd.setText("error");
        }
        if (!TextUtils.isEmpty(phone) && !isMoiblephoneValid(phone)) {
            tv_phone.setText(getString(R.string.error_invalid_phone));
        }
        if(!isCodeVailid()){

        }
        else{
            AVUser.resetPasswordBySmsCodeInBackground(et_code.getText().toString(), password, new UpdatePasswordCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        FindpwdActivity.this.finish();
                    } else {
                        Toast.makeText(FindpwdActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    private boolean isMoiblephoneValid(String phone) {
        return phone.length() == 11;
    }

    private boolean isPasswordValid(String password) {
        //密码大于6位
        return password.length() > 5;
    }



    //判断验证码是否正确
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
                    Toast.makeText(FindpwdActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        return isCode;
    }

}
