package com.appdev.schoudhary.wittylife.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "crime_data")
public class CrimeData implements Parcelable
{

    @SerializedName("worried_attacked")
    @Expose
    private Float worriedAttacked;
    @SerializedName("level_of_crime")
    @Expose
    private Float levelOfCrime;
    @SerializedName("problem_property_crimes")
    @Expose
    private Float problemPropertyCrimes;
    @SerializedName("safe_alone_night")
    @Expose
    private Float safeAloneNight;
    @SerializedName("worried_skin_ethnic_religion")
    @Expose
    private Float worriedSkinEthnicReligion;
    @SerializedName("index_safety")
    @Expose
    private Float indexSafety;
    @SerializedName("worried_car_stolen")
    @Expose
    private Float worriedCarStolen;
    @SerializedName("worried_home_broken")
    @Expose
    private Float worriedHomeBroken;
    @SerializedName("worried_things_car_stolen")
    @Expose
    private Float worriedThingsCarStolen;
    @SerializedName("crime_increasing")
    @Expose
    private Float crimeIncreasing;
    @SerializedName("problem_corruption_bribery")
    @Expose
    private Float problemCorruptionBribery;
    @SerializedName("safe_alone_daylight")
    @Expose
    private Float safeAloneDaylight;
    @SerializedName("problem_drugs")
    @Expose
    private Float problemDrugs;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("worried_insulted")
    @Expose
    private Float worriedInsulted;
    @SerializedName("monthLastUpdate")
    @Expose
    private Integer monthLastUpdate;
    @SerializedName("contributors")
    @Expose
    private Integer contributors;
    @SerializedName("yearLastUpdate")
    @Expose
    private Integer yearLastUpdate;
    @SerializedName("index_crime")
    @Expose
    private Float indexCrime;
    @SerializedName("problem_violent_crimes")
    @Expose
    private Float problemViolentCrimes;
    @SerializedName("city_id")
    @Expose
    private Integer cityId;
    @SerializedName("worried_mugged_robbed")
    @Expose
    private Float worriedMuggedRobbed;
    public final static Parcelable.Creator<CrimeData> CREATOR = new Creator<CrimeData>() {


        @SuppressWarnings({
                "unchecked"
        })
        public CrimeData createFromParcel(Parcel in) {
            return new CrimeData(in);
        }

        public CrimeData[] newArray(int size) {
            return (new CrimeData[size]);
        }

    };

    @Ignore
    private CrimeData(Parcel in) {
        this.worriedAttacked = ((Float) in.readValue((Float.class.getClassLoader())));
        this.levelOfCrime = ((Float) in.readValue((Float.class.getClassLoader())));
        this.problemPropertyCrimes = ((Float) in.readValue((Float.class.getClassLoader())));
        this.safeAloneNight = ((Float) in.readValue((Float.class.getClassLoader())));
        this.worriedSkinEthnicReligion = ((Float) in.readValue((Float.class.getClassLoader())));
        this.indexSafety = ((Float) in.readValue((Float.class.getClassLoader())));
        this.worriedCarStolen = ((Float) in.readValue((Float.class.getClassLoader())));
        this.worriedHomeBroken = ((Float) in.readValue((Float.class.getClassLoader())));
        this.worriedThingsCarStolen = ((Float) in.readValue((Float.class.getClassLoader())));
        this.crimeIncreasing = ((Float) in.readValue((Float.class.getClassLoader())));
        this.problemCorruptionBribery = ((Float) in.readValue((Float.class.getClassLoader())));
        this.safeAloneDaylight = ((Float) in.readValue((Float.class.getClassLoader())));
        this.problemDrugs = ((Float) in.readValue((Float.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.worriedInsulted = ((Float) in.readValue((Float.class.getClassLoader())));
        this.monthLastUpdate = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.contributors = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.yearLastUpdate = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.indexCrime = ((Float) in.readValue((Float.class.getClassLoader())));
        this.problemViolentCrimes = ((Float) in.readValue((Float.class.getClassLoader())));
        this.cityId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.worriedMuggedRobbed = ((Float) in.readValue((Float.class.getClassLoader())));
    }


    /**
     *
     * @param worriedThingsCarStolen
     * @param contributors
     * @param worriedInsulted
     * @param indexSafety
     * @param cityId
     * @param levelOfCrime
     * @param worriedSkinEthnicReligion
     * @param problemCorruptionBribery
     * @param worriedHomeBroken
     * @param worriedAttacked
     * @param monthLastUpdate
     * @param indexCrime
     * @param crimeIncreasing
     * @param name
     * @param problemViolentCrimes
     * @param worriedCarStolen
     * @param safeAloneDaylight
     * @param problemDrugs
     * @param worriedMuggedRobbed
     * @param safeAloneNight
     * @param yearLastUpdate
     * @param problemPropertyCrimes
     */
    public CrimeData(Float worriedAttacked, Float levelOfCrime, Float problemPropertyCrimes, Float safeAloneNight, Float worriedSkinEthnicReligion, Float indexSafety, Float worriedCarStolen, Float worriedHomeBroken, Float worriedThingsCarStolen, Float crimeIncreasing, Float problemCorruptionBribery, Float safeAloneDaylight, Float problemDrugs, String name, Float worriedInsulted, Integer monthLastUpdate, Integer contributors, Integer yearLastUpdate, Float indexCrime, Float problemViolentCrimes, Integer cityId, Float worriedMuggedRobbed) {
        super();
        this.worriedAttacked = worriedAttacked;
        this.levelOfCrime = levelOfCrime;
        this.problemPropertyCrimes = problemPropertyCrimes;
        this.safeAloneNight = safeAloneNight;
        this.worriedSkinEthnicReligion = worriedSkinEthnicReligion;
        this.indexSafety = indexSafety;
        this.worriedCarStolen = worriedCarStolen;
        this.worriedHomeBroken = worriedHomeBroken;
        this.worriedThingsCarStolen = worriedThingsCarStolen;
        this.crimeIncreasing = crimeIncreasing;
        this.problemCorruptionBribery = problemCorruptionBribery;
        this.safeAloneDaylight = safeAloneDaylight;
        this.problemDrugs = problemDrugs;
        this.name = name;
        this.worriedInsulted = worriedInsulted;
        this.monthLastUpdate = monthLastUpdate;
        this.contributors = contributors;
        this.yearLastUpdate = yearLastUpdate;
        this.indexCrime = indexCrime;
        this.problemViolentCrimes = problemViolentCrimes;
        this.cityId = cityId;
        this.worriedMuggedRobbed = worriedMuggedRobbed;
    }

    public Float getWorriedAttacked() {
        return worriedAttacked;
    }

    public void setWorriedAttacked(Float worriedAttacked) {
        this.worriedAttacked = worriedAttacked;
    }

    public Float getLevelOfCrime() {
        return levelOfCrime;
    }

    public void setLevelOfCrime(Float levelOfCrime) {
        this.levelOfCrime = levelOfCrime;
    }

    public Float getProblemPropertyCrimes() {
        return problemPropertyCrimes;
    }

    public void setProblemPropertyCrimes(Float problemPropertyCrimes) {
        this.problemPropertyCrimes = problemPropertyCrimes;
    }

    public Float getSafeAloneNight() {
        return safeAloneNight;
    }

    public void setSafeAloneNight(Float safeAloneNight) {
        this.safeAloneNight = safeAloneNight;
    }

    public Float getWorriedSkinEthnicReligion() {
        return worriedSkinEthnicReligion;
    }

    public void setWorriedSkinEthnicReligion(Float worriedSkinEthnicReligion) {
        this.worriedSkinEthnicReligion = worriedSkinEthnicReligion;
    }

    public Float getIndexSafety() {
        return indexSafety;
    }

    public void setIndexSafety(Float indexSafety) {
        this.indexSafety = indexSafety;
    }

    public Float getWorriedCarStolen() {
        return worriedCarStolen;
    }

    public void setWorriedCarStolen(Float worriedCarStolen) {
        this.worriedCarStolen = worriedCarStolen;
    }

    public Float getWorriedHomeBroken() {
        return worriedHomeBroken;
    }

    public void setWorriedHomeBroken(Float worriedHomeBroken) {
        this.worriedHomeBroken = worriedHomeBroken;
    }

    public Float getWorriedThingsCarStolen() {
        return worriedThingsCarStolen;
    }

    public void setWorriedThingsCarStolen(Float worriedThingsCarStolen) {
        this.worriedThingsCarStolen = worriedThingsCarStolen;
    }

    public Float getCrimeIncreasing() {
        return crimeIncreasing;
    }

    public void setCrimeIncreasing(Float crimeIncreasing) {
        this.crimeIncreasing = crimeIncreasing;
    }

    public Float getProblemCorruptionBribery() {
        return problemCorruptionBribery;
    }

    public void setProblemCorruptionBribery(Float problemCorruptionBribery) {
        this.problemCorruptionBribery = problemCorruptionBribery;
    }

    public Float getSafeAloneDaylight() {
        return safeAloneDaylight;
    }

    public void setSafeAloneDaylight(Float safeAloneDaylight) {
        this.safeAloneDaylight = safeAloneDaylight;
    }

    public Float getProblemDrugs() {
        return problemDrugs;
    }

    public void setProblemDrugs(Float problemDrugs) {
        this.problemDrugs = problemDrugs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getWorriedInsulted() {
        return worriedInsulted;
    }

    public void setWorriedInsulted(Float worriedInsulted) {
        this.worriedInsulted = worriedInsulted;
    }

    public Integer getMonthLastUpdate() {
        return monthLastUpdate;
    }

    public void setMonthLastUpdate(Integer monthLastUpdate) {
        this.monthLastUpdate = monthLastUpdate;
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

    public Float getIndexCrime() {
        return indexCrime;
    }

    public void setIndexCrime(Float indexCrime) {
        this.indexCrime = indexCrime;
    }

    public Float getProblemViolentCrimes() {
        return problemViolentCrimes;
    }

    public void setProblemViolentCrimes(Float problemViolentCrimes) {
        this.problemViolentCrimes = problemViolentCrimes;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Float getWorriedMuggedRobbed() {
        return worriedMuggedRobbed;
    }

    public void setWorriedMuggedRobbed(Float worriedMuggedRobbed) {
        this.worriedMuggedRobbed = worriedMuggedRobbed;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(worriedAttacked);
        dest.writeValue(levelOfCrime);
        dest.writeValue(problemPropertyCrimes);
        dest.writeValue(safeAloneNight);
        dest.writeValue(worriedSkinEthnicReligion);
        dest.writeValue(indexSafety);
        dest.writeValue(worriedCarStolen);
        dest.writeValue(worriedHomeBroken);
        dest.writeValue(worriedThingsCarStolen);
        dest.writeValue(crimeIncreasing);
        dest.writeValue(problemCorruptionBribery);
        dest.writeValue(safeAloneDaylight);
        dest.writeValue(problemDrugs);
        dest.writeValue(name);
        dest.writeValue(worriedInsulted);
        dest.writeValue(monthLastUpdate);
        dest.writeValue(contributors);
        dest.writeValue(yearLastUpdate);
        dest.writeValue(indexCrime);
        dest.writeValue(problemViolentCrimes);
        dest.writeValue(cityId);
        dest.writeValue(worriedMuggedRobbed);
    }

    public int describeContents() {
        return 0;
    }

}