package com.mcsaatchi.gmfit.onboarding.models;

import android.os.Parcel;
import android.os.Parcelable;

public class MedicalCondition implements Parcelable {
  public static final Parcelable.Creator<MedicalCondition> CREATOR =
      new Parcelable.Creator<MedicalCondition>() {
        @Override public MedicalCondition createFromParcel(Parcel source) {
          return new MedicalCondition(source);
        }

        @Override public MedicalCondition[] newArray(int size) {
          return new MedicalCondition[size];
        }
      };
  private int id;
  private String medicalCondition;
  private boolean isSelected;

  public MedicalCondition() {
  }

  public MedicalCondition(int id, String medicalCondition, boolean isSelected) {
    this.id = id;
    this.medicalCondition = medicalCondition;
    this.isSelected = isSelected;
  }

  protected MedicalCondition(Parcel in) {
    this.id = in.readInt();
    this.medicalCondition = in.readString();
    this.isSelected = in.readByte() != 0;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getMedicalCondition() {
    return medicalCondition;
  }

  public void setMedicalCondition(String medicalCondition) {
    this.medicalCondition = medicalCondition;
  }

  public boolean isSelected() {
    return isSelected;
  }

  public void setSelected(boolean selected) {
    isSelected = selected;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.id);
    dest.writeString(this.medicalCondition);
    dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
  }
}
