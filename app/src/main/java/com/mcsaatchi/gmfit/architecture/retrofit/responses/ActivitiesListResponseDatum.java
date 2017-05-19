package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ActivitiesListResponseDatum implements Parcelable {
  public static final Creator<ActivitiesListResponseDatum> CREATOR =
      new Creator<ActivitiesListResponseDatum>() {
        @Override public ActivitiesListResponseDatum createFromParcel(Parcel source) {
          return new ActivitiesListResponseDatum(source);
        }

        @Override public ActivitiesListResponseDatum[] newArray(int size) {
          return new ActivitiesListResponseDatum[size];
        }
      };
  @Expose private Integer id;
  @SerializedName("fitness_activity_id") @Expose private String fitnessActivityId;
  @SerializedName("name") @Expose private String name;
  @SerializedName("rate") @Expose private String rate;
  @SerializedName("created_at") @Expose private String createdAt;
  @SerializedName("updated_at") @Expose private String updatedAt;

  public ActivitiesListResponseDatum() {
  }

  protected ActivitiesListResponseDatum(Parcel in) {
    this.id = (Integer) in.readValue(Integer.class.getClassLoader());
    this.fitnessActivityId = in.readString();
    this.name = in.readString();
    this.rate = in.readString();
    this.createdAt = in.readString();
    this.updatedAt = in.readString();
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getFitnessActivityId() {
    return fitnessActivityId;
  }

  public void setFitnessActivityId(String fitnessActivityId) {
    this.fitnessActivityId = fitnessActivityId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getRate() {
    return rate;
  }

  public void setRate(String rate) {
    this.rate = rate;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeValue(this.id);
    dest.writeString(this.fitnessActivityId);
    dest.writeString(this.name);
    dest.writeString(this.rate);
    dest.writeString(this.createdAt);
    dest.writeString(this.updatedAt);
  }
}
