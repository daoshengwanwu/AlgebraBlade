package com.zhouxiuya;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.graduation_project.android.algebrablade.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;

public class CollectionActivity extends AppCompatActivity {
    @BindView(R.id.gridview)
    GridView gridView;
    private List<Map<String, Object>> dataList;
    private SimpleAdapter mCollectionAdapterr;
    private List<AVObject> mList = new ArrayList<>();
    OkHttpClient client = new OkHttpClient();


    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, CollectionActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        ButterKnife.bind(this);

        gridView = (GridView) findViewById(R.id.gridview);
        //初始化数据
        initData();




    }

    private void initData() {

        String userid = AVUser.getCurrentUser().getObjectId();
        AVUser user = AVUser.getCurrentUser();
        AVQuery<AVObject> query = new AVQuery<>("User_File");
        query.whereEqualTo("user",user);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(e == null){
                    mList.addAll(list);
                    //文字
                    String []name = new String[100];
                    //url
                    String []url = new String[100];
                    int lenth = mList.size();
                    for(int i =0; i < lenth; i++){
                        url[i] = mList.get(i).getAVFile("filename").getUrl();
                        name[i] = mList.get(i).getAVFile("filename").getOriginalName();
                    }


                    dataList = new ArrayList<Map<String, Object>>();
                    for (int i = 0; i <url.length; i++) {
                        Map<String, Object> map=new HashMap<String, Object>();
//                        map.put("img", icno[i]);
                        map.put("img", returnBitMap(url[i]));
                        map.put("text",name[i]);
                        dataList.add(map);
                    }

//                    String[] from={"img","text"};
//
//                    int[] to={R.id.img,R.id.text};

                    mCollectionAdapterr=new SimpleAdapter(CollectionActivity.this, dataList, R.layout.item_collection,
                            new String[]{"img","text"}, new int[]{R.id.img,R.id.text});

                    mCollectionAdapterr.setViewBinder(new SimpleAdapter.ViewBinder() {
                        @Override
                        public boolean setViewValue(View view, Object data, String s) {
                            if(view instanceof ImageView && data instanceof Bitmap){
                                ImageView iv = (ImageView) view;
                                iv.setImageBitmap((Bitmap) data);
                                return true;
                            }
                            return false;
                        }
                    });
                    gridView.setAdapter(mCollectionAdapterr);

                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                                long arg3) {

                        }
                    });



                    mCollectionAdapterr.notifyDataSetChanged();

                }else {
                    e.printStackTrace();
                }
            }

        });

    }

    public Bitmap returnBitMap(String url) {
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }



    @Override
        public boolean onOptionsItemSelected (MenuItem item){
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
