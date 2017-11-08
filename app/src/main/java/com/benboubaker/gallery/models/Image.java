package com.benboubaker.gallery.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fekhe on 06/11/2017.
 */

public class Image implements Parcelable{

    public static final String TAG = "image";
    private int id;
    private String name;
    private String path;

    public Image() {}

    protected Image(Parcel in) {
        id = in.readInt();
        name = in.readString();
        path = in.readString();
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    public int getId() {
        return id;
    }

    public Image setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Image setName(String name) {
        this.name = name;
        return this;
    }

    public String getPath() {
        return path;
    }

    public Image setPath(String path) {
        this.path = path;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(path);
    }
}
