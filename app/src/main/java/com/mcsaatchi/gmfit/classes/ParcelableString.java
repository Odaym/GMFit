package com.mcsaatchi.gmfit.classes;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableString implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ParcelableString createFromParcel(Parcel in) {
            return new ParcelableString(in);
        }

        public ParcelableString[] newArray(int size) {
            return new ParcelableString[size];
        }
    };

    double value;
    int key;
    String title;

    public ParcelableString() {
    }

    public ParcelableString(Parcel in) {
        this.key = in.readInt();
        this.value = in.readDouble();
        this.title = in.readString();
    }

    public ParcelableString(int key, double value, String title) {
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
