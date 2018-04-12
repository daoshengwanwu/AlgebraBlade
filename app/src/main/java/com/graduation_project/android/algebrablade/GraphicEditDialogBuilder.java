package com.graduation_project.android.algebrablade;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.daoshengwanwu.math_util.calculator.VarAriExp;
import com.daoshengwanwu.math_util.calculator.VariableAssistant;
import com.graduation_project.android.algebrablade.model.CurveSource;


public class GraphicEditDialogBuilder {
    private int mId;
    private CurveSource mCurveSource;
    private Context mContext;

    private View mContentView;

    private OnClickListener mOnConfirmClickListener = null;
    private DialogInterface.OnClickListener mOnCancelClickListener = null;
    private OnClickListener mOnDeleteClickListener = null;


    public GraphicEditDialogBuilder(int id, CurveSource curveSource, Context context) {
        mId = id;
        mCurveSource = curveSource != null ? curveSource : new CurveSource();
        mContext = context;

        LayoutInflater inflater = LayoutInflater.from(context);
        View contentView = inflater.inflate(R.layout.dialog_graphic_edit, null, false);
        /**
         * 在此获取View到成员中, 并写入id与CurveSource中的数据
         */
    }

    public Dialog build() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("编辑函数");
        builder.setView(mContentView);
        if (mOnConfirmClickListener != null) {
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    int id = -1; //从成员中获取id
                    mOnConfirmClickListener.onClick(id, getCurveSource());
                }
            });
        }
        builder.setNegativeButton("取消", mOnCancelClickListener);
        if (mOnDeleteClickListener != null) {
            builder.setNeutralButton("删除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    int id = -1; //从成员中获取id
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
        /**
         * 从成员View中获取以下值
         */

        String expStr = "";
        boolean isDomainSet = false;
        int color = 0xff00ffff;
        double lowerLimit = 0.0;
        boolean isLowerOpen = false;
        double upperLimit = 0.0;
        boolean isUpperOpen = false;
        double span = 0.0;

        VariableAssistant varAssistant = new VariableAssistant();
        if (isDomainSet) {
            /**
             * 获取相关值
             */

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
