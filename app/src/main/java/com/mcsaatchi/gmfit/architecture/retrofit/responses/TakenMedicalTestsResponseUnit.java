package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TakenMedicalTestsResponseUnit implements Parcelable {
  public static final Creator<TakenMedicalTestsResponseUnit> CREATOR =
      new Creator<TakenMedicalTestsResponseUnit>() {
        @Override public TakenMedicalTestsResponseUnit createFromParcel(Parcel source) {
          return new TakenMedicalTestsResponseUnit(source);
        }

        @Override public TakenMedicalTestsResponseUnit[] newArray(int size) {
          return new TakenMedicalTestsResponseUnit[size];
        }
      };
  @SerializedName("id") @Expose private Integer id;
  @SerializedName("unit") @Expose private String unit;
  @SerializedName("selected") @Expose private Boolean selected;

  protected TakenMedicalTestsResponseUnit(Parcel in) {
    this.id = (Integer) in.readValue(Integer.class.getClassLoader());
    this.unit = in.readString();
    this.selected = (Boolean) in.readValue(Boolean.class.getClassLoader());
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

  /**
   * @return The selected
   */
  public Boolean getSelected() {
    return selected;
  }

  /**
   * @param selected The selected
   */
  public void setSelected(Boolean selected) {
    this.selected = selected;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeValue(this.id);
    dest.writeString(this.unit);
    dest.writeValue(this.selected);
  }
}
