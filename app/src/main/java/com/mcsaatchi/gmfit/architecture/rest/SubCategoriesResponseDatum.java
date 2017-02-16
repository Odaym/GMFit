package com.mcsaatchi.gmfit.architecture.rest;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubCategoriesResponseDatum implements Parcelable {
  public static final Creator<SubCategoriesResponseDatum> CREATOR =
      new Creator<SubCategoriesResponseDatum>() {
        @Override public SubCategoriesResponseDatum createFromParcel(Parcel source) {
          return new SubCategoriesResponseDatum(source);
        }

        @Override public SubCategoriesResponseDatum[] newArray(int size) {
          return new SubCategoriesResponseDatum[size];
        }
      };
  @SerializedName("id") @Expose private String id;
  @SerializedName("name") @Expose private String name;

  public SubCategoriesResponseDatum() {
  }

  protected SubCategoriesResponseDatum(Parcel in) {
    this.id = in.readString();
    this.name = in.readString();
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

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.id);
    dest.writeString(this.name);
  }
}
