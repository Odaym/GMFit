package com.mcsaatchi.gmfit.architecture.rest;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InsuranceLoginResponseContract implements Parcelable {
  public static final Creator<InsuranceLoginResponseContract> CREATOR =
      new Creator<InsuranceLoginResponseContract>() {
        @Override public InsuranceLoginResponseContract createFromParcel(Parcel source) {
          return new InsuranceLoginResponseContract(source);
        }

        @Override public InsuranceLoginResponseContract[] newArray(int size) {
          return new InsuranceLoginResponseContract[size];
        }
      };
  @SerializedName("number") @Expose private Integer number;
  @SerializedName("company") @Expose private String company;
  @SerializedName("username") @Expose private String username;

  public InsuranceLoginResponseContract() {
  }

  protected InsuranceLoginResponseContract(Parcel in) {
    this.number = (Integer) in.readValue(Integer.class.getClassLoader());
    this.company = in.readString();
    this.username = in.readString();
  }

  public Integer getNumber() {
    return number;
  }

  public void setNumber(Integer number) {
    this.number = number;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeValue(this.number);
    dest.writeString(this.company);
    dest.writeString(this.username);
  }
}
