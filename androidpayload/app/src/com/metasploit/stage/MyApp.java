package com.metasploit.stage;

import android.app.Application;
import android.content.Context;

public class MyApp extends Application {
    private static Context mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = getApplicationContext();
    }

    public static Context getContext(){
        return mAppContext;
    }
}