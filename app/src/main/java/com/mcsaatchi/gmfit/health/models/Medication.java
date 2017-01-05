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
  private int units;
  private String unitForm;
  private String remarks;
  private int treatmentDuration;

  public Medication() {
  }

  public Medication(String name, String description, String dosage, String when, int frequency,
      int units, String unitForm, int treatmentDuration, String remarks) {
    this.name = name;
    this.description = description;
    this.dosage = dosage;
    this.when = when;
    this.frequency = frequency;
    this.units = units;
    this.unitForm = unitForm;
    this.treatmentDuration = treatmentDuration;
    this.remarks = remarks;
  }

  protected Medication(Parcel in) {
    id = in.readInt();
    name = in.readString();
    description = in.readString();
    dosage = in.readString();
    when = in.readString();
    frequency = in.readInt();
    units = in.readInt();
    unitForm = in.readString();
    treatmentDuration = in.readInt();
    remarks = in.readString();
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

  public int getUnits() {
    return units;
  }

  public void setUnits(int units) {
    this.units = units;
  }

  public String getUnitForm() {
    return unitForm;
  }

  public void setUnitForm(String unitForm) {
    this.unitForm = unitForm;
  }

  public int getTreatmentDuration() {
    return treatmentDuration;
  }

  public void setTreatmentDuration(int treatmentDuration) {
    this.treatmentDuration = treatmentDuration;
  }

  public String getRemarks() {
    return remarks;
  }

  public void setRemarks(String remarks) {
    this.remarks = remarks;
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
    parcel.writeInt(units);
    parcel.writeString(unitForm);
    parcel.writeInt(treatmentDuration);
    parcel.writeString(remarks);
  }
}
