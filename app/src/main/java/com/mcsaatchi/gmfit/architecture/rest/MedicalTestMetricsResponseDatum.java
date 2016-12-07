package com.mcsaatchi.gmfit.architecture.rest;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MedicalTestMetricsResponseDatum implements Parcelable{
  public static final Creator<MedicalTestMetricsResponseDatum> CREATOR =
      new Creator<MedicalTestMetricsResponseDatum>() {
        @Override public MedicalTestMetricsResponseDatum createFromParcel(Parcel source) {
          return new MedicalTestMetricsResponseDatum(source);
        }

        @Override public MedicalTestMetricsResponseDatum[] newArray(int size) {
          return new MedicalTestMetricsResponseDatum[size];
        }
      };
  @SerializedName("id") @Expose private Integer id;
  @SerializedName("unit") @Expose private String unit;
  private boolean selected = false;

  public MedicalTestMetricsResponseDatum() {
  }

  protected MedicalTestMetricsResponseDatum(Parcel in) {
    this.id = (Integer) in.readValue(Integer.class.getClassLoader());
    this.unit = in.readString();
    this.selected = in.readByte() != 0;
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

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeValue(this.id);
    dest.writeString(this.unit);
    dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
  }
}
