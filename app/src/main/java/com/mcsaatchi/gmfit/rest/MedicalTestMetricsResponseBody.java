package com.mcsaatchi.gmfit.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class MedicalTestMetricsResponseBody {
  @SerializedName("name") @Expose private String name;
  @SerializedName("id") @Expose private Integer id;
  private String value;
  @SerializedName("low_value") @Expose private String lowValue;
  @SerializedName("high_value") @Expose private String highValue;
  @SerializedName("units") @Expose private List<MedicalTestMetricsResponseDatum> units =
      new ArrayList<>();

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
   * @return The lowValue
   */
  public String getLowValue() {
    return lowValue;
  }

  /**
   * @param lowValue The low_value
   */
  public void setLowValue(String lowValue) {
    this.lowValue = lowValue;
  }

  /**
   * @return The highValue
   */
  public String getHighValue() {
    return highValue;
  }

  /**
   * @param highValue The high_value
   */
  public void setHighValue(String highValue) {
    this.highValue = highValue;
  }

  /**
   * @return The units
   */
  public List<MedicalTestMetricsResponseDatum> getUnits() {
    return units;
  }

  /**
   * @param units The units
   */
  public void setUnits(List<MedicalTestMetricsResponseDatum> units) {
    this.units = units;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
