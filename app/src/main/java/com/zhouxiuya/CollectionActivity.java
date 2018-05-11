package com.zhouxiuya;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.graduation_project.android.algebrablade.R;
import com.zhouxiuya.adapter.MainRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class CollectionActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private MainRecyclerAdapter mRecyclerAdapter;
    private List<AVObject> mList = new ArrayList<>();


    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, CollectionActivity.class);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        ButterKnife.bind(this);

        initData();

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        AVAnalytics.onResume(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        AVAnalytics.onPause(this);
    }

    private void initData() {
        AVUser user = AVUser.getCurrentUser();
        AVQuery<AVObject> query = new AVQuery<>("User_File");
        query.whereEqualTo("user", user);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    mList.addAll(list);
                    mRecyclerAdapter.notifyDataSetChanged();

                } else {
                    e.printStackTrace();
                }
            }

        });
        mRecyclerView = (RecyclerView) findViewById(R.id.list_collection);
        //Item的改变不会影响RecyclerView的宽高的时候可以设置setHasFixedSize(true)
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(CollectionActivity.this, 2));
        mRecyclerAdapter = new MainRecyclerAdapter(mList, CollectionActivity.this);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setRecyclerListener(new RecyclerView.RecyclerListener() {
            @Override
            public void onViewRecycled(RecyclerView.ViewHolder holder) {

            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
            }
            return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
