package com.mcsaatchi.gmfit.health.models;

import android.os.Parcel;
import android.os.Parcelable;

public class DayChoice implements Parcelable {
  private String dayName;
  private boolean daySelected;

  public DayChoice(String dayName, boolean daySelected) {
    this.dayName = dayName;
    this.daySelected = daySelected;
  }

  protected DayChoice(Parcel in) {
    dayName = in.readString();
    daySelected = in.readByte() != 0;
  }

  public static final Creator<DayChoice> CREATOR = new Creator<DayChoice>() {
    @Override public DayChoice createFromParcel(Parcel in) {
      return new DayChoice(in);
    }

    @Override public DayChoice[] newArray(int size) {
      return new DayChoice[size];
    }
  };

  public String getDayName() {
    return dayName;
  }

  public void setDayName(String dayName) {
    this.dayName = dayName;
  }

  public boolean isDaySelected() {
    return daySelected;
  }

  public void setDaySelected(boolean daySelected) {
    this.daySelected = daySelected;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel parcel, int i) {
    parcel.writeString(dayName);
    parcel.writeByte((byte) (daySelected ? 1 : 0));
  }
}
