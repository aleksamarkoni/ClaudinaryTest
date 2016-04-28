package com.example.aleksandarmarkovic.claudinarytest;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.cloudinary.Cloudinary;
import com.cloudinary.UploadCallback;
import com.cloudinary.android.Utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by WingHinChan on 2016/04/28.
 */
public class UploadService extends IntentService implements UploadCallback {

    Cloudinary cloudinary;
    long filesize;

    public UploadService() {
        super(UploadService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        cloudinary = new Cloudinary(Utils.cloudinaryUrlFromContext(this));
        long time = System.currentTimeMillis();
        Log.d("ALEKSA", "" + time);
        try {
            Map<String, String> options = new HashMap<>();
            options.put("chunk_size", "5242881");
            File file = (File) intent.getSerializableExtra("file");
            filesize = file.length();
            Log.d("filesize", ""+filesize);
            Map map = cloudinary.uploader(this).upload(file, options);
            Log.d("url", map.get("url").toString());
            long finishTime = System.currentTimeMillis();

            //sending the intent back to mainactivity as the upload is now complete.
            Intent finishIntent = new Intent(MainActivity.FINISHED_SERVICE);
            finishIntent.putExtra("url", map.get("url").toString());
            finishIntent.putExtra("end", finishTime);
            LocalBroadcastManager.getInstance(this).sendBroadcast(finishIntent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void uploadListener(int var1) {
        Intent intent = new Intent(MainActivity.PHOTO_SERVICE);
        float percentage = ((float) var1 / filesize) * 100;
        intent.putExtra(MainActivity.PROGRESS, percentage);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        Log.d("ALEKSAService", "part number " + var1 + " % " + percentage);
    }
}
