package com.mcsaatchi.gmfit.classes;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableNutritionString implements Parcelable {
    public static final Creator CREATOR = new Creator() {
        public ParcelableNutritionString createFromParcel(Parcel in) {
            return new ParcelableNutritionString(in);
        }

        public ParcelableNutritionString[] newArray(int size) {
            return new ParcelableNutritionString[size];
        }
    };

    double value;
    String title;
    String measurementUnit;
    double percentage;

    public ParcelableNutritionString() {
    }

    public ParcelableNutritionString(Parcel in) {
        this.value = in.readDouble();
        this.title = in.readString();
        this.measurementUnit = in.readString();
        this.percentage = in.readDouble();
    }

    public ParcelableNutritionString(String title, double value, String measurementUnit, double percentage) {
        this.value = value;
        this.title = title;
        this.measurementUnit = measurementUnit;
        this.percentage = percentage;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMeasurementUnit() {
        return measurementUnit;
    }

    public void setMeasurementUnit(String measurementUnit) {
        this.measurementUnit = measurementUnit;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(value);
        dest.writeString(title);
        dest.writeString(measurementUnit);
        dest.writeDouble(percentage);
    }
}
