package com.zhouxiuya.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
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
import com.zhouxiuya.util.RoundedTransformation;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
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
    holder.mName.setText(mList.get(position).getAVFile("filename") == null ? "" : mList.get(position).getAVFile("filename").getOriginalName());
    Picasso.with(mContext).load(mList.get(position).getAVFile("filename") == null ? "www" : mList.get(position).getAVFile("filename").getUrl()).transform(new RoundedTransformation(9, 0)).into(holder.mPicture);
    holder.mItem.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
//        final AlertDialog dialog = new AlertDialog.Builder(mContext).create();
//        ImageView imgView = getView(position);
//        dialog.setView(imgView);
//        dialog.show();
//        imgView.setOnClickListener(new View.OnClickListener() {
//          @Override
//          public void onClick(View view) {
//            dialog.dismiss();
//          }
//        });

      }
      public void onLongClick(View view){

      }
    });
  }
  private ImageView getView(int position) {
    ImageView imgView = new ImageView(mContext);
    imgView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

    try {
      URL url = new URL(mList.get(position).getAVFile("filename").getUrl());
      InputStream is = url.openStream();
      Drawable drawable = BitmapDrawable.createFromStream(is, null);
      imgView.setImageDrawable(drawable);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }


    return imgView;
  }

  @Override
  public int getItemCount() {
    return mList.size();
  }

  class MainViewHolder extends RecyclerView.ViewHolder {
    private TextView mName;
    private CardView mItem;
    private ImageView mPicture;

    public MainViewHolder(View itemView) {
      super(itemView);
      mName = (TextView) itemView.findViewById(R.id.text);
      mPicture = (ImageView) itemView.findViewById(R.id.img);
      mItem = (CardView) itemView.findViewById(R.id.item_collection);
    }
  }
}
