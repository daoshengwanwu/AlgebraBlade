package com.zhouxiuya.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CloudQueryCallback;
import com.avos.avoscloud.DeleteCallback;
import com.graduation_project.android.algebrablade.R;
import com.squareup.picasso.Picasso;
import com.zhouxiuya.PictureActivity;
import com.zhouxiuya.util.RoundedTransformation;

import java.util.List;

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.MainViewHolder> {
    private Context mContext;
    private List<AVObject> mList;
    Dialog dia;
    private OnItemLongClickListener mOnItemLongClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }
    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    public MainRecyclerAdapter(List<AVObject> list, Context context) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MainViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_collection, parent, false));
    }

    @Override
    public void onBindViewHolder(final MainViewHolder holder, final int position) {
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
                holder.bindData();
            }
        });


        holder.mItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            //true为不加短按,false为加入短按
            public boolean onLongClick(View view) {
                int position = holder.getLayoutPosition();
                mOnItemLongClickListener.onItemLongClick(holder.mItem,position);
                return true;
            }
        });
    }

//  private ImageView getView(int position) {
//    ImageView imgView = new ImageView(mContext);
//    imgView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//
//    try {
//      URL url = new URL(mList.get(position).getAVFile("filename").getUrl());
//      InputStream is = url.openStream();
//      Drawable drawable = BitmapDrawable.createFromStream(is, null);
//      imgView.setImageDrawable(drawable);
//    } catch (MalformedURLException e) {
//      e.printStackTrace();
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//    return imgView;
//  }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void removeItem(int pos) {
        mList.remove(pos);
        notifyItemRemoved(pos);
        //删除user_file  _file表里的数据
        final AVFile file = mList.get(pos).getAVFile("filename");
        final AVObject user_file = mList.get(pos);
        user_file.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(AVException e) {
                if (e==null){
                    Log.d("test","User_File delete success");

                }
            }
        });
        file.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(AVException e) {
                if (e==null){
                    Log.d("test","_File delete success"+file.getOriginalName());

                }
            }
        });


    }


    class MainViewHolder extends RecyclerView.ViewHolder {
        private TextView mName;
        private CardView mItem;
        private ImageView mPicture;
        private AVObject mAVObject;

        public MainViewHolder(View itemView) {
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
            mItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    return true;
                }
            });
        }



        private void checkLargePicture(String url, View view) {
            Intent intent = PictureActivity.newIntent(mContext, url, "");

            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (Activity) mContext, view, "shared_pic");

            ActivityCompat.startActivity(mContext, intent, optionsCompat.toBundle());
        }
    }


}
