package com.zhouxiuya.util;

import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.content.ContentValues.TAG;

/**
 * Created by zhouxiuya on 2018/4/26.
 * 文件存储工具类
 */

public class FileUtil {

    //保存图片
    public static void saveFile(byte[] mImageBytes){
        final AVObject user_file = new AVObject("User_File");
        user_file.put("filename",new AVFile("pic", mImageBytes));
        user_file.put("user", AVUser.getCurrentUser());
        user_file.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e==null){
                    Log.d(TAG,user_file.getObjectId());//返回一个唯一的 Url 地址
                }else {

                }
            }
        });
    }

    //获取数据流
    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, len);
        }
        return byteArrayOutputStream.toByteArray();
    }


}
