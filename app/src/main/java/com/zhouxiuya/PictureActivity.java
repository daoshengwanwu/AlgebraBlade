package com.zhouxiuya;


import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;
import com.graduation_project.android.algebrablade.R;
import com.squareup.picasso.Picasso;
import com.zhouxiuya.util.RoundedTransformation;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PictureActivity extends AppCompatActivity {
    private static final String EXTRA_URL = "extra_url";
    private static final String EXTRA_DESC = "extra_desc";

    @BindView(R.id.photo_view)
    PhotoView mPhotoView;


    public static Intent newIntent(Context packageContext, String url, String desc) {
        Intent intent = new Intent(packageContext, PictureActivity.class);

        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_DESC, desc);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        String url = getIntent().getStringExtra(EXTRA_URL);
        Picasso.with(this).load(url).transform(
                new RoundedTransformation(9, 0)).into(mPhotoView);
    }
}
