
package com.appdev.schoudhary.wittylife.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "trafficranking")
public class TrafficRanking implements Parcelable {

    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("city_name")
    @Expose
    private String cityName;
    @SerializedName("time_exp_index")
    @Expose
    private Double timeExpIndex;
    @SerializedName("traffic_index")
    @Expose
    private Double trafficIndex;
    @SerializedName("time_index")
    @Expose
    private Double timeIndex;
    @SerializedName("inefficiency_index")
    @Expose
    private Double inefficiencyIndex;
    @SerializedName("co2_emission_index")
    @Expose
    private Double co2EmissionIndex;
    @SerializedName("city_id")
    @Expose
    @PrimaryKey
    @ColumnInfo(name = "city_id")
    private Integer cityId;
    public final static Parcelable.Creator<TrafficRanking> CREATOR = new Creator<TrafficRanking>() {


        @SuppressWarnings({
                "unchecked"
        })
        public TrafficRanking createFromParcel(Parcel in) {
            return new TrafficRanking(in);
        }

        public TrafficRanking[] newArray(int size) {
            return (new TrafficRanking[size]);
        }

    };

    @Ignore
    private TrafficRanking(Parcel in) {
        this.country = ((String) in.readValue((String.class.getClassLoader())));
        this.cityName = ((String) in.readValue((String.class.getClassLoader())));
        this.timeExpIndex = ((Double) in.readValue((Double.class.getClassLoader())));
        this.trafficIndex = ((Double) in.readValue((Double.class.getClassLoader())));
        this.timeIndex = ((Double) in.readValue((Double.class.getClassLoader())));
        this.inefficiencyIndex = ((Double) in.readValue((Double.class.getClassLoader())));
        this.co2EmissionIndex = ((Double) in.readValue((Double.class.getClassLoader())));
        this.cityId = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     */
//    public TrafficRanking() {
//    }

    /**
     * @param cityId
     * @param co2EmissionIndex
     * @param inefficiencyIndex
     * @param timeIndex
     * @param cityName
     * @param trafficIndex
     * @param timeExpIndex
     * @param country
     */
    public TrafficRanking(String country, String cityName, Double timeExpIndex, Double trafficIndex, Double timeIndex, Double inefficiencyIndex, Double co2EmissionIndex, Integer cityId) {
        super();
        this.country = country;
        this.cityName = cityName;
        this.timeExpIndex = timeExpIndex;
        this.trafficIndex = trafficIndex;
        this.timeIndex = timeIndex;
        this.inefficiencyIndex = inefficiencyIndex;
        this.co2EmissionIndex = co2EmissionIndex;
        this.cityId = cityId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Double getTimeExpIndex() {
        return timeExpIndex;
    }

    public void setTimeExpIndex(Double timeExpIndex) {
        this.timeExpIndex = timeExpIndex;
    }

    public Double getTrafficIndex() {
        return trafficIndex;
    }

    public void setTrafficIndex(Double trafficIndex) {
        this.trafficIndex = trafficIndex;
    }

    public Double getTimeIndex() {
        return timeIndex;
    }

    public void setTimeIndex(Double timeIndex) {
        this.timeIndex = timeIndex;
    }

    public Double getInefficiencyIndex() {
        return inefficiencyIndex;
    }

    public void setInefficiencyIndex(Double inefficiencyIndex) {
        this.inefficiencyIndex = inefficiencyIndex;
    }

    public Double getCo2EmissionIndex() {
        return co2EmissionIndex;
    }

    public void setCo2EmissionIndex(Double co2EmissionIndex) {
        this.co2EmissionIndex = co2EmissionIndex;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(country);
        dest.writeValue(cityName);
        dest.writeValue(timeExpIndex);
        dest.writeValue(trafficIndex);
        dest.writeValue(timeIndex);
        dest.writeValue(inefficiencyIndex);
        dest.writeValue(co2EmissionIndex);
        dest.writeValue(cityId);
    }

    public int describeContents() {
        return 0;
    }

}
