package com.mcsaatchi.gmfit.insurance.models;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Calendar;

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
  private Calendar fromDate;
  private Calendar toDate;
  private String status;

  public TreatmentsModel(String name, Calendar fromDate, Calendar toDate, String status) {
    this.name = name;
    this.fromDate = fromDate;
    this.toDate = toDate;
    this.status = status;
  }

  protected TreatmentsModel(Parcel in) {
    this.id = in.readInt();
    this.name = in.readString();
    this.fromDate = (Calendar) in.readSerializable();
    this.toDate = (Calendar) in.readSerializable();
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

  public Calendar getFromDate() {
    return fromDate;
  }

  public void setFromDate(Calendar fromDate) {
    this.fromDate = fromDate;
  }

  public Calendar getToDate() {
    return toDate;
  }

  public void setToDate(Calendar toDate) {
    this.toDate = toDate;
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
    dest.writeSerializable(this.fromDate);
    dest.writeSerializable(this.toDate);
    dest.writeString(this.status);
  }
}