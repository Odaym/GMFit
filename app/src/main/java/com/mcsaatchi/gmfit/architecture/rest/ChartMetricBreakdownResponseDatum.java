package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChartMetricBreakdownResponseDatum {
  @SerializedName("metric_id") @Expose private String metricId;
  @SerializedName("name") @Expose private String name;
  @SerializedName("value") @Expose private String value;
  @SerializedName("type") @Expose private String type;
  @SerializedName("slug") @Expose private String slug;
  @SerializedName("date") @Expose private String date;

  /**
   * @return The metricId
   */
  public String getMetricId() {
    return metricId;
  }

  /**
   * @param metricId The metric_id
   */
  public void setMetricId(String metricId) {
    this.metricId = metricId;
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
   * @return The value
   */
  public String getValue() {
    return value;
  }

  /**
   * @param value The value
   */
  public void setValue(String value) {
    this.value = value;
  }

  /**
   * @return The type
   */
  public String getType() {
    return type;
  }

  /**
   * @param type The type
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * @return The slug
   */
  public String getSlug() {
    return slug;
  }

  /**
   * @param slug The slug
   */
  public void setSlug(String slug) {
    this.slug = slug;
  }

  /**
   * @return The date
   */
  public String getDate() {
    return date;
  }

  /**
   * @param date The date
   */
  public void setDate(String date) {
    this.date = date;
  }
}
