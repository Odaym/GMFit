package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class OperationContactsResponseLocation implements Parcelable {
  public static final Creator<OperationContactsResponseLocation> CREATOR =
      new Creator<OperationContactsResponseLocation>() {
        @Override public OperationContactsResponseLocation createFromParcel(Parcel source) {
          return new OperationContactsResponseLocation(source);
        }

        @Override public OperationContactsResponseLocation[] newArray(int size) {
          return new OperationContactsResponseLocation[size];
        }
      };
  @SerializedName("id") @Expose private Integer id;
  @SerializedName("operation_id") @Expose private String operationId;
  @SerializedName("location") @Expose private String location;
  @SerializedName("phone_numbers") @Expose private List<OperationContactsResponsePhoneNumber>
      phoneNumbers = null;

  public OperationContactsResponseLocation() {
  }

  protected OperationContactsResponseLocation(Parcel in) {
    this.id = (Integer) in.readValue(Integer.class.getClassLoader());
    this.operationId = in.readString();
    this.location = in.readString();
    this.phoneNumbers = new ArrayList<OperationContactsResponsePhoneNumber>();
    in.readList(this.phoneNumbers, OperationContactsResponsePhoneNumber.class.getClassLoader());
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getOperationId() {
    return operationId;
  }

  public void setOperationId(String operationId) {
    this.operationId = operationId;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public List<OperationContactsResponsePhoneNumber> getPhoneNumbers() {
    return phoneNumbers;
  }

  public void setPhoneNumbers(List<OperationContactsResponsePhoneNumber> phoneNumbers) {
    this.phoneNumbers = phoneNumbers;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeValue(this.id);
    dest.writeString(this.operationId);
    dest.writeString(this.location);
    dest.writeList(this.phoneNumbers);
  }
}
