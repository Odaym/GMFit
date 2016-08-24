package com.mcsaatchi.gmfit.rest;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SlugBreakdownResponseYearly implements Parcelable {
    public static final Creator<SlugBreakdownResponseYearly> CREATOR = new Creator<SlugBreakdownResponseYearly>() {
        @Override
        public SlugBreakdownResponseYearly createFromParcel(Parcel in) {
            return new SlugBreakdownResponseYearly(in);
        }

        @Override
        public SlugBreakdownResponseYearly[] newArray(int size) {
            return new SlugBreakdownResponseYearly[size];
        }
    };
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("total")
    @Expose
    private String total;

    protected SlugBreakdownResponseYearly(Parcel in) {
        date = in.readString();
        total = in.readString();
    }

    /**
     *
     * @return
     * The date
     */
    public String getDate() {
        return date;
    }

    /**
     *
     * @param date
     * The date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     *
     * @return
     * The total
     */
    public String getTotal() {
        return total;
    }

    /**
     *
     * @param total
     * The total
     */
    public void setTotal(String total) {
        this.total = total;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(date);
        parcel.writeString(total);
    }
}
