package com.mcsaatchi.gmfit.health.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.io.Serializable;
import java.util.Calendar;

@DatabaseTable(tableName = "MedicationReminder") public class MedicationReminder
    implements Parcelable, Serializable {

  @DatabaseField(generatedId = true) private int id;
  @DatabaseField(dataType = DataType.SERIALIZABLE, foreign = true) private Medication medication;
  @DatabaseField(dataType = DataType.SERIALIZABLE) private int[] days_of_week;
  @DatabaseField private int hour;
  @DatabaseField private int minute;
  @DatabaseField private boolean enabled;
  @DatabaseField private int totalTimesToTrigger;

  public MedicationReminder() {
  }

  public MedicationReminder(Calendar alarmTime) {

  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Medication getMedication() {
    return medication;
  }

  public void setMedication(Medication medication) {
    this.medication = medication;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public int getTotalTimesToTrigger() {
    return totalTimesToTrigger;
  }

  public int[] getDays_of_week() {
    return days_of_week;
  }

  public int getHour() {
    return hour;
  }

  public void setHour(int hour) {
    this.hour = hour;
  }

  public int getMinute() {
    return minute;
  }

  public void setMinute(int minute) {
    this.minute = minute;
  }

  public void setDays_of_week(int[] days_of_week) {
    this.days_of_week = days_of_week;
  }

  public void setTotalTimesToTrigger(int totalTimesToTrigger) {
    this.totalTimesToTrigger = totalTimesToTrigger;
  }

  public static Creator<MedicationReminder> getCREATOR() {
    return CREATOR;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.id);
    dest.writeSerializable(this.medication);
    dest.writeIntArray(this.days_of_week);
    dest.writeInt(hour);
    dest.writeInt(minute);
    dest.writeByte(this.enabled ? (byte) 1 : (byte) 0);
    dest.writeInt(this.totalTimesToTrigger);
  }

  public MedicationReminder(Parcel in) {
    this.id = in.readInt();
    this.medication = (Medication) in.readSerializable();
    this.days_of_week = in.createIntArray();
    this.hour = in.readInt();
    this.minute = in.readInt();
    this.enabled = in.readByte() != 0;
    this.totalTimesToTrigger = in.readInt();
  }

  public static final Creator<MedicationReminder> CREATOR = new Creator<MedicationReminder>() {
    @Override public MedicationReminder createFromParcel(Parcel source) {
      return new MedicationReminder(source);
    }

    @Override public MedicationReminder[] newArray(int size) {
      return new MedicationReminder[size];
    }
  };
}
