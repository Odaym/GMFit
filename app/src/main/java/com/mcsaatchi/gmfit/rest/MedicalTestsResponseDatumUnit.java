package com.mcsaatchi.gmfit.rest;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MedicalTestsResponseDatumUnit implements Parcelable{
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("unit")
    @Expose
    private String unit;

    protected MedicalTestsResponseDatumUnit(Parcel in) {
        unit = in.readString();
    }

    public static final Creator<MedicalTestsResponseDatumUnit> CREATOR = new Creator<MedicalTestsResponseDatumUnit>() {
        @Override
        public MedicalTestsResponseDatumUnit createFromParcel(Parcel in) {
            return new MedicalTestsResponseDatumUnit(in);
        }

        @Override
        public MedicalTestsResponseDatumUnit[] newArray(int size) {
            return new MedicalTestsResponseDatumUnit[size];
        }
    };

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
     * @return The unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * @param unit The unit
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(unit);
    }
}
