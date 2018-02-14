package com.graduation_project.android.algebrablade;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.daoshengwanwu.math_util.calculator.Calculator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LauncherActivity extends AppCompatActivity {
    private Calculator mCalculator = new Calculator();

    @BindView(R.id.expression_edit_text) EditText mExpInputEditText;
    @BindView(R.id.calculate_button) Button mCalculateButton;
    @BindView(R.id.result_text_view) TextView mResultTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.calculate_button)
    void onCalculateButtonClicked() {
        String expression = mExpInputEditText.getText().toString();
        String result = "";
        try {
            result += mCalculator.calculate(expression);
        } catch (RuntimeException e) {
            result = e.getMessage();
        }
        mResultTextView.setText(result);
    }
}
