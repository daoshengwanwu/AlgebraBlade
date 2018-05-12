package com.graduation_project.android.algebrablade;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVAnalytics;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.graduation_project.android.algebrablade.utils.Quadrature.getValue;

public class QuadratureActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.et_beijihanshu)
    EditText et_beijihanshu;
    @BindView(R.id.et_jifenshangxian)
    EditText et_jifenshangxian;
    @BindView(R.id.et_jifenxiaxian)
    EditText et_jifenxiaxian;
    @BindView(R.id.btn_qiujifen)
    Button btn_qiujifen;
    @BindView(R.id.tv_jifenjieguo)
    TextView tv_jifenjieguo;
    @BindView(R.id.btn_qingchuneirong)
    Button btn_qingchuneirong;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quadrature2);
        ButterKnife.bind(this);

        btn_qingchuneirong.setOnClickListener(this);
        btn_qiujifen.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_qiujifen:
                getResult();
                break;
            case R.id.btn_qingchuneirong:
                cleanText();
                break;
            default:
                break;

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
            } break;

            default: return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void getResult(){
        final String beijihanshu =  et_beijihanshu.getText().toString();
        final String jifenshangxian =  et_jifenshangxian.getText().toString();
        final String jifenxiaxian =  et_jifenxiaxian.getText().toString();
        int a = Integer.parseInt(jifenshangxian );
        int b= Integer.parseInt(jifenxiaxian );
        if(beijihanshu ==null || beijihanshu.isEmpty()||jifenshangxian ==null || jifenshangxian.isEmpty()||jifenxiaxian ==null || jifenxiaxian.isEmpty()){
            tv_jifenjieguo.setText("请输入完整");
        }else{
            double jifenzhi=getValue(a,b,beijihanshu);
            tv_jifenjieguo.setText(jifenzhi+"");
            System.out.println(beijihanshu);
            System.out.println(a);
            System.out.println(b);
            System.out.println(jifenzhi);
        }

    }

    private  void cleanText(){
        et_beijihanshu.setText("");
        et_jifenshangxian.setText("");
        et_jifenxiaxian.setText("");
        tv_jifenjieguo.setText("");

    }

}
