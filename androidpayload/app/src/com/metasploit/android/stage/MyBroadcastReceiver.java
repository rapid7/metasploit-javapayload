package com.metasploit.android.stage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBroadcastReceiver extends BroadcastReceiver {
	
    @Override
    public void onReceive(Context context, Intent intent) {
    	
        //Intent startServiceIntent = new Intent(context, StageService.class);
        //context.startService(startServiceIntent);
    }
}