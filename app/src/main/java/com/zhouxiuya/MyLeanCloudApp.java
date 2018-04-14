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
        AVOSCloud.initialize(this,"qXiPsPttUSeNI3Yw4BO47fKj-gzGzoHsz","FILqTQPNig7U4eilzfqA4B1L");
    }
}
