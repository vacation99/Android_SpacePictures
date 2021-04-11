package com.example.spacepictures.object;

import android.os.Parcel;
import android.os.Parcelable;

public class Picture implements Parcelable {
    private String url, title, description;

    public Picture(String url, String title, String description) {
        this.url = url;
        this.title = title;
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Picture(Parcel in) {
        String[] data = new String[3];

        in.readStringArray(data);
        this.url = data[0];
        this.title = data[1];
        this.description = data[2];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.url, this.title, this.description});
    }

    public static final Parcelable.Creator<Picture> CREATOR = new Parcelable.Creator<Picture>() {

        @Override
        public Picture createFromParcel(Parcel source) {
            return new Picture(source);
        }

        @Override
        public Picture[] newArray(int size) {
            return new Picture[size];
        }
    };
}