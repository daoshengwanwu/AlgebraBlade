package com.graduation_project.android.algebrablade.views.custom_keyboard;


import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.graduation_project.android.algebrablade.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class KeyboardPage1Fragment extends Fragment {
    @BindView(R.id.fact_or_x)
    Button mFactOrXButton;
    private int mKeyboardType;
    private OnEnterClickListener mOnClickListener = null;
    private View.OnClickListener mOnDelClickListener = null;

    public static KeyboardPage1Fragment newFragment(int type) {
        KeyboardPage1Fragment fragment = new KeyboardPage1Fragment();
        fragment.mKeyboardType = type;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_keyboard_page_1, container, false);

        ButterKnife.bind(this, v);
        if (mKeyboardType == CustomKeyboard.KeyboardType.TYPE_CALCULATOR) {
            mFactOrXButton.setText("!");
        } else if (mKeyboardType == CustomKeyboard.KeyboardType.TYPE_FUNCTION) {
            mFactOrXButton.setText("x");
        }
        return v;
    }

    public void setOnEnterClickListener(OnEnterClickListener listener) {
        mOnClickListener = listener;
    }

    public void setOnDelClickListener(View.OnClickListener listener) {
        mOnDelClickListener = listener;
    }

    @OnClick(
    {R.id.sqrt, R.id.move_left, R.id.move_right,
    R.id.left_brackets, R.id.right_brackets, R.id.fact_or_x,
    R.id.sin, R.id.cos, R.id.power, R.id.pi,
    R.id.num_0, R.id.num_1, R.id.num_2, R.id.num_3,
    R.id.num_4, R.id.num_5, R.id.num_6, R.id.num_7,
    R.id.num_8, R.id.num_9, R.id.point,
    R.id.e, R.id.mul, R.id.div, R.id.add, R.id.sub,
    R.id.mod, R.id.abs})
    void onKeyClick(Button btn) {
        //获取与键盘关联的输入框
        EditText relateEditText = CustomInputMethodManager.getCurrentKeyboard().getRelateEditText();

        if (relateEditText == null) {
            return;
        }

        int cursorStart = relateEditText.getSelectionStart();
        int cursorEnd = relateEditText.getSelectionEnd();
        boolean isSel = cursorStart < cursorEnd;//当前输入框是否选择了文字

        switch (btn.getId()) {
            case R.id.move_left: {
                if (isSel) {
                    relateEditText.setSelection(cursorStart);
                } else if (cursorStart > 0) {
                    relateEditText.setSelection(cursorStart - 1);
                }
            } break;

            case R.id.move_right: {
                int len = relateEditText.getText().toString().length();

                if (isSel) {
                    relateEditText.setSelection(cursorEnd);
                } else if (cursorEnd < len) {
                    relateEditText.setSelection(cursorEnd + 1);
                }
            } break;

            default: {
                String oriText = relateEditText.getText().toString();
                String text = null;
                if (!isSel) {
                    text = oriText.substring(0, cursorStart) +
                            btn.getText().toString() + oriText.substring(cursorStart, oriText.length());
                } else {
                    text = oriText.substring(0, cursorStart) +
                            btn.getText().toString() + oriText.substring(cursorEnd, oriText.length());
                }

                relateEditText.setText(text);
                relateEditText.setSelection(cursorStart + btn.getText().length());
            } break;
        }
    }

    @OnClick({R.id.enter, R.id.del})
    void onEnterOrDelClick(ImageButton btn) {
        EditText relateEditText = CustomInputMethodManager.getCurrentKeyboard().getRelateEditText();

        if (relateEditText == null) {
            return;
        }

        int cursorStart = relateEditText.getSelectionStart();
        int cursorEnd = relateEditText.getSelectionEnd();
        boolean isSel = cursorStart < cursorEnd;

        switch (btn.getId()) {
            case R.id.del: {
                String oriText = relateEditText.getText().toString();
                String text = null;
                if (isSel) {
                    text = oriText.substring(0, cursorStart) +
                            oriText.substring(cursorEnd, oriText.length());
                } else if (cursorStart > 0) {
                    text = oriText.substring(0, cursorStart - 1) +
                            oriText.substring(cursorEnd, oriText.length());
                } else {
                    mOnDelClickListener.onClick(btn);
                    break;
                }

                relateEditText.setText(text);

                if (!isSel) {
                    relateEditText.setSelection(cursorStart - 1);
                } else {
                    relateEditText.setSelection(cursorStart);
                }
            } break;

            case R.id.enter: {
                if (mOnClickListener != null) {
                    mOnClickListener.onClick(relateEditText.getText().toString());
                }
            } break;
        }
    }


    public interface OnEnterClickListener {
        void onClick(String expStr);
    }
}
