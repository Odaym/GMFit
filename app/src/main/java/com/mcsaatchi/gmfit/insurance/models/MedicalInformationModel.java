package com.mcsaatchi.gmfit.insurance.models;

import android.os.Parcel;
import android.os.Parcelable;

public class MedicalInformationModel implements Parcelable{
  private String medicineName;
  private String status;
  private String tabletCount;
  private String frequency;
  private String duration;

  public MedicalInformationModel(String medicineName, String status, String tabletCount,
      String frequency, String duration) {
    this.medicineName = medicineName;
    this.status = status;
    this.tabletCount = tabletCount;
    this.frequency = frequency;
    this.duration = duration;
  }

  public String getMedicineName() {
    return medicineName;
  }

  public void setMedicineName(String medicineName) {
    this.medicineName = medicineName;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getTabletCount() {
    return tabletCount;
  }

  public void setTabletCount(String tabletCount) {
    this.tabletCount = tabletCount;
  }

  public String getFrequency() {
    return frequency;
  }

  public void setFrequency(String frequency) {
    this.frequency = frequency;
  }

  public String getDuration() {
    return duration;
  }

  public void setDuration(String duration) {
    this.duration = duration;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.medicineName);
    dest.writeString(this.status);
    dest.writeString(this.tabletCount);
    dest.writeString(this.frequency);
    dest.writeString(this.duration);
  }

  protected MedicalInformationModel(Parcel in) {
    this.medicineName = in.readString();
    this.status = in.readString();
    this.tabletCount = in.readString();
    this.frequency = in.readString();
    this.duration = in.readString();
  }

  public static final Creator<MedicalInformationModel> CREATOR =
      new Creator<MedicalInformationModel>() {
        @Override public MedicalInformationModel createFromParcel(Parcel source) {
          return new MedicalInformationModel(source);
        }

        @Override public MedicalInformationModel[] newArray(int size) {
          return new MedicalInformationModel[size];
        }
      };
}