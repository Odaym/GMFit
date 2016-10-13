package com.mcsaatchi.gmfit.rest;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MedicalTestsResponseDatum implements Parcelable {
    public static final Creator<MedicalTestsResponseDatum> CREATOR = new Creator<MedicalTestsResponseDatum>() {
        @Override
        public MedicalTestsResponseDatum createFromParcel(Parcel source) {
            return new MedicalTestsResponseDatum(source);
        }

        @Override
        public MedicalTestsResponseDatum[] newArray(int size) {
            return new MedicalTestsResponseDatum[size];
        }
    };
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("low_value")
    @Expose
    private String lowValue;
    @SerializedName("high_value")
    @Expose
    private String highValue;
    @SerializedName("units")
    @Expose
    private List<MedicalTestsResponseDatumUnit> units = new ArrayList<MedicalTestsResponseDatumUnit>();

    protected MedicalTestsResponseDatum(Parcel in) {
        this.name = in.readString();
        this.type = in.readString();
        this.id = in.readString();
        this.lowValue = in.readString();
        this.highValue = in.readString();
        this.units = in.createTypedArrayList(MedicalTestsResponseDatumUnit.CREATOR);
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
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
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
     * @return The units
     */
    public List<MedicalTestsResponseDatumUnit> getUnits() {
        return units;
    }

    /**
     * @param units The units
     */
    public void setUnits(List<MedicalTestsResponseDatumUnit> units) {
        this.units = units;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.type);
        dest.writeString(this.id);
        dest.writeString(this.lowValue);
        dest.writeString(this.highValue);
        dest.writeTypedList(this.units);
    }
}
