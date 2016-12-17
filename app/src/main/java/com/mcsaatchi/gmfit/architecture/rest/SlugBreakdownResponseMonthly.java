package com.mcsaatchi.gmfit.architecture.rest;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SlugBreakdownResponseMonthly implements Parcelable {
  public static final Creator<SlugBreakdownResponseMonthly> CREATOR =
      new Creator<SlugBreakdownResponseMonthly>() {
        @Override public SlugBreakdownResponseMonthly createFromParcel(Parcel in) {
          return new SlugBreakdownResponseMonthly(in);
        }

        @Override public SlugBreakdownResponseMonthly[] newArray(int size) {
          return new SlugBreakdownResponseMonthly[size];
        }
      };
  @SerializedName("date") @Expose private String date;
  @SerializedName("total") @Expose private String total;

  protected SlugBreakdownResponseMonthly(Parcel in) {
    date = in.readString();
    total = in.readString();
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
   * @return The total
   */
  public String getTotal() {
    return total;
  }

  /**
   * @param total The total
   */
  public void setTotal(String total) {
    this.total = total;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel parcel, int i) {
    parcel.writeString(date);
    parcel.writeString(total);
  }
}
