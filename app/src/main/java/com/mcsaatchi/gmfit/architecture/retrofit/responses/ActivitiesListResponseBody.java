package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ActivitiesListResponseBody {
  @SerializedName("id") @Expose private Integer id;
  @SerializedName("name") @Expose private String name;
  @SerializedName("name_ar") @Expose private Object nameAr;
  @SerializedName("icon") @Expose private String icon;
  @SerializedName("created_at") @Expose private String createdAt;
  @SerializedName("updated_at") @Expose private String updatedAt;
  @SerializedName("activity_levels") @Expose private List<ActivitiesListResponseDatum> activityLevels = null;

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

  public Object getNameAr() {
    return nameAr;
  }

  public void setNameAr(Object nameAr) {
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
}
