package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MealMetricsResponseBody {
  @SerializedName("id") @Expose private Integer id;
  @SerializedName("name") @Expose private String name;
  @SerializedName("measurement_unit") @Expose private String measurementUnit;
  @SerializedName("metrics") @Expose private List<MealMetricsResponseDatum> metrics =
      new ArrayList<MealMetricsResponseDatum>();

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

  /**
   * @return The metrics
   */
  public List<MealMetricsResponseDatum> getMetrics() {
    return metrics;
  }

  /**
   * @param metrics The metrics
   */
  public void setMetrics(List<MealMetricsResponseDatum> metrics) {
    this.metrics = metrics;
  }
}
