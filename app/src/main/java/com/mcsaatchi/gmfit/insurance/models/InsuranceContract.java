package com.mcsaatchi.gmfit.insurance.models;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Calendar;

public class InsuranceContract implements Parcelable {
  public static final Creator<InsuranceContract> CREATOR = new Creator<InsuranceContract>() {
    @Override public InsuranceContract createFromParcel(Parcel source) {
      return new InsuranceContract(source);
    }

    @Override public InsuranceContract[] newArray(int size) {
      return new InsuranceContract[size];
    }
  };
  private String title;
  private String number;
  private String insuranceCompany;
  private String holdername;
  private Calendar expiryDate;
  private boolean selected;

  public InsuranceContract() {
  }

  protected InsuranceContract(Parcel in) {
    this.title = in.readString();
    this.number = in.readString();
    this.insuranceCompany = in.readString();
    this.holdername = in.readString();
    this.expiryDate = (Calendar) in.readSerializable();
    this.selected = in.readByte() != 0;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getNumber() {
    return number;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  public String getInsuranceCompany() {
    return insuranceCompany;
  }

  public void setInsuranceCompany(String insuranceCompany) {
    this.insuranceCompany = insuranceCompany;
  }

  public Calendar getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(Calendar expiryDate) {
    this.expiryDate = expiryDate;
  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  public String getHoldername() {
    return holdername;
  }

  public void setHoldername(String holdername) {
    this.holdername = holdername;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.title);
    dest.writeString(this.number);
    dest.writeString(this.insuranceCompany);
    dest.writeString(this.holdername);
    dest.writeSerializable(this.expiryDate);
    dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
  }
}
