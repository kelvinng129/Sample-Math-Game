package com.mobile.game;

import android.app.Application;

import com.tencent.mmkv.MMKV;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MMKV.initialize(this);// Initialize MMKV with the application context
    }
}

/*
package com.mobile.game;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class BaseApplication extends Application {
    private static SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
    }

    public static SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public static SharedPreferences.Editor getEditor() {
        return sharedPreferences.edit();
    }
}

 */