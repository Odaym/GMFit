package com.mcsaatchi.gmfit.rest;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SlugBreakdownResponseInnerData implements Parcelable {
  public static final Creator<SlugBreakdownResponseInnerData> CREATOR =
      new Creator<SlugBreakdownResponseInnerData>() {
        @Override public SlugBreakdownResponseInnerData createFromParcel(Parcel in) {
          return new SlugBreakdownResponseInnerData(in);
        }

        @Override public SlugBreakdownResponseInnerData[] newArray(int size) {
          return new SlugBreakdownResponseInnerData[size];
        }
      };
  @SerializedName("daily") @Expose private List<SlugBreakdownResponseDaily> daily =
      new ArrayList<>();
  @SerializedName("monthly") @Expose private List<SlugBreakdownResponseMonthly> monthly =
      new ArrayList<>();
  @SerializedName("yearly") @Expose private List<SlugBreakdownResponseYearly> yearly =
      new ArrayList<>();

  protected SlugBreakdownResponseInnerData(Parcel in) {
    daily = in.createTypedArrayList(SlugBreakdownResponseDaily.CREATOR);
    monthly = in.createTypedArrayList(SlugBreakdownResponseMonthly.CREATOR);
    yearly = in.createTypedArrayList(SlugBreakdownResponseYearly.CREATOR);
  }

  /**
   * @return The daily
   */
  public List<SlugBreakdownResponseDaily> getDaily() {
    return daily;
  }

  /**
   * @param daily The daily
   */
  public void setDaily(List<SlugBreakdownResponseDaily> daily) {
    this.daily = daily;
  }

  /**
   * @return The monthly
   */
  public List<SlugBreakdownResponseMonthly> getMonthly() {
    return monthly;
  }

  /**
   * @param monthly The monthly
   */
  public void setMonthly(List<SlugBreakdownResponseMonthly> monthly) {
    this.monthly = monthly;
  }

  /**
   * @return The yearly
   */
  public List<SlugBreakdownResponseYearly> getYearly() {
    return yearly;
  }

  /**
   * @param yearly The yearly
   */
  public void setYearly(List<SlugBreakdownResponseYearly> yearly) {
    this.yearly = yearly;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel parcel, int i) {
    parcel.writeTypedList(daily);
    parcel.writeTypedList(monthly);
    parcel.writeTypedList(yearly);
  }
}
