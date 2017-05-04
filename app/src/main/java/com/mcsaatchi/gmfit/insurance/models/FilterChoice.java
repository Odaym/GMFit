package com.mcsaatchi.gmfit.insurance.models;

import android.os.Parcel;
import android.os.Parcelable;

public class FilterChoice implements Parcelable {
  public static final Creator<FilterChoice> CREATOR = new Creator<FilterChoice>() {
    @Override public FilterChoice createFromParcel(Parcel source) {
      return new FilterChoice(source);
    }

    @Override public FilterChoice[] newArray(int size) {
      return new FilterChoice[size];
    }
  };
  private String name;
  private boolean selected;

  public FilterChoice(String name, boolean selected) {
    this.name = name;
    this.selected = selected;
  }

  protected FilterChoice(Parcel in) {
    this.name = in.readString();
    this.selected = in.readByte() != 0;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.name);
    dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
  }
}
