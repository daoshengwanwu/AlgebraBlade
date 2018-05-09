package com.graduation_project.android.algebrablade.views.custom_keyboard;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.graduation_project.android.algebrablade.R;

import java.util.ArrayList;
import java.util.List;


public class CustomKeyboard extends FrameLayout {
    private int mKeyboardType = -1;
    private boolean mIsInit = false;

    private CustomEditText mRelateEditText = null;

    private FragmentManager mFragmentManager = null;

    private List<Fragment> mKeyboardFragments = new ArrayList<>();

    private ViewPager mViewPager;
    private ImageView mFirstDot;
    private ImageView mSecondDot;

    public CustomKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.layout_custom_keyboard, this);

        mViewPager = findViewById(R.id.view_pager);
        mFirstDot = findViewById(R.id.dot_first);
        mSecondDot = findViewById(R.id.dot_second);
        mViewPager.setBackgroundColor(
                ContextCompat.getColor(context, R.color.color_keyboard_light));
    }

    public void init(FragmentManager fragmentManager, int keyboardType) {
        if (mIsInit) {
            return;
        }

        if (fragmentManager == null || keyboardType < KeyboardType.TYPE_CALCULATOR ||
                keyboardType > KeyboardType.TYPE_FUNCTION) {
            throw new RuntimeException("fragmentManager == null || keyboardType非法");
        }

        mKeyboardType = keyboardType;
        mFragmentManager = fragmentManager;
        mViewPager.setAdapter(new KeyboardPagerAdapter(mFragmentManager));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0: {
                        mFirstDot.setImageResource(R.drawable.dot_focused);
                        mSecondDot.setImageResource(R.drawable.dot_normal);
                    } break;

                    case 1: {
                        mFirstDot.setImageResource(R.drawable.dot_normal);
                        mSecondDot.setImageResource(R.drawable.dot_focused);
                    } break;

                    default: break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mIsInit = true;

        CustomInputMethodManager.registerKeyboard(this);
    }

    public void setRelateEditText(CustomEditText editText) {
        mRelateEditText = editText;
    }

    public void setOnEnterClickListener(KeyboardPage1Fragment.OnEnterClickListener listener) {
        ((KeyboardPage1Fragment)mKeyboardFragments.get(0)).setOnEnterClickListener(listener);
    }

    public void setOnDelClickListener(View.OnClickListener listener) {
        ((KeyboardPage1Fragment)mKeyboardFragments.get(0)).setOnDelClickListener(listener);
    }

    public CustomEditText getRelateEditText() {
        return mRelateEditText;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (!mIsInit) {
            throw new RuntimeException("请调用本控件的init方法来初始化本控件");
        }
    }


    public static class KeyboardType {
        public static final int TYPE_CALCULATOR = 0;
        public static final int TYPE_FUNCTION = 1;
    }

    private class KeyboardPagerAdapter extends FragmentStatePagerAdapter {
        private KeyboardPagerAdapter(FragmentManager fm) {
            super(fm);

            mKeyboardFragments.clear();
            KeyboardPage1Fragment page1Fragment = null;

            if (mKeyboardType == KeyboardType.TYPE_CALCULATOR) {
                page1Fragment = KeyboardPage1Fragment.newFragment(KeyboardType.TYPE_CALCULATOR);

            } else if (mKeyboardType == KeyboardType.TYPE_FUNCTION) {
                page1Fragment = KeyboardPage1Fragment.newFragment(KeyboardType.TYPE_FUNCTION);
            }

            mKeyboardFragments.add(page1Fragment);
            mKeyboardFragments.add(KeyboardCommonPage2Fragment.newFragment(mRelateEditText));

        }

        @Override
        public Fragment getItem(int position) {
            return mKeyboardFragments.get(position);
        }

        @Override
        public int getCount() {
            return mKeyboardFragments.size();
        }
    }
}
