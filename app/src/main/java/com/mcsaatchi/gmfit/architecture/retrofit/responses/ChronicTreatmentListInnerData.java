package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChronicTreatmentListInnerData implements Parcelable {
  public static final Creator<ChronicTreatmentListInnerData> CREATOR =
      new Creator<ChronicTreatmentListInnerData>() {
        @Override public ChronicTreatmentListInnerData createFromParcel(Parcel source) {
          return new ChronicTreatmentListInnerData(source);
        }

        @Override public ChronicTreatmentListInnerData[] newArray(int size) {
          return new ChronicTreatmentListInnerData[size];
        }
      };
  @SerializedName("name") @Expose private String name;
  @SerializedName("requestNbr") @Expose private String requestNbr;
  @SerializedName("status") @Expose private String status;
  @SerializedName("statusCode") @Expose private String statusCode;

  public ChronicTreatmentListInnerData() {
  }

  protected ChronicTreatmentListInnerData(Parcel in) {
    this.name = in.readString();
    this.requestNbr = in.readString();
    this.status = in.readString();
    this.statusCode = in.readString();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getRequestNbr() {
    return requestNbr;
  }

  public void setRequestNbr(String requestNbr) {
    this.requestNbr = requestNbr;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(String statusCode) {
    this.statusCode = statusCode;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.name);
    dest.writeString(this.requestNbr);
    dest.writeString(this.status);
    dest.writeString(this.statusCode);
  }
}
