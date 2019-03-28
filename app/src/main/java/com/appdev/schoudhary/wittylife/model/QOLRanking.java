package com.appdev.schoudhary.wittylife.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class QOLRanking implements Parcelable {

    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("traffic_time_index")
    @Expose
    private Double trafficTimeIndex;
    @SerializedName("city_name")
    @Expose
    private String cityName;
    @SerializedName("quality_of_life_index")
    @Expose
    private Double qualityOfLifeIndex;
    @SerializedName("healthcare_index")
    @Expose
    private Double healthcareIndex;
    @SerializedName("purchasing_power_incl_rent_index")
    @Expose
    private Double purchasingPowerInclRentIndex;
    @SerializedName("house_price_to_income_ratio")
    @Expose
    private Double housePriceToIncomeRatio;
    @SerializedName("pollution_index")
    @Expose
    private Double pollutionIndex;
    @SerializedName("climate_index")
    @Expose
    private Double climateIndex;
    @SerializedName("safety_index")
    @Expose
    private Double safetyIndex;
    @SerializedName("city_id")
    @Expose
    private Integer cityId;
    @SerializedName("cpi_index")
    @Expose
    private Double cpiIndex;

    public final static Parcelable.Creator<QOLRanking> CREATOR = new Creator<QOLRanking>() {


        @SuppressWarnings({
                "unchecked"
        })
        public QOLRanking createFromParcel(Parcel in) {
            return new QOLRanking(in);
        }

        public QOLRanking[] newArray(int size) {
            return (new QOLRanking[size]);
        }

    };

    private QOLRanking(Parcel in) {
        this.country = ((String) in.readValue((String.class.getClassLoader())));
        this.trafficTimeIndex = ((Double) in.readValue((Double.class.getClassLoader())));
        this.cityName = ((String) in.readValue((String.class.getClassLoader())));
        this.qualityOfLifeIndex = ((Double) in.readValue((Double.class.getClassLoader())));
        this.healthcareIndex = ((Double) in.readValue((Double.class.getClassLoader())));
        this.purchasingPowerInclRentIndex = ((Double) in.readValue((Double.class.getClassLoader())));
        this.housePriceToIncomeRatio = ((Double) in.readValue((Double.class.getClassLoader())));
        this.pollutionIndex = ((Double) in.readValue((Double.class.getClassLoader())));
        this.climateIndex = ((Double) in.readValue((Double.class.getClassLoader())));
        this.safetyIndex = ((Double) in.readValue((Double.class.getClassLoader())));
        this.cityId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.cpiIndex = ((Double) in.readValue((Double.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     */
    public QOLRanking() {
    }

    /**
     * @param healthcareIndex
     * @param pollutionIndex
     * @param cpiIndex
     * @param cityId
     * @param cityName
     * @param trafficTimeIndex
     * @param safetyIndex
     * @param purchasingPowerInclRentIndex
     * @param qualityOfLifeIndex
     * @param climateIndex
     * @param housePriceToIncomeRatio
     * @param country
     */
    public QOLRanking(String country, Double trafficTimeIndex, String cityName, Double qualityOfLifeIndex, Double healthcareIndex, Double purchasingPowerInclRentIndex, Double housePriceToIncomeRatio, Double pollutionIndex, Double climateIndex, Double safetyIndex, Integer cityId, Double cpiIndex) {
        super();
        this.country = country;
        this.trafficTimeIndex = trafficTimeIndex;
        this.cityName = cityName;
        this.qualityOfLifeIndex = qualityOfLifeIndex;
        this.healthcareIndex = healthcareIndex;
        this.purchasingPowerInclRentIndex = purchasingPowerInclRentIndex;
        this.housePriceToIncomeRatio = housePriceToIncomeRatio;
        this.pollutionIndex = pollutionIndex;
        this.climateIndex = climateIndex;
        this.safetyIndex = safetyIndex;
        this.cityId = cityId;
        this.cpiIndex = cpiIndex;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Double getTrafficTimeIndex() {
        return trafficTimeIndex;
    }

    public void setTrafficTimeIndex(Double trafficTimeIndex) {
        this.trafficTimeIndex = trafficTimeIndex;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Double getQualityOfLifeIndex() {
        return qualityOfLifeIndex;
    }

    public void setQualityOfLifeIndex(Double qualityOfLifeIndex) {
        this.qualityOfLifeIndex = qualityOfLifeIndex;
    }

    public Double getHealthcareIndex() {
        return healthcareIndex;
    }

    public void setHealthcareIndex(Double healthcareIndex) {
        this.healthcareIndex = healthcareIndex;
    }

    public Double getPurchasingPowerInclRentIndex() {
        return purchasingPowerInclRentIndex;
    }

    public void setPurchasingPowerInclRentIndex(Double purchasingPowerInclRentIndex) {
        this.purchasingPowerInclRentIndex = purchasingPowerInclRentIndex;
    }

    public Double getHousePriceToIncomeRatio() {
        return housePriceToIncomeRatio;
    }

    public void setHousePriceToIncomeRatio(Double housePriceToIncomeRatio) {
        this.housePriceToIncomeRatio = housePriceToIncomeRatio;
    }

    public Double getPollutionIndex() {
        return pollutionIndex;
    }

    public void setPollutionIndex(Double pollutionIndex) {
        this.pollutionIndex = pollutionIndex;
    }

    public Double getClimateIndex() {
        return climateIndex;
    }

    public void setClimateIndex(Double climateIndex) {
        this.climateIndex = climateIndex;
    }

    public Double getSafetyIndex() {
        return safetyIndex;
    }

    public void setSafetyIndex(Double safetyIndex) {
        this.safetyIndex = safetyIndex;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Double getCpiIndex() {
        return cpiIndex;
    }

    public void setCpiIndex(Double cpiIndex) {
        this.cpiIndex = cpiIndex;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(country);
        dest.writeValue(trafficTimeIndex);
        dest.writeValue(cityName);
        dest.writeValue(qualityOfLifeIndex);
        dest.writeValue(healthcareIndex);
        dest.writeValue(purchasingPowerInclRentIndex);
        dest.writeValue(housePriceToIncomeRatio);
        dest.writeValue(pollutionIndex);
        dest.writeValue(climateIndex);
        dest.writeValue(safetyIndex);
        dest.writeValue(cityId);
        dest.writeValue(cpiIndex);
    }

    public int describeContents() {
        return 0;
    }

}
