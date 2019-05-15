package com.appdev.schoudhary.wittylife.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class DestinationImg implements Parcelable {

    public final static Parcelable.Creator<DestinationImg> CREATOR = new Creator<DestinationImg>() {


        @SuppressWarnings({
                "unchecked"
        })
        public DestinationImg createFromParcel(Parcel in) {
            return new DestinationImg(in);
        }

        public DestinationImg[] newArray(int size) {
            return (new DestinationImg[size]);
        }

    };
    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("total_pages")
    @Expose
    private Integer totalPages;
    @SerializedName("results")
    @Expose
    private List<Result> results = null;

    private DestinationImg(Parcel in) {
        this.total = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.totalPages = ((Integer) in.readValue((Integer.class.getClassLoader())));
        in.readList(this.results, (com.appdev.schoudhary.wittylife.model.Result.class.getClassLoader()));
    }

    /**
     * @param total
     * @param results
     * @param totalPages
     */
    public DestinationImg(Integer total, Integer totalPages, List<Result> results) {
        super();
        this.total = total;
        this.totalPages = totalPages;
        this.results = results;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(total);
        dest.writeValue(totalPages);
        dest.writeList(results);
    }

    public int describeContents() {
        return 0;
    }

}
