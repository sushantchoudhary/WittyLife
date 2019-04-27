package com.appdev.schoudhary.wittylife.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "destination_indices")
public class DestinationIndices implements Parcelable
{

    @SerializedName("crime_index")
    @Expose
    private Float crimeIndex;
    @SerializedName("traffic_time_index")
    @Expose
    private Float trafficTimeIndex;
    @SerializedName("cpi_and_rent_index")
    @Expose
    private Float cpiAndRentIndex;
    @SerializedName("purchasing_power_incl_rent_index")
    @Expose
    private Float purchasingPowerInclRentIndex;
    @SerializedName("restaurant_price_index")
    @Expose
    private Float restaurantPriceIndex;
    @SerializedName("property_price_to_income_ratio")
    @Expose
    private Float propertyPriceToIncomeRatio;
    @SerializedName("climate_index")
    @Expose
    private Float climateIndex;
    @SerializedName("safety_index")
    @Expose
    private Float safetyIndex;
    @SerializedName("traffic_co2_index")
    @Expose
    private Float trafficCo2Index;
    @SerializedName("cpi_index")
    @Expose
    private Float cpiIndex;
    @SerializedName("traffic_inefficiency_index")
    @Expose
    private Float trafficInefficiencyIndex;
    @SerializedName("quality_of_life_index")
    @Expose
    private Float qualityOfLifeIndex;
    @SerializedName("rent_index")
    @Expose
    private Float rentIndex;
    @SerializedName("health_care_index")
    @Expose
    private Float healthCareIndex;
    @SerializedName("traffic_index")
    @Expose
    private Float trafficIndex;
    @SerializedName("groceries_index")
    @Expose
    private Float groceriesIndex;
    @SerializedName("name")
    @Expose
    @PrimaryKey
    @ColumnInfo(name = "city_name")
    @NonNull
    private String name;
    @SerializedName("pollution_index")
    @Expose
    private Float pollutionIndex;
    public final static Parcelable.Creator<DestinationIndices> CREATOR = new Creator<DestinationIndices>() {


        @SuppressWarnings({
                "unchecked"
        })
        public DestinationIndices createFromParcel(Parcel in) {
            return new DestinationIndices(in);
        }

        public DestinationIndices[] newArray(int size) {
            return (new DestinationIndices[size]);
        }

    }
            ;

    @Ignore
    private DestinationIndices(Parcel in) {
        this.crimeIndex = ((Float) in.readValue((Float.class.getClassLoader())));
        this.trafficTimeIndex = ((Float) in.readValue((Float.class.getClassLoader())));
        this.cpiAndRentIndex = ((Float) in.readValue((Float.class.getClassLoader())));
        this.purchasingPowerInclRentIndex = ((Float) in.readValue((Float.class.getClassLoader())));
        this.restaurantPriceIndex = ((Float) in.readValue((Float.class.getClassLoader())));
        this.propertyPriceToIncomeRatio = ((Float) in.readValue((Float.class.getClassLoader())));
        this.climateIndex = ((Float) in.readValue((Float.class.getClassLoader())));
        this.safetyIndex = ((Float) in.readValue((Float.class.getClassLoader())));
        this.trafficCo2Index = ((Float) in.readValue((Float.class.getClassLoader())));
        this.cpiIndex = ((Float) in.readValue((Float.class.getClassLoader())));
        this.trafficInefficiencyIndex = ((Float) in.readValue((Float.class.getClassLoader())));
        this.qualityOfLifeIndex = ((Float) in.readValue((Float.class.getClassLoader())));
        this.rentIndex = ((Float) in.readValue((Float.class.getClassLoader())));
        this.healthCareIndex = ((Float) in.readValue((Float.class.getClassLoader())));
        this.trafficIndex = ((Float) in.readValue((Float.class.getClassLoader())));
        this.groceriesIndex = ((Float) in.readValue((Float.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.pollutionIndex = ((Float) in.readValue((Float.class.getClassLoader())));
    }

//    /**
//     * No args constructor for use in serialization
//     *
//     */
//    public DestinationIndices() {
//    }

    /**
     *
     * @param cpiIndex
     * @param safetyIndex
     * @param groceriesIndex
     * @param healthCareIndex
     * @param rentIndex
     * @param climateIndex
     * @param trafficCo2Index
     * @param crimeIndex
     * @param pollutionIndex
     * @param cpiAndRentIndex
     * @param name
     * @param trafficTimeIndex
     * @param propertyPriceToIncomeRatio
     * @param trafficIndex
     * @param purchasingPowerInclRentIndex
     * @param trafficInefficiencyIndex
     * @param qualityOfLifeIndex
     * @param restaurantPriceIndex
     */
    public DestinationIndices(Float crimeIndex, Float trafficTimeIndex, Float cpiAndRentIndex, Float purchasingPowerInclRentIndex, Float restaurantPriceIndex, Float propertyPriceToIncomeRatio, Float climateIndex, Float safetyIndex, Float trafficCo2Index, Float cpiIndex, Float trafficInefficiencyIndex, Float qualityOfLifeIndex, Float rentIndex, Float healthCareIndex, Float trafficIndex, Float groceriesIndex, String name, Float pollutionIndex) {
        super();
        this.crimeIndex = crimeIndex;
        this.trafficTimeIndex = trafficTimeIndex;
        this.cpiAndRentIndex = cpiAndRentIndex;
        this.purchasingPowerInclRentIndex = purchasingPowerInclRentIndex;
        this.restaurantPriceIndex = restaurantPriceIndex;
        this.propertyPriceToIncomeRatio = propertyPriceToIncomeRatio;
        this.climateIndex = climateIndex;
        this.safetyIndex = safetyIndex;
        this.trafficCo2Index = trafficCo2Index;
        this.cpiIndex = cpiIndex;
        this.trafficInefficiencyIndex = trafficInefficiencyIndex;
        this.qualityOfLifeIndex = qualityOfLifeIndex;
        this.rentIndex = rentIndex;
        this.healthCareIndex = healthCareIndex;
        this.trafficIndex = trafficIndex;
        this.groceriesIndex = groceriesIndex;
        this.name = name;
        this.pollutionIndex = pollutionIndex;
    }

    public Float getCrimeIndex() {
        return crimeIndex;
    }

    public void setCrimeIndex(Float crimeIndex) {
        this.crimeIndex = crimeIndex;
    }

    public Float getTrafficTimeIndex() {
        return trafficTimeIndex;
    }

    public void setTrafficTimeIndex(Float trafficTimeIndex) {
        this.trafficTimeIndex = trafficTimeIndex;
    }

    public Float getCpiAndRentIndex() {
        return cpiAndRentIndex;
    }

    public void setCpiAndRentIndex(Float cpiAndRentIndex) {
        this.cpiAndRentIndex = cpiAndRentIndex;
    }

    public Float getPurchasingPowerInclRentIndex() {
        return purchasingPowerInclRentIndex;
    }

    public void setPurchasingPowerInclRentIndex(Float purchasingPowerInclRentIndex) {
        this.purchasingPowerInclRentIndex = purchasingPowerInclRentIndex;
    }

    public Float getRestaurantPriceIndex() {
        return restaurantPriceIndex;
    }

    public void setRestaurantPriceIndex(Float restaurantPriceIndex) {
        this.restaurantPriceIndex = restaurantPriceIndex;
    }

    public Float getPropertyPriceToIncomeRatio() {
        return propertyPriceToIncomeRatio;
    }

    public void setPropertyPriceToIncomeRatio(Float propertyPriceToIncomeRatio) {
        this.propertyPriceToIncomeRatio = propertyPriceToIncomeRatio;
    }

    public Float getClimateIndex() {
        return climateIndex;
    }

    public void setClimateIndex(Float climateIndex) {
        this.climateIndex = climateIndex;
    }

    public Float getSafetyIndex() {
        return safetyIndex;
    }

    public void setSafetyIndex(Float safetyIndex) {
        this.safetyIndex = safetyIndex;
    }

    public Float getTrafficCo2Index() {
        return trafficCo2Index;
    }

    public void setTrafficCo2Index(Float trafficCo2Index) {
        this.trafficCo2Index = trafficCo2Index;
    }

    public Float getCpiIndex() {
        return cpiIndex;
    }

    public void setCpiIndex(Float cpiIndex) {
        this.cpiIndex = cpiIndex;
    }

    public Float getTrafficInefficiencyIndex() {
        return trafficInefficiencyIndex;
    }

    public void setTrafficInefficiencyIndex(Float trafficInefficiencyIndex) {
        this.trafficInefficiencyIndex = trafficInefficiencyIndex;
    }

    public Float getQualityOfLifeIndex() {
        return qualityOfLifeIndex;
    }

    public void setQualityOfLifeIndex(Float qualityOfLifeIndex) {
        this.qualityOfLifeIndex = qualityOfLifeIndex;
    }

    public Float getRentIndex() {
        return rentIndex;
    }

    public void setRentIndex(Float rentIndex) {
        this.rentIndex = rentIndex;
    }

    public Float getHealthCareIndex() {
        return healthCareIndex;
    }

    public void setHealthCareIndex(Float healthCareIndex) {
        this.healthCareIndex = healthCareIndex;
    }

    public Float getTrafficIndex() {
        return trafficIndex;
    }

    public void setTrafficIndex(Float trafficIndex) {
        this.trafficIndex = trafficIndex;
    }

    public Float getGroceriesIndex() {
        return groceriesIndex;
    }

    public void setGroceriesIndex(Float groceriesIndex) {
        this.groceriesIndex = groceriesIndex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getPollutionIndex() {
        return pollutionIndex;
    }

    public void setPollutionIndex(Float pollutionIndex) {
        this.pollutionIndex = pollutionIndex;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(crimeIndex);
        dest.writeValue(trafficTimeIndex);
        dest.writeValue(cpiAndRentIndex);
        dest.writeValue(purchasingPowerInclRentIndex);
        dest.writeValue(restaurantPriceIndex);
        dest.writeValue(propertyPriceToIncomeRatio);
        dest.writeValue(climateIndex);
        dest.writeValue(safetyIndex);
        dest.writeValue(trafficCo2Index);
        dest.writeValue(cpiIndex);
        dest.writeValue(trafficInefficiencyIndex);
        dest.writeValue(qualityOfLifeIndex);
        dest.writeValue(rentIndex);
        dest.writeValue(healthCareIndex);
        dest.writeValue(trafficIndex);
        dest.writeValue(groceriesIndex);
        dest.writeValue(name);
        dest.writeValue(pollutionIndex);
    }

    public int describeContents() {
        return 0;
    }

}
