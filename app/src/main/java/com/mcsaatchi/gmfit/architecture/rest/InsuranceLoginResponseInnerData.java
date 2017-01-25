package com.mcsaatchi.gmfit.architecture.rest;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InsuranceLoginResponseInnerData implements Parcelable {
  public static final Creator<InsuranceLoginResponseInnerData> CREATOR =
      new Creator<InsuranceLoginResponseInnerData>() {
        @Override public InsuranceLoginResponseInnerData createFromParcel(Parcel source) {
          return new InsuranceLoginResponseInnerData(source);
        }

        @Override public InsuranceLoginResponseInnerData[] newArray(int size) {
          return new InsuranceLoginResponseInnerData[size];
        }
      };
  @SerializedName("status") @Expose private String status;
  @SerializedName("transactionNo") @Expose private Integer transactionNo;
  @SerializedName("contractNo") @Expose private Integer contractNo;
  @SerializedName("email") @Expose private String email;
  @SerializedName("insuranceCompany") @Expose private String insuranceCompany;
  @SerializedName("isFirstLogin") @Expose private Integer isFirstLogin;
  @SerializedName("mobileNo") @Expose private String mobileNo;
  @SerializedName("username") @Expose private String username;

  public InsuranceLoginResponseInnerData() {
  }

  protected InsuranceLoginResponseInnerData(Parcel in) {
    this.status = in.readString();
    this.transactionNo = (Integer) in.readValue(Integer.class.getClassLoader());
    this.contractNo = (Integer) in.readValue(Integer.class.getClassLoader());
    this.email = in.readString();
    this.insuranceCompany = in.readString();
    this.isFirstLogin = (Integer) in.readValue(Integer.class.getClassLoader());
    this.mobileNo = in.readString();
    this.username = in.readString();
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

  public Integer getContractNo() {
    return contractNo;
  }

  public void setContractNo(Integer contractNo) {
    this.contractNo = contractNo;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getInsuranceCompany() {
    return insuranceCompany;
  }

  public void setInsuranceCompany(String insuranceCompany) {
    this.insuranceCompany = insuranceCompany;
  }

  public Integer getIsFirstLogin() {
    return isFirstLogin;
  }

  public void setIsFirstLogin(Integer isFirstLogin) {
    this.isFirstLogin = isFirstLogin;
  }

  public String getMobileNo() {
    return mobileNo;
  }

  public void setMobileNo(String mobileNo) {
    this.mobileNo = mobileNo;
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
    dest.writeString(this.status);
    dest.writeValue(this.transactionNo);
    dest.writeValue(this.contractNo);
    dest.writeString(this.email);
    dest.writeString(this.insuranceCompany);
    dest.writeValue(this.isFirstLogin);
    dest.writeString(this.mobileNo);
    dest.writeString(this.username);
  }
}
