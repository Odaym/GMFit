package com.mcsaatchi.gmfit.rest;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class MedicalTestMetricsResponseBody implements Parcelable{
  @SerializedName("name") @Expose private String name;
  @SerializedName("id") @Expose private Integer id;
  private String value;
  private String unit;
  private int unit_position;
  @SerializedName("low_value") @Expose private String lowValue;
  @SerializedName("high_value") @Expose private String highValue;
  @SerializedName("units") @Expose private List<MedicalTestMetricsResponseDatum> units =
      new ArrayList<>();

  protected MedicalTestMetricsResponseBody(Parcel in) {
    name = in.readString();
    value = in.readString();
    unit_position = in.readInt();
    lowValue = in.readString();
    highValue = in.readString();
  }

  public static final Creator<MedicalTestMetricsResponseBody> CREATOR =
      new Creator<MedicalTestMetricsResponseBody>() {
        @Override public MedicalTestMetricsResponseBody createFromParcel(Parcel in) {
          return new MedicalTestMetricsResponseBody(in);
        }

        @Override public MedicalTestMetricsResponseBody[] newArray(int size) {
          return new MedicalTestMetricsResponseBody[size];
        }
      };

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

  public int getUnit_position() {
    return unit_position;
  }

  public void setUnit_position(int unit_position) {
    this.unit_position = unit_position;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel parcel, int i) {
    parcel.writeString(name);
    parcel.writeString(value);
    parcel.writeInt(unit_position);
    parcel.writeString(lowValue);
    parcel.writeString(highValue);
  }
}
