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
    String value;
    int key;

    public ParcelableString() {
    }

    public ParcelableString(Parcel in) {
        this.key = in.readInt();
        this.value = in.readString();
    }

    public ParcelableString(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(key);
        dest.writeString(value);
    }
}
