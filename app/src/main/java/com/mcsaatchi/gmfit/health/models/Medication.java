package com.mcsaatchi.gmfit.health.models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import java.io.Serializable;
import java.util.ArrayList;

@DatabaseTable(tableName = "Medication") public class Medication implements Serializable {
  @DatabaseField(generatedId = true) private int id;
  @DatabaseField private String name;
  @DatabaseField private String medCode;
  @DatabaseField private String description;
  @DatabaseField private String dosage;
  @DatabaseField private int frequency;
  @DatabaseField private int frequencyType;
  @DatabaseField private int treatmentDuration;
  @ForeignCollectionField private ForeignCollection<MedicationReminder> medicationReminders;
  @DatabaseField(dataType = DataType.SERIALIZABLE) private ArrayList<SelectionItem> when;
  @DatabaseField private String whenString;
  @DatabaseField private int units;
  @DatabaseField private String unitForm;
  @DatabaseField private String remarks;
  @DatabaseField private boolean remindersEnabled;

  public Medication() {
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

  public int getTreatmentDuration() {
    return treatmentDuration;
  }

  public void setTreatmentDuration(int treatmentDuration) {
    this.treatmentDuration = treatmentDuration;
  }

  public ForeignCollection<MedicationReminder> getMedicationReminders() {
    return medicationReminders;
  }

  public void setMedicationReminders(ForeignCollection<MedicationReminder> medicationReminders) {
    this.medicationReminders = medicationReminders;
  }

  public ArrayList<SelectionItem> getWhen() {
    return when;
  }

  public void setWhen(ArrayList<SelectionItem> when) {
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

  public int getFrequencyType() {
    return frequencyType;
  }

  public void setFrequencyType(int frequencyType) {
    this.frequencyType = frequencyType;
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

  public String getRemarks() {
    return remarks;
  }

  public void setRemarks(String remarks) {
    this.remarks = remarks;
  }

  public boolean isRemindersEnabled() {
    return remindersEnabled;
  }

  public void setRemindersEnabled(boolean remindersEnabled) {
    this.remindersEnabled = remindersEnabled;
  }

  public String getMedCode() {
    return medCode;
  }

  public void setMedCode(String medCode) {
    this.medCode = medCode;
  }

  @Override public String toString() {
    return "Medication{"
        + "id="
        + id
        + ", name='"
        + name
        + '\''
        + ", description='"
        + description
        + '\''
        + ", dosage='"
        + dosage
        + '\''
        + ", frequency="
        + frequency
        + ", frequencyType="
        + frequencyType
        + ", treatmentDuration="
        + treatmentDuration
        + ", medicationReminders="
        + medicationReminders
        + ", when="
        + when
        + ", whenString='"
        + whenString
        + '\''
        + ", units="
        + units
        + ", unitForm='"
        + unitForm
        + '\''
        + ", remarks='"
        + remarks
        + '\''
        + ", remindersEnabled="
        + remindersEnabled
        + '}';
  }
}