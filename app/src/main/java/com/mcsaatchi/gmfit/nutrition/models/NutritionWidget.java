package com.mcsaatchi.gmfit.nutrition.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "NutritionWidget") public class NutritionWidget implements Parcelable {
  public static final Creator<NutritionWidget> CREATOR = new Creator<NutritionWidget>() {
    @Override public NutritionWidget createFromParcel(Parcel source) {
      return new NutritionWidget(source);
    }

    @Override public NutritionWidget[] newArray(int size) {
      return new NutritionWidget[size];
    }
  };

  @DatabaseField(generatedId = true) private int id;
  @DatabaseField private double value;
  @DatabaseField private String title;
  @DatabaseField private String measurementUnit;
  @DatabaseField private double percentage;
  @DatabaseField private int position;
  @DatabaseField private int widget_id;

  public NutritionWidget() {
  }

  protected NutritionWidget(Parcel in) {
    this.id = in.readInt();
    this.value = in.readDouble();
    this.title = in.readString();
    this.measurementUnit = in.readString();
    this.percentage = in.readDouble();
    this.position = in.readInt();
    this.widget_id = in.readInt();
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public double getValue() {
    return value;
  }

  public void setValue(double value) {
    this.value = value;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getMeasurementUnit() {
    return measurementUnit;
  }

  public void setMeasurementUnit(String measurementUnit) {
    this.measurementUnit = measurementUnit;
  }

  public double getPercentage() {
    return percentage;
  }

  public void setPercentage(double percentage) {
    this.percentage = percentage;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public int getWidget_id() {
    return widget_id;
  }

  public void setWidget_id(int widget_id) {
    this.widget_id = widget_id;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.id);
    dest.writeDouble(this.value);
    dest.writeString(this.title);
    dest.writeString(this.measurementUnit);
    dest.writeDouble(this.percentage);
    dest.writeInt(this.position);
    dest.writeInt(this.widget_id);
  }
}
