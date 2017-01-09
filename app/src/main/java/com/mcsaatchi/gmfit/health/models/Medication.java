package com.mcsaatchi.gmfit.health.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.ArrayList;

@DatabaseTable(tableName = "Medication") public class Medication implements Parcelable {
  public static final Creator<Medication> CREATOR = new Creator<Medication>() {
    @Override public Medication createFromParcel(Parcel source) {
      return new Medication(source);
    }

    @Override public Medication[] newArray(int size) {
      return new Medication[size];
    }
  };
  @DatabaseField(generatedId = true) private int id;
  @DatabaseField private String name;
  @DatabaseField private String description;
  @DatabaseField private String dosage;
  @DatabaseField(dataType = DataType.SERIALIZABLE) private ArrayList<DayChoice> when;
  @DatabaseField private String whenString;
  @DatabaseField private int frequency;
  @DatabaseField private int units;
  @DatabaseField private String unitForm;
  @DatabaseField private String remarks;
  @DatabaseField private int treatmentDuration;

  public Medication() {
  }

  public Medication(String name, String description, String dosage, ArrayList<DayChoice> when,
      String whenString, int frequency, int units, String unitForm, int treatmentDuration,
      String remarks) {
    this.name = name;
    this.description = description;
    this.dosage = dosage;
    this.when = when;
    this.whenString = whenString;
    this.frequency = frequency;
    this.units = units;
    this.unitForm = unitForm;
    this.treatmentDuration = treatmentDuration;
    this.remarks = remarks;
  }

  protected Medication(Parcel in) {
    this.id = in.readInt();
    this.name = in.readString();
    this.description = in.readString();
    this.dosage = in.readString();
    this.when = in.createTypedArrayList(DayChoice.CREATOR);
    this.whenString = in.readString();
    this.frequency = in.readInt();
    this.units = in.readInt();
    this.unitForm = in.readString();
    this.remarks = in.readString();
    this.treatmentDuration = in.readInt();
  }

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

  public ArrayList<DayChoice> getWhen() {
    return when;
  }

  public void setWhen(ArrayList<DayChoice> when) {
    this.when = when;
  }

  public String getWhenString() {
    return whenString;
  }

  public void setWhenString(String whenString) {
    this.whenString = whenString;
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

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.id);
    dest.writeString(this.name);
    dest.writeString(this.description);
    dest.writeString(this.dosage);
    dest.writeTypedList(this.when);
    dest.writeString(this.whenString);
    dest.writeInt(this.frequency);
    dest.writeInt(this.units);
    dest.writeString(this.unitForm);
    dest.writeString(this.remarks);
    dest.writeInt(this.treatmentDuration);
  }
}
