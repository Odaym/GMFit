package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WidgetsResponseDatum {

  @SerializedName("widget_id") @Expose private Integer widgetId;
  @SerializedName("name") @Expose private String name;
  @SerializedName("slug") @Expose private String slug;
  @SerializedName("unit") @Expose private String unit;
  @SerializedName("position") @Expose private String position;
  @SerializedName("total") @Expose private String total;

  /**
   * @return The widgetId
   */
  public Integer getWidgetId() {
    return widgetId;
  }

  /**
   * @param widgetId The widget_id
   */
  public void setWidgetId(Integer widgetId) {
    this.widgetId = widgetId;
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
   * @return The unit
   */
  public String getUnit() {
    return unit;
  }

  /**
   * @param unit The unit
   */
  public void setUnit(String unit) {
    this.unit = unit;
  }

  /**
   * @return The position
   */
  public String getPosition() {
    return position;
  }

  /**
   * @param position The position
   */
  public void setPosition(String position) {
    this.position = position;
  }

  /**
   * @return The total
   */
  public String getTotal() {
    return total;
  }

  /**
   * @param total The total
   */
  public void setTotal(String total) {
    this.total = total;
  }
}
