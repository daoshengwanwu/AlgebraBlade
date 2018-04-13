package com.graduation_project.android.algebrablade;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.daoshengwanwu.math_util.calculator.VarAriExp;
import com.daoshengwanwu.math_util.calculator.Variable;
import com.daoshengwanwu.math_util.calculator.VariableAssistant;
import com.graduation_project.android.algebrablade.model.CurveSource;
import com.graduation_project.android.algebrablade.model.CurveSourceLab;

import butterknife.BindView;
import butterknife.ButterKnife;


public class GraphicEditDialogBuilder {
    private int mId;
    private int mCurveColor;
    private CurveSource mCurveSource;
    private Context mContext;

    private View mContentView;
    @BindView(R.id.curve_id_text_view)
    TextView mIdTextView;

    @BindView(R.id.curve_color_text_view)
    TextView mColorTextView;

    @BindView(R.id.exp_input_edit_text)
    EditText mExpInputEditText;

    @BindView(R.id.is_domain_set_check_box)
    CheckBox mIsDomainSetCheckBox;

    @BindView(R.id.is_lower_open_check_box)
    CheckBox mIsLowerOpenCheckBox;

    @BindView(R.id.is_upper_open_checkbox)
    CheckBox mIsUpperOpenCheckBox;

    @BindView(R.id.delta_input_edit_text)
    EditText mDeltaEditText;

    @BindView(R.id.lower_input_edit_text)
    EditText mLowerLimitEditText;

    @BindView(R.id.upper_input_edit_text)
    EditText mUpperLimitEditText;

    private OnClickListener mOnConfirmClickListener = null;
    private DialogInterface.OnClickListener mOnCancelClickListener = null;
    private OnClickListener mOnDeleteClickListener = null;


    public GraphicEditDialogBuilder(int id, CurveSource curveSource, Context context) {
        mId = id;
        mCurveSource = curveSource != null ? curveSource : new CurveSource();
        mContext = context;

        LayoutInflater inflater = LayoutInflater.from(context);
        mContentView = inflater.inflate(R.layout.dialog_graphic_edit, null, false);
        ButterKnife.bind(this, mContentView);

        mIdTextView.setText("ID：" + mId);
        mIsDomainSetCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mDeltaEditText.setEnabled(true);
                    mIsLowerOpenCheckBox.setEnabled(true);
                    mIsUpperOpenCheckBox.setEnabled(true);
                    mLowerLimitEditText.setEnabled(true);
                    mUpperLimitEditText.setEnabled(true);
                } else {
                    mDeltaEditText.setEnabled(false);
                    mIsLowerOpenCheckBox.setEnabled(false);
                    mIsUpperOpenCheckBox.setEnabled(false);
                    mLowerLimitEditText.setEnabled(false);
                    mUpperLimitEditText.setEnabled(false);
                }
            }
        });
        if (curveSource != null) {
            mCurveColor = curveSource.color;
            boolean isDomainSet = curveSource.isDomainSet;

            mColorTextView.setBackgroundColor(mCurveColor);
            mColorTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCurveColor = CurveSourceLab.getRandomColor();
                    mColorTextView.setBackgroundColor(mCurveColor);
                }
            });
            mIsDomainSetCheckBox.setChecked(isDomainSet);

            VarAriExp varAriExp = curveSource.varAriExp;
            if (varAriExp != null) {
                String expStr = varAriExp.toString();
                mExpInputEditText.setText(expStr);
                if (isDomainSet) {
                    Variable x = varAriExp.getVariableAssistant().getVariable("x");
                    double span = x.getSpan();
                    double lowerLimit = x.getLowerLimit();
                    double upperLimit = x.getUpperLimit();

                    mDeltaEditText.setText("" + span);
                    mLowerLimitEditText.setText("" + lowerLimit);
                    mUpperLimitEditText.setText("" + upperLimit);
                }
            }
        }
    }

    public Dialog build() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("编辑函数");
        builder.setView(mContentView);
        if (mOnConfirmClickListener != null) {
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    int id = mId;
                    mOnConfirmClickListener.onClick(id, getCurveSource());
                }
            });
        }
        builder.setNegativeButton("取消", mOnCancelClickListener);
        if (mOnDeleteClickListener != null) {
            builder.setNeutralButton("删除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    int id = mId;
                    mOnDeleteClickListener.onClick(id, getCurveSource());
                }
            });
        }

        return builder.create();
    }

    public GraphicEditDialogBuilder setOnConfirmClickListener(OnClickListener listener) {
        mOnConfirmClickListener = listener;

        return this;
    }

    public GraphicEditDialogBuilder setOnCancelClickListener(DialogInterface.OnClickListener listener) {
        mOnCancelClickListener = listener;

        return this;
    }

    public GraphicEditDialogBuilder setOnDeleteClickListener(OnClickListener listener) {
        mOnDeleteClickListener = listener;

        return this;
    }

    private CurveSource getCurveSource() {
        String expStr = mExpInputEditText.getText().toString();
        boolean isDomainSet = mIsDomainSetCheckBox.isChecked();
        int color = mCurveColor;

        double lowerLimit = 0.0;
        boolean isLowerOpen = false;
        double upperLimit = 0.0;
        boolean isUpperOpen = false;
        double span = 0.0;

        VariableAssistant varAssistant = new VariableAssistant();
        if (isDomainSet) {
            lowerLimit = Double.parseDouble(mLowerLimitEditText.getText().toString());
            upperLimit = Double.parseDouble(mUpperLimitEditText.getText().toString());
            isLowerOpen = mIsLowerOpenCheckBox.isChecked();
            isUpperOpen = mIsUpperOpenCheckBox.isChecked();
            span = Double.parseDouble(mDeltaEditText.getText().toString());

            varAssistant.addVariable("x", lowerLimit, isLowerOpen,
                    upperLimit, isUpperOpen, span);

        } else {
            varAssistant.addVariable("x");
        }

        mCurveSource.color = color;
        mCurveSource.isDomainSet = isDomainSet;
        mCurveSource.varAriExp = new VarAriExp(expStr, varAssistant);
        return mCurveSource;
    }


    public interface OnClickListener {
        void onClick(int id, CurveSource curveSource);
    }
}
