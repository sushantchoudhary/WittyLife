package com.appdev.schoudhary.wittylife.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InsuranceType implements Parcelable
{

    @SerializedName("Employer Sponsored")
    @Expose
    private Float employerSponsored;
    @SerializedName("Private")
    @Expose
    private Float _private;
    @SerializedName("Public")
    @Expose
    private Float _public;
    @SerializedName("None")
    @Expose
    private Float none;
    public final static Parcelable.Creator<InsuranceType> CREATOR = new Creator<InsuranceType>() {


        @SuppressWarnings({
                "unchecked"
        })
        public InsuranceType createFromParcel(Parcel in) {
            return new InsuranceType(in);
        }

        public InsuranceType[] newArray(int size) {
            return (new InsuranceType[size]);
        }

    }
            ;

    protected InsuranceType(Parcel in) {
        this.employerSponsored = ((Float) in.readValue((Float.class.getClassLoader())));
        this._private = ((Float) in.readValue((Float.class.getClassLoader())));
        this._public = ((Float) in.readValue((Float.class.getClassLoader())));
        this.none = ((Float) in.readValue((Float.class.getClassLoader())));
    }

    /**
     *
     * @param employerSponsored
     * @param _public
     * @param _private
     * @param none
     */
    public InsuranceType(Float employerSponsored, Float _private, Float _public, Float none) {
        super();
        this.employerSponsored = employerSponsored;
        this._private = _private;
        this._public = _public;
        this.none = none;
    }

    public Float getEmployerSponsored() {
        return employerSponsored;
    }

    public void setEmployerSponsored(Float employerSponsored) {
        this.employerSponsored = employerSponsored;
    }

    public Float getPrivate() {
        return _private;
    }

    public void setPrivate(Float _private) {
        this._private = _private;
    }

    public Float getPublic() {
        return _public;
    }

    public void setPublic(Float _public) {
        this._public = _public;
    }

    public Float getNone() {
        return none;
    }

    public void setNone(Float none) {
        this.none = none;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(employerSponsored);
        dest.writeValue(_private);
        dest.writeValue(_public);
        dest.writeValue(none);
    }

    public int describeContents() {
        return 0;
    }

}