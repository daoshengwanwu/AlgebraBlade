package com.zhouxiuya.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.TimeZone;

import static android.content.ContentValues.TAG;

/**
 * Created by zhouxiuya on 2018/4/26.
 * 文件存储工具类
 */

public class FileUtil {
    public static final String APPLICATION_STORAGE_DIRECTORY =
            Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/AlgebraBlade";

    public static final String COLLECTION_DIRECTORY =
            APPLICATION_STORAGE_DIRECTORY + "/Collections";
    public static Calendar cal;
    public static String year;
    public static String month;
    public static String day;
    public static String hour;
    public static String minute;
    public static String second;
    public static String time;

    public static String getTime(){
        cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

        year = String.valueOf(cal.get(Calendar.YEAR));
        month = String.valueOf(cal.get(Calendar.MONTH)+1);
        day = String.valueOf(cal.get(Calendar.DATE));
        if (cal.get(Calendar.AM_PM) == 0)
            hour = String.valueOf(cal.get(Calendar.HOUR));
        else
            hour = String.valueOf(cal.get(Calendar.HOUR)+12);
        minute = String.valueOf(cal.get(Calendar.MINUTE));
        second = String.valueOf(cal.get(Calendar.SECOND));
        time = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
        return time;
    }
    //保存图片
    public static void saveScreenshotToCloud(final Context context, Bitmap bitmap){
        byte[] bytes = bitmap2ByteArray(bitmap);

        if(AVUser.getCurrentUser()!=null){

            final AVObject user_file = new AVObject("User_File");
            user_file.put("filename",new AVFile(getTime(), bytes));
            user_file.put("user", AVUser.getCurrentUser());
            user_file.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e==null){
                        Log.d(TAG,user_file.getObjectId());//返回一个唯一的 Url 地址
                        Toast.makeText(context, "上传成功", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(context, "上传失败", Toast.LENGTH_SHORT).show();
                    }
                }


            });
        }else {
            Toast.makeText(context, "请先登录！", Toast.LENGTH_SHORT).show();
        }

    }

    public static void saveScreenshotToLocal(Context context, Bitmap bitmap) {
        File dir = new File(FileUtil.COLLECTION_DIRECTORY);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        if (dir.exists()) {
            File out = new File(dir, "Graphic_" + System.currentTimeMillis() + ".jpg");

            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(out));
                Toast.makeText(context, "截图已经保存至" + out.getAbsolutePath(),
                        Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "创建应用文件夹失败，请允许应用的存储权限。",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //获取数据流
    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, len);
        }
        return byteArrayOutputStream.toByteArray();
    }

    private static byte[] bitmap2ByteArray(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        return baos.toByteArray();
    }
}
