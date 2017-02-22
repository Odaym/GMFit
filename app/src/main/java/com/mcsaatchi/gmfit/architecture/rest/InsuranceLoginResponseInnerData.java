package com.mcsaatchi.gmfit.architecture.rest;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class InsuranceLoginResponseInnerData implements Parcelable{
  public static final Creator<InsuranceLoginResponseInnerData> CREATOR =
      new Creator<InsuranceLoginResponseInnerData>() {
        @Override public InsuranceLoginResponseInnerData createFromParcel(Parcel source) {
          return new InsuranceLoginResponseInnerData(source);
        }

        @Override public InsuranceLoginResponseInnerData[] newArray(int size) {
          return new InsuranceLoginResponseInnerData[size];
        }
      };
  @SerializedName("username") @Expose private String username;
  @SerializedName("email") @Expose private String email;
  @SerializedName("isFirstLogin") @Expose private boolean isFirstLogin;
  @SerializedName("mobile") @Expose private String mobile;
  @SerializedName("contracts") @Expose private List<InsuranceLoginResponseContract> contracts =
      null;

  public InsuranceLoginResponseInnerData() {
  }

  protected InsuranceLoginResponseInnerData(Parcel in) {
    this.username = in.readString();
    this.email = in.readString();
    this.isFirstLogin = in.readByte() != 0;
    this.mobile = in.readString();
    this.contracts = in.createTypedArrayList(InsuranceLoginResponseContract.CREATOR);
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public boolean getIsFirstLogin() {
    return isFirstLogin;
  }

  public void setIsFirstLogin(boolean isFirstLogin) {
    this.isFirstLogin = isFirstLogin;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public List<InsuranceLoginResponseContract> getContracts() {
    return contracts;
  }

  public void setContracts(List<InsuranceLoginResponseContract> contracts) {
    this.contracts = contracts;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.username);
    dest.writeString(this.email);
    dest.writeByte(this.isFirstLogin ? (byte) 1 : (byte) 0);
    dest.writeString(this.mobile);
    dest.writeTypedList(this.contracts);
  }
}
