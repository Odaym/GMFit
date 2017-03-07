package com.mcsaatchi.gmfit.insurance.models;

import android.os.Parcel;
import android.os.Parcelable;

public class TreatmentsModel implements Parcelable {
  public static final Creator<TreatmentsModel> CREATOR = new Creator<TreatmentsModel>() {
    @Override public TreatmentsModel createFromParcel(Parcel source) {
      return new TreatmentsModel(source);
    }

    @Override public TreatmentsModel[] newArray(int size) {
      return new TreatmentsModel[size];
    }
  };
  private int id;
  private String name;
  private String startDate;
  private String endDate;
  private String status;

  public TreatmentsModel(String name, String startDate, String endDate, String status) {
    this.name = name;
    this.startDate = startDate;
    this.endDate = endDate;
    this.status = status;
  }

  protected TreatmentsModel(Parcel in) {
    this.id = in.readInt();
    this.name = in.readString();
    this.startDate = in.readString();
    this.endDate = in.readString();
    this.status = in.readString();
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFromDate() {
    return startDate;
  }

  public void setFromDate(String startDate) {
    this.startDate = startDate;
  }

  public String getToDate() {
    return endDate;
  }

  public void setToDate(String endDate) {
    this.endDate = endDate;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.id);
    dest.writeString(this.name);
    dest.writeString(this.startDate);
    dest.writeString(this.endDate);
    dest.writeString(this.status);
  }
}