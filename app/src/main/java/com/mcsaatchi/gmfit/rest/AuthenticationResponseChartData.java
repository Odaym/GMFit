package com.mcsaatchi.gmfit.rest;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthenticationResponseChartData implements Parcelable {
  public static final Creator<AuthenticationResponseChartData> CREATOR =
      new Creator<AuthenticationResponseChartData>() {
        @Override public AuthenticationResponseChartData createFromParcel(Parcel in) {
          return new AuthenticationResponseChartData(in);
        }

        @Override public AuthenticationResponseChartData[] newArray(int size) {
          return new AuthenticationResponseChartData[size];
        }
      };
  @SerializedName("date") @Expose private String date;
  @SerializedName("value") @Expose private String value;

  public AuthenticationResponseChartData(String date, String value) {
    this.date = date;
    this.value = value;
  }

  public AuthenticationResponseChartData(Parcel in) {
    date = in.readString();
    value = in.readString();
  }

  /**
   * @return The date
   */
  public String getDate() {
    return date;
  }

  /**
   * @param date The date
   */
  public void setDate(String date) {
    this.date = date;
  }

  /**
   * @return The value
   */
  public String getValue() {
    return value;
  }

  /**
   * @param value The value
   */
  public void setValue(String value) {
    this.value = value;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel parcel, int i) {
    parcel.writeString(date);
    parcel.writeString(value);
  }
}
