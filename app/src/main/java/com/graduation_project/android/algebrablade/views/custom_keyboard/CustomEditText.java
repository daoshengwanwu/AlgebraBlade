package com.graduation_project.android.algebrablade.views.custom_keyboard;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class CustomEditText extends android.support.v7.widget.AppCompatEditText {
    private static Method mHideSystemInputMethod = null;
    private OnFocusChangeListener mOnFocusChangeListener = null;

    static {
        try {
            mHideSystemInputMethod = TextView.class
                    .getDeclaredMethod("setShowSoftInputOnFocus", boolean.class);

        } catch (NoSuchMethodException e) {
            try {
                mHideSystemInputMethod = TextView.class
                        .getDeclaredMethod("setSoftInputShowOnFocus", boolean.class);

            } catch (NoSuchMethodException e1) {
                e1.printStackTrace();
            }
        }

        if (mHideSystemInputMethod != null) {
            mHideSystemInputMethod.setAccessible(true);
        }
    }


    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        try {
            mHideSystemInputMethod.invoke(this, false);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        //焦点改变时设置监听
        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    CustomInputMethodManager.
                            getCurrentKeyboard().setRelateEditText(CustomEditText.this);
                } else if (CustomEditText.this ==
                        CustomInputMethodManager.getCurrentKeyboard().getRelateEditText()) {

                    CustomInputMethodManager.
                            getCurrentKeyboard().setRelateEditText(null);
                }

                mOnFocusChangeListener.onFocusChange(view, b);
            }
        });
    }

    public void setExtraOnFocusChangeListener(OnFocusChangeListener listener) {
        mOnFocusChangeListener = listener;
    }
}
