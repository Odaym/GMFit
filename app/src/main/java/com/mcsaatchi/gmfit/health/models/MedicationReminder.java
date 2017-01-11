package com.mcsaatchi.gmfit.health.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.io.Serializable;
import java.util.Arrays;

@DatabaseTable(tableName = "MedicationReminder") public class MedicationReminder
    implements Parcelable, Serializable {

  public static final Creator<MedicationReminder> CREATOR = new Creator<MedicationReminder>() {
    @Override public MedicationReminder createFromParcel(Parcel source) {
      return new MedicationReminder(source);
    }

    @Override public MedicationReminder[] newArray(int size) {
      return new MedicationReminder[size];
    }
  };
  @DatabaseField(generatedId = true) private int id;
  @DatabaseField(dataType = DataType.SERIALIZABLE, foreign = true) private Medication medication;
  @DatabaseField(dataType = DataType.SERIALIZABLE) private int[] days_of_week;
  @DatabaseField private int hour;
  @DatabaseField private int minute;
  @DatabaseField private int second;
  @DatabaseField private String AM_PM;
  @DatabaseField private boolean enabled;
  @DatabaseField private int totalTimesToTrigger;

  public MedicationReminder() {
  }

  public MedicationReminder(int[] days_of_week, int hour, int minute, int second, String AM_PM) {
    this.days_of_week = days_of_week;
    this.hour = hour;
    this.minute = minute;
    this.second = second;
    this.AM_PM = AM_PM;
  }

  protected MedicationReminder(Parcel in) {
    this.id = in.readInt();
    this.days_of_week = in.createIntArray();
    this.hour = in.readInt();
    this.minute = in.readInt();
    this.second = in.readInt();
    this.AM_PM = in.readString();
    this.enabled = in.readByte() != 0;
    this.totalTimesToTrigger = in.readInt();
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

  public int[] getDays_of_week() {
    return days_of_week;
  }

  public void setDays_of_week(int[] days_of_week) {
    this.days_of_week = days_of_week;
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

  public int getSecond() {
    return second;
  }

  public void setSecond(int second) {
    this.second = second;
  }

  public String getAM_PM() {
    return AM_PM;
  }

  public void setAM_PM(String AM_PM) {
    this.AM_PM = AM_PM;
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

  public void setTotalTimesToTrigger(int totalTimesToTrigger) {
    this.totalTimesToTrigger = totalTimesToTrigger;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.id);
    dest.writeIntArray(this.days_of_week);
    dest.writeInt(this.hour);
    dest.writeInt(this.minute);
    dest.writeInt(this.second);
    dest.writeString(this.AM_PM);
    dest.writeByte(this.enabled ? (byte) 1 : (byte) 0);
    dest.writeInt(this.totalTimesToTrigger);
  }

  @Override public String toString() {
    return "MedicationReminder{" +
        "id=" + id +
        ", medication=" + medication +
        ", days_of_week=" + Arrays.toString(days_of_week) +
        ", hour=" + hour +
        ", minute=" + minute +
        ", second=" + second +
        ", AM_PM='" + AM_PM + '\'' +
        ", enabled=" + enabled +
        ", totalTimesToTrigger=" + totalTimesToTrigger +
        '}';
  }
}
