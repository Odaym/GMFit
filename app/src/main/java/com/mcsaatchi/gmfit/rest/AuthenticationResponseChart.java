package com.mcsaatchi.gmfit.rest;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AuthenticationResponseChart implements Parcelable {

    public static final Creator<AuthenticationResponseChart> CREATOR = new Creator<AuthenticationResponseChart>() {
        @Override
        public AuthenticationResponseChart createFromParcel(Parcel in) {
            return new AuthenticationResponseChart(in);
        }

        @Override
        public AuthenticationResponseChart[] newArray(int size) {
            return new AuthenticationResponseChart[size];
        }
    };
    @SerializedName("chart_id")
    @Expose
    private Integer chartId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("slug")
    @Expose
    private String slug;
    @SerializedName("position")
    @Expose
    private String position;
    @SerializedName("data")
    @Expose
    private List<AuthenticationResponseChartData> data = new ArrayList<>();

    protected AuthenticationResponseChart(Parcel in) {
        name = in.readString();
        slug = in.readString();
        position = in.readString();
        data = in.createTypedArrayList(AuthenticationResponseChartData.CREATOR);
    }

    /**
     * @return The chartId
     */
    public Integer getChartId() {
        return chartId;
    }

    /**
     * @param chartId The chart_id
     */
    public void setChartId(Integer chartId) {
        this.chartId = chartId;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The slug
     */
    public String getSlug() {
        return slug;
    }

    /**
     * @param slug The slug
     */
    public void setSlug(String slug) {
        this.slug = slug;
    }

    /**
     * @return The position
     */
    public String getPosition() {
        return position;
    }

    /**
     * @param position The position
     */
    public void setPosition(String position) {
        this.position = position;
    }

    /**
     * @return The data
     */
    public List<AuthenticationResponseChartData> getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(List<AuthenticationResponseChartData> data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(chartId);
        parcel.writeString(name);
        parcel.writeString(slug);
        parcel.writeString(position);
    }
}
