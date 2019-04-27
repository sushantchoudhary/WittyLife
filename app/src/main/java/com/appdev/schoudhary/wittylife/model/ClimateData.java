package com.appdev.schoudhary.wittylife.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "climate_data")
public class ClimateData implements Parcelable
{

    @SerializedName("green_and_parks_quality")
    @Expose
    private Float greenAndParksQuality;
    @SerializedName("pm2.5")
    @Expose
    private Integer pm25;
    @SerializedName("comfortable_to_spend_time")
    @Expose
    private Float comfortableToSpendTime;
    @SerializedName("pm10")
    @Expose
    private Integer pm10;
    @SerializedName("air_quality")
    @Expose
    private Float airQuality;
    @SerializedName("garbage_disposal_satisfaction")
    @Expose
    private Float garbageDisposalSatisfaction;
    @SerializedName("index_pollution")
    @Expose
    private Float indexPollution;
    @SerializedName("drinking_water_quality_accessibility")
    @Expose
    private Float drinkingWaterQualityAccessibility;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("monthLastUpdate")
    @Expose
    private Integer monthLastUpdate;
    @SerializedName("clean_and_tidy")
    @Expose
    private Float cleanAndTidy;
    @SerializedName("noise_and_light_pollution")
    @Expose
    private Float noiseAndLightPollution;
    @SerializedName("contributors")
    @Expose
    private Integer contributors;
    @SerializedName("yearLastUpdate")
    @Expose
    private Integer yearLastUpdate;
    @SerializedName("water_pollution")
    @Expose
    private Float waterPollution;
    @SerializedName("city_id")
    @Expose
    @PrimaryKey
    @ColumnInfo(name = "city_id")
    private Integer cityId;
    public final static Parcelable.Creator<ClimateData> CREATOR = new Creator<ClimateData>() {


        @SuppressWarnings({
                "unchecked"
        })
        public ClimateData createFromParcel(Parcel in) {
            return new ClimateData(in);
        }

        public ClimateData[] newArray(int size) {
            return (new ClimateData[size]);
        }

    }
            ;

    @Ignore
    protected ClimateData(Parcel in) {
        this.greenAndParksQuality = ((Float) in.readValue((Float.class.getClassLoader())));
        this.pm25 = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.comfortableToSpendTime = ((Float) in.readValue((Float.class.getClassLoader())));
        this.pm10 = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.airQuality = ((Float) in.readValue((Float.class.getClassLoader())));
        this.garbageDisposalSatisfaction = ((Float) in.readValue((Float.class.getClassLoader())));
        this.indexPollution = ((Float) in.readValue((Float.class.getClassLoader())));
        this.drinkingWaterQualityAccessibility = ((Float) in.readValue((Float.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.monthLastUpdate = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.cleanAndTidy = ((Float) in.readValue((Float.class.getClassLoader())));
        this.noiseAndLightPollution = ((Float) in.readValue((Float.class.getClassLoader())));
        this.contributors = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.yearLastUpdate = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.waterPollution = ((Float) in.readValue((Float.class.getClassLoader())));
        this.cityId = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    /**
     *
     * @param contributors
     * @param garbageDisposalSatisfaction
     * @param cityId
     * @param waterPollution
     * @param noiseAndLightPollution
     * @param greenAndParksQuality
     * @param indexPollution
     * @param monthLastUpdate
     * @param drinkingWaterQualityAccessibility
     * @param comfortableToSpendTime
     * @param airQuality
     * @param name
     * @param pm10
     * @param pm25
     * @param cleanAndTidy
     * @param yearLastUpdate
     */
    public ClimateData(Float greenAndParksQuality, Integer pm25, Float comfortableToSpendTime, Integer pm10, Float airQuality, Float garbageDisposalSatisfaction, Float indexPollution, Float drinkingWaterQualityAccessibility, String name, Integer monthLastUpdate, Float cleanAndTidy, Float noiseAndLightPollution, Integer contributors, Integer yearLastUpdate, Float waterPollution, Integer cityId) {
        super();
        this.greenAndParksQuality = greenAndParksQuality;
        this.pm25 = pm25;
        this.comfortableToSpendTime = comfortableToSpendTime;
        this.pm10 = pm10;
        this.airQuality = airQuality;
        this.garbageDisposalSatisfaction = garbageDisposalSatisfaction;
        this.indexPollution = indexPollution;
        this.drinkingWaterQualityAccessibility = drinkingWaterQualityAccessibility;
        this.name = name;
        this.monthLastUpdate = monthLastUpdate;
        this.cleanAndTidy = cleanAndTidy;
        this.noiseAndLightPollution = noiseAndLightPollution;
        this.contributors = contributors;
        this.yearLastUpdate = yearLastUpdate;
        this.waterPollution = waterPollution;
        this.cityId = cityId;
    }

    public Float getGreenAndParksQuality() {
        return greenAndParksQuality;
    }

    public void setGreenAndParksQuality(Float greenAndParksQuality) {
        this.greenAndParksQuality = greenAndParksQuality;
    }

    public Integer getPm25() {
        return pm25;
    }

    public void setPm25(Integer pm25) {
        this.pm25 = pm25;
    }

    public Float getComfortableToSpendTime() {
        return comfortableToSpendTime;
    }

    public void setComfortableToSpendTime(Float comfortableToSpendTime) {
        this.comfortableToSpendTime = comfortableToSpendTime;
    }

    public Integer getPm10() {
        return pm10;
    }

    public void setPm10(Integer pm10) {
        this.pm10 = pm10;
    }

    public Float getAirQuality() {
        return airQuality;
    }

    public void setAirQuality(Float airQuality) {
        this.airQuality = airQuality;
    }

    public Float getGarbageDisposalSatisfaction() {
        return garbageDisposalSatisfaction;
    }

    public void setGarbageDisposalSatisfaction(Float garbageDisposalSatisfaction) {
        this.garbageDisposalSatisfaction = garbageDisposalSatisfaction;
    }

    public Float getIndexPollution() {
        return indexPollution;
    }

    public void setIndexPollution(Float indexPollution) {
        this.indexPollution = indexPollution;
    }

    public Float getDrinkingWaterQualityAccessibility() {
        return drinkingWaterQualityAccessibility;
    }

    public void setDrinkingWaterQualityAccessibility(Float drinkingWaterQualityAccessibility) {
        this.drinkingWaterQualityAccessibility = drinkingWaterQualityAccessibility;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMonthLastUpdate() {
        return monthLastUpdate;
    }

    public void setMonthLastUpdate(Integer monthLastUpdate) {
        this.monthLastUpdate = monthLastUpdate;
    }

    public Float getCleanAndTidy() {
        return cleanAndTidy;
    }

    public void setCleanAndTidy(Float cleanAndTidy) {
        this.cleanAndTidy = cleanAndTidy;
    }

    public Float getNoiseAndLightPollution() {
        return noiseAndLightPollution;
    }

    public void setNoiseAndLightPollution(Float noiseAndLightPollution) {
        this.noiseAndLightPollution = noiseAndLightPollution;
    }

    public Integer getContributors() {
        return contributors;
    }

    public void setContributors(Integer contributors) {
        this.contributors = contributors;
    }

    public Integer getYearLastUpdate() {
        return yearLastUpdate;
    }

    public void setYearLastUpdate(Integer yearLastUpdate) {
        this.yearLastUpdate = yearLastUpdate;
    }

    public Float getWaterPollution() {
        return waterPollution;
    }

    public void setWaterPollution(Float waterPollution) {
        this.waterPollution = waterPollution;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(greenAndParksQuality);
        dest.writeValue(pm25);
        dest.writeValue(comfortableToSpendTime);
        dest.writeValue(pm10);
        dest.writeValue(airQuality);
        dest.writeValue(garbageDisposalSatisfaction);
        dest.writeValue(indexPollution);
        dest.writeValue(drinkingWaterQualityAccessibility);
        dest.writeValue(name);
        dest.writeValue(monthLastUpdate);
        dest.writeValue(cleanAndTidy);
        dest.writeValue(noiseAndLightPollution);
        dest.writeValue(contributors);
        dest.writeValue(yearLastUpdate);
        dest.writeValue(waterPollution);
        dest.writeValue(cityId);
    }

    public int describeContents() {
        return 0;
    }

}