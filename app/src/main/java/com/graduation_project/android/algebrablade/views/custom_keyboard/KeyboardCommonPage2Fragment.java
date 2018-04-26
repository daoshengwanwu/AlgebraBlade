package com.graduation_project.android.algebrablade.views.custom_keyboard;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.graduation_project.android.algebrablade.R;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class KeyboardCommonPage2Fragment extends Fragment {
    public static Fragment newFragment(EditText relateEditText) {
        KeyboardCommonPage2Fragment fragment = new KeyboardCommonPage2Fragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_common_page_2, container, false);

        ButterKnife.bind(this, v);

        return v;
    }

    @OnClick({
        R.id.tan, R.id.arcsin, R.id.arccos,
        R.id.arctan, R.id.lg, R.id.ln, R.id.log,
        R.id.log_end
    })
    void onClick(Button btn) {
        EditText relateEditText = CustomInputMethodManager.getCurrentKeyboard().getRelateEditText();

        if (relateEditText == null) {
            return;
        }

        int cursorStart = relateEditText.getSelectionStart();
        int cursorEnd = relateEditText.getSelectionEnd();
        boolean isSel = cursorStart < cursorEnd;

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
    }
}
