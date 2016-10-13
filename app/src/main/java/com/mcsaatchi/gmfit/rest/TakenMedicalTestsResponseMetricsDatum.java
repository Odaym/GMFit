package com.mcsaatchi.gmfit.rest;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TakenMedicalTestsResponseMetricsDatum implements Parcelable {
    public static final Creator<TakenMedicalTestsResponseMetricsDatum> CREATOR = new Creator<TakenMedicalTestsResponseMetricsDatum>() {
        @Override
        public TakenMedicalTestsResponseMetricsDatum createFromParcel(Parcel source) {
            return new TakenMedicalTestsResponseMetricsDatum(source);
        }

        @Override
        public TakenMedicalTestsResponseMetricsDatum[] newArray(int size) {
            return new TakenMedicalTestsResponseMetricsDatum[size];
        }
    };
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("low_value")
    @Expose
    private String lowValue;
    @SerializedName("high_value")
    @Expose
    private String highValue;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("units")
    @Expose
    private List<TakenMedicalTestsResponseUnit> units = new ArrayList<>();

    protected TakenMedicalTestsResponseMetricsDatum(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.name = in.readString();
        this.lowValue = in.readString();
        this.highValue = in.readString();
        this.value = in.readString();
        this.units = new ArrayList<>();
        in.readList(this.units, TakenMedicalTestsResponseUnit.class.getClassLoader());
    }

    /**
     * @return The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(Integer id) {
        this.id = id;
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
     * @return The lowValue
     */
    public String getLowValue() {
        return lowValue;
    }

    /**
     * @param lowValue The low_value
     */
    public void setLowValue(String lowValue) {
        this.lowValue = lowValue;
    }

    /**
     * @return The highValue
     */
    public String getHighValue() {
        return highValue;
    }

    /**
     * @param highValue The high_value
     */
    public void setHighValue(String highValue) {
        this.highValue = highValue;
    }

    /**
     * @return The value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value The value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return The units
     */
    public List<TakenMedicalTestsResponseUnit> getUnits() {
        return units;
    }

    /**
     * @param units The units
     */
    public void setUnits(List<TakenMedicalTestsResponseUnit> units) {
        this.units = units;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.name);
        dest.writeString(this.lowValue);
        dest.writeString(this.highValue);
        dest.writeString(this.value);
        dest.writeList(this.units);
    }
}
