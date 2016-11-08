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

  int value;
  int drawableResId;
  String title;
  String measurementUnit;

  public ParcelableFitnessString() {
  }

  public ParcelableFitnessString(Parcel in) {
    this.drawableResId = in.readInt();
    this.value = in.readInt();
    this.title = in.readString();
    this.measurementUnit = in.readString();
  }

  public ParcelableFitnessString(int drawableResId, int value, String title,
      String measurementUnit) {
    this.value = value;
    this.drawableResId = drawableResId;
    this.title = title;
    this.measurementUnit = measurementUnit;
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }

  public int getDrawableResId() {
    return drawableResId;
  }

  public void setDrawableResId(int drawableResId) {
    this.drawableResId = drawableResId;
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

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(drawableResId);
    dest.writeInt(value);
    dest.writeString(title);
    dest.writeString(measurementUnit);
  }
}
