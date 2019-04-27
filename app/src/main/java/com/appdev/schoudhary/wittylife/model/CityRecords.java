package com.appdev.schoudhary.wittylife.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CityRecords implements Parcelable
{

    @SerializedName("cities")
    @Expose
    private List<City> cities = null;
    public final static Parcelable.Creator<CityRecords> CREATOR = new Creator<CityRecords>() {


        @SuppressWarnings({
                "unchecked"
        })
        public CityRecords createFromParcel(Parcel in) {
            return new CityRecords(in);
        }

        public CityRecords[] newArray(int size) {
            return (new CityRecords[size]);
        }

    }
            ;

    private CityRecords(Parcel in) {
        in.readList(this.cities, (com.appdev.schoudhary.wittylife.model.City.class.getClassLoader()));
    }

//    /**
//     * No args constructor for use in serialization
//     *
//     */
//    public CityRecords() {
//    }

    /**
     *
     * @param cities
     */
    public CityRecords(List<City> cities) {
        super();
        this.cities = cities;
    }

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(cities);
    }

    public int describeContents() {
        return 0;
    }

}