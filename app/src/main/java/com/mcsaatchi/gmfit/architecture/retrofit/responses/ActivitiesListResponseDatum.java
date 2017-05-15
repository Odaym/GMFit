package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ActivitiesListResponseDatum {
  @Expose
  private Integer id;
  @SerializedName("fitness_activity_id")
  @Expose
  private String fitnessActivityId;
  @SerializedName("name")
  @Expose
  private String name;
  @SerializedName("rate")
  @Expose
  private String rate;
  @SerializedName("created_at")
  @Expose
  private String createdAt;
  @SerializedName("updated_at")
  @Expose
  private String updatedAt;

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
}
