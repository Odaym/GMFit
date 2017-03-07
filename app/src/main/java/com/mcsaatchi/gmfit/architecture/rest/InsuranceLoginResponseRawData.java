package com.mcsaatchi.gmfit.architecture.rest;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InsuranceLoginResponseRawData implements Parcelable {
  public static final Creator<InsuranceLoginResponseRawData> CREATOR =
      new Creator<InsuranceLoginResponseRawData>() {
        @Override public InsuranceLoginResponseRawData createFromParcel(Parcel source) {
          return new InsuranceLoginResponseRawData(source);
        }

        @Override public InsuranceLoginResponseRawData[] newArray(int size) {
          return new InsuranceLoginResponseRawData[size];
        }
      };
  @SerializedName("status") @Expose private String status;
  @SerializedName("transactionNo") @Expose private Integer transactionNo;
  @SerializedName("indvLst") @Expose private InsuranceLoginResponseIndvLst indvLst;

  public InsuranceLoginResponseRawData() {
  }

  protected InsuranceLoginResponseRawData(Parcel in) {
    this.status = in.readString();
    this.transactionNo = (Integer) in.readValue(Integer.class.getClassLoader());
    this.indvLst = in.readParcelable(InsuranceLoginResponseIndvLst.class.getClassLoader());
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Integer getTransactionNo() {
    return transactionNo;
  }

  public void setTransactionNo(Integer transactionNo) {
    this.transactionNo = transactionNo;
  }

  public InsuranceLoginResponseIndvLst getIndvLst() {
    return indvLst;
  }

  public void setIndvLst(InsuranceLoginResponseIndvLst indvLst) {
    this.indvLst = indvLst;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.status);
    dest.writeValue(this.transactionNo);
    dest.writeParcelable(this.indvLst, flags);
  }
}
