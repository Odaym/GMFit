package com.mcsaatchi.gmfit.classes;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableFitnessString implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ParcelableFitnessString createFromParcel(Parcel in) {
            return new ParcelableFitnessString(in);
        }

        public ParcelableFitnessString[] newArray(int size) {
            return new ParcelableFitnessString[size];
        }
    };

    double value;
    int key;
    String title;

    public ParcelableFitnessString() {
    }

    public ParcelableFitnessString(Parcel in) {
        this.key = in.readInt();
        this.value = in.readDouble();
        this.title = in.readString();
    }

    public ParcelableFitnessString(int key, double value, String title) {
        this.key = key;
        this.value = value;
        this.title = title;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(key);
        dest.writeDouble(value);
        dest.writeString(title);
    }
}
