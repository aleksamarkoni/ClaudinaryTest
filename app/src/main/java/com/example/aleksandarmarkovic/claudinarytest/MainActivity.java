package com.example.aleksandarmarkovic.claudinarytest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.cloudinary.Cloudinary;
import com.cloudinary.UploadCallback;
import com.cloudinary.android.Utils;
import com.cloudinary.utils.ObjectUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Cloudinary cloudinary;
    Button mUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        cloudinary = new Cloudinary(Utils.cloudinaryUrlFromContext(this));
        mUpload = (Button) findViewById(R.id.uploadButton);

        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });


    }

    private void uploadImage() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void ... params) {
                long time = System.currentTimeMillis();
                Log.d("ALEKSA", "" + time);
                InputStream ins = getResources().openRawResource(R.raw.testimage);
                try {
                    Map<String, String> options = new HashMap<>();
                    options.put("chunk_size", "5242881");
                    cloudinary.uploader(new UploadCallback() {
                        @Override
                        public void uploadListener(int i) {
                            Log.d("ALEKSA", "part number " + i);
                        }
                    }).upload(ins , options);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        ins.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                long finishTime = System.currentTimeMillis();
                Log.d("ALEKSA", "" + finishTime);
                Log.d("ALEKSA", "is " + (finishTime - time));
                return null;
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
