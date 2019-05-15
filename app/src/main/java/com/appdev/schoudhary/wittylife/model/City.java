package com.appdev.schoudhary.wittylife.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

@Entity(tableName = "city")
public class City implements Parcelable
{

    @SerializedName("city_id")
    @Expose
    private Integer cityId;
    @SerializedName("longitude")
    @Expose
    private Float longitude;
    @SerializedName("latitude")
    @Expose
    private Float latitude;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("city")
    @Expose
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "city_name")
    private String city;
    public final static Parcelable.Creator<City> CREATOR = new Creator<City>() {


        public City createFromParcel(Parcel in) {
            return new City(in);
        }

        public City[] newArray(int size) {
            return (new City[size]);
        }

    }
            ;

    private City(Parcel in) {
        this.cityId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.longitude = ((Float) in.readValue((Float.class.getClassLoader())));
        this.latitude = ((Float) in.readValue((Float.class.getClassLoader())));
        this.country = ((String) in.readValue((String.class.getClassLoader())));
        this.city = ((String) Objects.requireNonNull(in.readValue((String.class.getClassLoader()))));
    }

//    /**
//     * No args constructor for use in serialization
//     *
//     */
//    public City() {
//    }

    /**
     *
     * @param cityId
     * @param longitude
     * @param latitude
     * @param city
     * @param country
     */
    public City(Integer cityId, Float longitude, Float latitude, String country, String city) {
        super();
        this.cityId = cityId;
        this.longitude = longitude;
        this.latitude = latitude;
        this.country = country;
        this.city = city;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(cityId);
        dest.writeValue(longitude);
        dest.writeValue(latitude);
        dest.writeValue(country);
        dest.writeValue(city);
    }

    public int describeContents() {
        return 0;
    }

}