package com.zhouxiuya;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;

/**
 * Created by zxy on 2018/4/9.
 */

public class MyLeanCloudApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this,"fQm0B3CqunDTUKH3UKiazic5-gzGzoHsz","M6SjT5oXWiYrImSIJlxIBBEV");
    }
}
