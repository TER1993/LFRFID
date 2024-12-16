package com.spd.lfrfid;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Build;

/**
 * @author xuyan  准备判断型号
 */
public class MyApp extends Application {
    @SuppressLint("StaticFieldLeak")
    private static MyApp sInstance;
    /**
     * 设备真实型号，用于判断是否需要旋转图片
     */
    public static String model;
    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

    }

    public static MyApp getInstance() {
        return sInstance;
    }

    public static String getModel() {
        model = android.os.SystemProperties.get("ro.build.developer");
        if (model == null || model.isEmpty()) {
            model = Build.MODEL;
        }
        return model;
    }
}
