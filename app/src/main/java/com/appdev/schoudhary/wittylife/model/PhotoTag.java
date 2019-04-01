package com.appdev.schoudhary.wittylife.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PhotoTag implements Parcelable {

    public final static Parcelable.Creator<PhotoTag> CREATOR = new Creator<PhotoTag>() {


        @SuppressWarnings({
                "unchecked"
        })
        public PhotoTag createFromParcel(Parcel in) {
            return new PhotoTag(in);
        }

        public PhotoTag[] newArray(int size) {
            return (new PhotoTag[size]);
        }

    };
    @SerializedName("title")
    @Expose
    private String title;

    protected PhotoTag(Parcel in) {
        this.title = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     */
    public PhotoTag() {
    }

    /**
     * @param title
     */
    public PhotoTag(String title) {
        super();
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(title);
    }

    public int describeContents() {
        return 0;
    }

}