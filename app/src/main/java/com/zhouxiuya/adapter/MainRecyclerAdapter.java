package com.zhouxiuya.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.graduation_project.android.algebrablade.R;
import com.squareup.picasso.Picasso;
import com.zhouxiuya.MainActivity;
import com.zhouxiuya.PictureActivity;
import com.zhouxiuya.util.RoundedTransformation;

import java.util.List;

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.MainViewHolder> {
    private Context mContext;
    private List<AVObject> mList;
    Dialog dia;

    public MainRecyclerAdapter(List<AVObject> list, Context context) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_collection, parent, false));
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, final int position) {
        holder.bindData();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MainViewHolder extends RecyclerView.ViewHolder {
        private TextView mName;
        private CardView mItem;
        private ImageView mPicture;
        private AVObject mAVObject;


        MainViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.text);
            mPicture = (ImageView) itemView.findViewById(R.id.img);
            mItem = (CardView) itemView.findViewById(R.id.item_collection);
        }

        void bindData() {
            mAVObject = mList.get(getAdapterPosition());

            mName.setText(mAVObject.getAVFile("filename") == null ?
                    "" : mAVObject.getAVFile("filename").getOriginalName());

            Picasso.with(mContext).load(mAVObject.getAVFile("filename") == null ?
                    "www" : mAVObject.getAVFile("filename").getUrl()).
                    transform(new RoundedTransformation(9, 0)).into(mPicture);

            mItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkLargePicture(mAVObject.getAVFile("filename").getUrl(), mPicture);
                }
            });
        }

        private void checkLargePicture(String url, View view) {
            Intent intent = PictureActivity.newIntent(mContext, url, "");

            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (Activity)mContext, view, "shared_pic");

            ActivityCompat.startActivity(mContext, intent, optionsCompat.toBundle());
        }
    }
}
