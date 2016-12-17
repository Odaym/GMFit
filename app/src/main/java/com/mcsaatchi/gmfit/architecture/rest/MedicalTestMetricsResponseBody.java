package com.mcsaatchi.gmfit.architecture.rest;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class MedicalTestMetricsResponseBody implements Parcelable {
  public static final Creator<MedicalTestMetricsResponseBody> CREATOR =
      new Creator<MedicalTestMetricsResponseBody>() {
        @Override public MedicalTestMetricsResponseBody createFromParcel(Parcel source) {
          return new MedicalTestMetricsResponseBody(source);
        }

        @Override public MedicalTestMetricsResponseBody[] newArray(int size) {
          return new MedicalTestMetricsResponseBody[size];
        }
      };
  @SerializedName("name") @Expose private String name;
  @SerializedName("id") @Expose private Integer id;
  private String value;
  @SerializedName("low_value") @Expose private String lowValue;
  @SerializedName("high_value") @Expose private String highValue;
  @SerializedName("units") @Expose private List<MedicalTestMetricsResponseDatum> units =
      new ArrayList<>();

  public MedicalTestMetricsResponseBody() {
  }

  protected MedicalTestMetricsResponseBody(Parcel in) {
    this.name = in.readString();
    this.id = (Integer) in.readValue(Integer.class.getClassLoader());
    this.value = in.readString();
    this.lowValue = in.readString();
    this.highValue = in.readString();
    this.units = new ArrayList<MedicalTestMetricsResponseDatum>();
    in.readList(this.units, MedicalTestMetricsResponseDatum.class.getClassLoader());
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

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.name);
    dest.writeValue(this.id);
    dest.writeString(this.value);
    dest.writeString(this.lowValue);
    dest.writeString(this.highValue);
    dest.writeList(this.units);
  }
}
