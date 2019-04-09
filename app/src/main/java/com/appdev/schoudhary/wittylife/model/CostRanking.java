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

@Entity(tableName = "costranking")
public class CostRanking implements Parcelable
{

    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("city_name")
    @Expose
    private String cityName;
    @SerializedName("cpi_and_rent_index")
    @Expose
    private Double cpiAndRentIndex;
    @SerializedName("rent_index")
    @Expose
    private Double rentIndex;
    @SerializedName("purchasing_power_incl_rent_index")
    @Expose
    private Double purchasingPowerInclRentIndex;
    @SerializedName("restaurant_price_index")
    @Expose
    private Double restaurantPriceIndex;
    @SerializedName("groceries_index")
    @Expose
    private Double groceriesIndex;
    public final static Parcelable.Creator<CostRanking> CREATOR = new Creator<CostRanking>() {


        @SuppressWarnings({
                "unchecked"
        })
        public CostRanking createFromParcel(Parcel in) {
            return new CostRanking(in);
        }

        public CostRanking[] newArray(int size) {
            return (new CostRanking[size]);
        }

    };
    @SerializedName("cpi_index")
    @Expose
    private Double cpiIndex;
    @SerializedName("city_id")
    @Expose
    @PrimaryKey
    @ColumnInfo(name = "city_id")
    private Integer cityId;

    @Ignore
    private CostRanking(Parcel in) {
        this.country = ((String) in.readValue((String.class.getClassLoader())));
        this.cityName = ((String) in.readValue((String.class.getClassLoader())));
        this.cpiAndRentIndex = ((Double) in.readValue((Double.class.getClassLoader())));
        this.rentIndex = ((Double) in.readValue((Double.class.getClassLoader())));
        this.purchasingPowerInclRentIndex = ((Double) in.readValue((Double.class.getClassLoader())));
        this.restaurantPriceIndex = ((Double) in.readValue((Double.class.getClassLoader())));
        this.groceriesIndex = ((Double) in.readValue((Double.class.getClassLoader())));
        this.cityId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.cpiIndex = ((Double) in.readValue((Double.class.getClassLoader())));
    }

    /**
     *
     * @param cpiIndex
     * @param cityId
     * @param cpiAndRentIndex
     * @param cityName
     * @param groceriesIndex
     * @param purchasingPowerInclRentIndex
     * @param restaurantPriceIndex
     * @param rentIndex
     * @param country
     */
    public CostRanking(String country, String cityName, Double cpiAndRentIndex, Double rentIndex, Double purchasingPowerInclRentIndex, Double restaurantPriceIndex, Double groceriesIndex, Integer cityId, Double cpiIndex) {
        super();
        this.country = country;
        this.cityName = cityName;
        this.cpiAndRentIndex = cpiAndRentIndex;
        this.rentIndex = rentIndex;
        this.purchasingPowerInclRentIndex = purchasingPowerInclRentIndex;
        this.restaurantPriceIndex = restaurantPriceIndex;
        this.groceriesIndex = groceriesIndex;
        this.cityId = cityId;
        this.cpiIndex = cpiIndex;
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

    public Double getCpiAndRentIndex() {
        return cpiAndRentIndex;
    }

    public void setCpiAndRentIndex(Double cpiAndRentIndex) {
        this.cpiAndRentIndex = cpiAndRentIndex;
    }

    public Double getRentIndex() {
        return rentIndex;
    }

    public void setRentIndex(Double rentIndex) {
        this.rentIndex = rentIndex;
    }

    public Double getPurchasingPowerInclRentIndex() {
        return purchasingPowerInclRentIndex;
    }

    public void setPurchasingPowerInclRentIndex(Double purchasingPowerInclRentIndex) {
        this.purchasingPowerInclRentIndex = purchasingPowerInclRentIndex;
    }

    public Double getRestaurantPriceIndex() {
        return restaurantPriceIndex;
    }

    public void setRestaurantPriceIndex(Double restaurantPriceIndex) {
        this.restaurantPriceIndex = restaurantPriceIndex;
    }

    public Double getGroceriesIndex() {
        return groceriesIndex;
    }

    public void setGroceriesIndex(Double groceriesIndex) {
        this.groceriesIndex = groceriesIndex;
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
        dest.writeValue(cityName);
        dest.writeValue(cpiAndRentIndex);
        dest.writeValue(rentIndex);
        dest.writeValue(purchasingPowerInclRentIndex);
        dest.writeValue(restaurantPriceIndex);
        dest.writeValue(groceriesIndex);
        dest.writeValue(cityId);
        dest.writeValue(cpiIndex);
    }

    public int describeContents() {
        return 0;
    }

}