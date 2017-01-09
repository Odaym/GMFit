package com.mcsaatchi.gmfit.health.models;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;

public class ReminderTime implements Parcelable, Serializable {
  public static final Creator<ReminderTime> CREATOR = new Creator<ReminderTime>() {
    @Override public ReminderTime createFromParcel(Parcel source) {
      return new ReminderTime(source);
    }

    @Override public ReminderTime[] newArray(int size) {
      return new ReminderTime[size];
    }
  };
  private int hour;
  private int minute;
  private String fullTime;

  public ReminderTime(int hour, int minute, String fullTime) {
    this.hour = hour;
    this.minute = minute;
    this.fullTime = fullTime;
  }

  protected ReminderTime(Parcel in) {
    this.hour = in.readInt();
    this.minute = in.readInt();
    this.fullTime = in.readString();
  }

  public int getHour() {
    return hour;
  }

  public void setHour(int hour) {
    this.hour = hour;
  }

  public int getMinute() {
    return minute;
  }

  public void setMinute(int minute) {
    this.minute = minute;
  }

  public String getFullTime() {
    return fullTime;
  }

  public void setFullTime(String fullTime) {
    this.fullTime = fullTime;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.hour);
    dest.writeInt(this.minute);
    dest.writeString(this.fullTime);
  }
}
