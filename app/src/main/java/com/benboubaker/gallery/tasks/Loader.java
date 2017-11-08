package com.benboubaker.gallery.tasks;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.benboubaker.gallery.models.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fekhe on 06/11/2017.
 */

public class Loader extends AsyncTask<Void,Void,List<Image>> {

    private Activity activity;
    private CallBack callBack;

    public Loader(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected List<Image> doInBackground(Void... params) {
        List<Image> images = new ArrayList<>();
        String[] projection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATA
        };
        String orderBy = MediaStore.Images.ImageColumns.DATE_ADDED + " DESC";
        Cursor cursor = activity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                orderBy);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Image image = new Image()
                    .setId(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID)))
                    .setName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME)))
                    .setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA)));
            images.add(image);
        }
        return images;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (callBack != null) {
            callBack.onStart();
        }
    }

    @Override
    protected void onPostExecute(List<Image> images) {
        super.onPostExecute(images);
        if (callBack != null) {
            callBack.onFinish(images);
        }
    }

    public Loader addCallBack(CallBack callBack) {
        this.callBack = callBack;
        return this;
    }

    public void start() {
        execute();
    }
}
