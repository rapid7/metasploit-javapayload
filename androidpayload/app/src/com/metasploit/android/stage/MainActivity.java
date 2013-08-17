package com.metasploit.android.stage;

import com.metasploit.android.stage.R;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		moveTaskToBack(true);
	}
}
