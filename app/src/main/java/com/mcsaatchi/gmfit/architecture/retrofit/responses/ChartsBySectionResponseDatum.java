package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChartsBySectionResponseDatum {
  @SerializedName("id") @Expose private Integer id;
  @SerializedName("name") @Expose private String name;
  @SerializedName("unit") @Expose private String unit;
  @SerializedName("slug") @Expose private String slug;

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

  public String getSlug() {
    return slug;
  }

  public void setSlug(String slug) {
    this.slug = slug;
  }
}
