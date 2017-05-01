package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchMealItemResponseDatum {
  @SerializedName("id") @Expose private Integer id;
  @SerializedName("name") @Expose private String name;
  @SerializedName("measurement_unit") @Expose private String measurementUnit;

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
}
