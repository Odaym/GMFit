package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserActivitiesResponseBody implements Parcelable {

  public static final Creator<UserActivitiesResponseBody> CREATOR =
      new Creator<UserActivitiesResponseBody>() {
        @Override public UserActivitiesResponseBody createFromParcel(Parcel source) {
          return new UserActivitiesResponseBody(source);
        }

        @Override public UserActivitiesResponseBody[] newArray(int size) {
          return new UserActivitiesResponseBody[size];
        }
      };
  @SerializedName("instance_id") @Expose private Integer instanceId;
  @SerializedName("activity_id") @Expose private Integer activityId;
  @SerializedName("activity_level_id") @Expose private Integer activityLevelId;
  @SerializedName("name") @Expose private String name;
  @SerializedName("activity_name") @Expose private String activityName;
  @SerializedName("rate") @Expose private String rate;
  @SerializedName("duration") @Expose private String duration;
  @SerializedName("date") @Expose private String date;
  @SerializedName("calories") @Expose private double calories;

  public UserActivitiesResponseBody() {
  }

  protected UserActivitiesResponseBody(Parcel in) {
    this.instanceId = (Integer) in.readValue(Integer.class.getClassLoader());
    this.activityId = (Integer) in.readValue(Integer.class.getClassLoader());
    this.activityLevelId = (Integer) in.readValue(Integer.class.getClassLoader());
    this.name = in.readString();
    this.activityName = in.readString();
    this.rate = in.readString();
    this.duration = in.readString();
    this.date = in.readString();
    this.calories = in.readDouble();
  }

  public Integer getInstanceId() {
    return instanceId;
  }

  public void setInstanceId(Integer instanceId) {
    this.instanceId = instanceId;
  }

  public Integer getActivityId() {
    return activityId;
  }

  public void setActivityId(Integer activityId) {
    this.activityId = activityId;
  }

  public Integer getActivityLevelId() {
    return activityLevelId;
  }

  public void setActivityLevelId(Integer activityLevelId) {
    this.activityLevelId = activityLevelId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getActivityName() {
    return activityName;
  }

  public void setActivityName(String activityName) {
    this.activityName = activityName;
  }

  public String getRate() {
    return rate;
  }

  public void setRate(String rate) {
    this.rate = rate;
  }

  public String getDuration() {
    return duration;
  }

  public void setDuration(String duration) {
    this.duration = duration;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public double getCalories() {
    return calories;
  }

  public void setCalories(Integer calories) {
    this.calories = calories;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeValue(this.instanceId);
    dest.writeValue(this.activityId);
    dest.writeValue(this.activityLevelId);
    dest.writeString(this.name);
    dest.writeString(this.activityName);
    dest.writeString(this.rate);
    dest.writeString(this.duration);
    dest.writeString(this.date);
    dest.writeDouble(this.calories);
  }
}
