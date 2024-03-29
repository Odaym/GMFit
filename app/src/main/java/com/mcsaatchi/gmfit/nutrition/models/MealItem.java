package com.mcsaatchi.gmfit.nutrition.models;

import android.os.Parcel;
import android.os.Parcelable;

public class MealItem implements Parcelable {
  public static final Creator<MealItem> CREATOR = new Creator<MealItem>() {
    @Override public MealItem createFromParcel(Parcel source) {
      return new MealItem(source);
    }

    @Override public MealItem[] newArray(int size) {
      return new MealItem[size];
    }
  };
  private int id;
  private int instance_id;
  private int meal_id;
  private String name;
  private String type;
  private int sectionType;
  private String amount;
  private String measurementUnit;
  private float totalCalories;
  private String created_at;

  public MealItem() {
  }

  public MealItem(String name, String type, int sectionType) {
    this.name = name;
    this.type = type;
    this.sectionType = sectionType;
  }

  protected MealItem(Parcel in) {
    this.id = in.readInt();
    this.instance_id = in.readInt();
    this.meal_id = in.readInt();
    this.name = in.readString();
    this.type = in.readString();
    this.sectionType = in.readInt();
    this.amount = in.readString();
    this.measurementUnit = in.readString();
    this.totalCalories = in.readFloat();
  }

  public int getMeal_id() {
    return meal_id;
  }

  public void setMeal_id(int meal_id) {
    this.meal_id = meal_id;
  }

  public int getInstance_id() {
    return instance_id;
  }

  public void setInstance_id(int instance_id) {
    this.instance_id = instance_id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getSectionType() {
    return sectionType;
  }

  public void setSectionType(int sectionType) {
    this.sectionType = sectionType;
  }

  public String getAmount() {
    return amount;
  }

  public void setAmount(String amount) {
    this.amount = amount;
  }

  public String getMeasurementUnit() {
    return measurementUnit;
  }

  public void setMeasurementUnit(String measurementUnit) {
    this.measurementUnit = measurementUnit;
  }

  public float getTotalCalories() {
    return totalCalories;
  }

  public void setTotalCalories(float totalCalories) {
    this.totalCalories = totalCalories;
  }

  public String getCreated_at() {
    return created_at;
  }

  public void setCreated_at(String created_at) {
    this.created_at = created_at;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.id);
    dest.writeInt(this.instance_id);
    dest.writeInt(this.meal_id);
    dest.writeString(this.name);
    dest.writeString(this.type);
    dest.writeInt(this.sectionType);
    dest.writeString(this.amount);
    dest.writeString(this.measurementUnit);
    dest.writeFloat(this.totalCalories);
  }
}
