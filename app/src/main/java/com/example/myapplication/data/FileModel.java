package com.example.myapplication.data;

import android.os.Parcel;
import android.os.Parcelable;

public class FileModel extends BaseModel {
    private String name;
    private String path;
    private long lastModified;
    private long length;

    public FileModel(String name, String path, long lastModified, long length) {

        this.name = name;
        this.path = path;
        this.lastModified = lastModified;
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.length);
        dest.writeLong(this.lastModified);
        dest.writeString(this.path);
        dest.writeString(this.name);
    }
}
