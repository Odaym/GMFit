package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OperationContactsResponsePhoneNumber implements Parcelable {
  public static final Creator<OperationContactsResponsePhoneNumber> CREATOR =
      new Creator<OperationContactsResponsePhoneNumber>() {
        @Override public OperationContactsResponsePhoneNumber createFromParcel(Parcel source) {
          return new OperationContactsResponsePhoneNumber(source);
        }

        @Override public OperationContactsResponsePhoneNumber[] newArray(int size) {
          return new OperationContactsResponsePhoneNumber[size];
        }
      };
  @SerializedName("id") @Expose private Integer id;
  @SerializedName("location_id") @Expose private String locationId;
  @SerializedName("name") @Expose private String name;
  @SerializedName("number") @Expose private String number;

  public OperationContactsResponsePhoneNumber() {
  }

  protected OperationContactsResponsePhoneNumber(Parcel in) {
    this.id = (Integer) in.readValue(Integer.class.getClassLoader());
    this.locationId = in.readString();
    this.name = in.readString();
    this.number = in.readString();
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getLocationId() {
    return locationId;
  }

  public void setLocationId(String locationId) {
    this.locationId = locationId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getNumber() {
    return number;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeValue(this.id);
    dest.writeString(this.locationId);
    dest.writeString(this.name);
    dest.writeString(this.number);
  }
}
