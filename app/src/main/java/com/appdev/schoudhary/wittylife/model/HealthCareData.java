package com.appdev.schoudhary.wittylife.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HealthCareData implements Parcelable
{

    @SerializedName("skill_and_competency")
    @Expose
    private Float skillAndCompetency;
    @SerializedName("cost")
    @Expose
    private Float cost;
    @SerializedName("responsiveness_waitings")
    @Expose
    private Float responsivenessWaitings;
    @SerializedName("index_healthcare")
    @Expose
    private Float indexHealthcare;
    @SerializedName("speed")
    @Expose
    private Float speed;
    @SerializedName("accuracy_and_completeness")
    @Expose
    private Float accuracyAndCompleteness;
    @SerializedName("friendliness_and_courtesy")
    @Expose
    private Float friendlinessAndCourtesy;
    @SerializedName("insurance_type")
    @Expose
    private InsuranceType insuranceType;
    @SerializedName("modern_equipment")
    @Expose
    private Float modernEquipment;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("monthLastUpdate")
    @Expose
    private Integer monthLastUpdate;
    @SerializedName("location")
    @Expose
    private Float location;
    @SerializedName("contributors")
    @Expose
    private Integer contributors;
    @SerializedName("yearLastUpdate")
    @Expose
    private Integer yearLastUpdate;
    @SerializedName("city_id")
    @Expose
    private Integer cityId;
    public final static Parcelable.Creator<HealthCareData> CREATOR = new Creator<HealthCareData>() {


        @SuppressWarnings({
                "unchecked"
        })
        public HealthCareData createFromParcel(Parcel in) {
            return new HealthCareData(in);
        }

        public HealthCareData[] newArray(int size) {
            return (new HealthCareData[size]);
        }

    }
            ;

    private HealthCareData(Parcel in) {
        this.skillAndCompetency = ((Float) in.readValue((Float.class.getClassLoader())));
        this.cost = ((Float) in.readValue((Float.class.getClassLoader())));
        this.responsivenessWaitings = ((Float) in.readValue((Float.class.getClassLoader())));
        this.indexHealthcare = ((Float) in.readValue((Float.class.getClassLoader())));
        this.speed = ((Float) in.readValue((Float.class.getClassLoader())));
        this.accuracyAndCompleteness = ((Float) in.readValue((Float.class.getClassLoader())));
        this.friendlinessAndCourtesy = ((Float) in.readValue((Float.class.getClassLoader())));
        this.insuranceType = ((InsuranceType) in.readValue((InsuranceType.class.getClassLoader())));
        this.modernEquipment = ((Float) in.readValue((Float.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.monthLastUpdate = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.location = ((Float) in.readValue((Float.class.getClassLoader())));
        this.contributors = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.yearLastUpdate = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.cityId = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     *
     */
    public HealthCareData() {
    }

    /**
     *
     * @param contributors
     * @param indexHealthcare
     * @param cityId
     * @param speed
     * @param location
     * @param accuracyAndCompleteness
     * @param skillAndCompetency
     * @param responsivenessWaitings
     * @param cost
     * @param monthLastUpdate
     * @param friendlinessAndCourtesy
     * @param name
     * @param insuranceType
     * @param modernEquipment
     * @param yearLastUpdate
     */
    public HealthCareData(Float skillAndCompetency, Float cost, Float responsivenessWaitings, Float indexHealthcare, Float speed, Float accuracyAndCompleteness, Float friendlinessAndCourtesy, InsuranceType insuranceType, Float modernEquipment, String name, Integer monthLastUpdate, Float location, Integer contributors, Integer yearLastUpdate, Integer cityId) {
        super();
        this.skillAndCompetency = skillAndCompetency;
        this.cost = cost;
        this.responsivenessWaitings = responsivenessWaitings;
        this.indexHealthcare = indexHealthcare;
        this.speed = speed;
        this.accuracyAndCompleteness = accuracyAndCompleteness;
        this.friendlinessAndCourtesy = friendlinessAndCourtesy;
        this.insuranceType = insuranceType;
        this.modernEquipment = modernEquipment;
        this.name = name;
        this.monthLastUpdate = monthLastUpdate;
        this.location = location;
        this.contributors = contributors;
        this.yearLastUpdate = yearLastUpdate;
        this.cityId = cityId;
    }

    public Float getSkillAndCompetency() {
        return skillAndCompetency;
    }

    public void setSkillAndCompetency(Float skillAndCompetency) {
        this.skillAndCompetency = skillAndCompetency;
    }

    public Float getCost() {
        return cost;
    }

    public void setCost(Float cost) {
        this.cost = cost;
    }

    public Float getResponsivenessWaitings() {
        return responsivenessWaitings;
    }

    public void setResponsivenessWaitings(Float responsivenessWaitings) {
        this.responsivenessWaitings = responsivenessWaitings;
    }

    public Float getIndexHealthcare() {
        return indexHealthcare;
    }

    public void setIndexHealthcare(Float indexHealthcare) {
        this.indexHealthcare = indexHealthcare;
    }

    public Float getSpeed() {
        return speed;
    }

    public void setSpeed(Float speed) {
        this.speed = speed;
    }

    public Float getAccuracyAndCompleteness() {
        return accuracyAndCompleteness;
    }

    public void setAccuracyAndCompleteness(Float accuracyAndCompleteness) {
        this.accuracyAndCompleteness = accuracyAndCompleteness;
    }

    public Float getFriendlinessAndCourtesy() {
        return friendlinessAndCourtesy;
    }

    public void setFriendlinessAndCourtesy(Float friendlinessAndCourtesy) {
        this.friendlinessAndCourtesy = friendlinessAndCourtesy;
    }

    public InsuranceType getInsuranceType() {
        return insuranceType;
    }

    public void setInsuranceType(InsuranceType insuranceType) {
        this.insuranceType = insuranceType;
    }

    public Float getModernEquipment() {
        return modernEquipment;
    }

    public void setModernEquipment(Float modernEquipment) {
        this.modernEquipment = modernEquipment;
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

    public Float getLocation() {
        return location;
    }

    public void setLocation(Float location) {
        this.location = location;
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

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(skillAndCompetency);
        dest.writeValue(cost);
        dest.writeValue(responsivenessWaitings);
        dest.writeValue(indexHealthcare);
        dest.writeValue(speed);
        dest.writeValue(accuracyAndCompleteness);
        dest.writeValue(friendlinessAndCourtesy);
        dest.writeValue(insuranceType);
        dest.writeValue(modernEquipment);
        dest.writeValue(name);
        dest.writeValue(monthLastUpdate);
        dest.writeValue(location);
        dest.writeValue(contributors);
        dest.writeValue(yearLastUpdate);
        dest.writeValue(cityId);
    }

    public int describeContents() {
        return 0;
    }

}