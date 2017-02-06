package com.mcsaatchi.gmfit.insurance.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ClinicOpeningHours implements Parcelable {
  public static final Creator<ClinicOpeningHours> CREATOR = new Creator<ClinicOpeningHours>() {
    @Override public ClinicOpeningHours createFromParcel(Parcel source) {
      return new ClinicOpeningHours(source);
    }

    @Override public ClinicOpeningHours[] newArray(int size) {
      return new ClinicOpeningHours[size];
    }
  };
  private String monday;
  private String tuesday;
  private String wednesday;
  private String thursday;
  private String friday;
  private String saturday;
  private String sunday;

  public ClinicOpeningHours(String monday, String tuesday, String wednesday, String thursday,
      String friday, String saturday, String sunday) {
    this.monday = monday;
    this.tuesday = tuesday;
    this.wednesday = wednesday;
    this.thursday = thursday;
    this.friday = friday;
    this.saturday = saturday;
    this.sunday = sunday;
  }

  protected ClinicOpeningHours(Parcel in) {
    this.monday = in.readString();
    this.tuesday = in.readString();
    this.wednesday = in.readString();
    this.thursday = in.readString();
    this.friday = in.readString();
    this.saturday = in.readString();
    this.sunday = in.readString();
  }

  public String getMonday() {
    return monday;
  }

  public void setMonday(String monday) {
    this.monday = monday;
  }

  public String getTuesday() {
    return tuesday;
  }

  public void setTuesday(String tuesday) {
    this.tuesday = tuesday;
  }

  public String getWednesday() {
    return wednesday;
  }

  public void setWednesday(String wednesday) {
    this.wednesday = wednesday;
  }

  public String getThursday() {
    return thursday;
  }

  public void setThursday(String thursday) {
    this.thursday = thursday;
  }

  public String getFriday() {
    return friday;
  }

  public void setFriday(String friday) {
    this.friday = friday;
  }

  public String getSaturday() {
    return saturday;
  }

  public void setSaturday(String saturday) {
    this.saturday = saturday;
  }

  public String getSunday() {
    return sunday;
  }

  public void setSunday(String sunday) {
    this.sunday = sunday;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.monday);
    dest.writeString(this.tuesday);
    dest.writeString(this.wednesday);
    dest.writeString(this.thursday);
    dest.writeString(this.friday);
    dest.writeString(this.saturday);
    dest.writeString(this.sunday);
  }
}
