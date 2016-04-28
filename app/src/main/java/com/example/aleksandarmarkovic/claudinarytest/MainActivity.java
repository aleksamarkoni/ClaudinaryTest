package com.example.aleksandarmarkovic.claudinarytest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import java.io.File;

public class MainActivity extends AppCompatActivity /*implements UploadCallback*/ {

    Button mUpload;
    Uri destination;
    ProgressBar progressBar;
    public static final String PHOTO_SERVICE = "Photo Service";
    public static final String FINISHED_SERVICE = "Finished Service";
    public static final String PROGRESS = "Progress";
    TextView end, diff, url;
    long startTime;
    ImageView picture;

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(photoUploadServiceReceiver,
                new IntentFilter(PHOTO_SERVICE));
        LocalBroadcastManager.getInstance(this).registerReceiver(finishServiceReceiver,
                new IntentFilter(FINISHED_SERVICE));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(photoUploadServiceReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(finishServiceReceiver);
    }

    private BroadcastReceiver photoUploadServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final float progress = intent.getFloatExtra(PROGRESS, -1);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setProgress(Math.round(progress));
                }
            });
        }
    };

    private BroadcastReceiver finishServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String url = intent.getStringExtra("url");
            final long endTime = intent.getLongExtra("end", -1);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MainActivity.this.end.setText(String.valueOf(endTime));
                    MainActivity.this.diff.setText(String.valueOf(startTime - endTime));
                    MainActivity.this.url.setText(url);
                    Picasso.with(MainActivity.this).load(url).into(MainActivity.this.picture);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar = (ProgressBar)  findViewById(R.id.progress);
        picture = (ImageView) findViewById(R.id.picture);
        end = (TextView) findViewById(R.id.end);
        diff = (TextView) findViewById(R.id.diff);
        url = (TextView) findViewById(R.id.url);

        mUpload = (Button) findViewById(R.id.uploadButton);
        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Crop.pickImage(MainActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Crop.REQUEST_PICK) {
                beginCrop(data.getData());
            } else if (requestCode == Crop.REQUEST_CROP) {
                handleCrop(resultCode, data);
            }
        }
    }

    private void beginCrop(Uri source) {
        ContextWrapper cw = new ContextWrapper(this);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File file = new File(directory,"/test");
        destination = Uri.fromFile(file);
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            startTime = System.currentTimeMillis();
            TextView start = (TextView) findViewById(R.id.start);
            start.setText(String.valueOf(startTime));
            File image = new File(Crop.getOutput(result).getPath());
            Intent intent =new Intent(MainActivity.this, UploadService.class);
            intent.putExtra("file", image);
            startService(intent);
        }
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
