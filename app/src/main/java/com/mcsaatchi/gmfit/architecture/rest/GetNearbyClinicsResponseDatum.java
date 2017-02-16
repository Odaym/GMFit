package com.mcsaatchi.gmfit.architecture.rest;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetNearbyClinicsResponseDatum implements Parcelable {
  public static final Creator<GetNearbyClinicsResponseDatum> CREATOR =
      new Creator<GetNearbyClinicsResponseDatum>() {
        @Override public GetNearbyClinicsResponseDatum createFromParcel(Parcel source) {
          return new GetNearbyClinicsResponseDatum(source);
        }

        @Override public GetNearbyClinicsResponseDatum[] newArray(int size) {
          return new GetNearbyClinicsResponseDatum[size];
        }
      };
  @SerializedName("id") @Expose private String id;
  @SerializedName("name") @Expose private String name;
  @SerializedName("address") @Expose private String address;
  @SerializedName("latitude") @Expose private String latitude;
  @SerializedName("longitude") @Expose private String longitude;
  @SerializedName("partOfNetwork") @Expose private Boolean partOfNetwork;
  @SerializedName("twentyfourseven") @Expose private Boolean twentyfourseven;
  @SerializedName("online") @Expose private Boolean online;
  @SerializedName("phone") @Expose private String phone;
  @SerializedName("mobile") @Expose private String mobile;
  @SerializedName("email") @Expose private String email;
  @SerializedName("openinghours") @Expose private String openinghours;

  public GetNearbyClinicsResponseDatum() {
  }

  protected GetNearbyClinicsResponseDatum(Parcel in) {
    this.id = in.readString();
    this.name = in.readString();
    this.address = in.readString();
    this.latitude = in.readString();
    this.longitude = in.readString();
    this.partOfNetwork = (Boolean) in.readValue(Boolean.class.getClassLoader());
    this.twentyfourseven = (Boolean) in.readValue(Boolean.class.getClassLoader());
    this.online = (Boolean) in.readValue(Boolean.class.getClassLoader());
    this.phone = in.readString();
    this.mobile = in.readString();
    this.email = in.readString();
    this.openinghours = in.readString();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getLatitude() {
    return latitude;
  }

  public void setLatitude(String latitude) {
    this.latitude = latitude;
  }

  public String getLongitude() {
    return longitude;
  }

  public void setLongitude(String longitude) {
    this.longitude = longitude;
  }

  public Boolean getPartOfNetwork() {
    return partOfNetwork;
  }

  public void setPartOfNetwork(Boolean partOfNetwork) {
    this.partOfNetwork = partOfNetwork;
  }

  public Boolean getTwentyfourseven() {
    return twentyfourseven;
  }

  public void setTwentyfourseven(Boolean twentyfourseven) {
    this.twentyfourseven = twentyfourseven;
  }

  public Boolean getOnline() {
    return online;
  }

  public void setOnline(Boolean online) {
    this.online = online;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getOpeninghours() {
    return openinghours;
  }

  public void setOpeninghours(String openinghours) {
    this.openinghours = openinghours;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.id);
    dest.writeString(this.name);
    dest.writeString(this.address);
    dest.writeString(this.latitude);
    dest.writeString(this.longitude);
    dest.writeValue(this.partOfNetwork);
    dest.writeValue(this.twentyfourseven);
    dest.writeValue(this.online);
    dest.writeString(this.phone);
    dest.writeString(this.mobile);
    dest.writeString(this.email);
    dest.writeString(this.openinghours);
  }
}
