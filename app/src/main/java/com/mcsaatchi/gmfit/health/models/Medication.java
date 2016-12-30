package com.mcsaatchi.gmfit.health.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Medication implements Parcelable{
  private int id;
  private String name;
  private String description;
  private String dosage;
  private String when;
  private int frequency;

  public Medication() {
  }

  public Medication(String name, String description, String dosage, String when, int frequency) {
    this.name = name;
    this.description = description;
    this.dosage = dosage;
    this.when = when;
    this.frequency = frequency;
  }

  protected Medication(Parcel in) {
    id = in.readInt();
    name = in.readString();
    description = in.readString();
    dosage = in.readString();
    when = in.readString();
    frequency = in.readInt();
  }

  public static final Creator<Medication> CREATOR = new Creator<Medication>() {
    @Override public Medication createFromParcel(Parcel in) {
      return new Medication(in);
    }

    @Override public Medication[] newArray(int size) {
      return new Medication[size];
    }
  };

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDosage() {
    return dosage;
  }

  public void setDosage(String dosage) {
    this.dosage = dosage;
  }

  public String getWhen() {
    return when;
  }

  public void setWhen(String when) {
    this.when = when;
  }

  public int getFrequency() {
    return frequency;
  }

  public void setFrequency(int frequency) {
    this.frequency = frequency;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel parcel, int i) {
    parcel.writeInt(id);
    parcel.writeString(name);
    parcel.writeString(description);
    parcel.writeString(dosage);
    parcel.writeString(when);
    parcel.writeInt(frequency);
  }
}
