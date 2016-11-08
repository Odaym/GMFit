package com.mcsaatchi.gmfit.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserMealsResponseBreakfast {
  @SerializedName("id") @Expose private Integer id;
  @SerializedName("instance_id") @Expose private Integer instance_id;
  @SerializedName("name") @Expose private String name;
  @SerializedName("amount") @Expose private String amount;
  @SerializedName("measurement_unit") @Expose private String measurementUnit;
  @SerializedName("created_at") @Expose private String createdAt;
  @SerializedName("total_calories") @Expose private Integer totalCalories;

  /**
   * @return The instance_id
   */
  public Integer getInstance_id() {
    return instance_id;
  }

  /**
   * @param instance_id The id
   */
  public void setInstance_id(Integer instance_id) {
    this.instance_id = instance_id;
  }

  /**
   * @return The id
   */
  public Integer getId() {
    return id;
  }

  /**
   * @param id The id
   */
  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * @return The name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name The name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return The amount
   */
  public String getAmount() {
    return amount;
  }

  /**
   * @param amount The amount
   */
  public void setAmount(String amount) {
    this.amount = amount;
  }

  /**
   * @return The measurementUnit
   */
  public String getMeasurementUnit() {
    return measurementUnit;
  }

  /**
   * @param measurementUnit The measurement_unit
   */
  public void setMeasurementUnit(String measurementUnit) {
    this.measurementUnit = measurementUnit;
  }

  /**
   * @return The createdAt
   */
  public String getCreatedAt() {
    return createdAt;
  }

  /**
   * @param createdAt The created_at
   */
  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  /**
   * @return The totalCalories
   */
  public Integer getTotalCalories() {
    return totalCalories;
  }

  /**
   * @param totalCalories The total_calories
   */
  public void setTotalCalories(Integer totalCalories) {
    this.totalCalories = totalCalories;
  }
}
