package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ActivitiesListResponseBody implements Parcelable {
  public static final Creator<ActivitiesListResponseBody> CREATOR =
      new Creator<ActivitiesListResponseBody>() {
        @Override public ActivitiesListResponseBody createFromParcel(Parcel source) {
          return new ActivitiesListResponseBody(source);
        }

        @Override public ActivitiesListResponseBody[] newArray(int size) {
          return new ActivitiesListResponseBody[size];
        }
      };
  @SerializedName("id") @Expose private Integer id;
  @SerializedName("name") @Expose private String name;
  @SerializedName("name_ar") @Expose private String nameAr;
  @SerializedName("icon") @Expose private String icon;
  @SerializedName("created_at") @Expose private String createdAt;
  @SerializedName("updated_at") @Expose private String updatedAt;
  @SerializedName("activity_levels") @Expose private List<ActivitiesListResponseDatum>
      activityLevels = null;

  public ActivitiesListResponseBody() {
  }

  protected ActivitiesListResponseBody(Parcel in) {
    this.id = (Integer) in.readValue(Integer.class.getClassLoader());
    this.name = in.readString();
    this.nameAr = in.readString();
    this.icon = in.readString();
    this.createdAt = in.readString();
    this.updatedAt = in.readString();
    this.activityLevels = in.createTypedArrayList(ActivitiesListResponseDatum.CREATOR);
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getNameAr() {
    return nameAr;
  }

  public void setNameAr(String nameAr) {
    this.nameAr = nameAr;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
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

  public List<ActivitiesListResponseDatum> getActivityLevels() {
    return activityLevels;
  }

  public void setActivityLevels(List<ActivitiesListResponseDatum> activityLevels) {
    this.activityLevels = activityLevels;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeValue(this.id);
    dest.writeString(this.name);
    dest.writeString(this.nameAr);
    dest.writeString(this.icon);
    dest.writeString(this.createdAt);
    dest.writeString(this.updatedAt);
    dest.writeTypedList(this.activityLevels);
  }
}
