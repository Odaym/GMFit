package com.mcsaatchi.gmfit.insurance.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Clinic implements Parcelable {
  public static final Creator<Clinic> CREATOR = new Creator<Clinic>() {
    @Override public Clinic createFromParcel(Parcel source) {
      return new Clinic(source);
    }

    @Override public Clinic[] newArray(int size) {
      return new Clinic[size];
    }
  };
  private String name;
  private String address;
  private ClinicOpeningHours openingHours;
  private boolean within_network;
  private boolean open_247;
  private boolean online;

  public Clinic(String name, String address, ClinicOpeningHours openingHours,
      boolean within_network, boolean open_247, boolean online) {
    this.name = name;
    this.address = address;
    this.openingHours = openingHours;
    this.within_network = within_network;
    this.open_247 = open_247;
    this.online = online;
  }

  protected Clinic(Parcel in) {
    this.name = in.readString();
    this.address = in.readString();
    this.openingHours = in.readParcelable(ClinicOpeningHours.class.getClassLoader());
    this.within_network = in.readByte() != 0;
    this.open_247 = in.readByte() != 0;
    this.online = in.readByte() != 0;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public boolean isWithin_network() {
    return within_network;
  }

  public void setWithin_network(boolean within_network) {
    this.within_network = within_network;
  }

  public boolean isOpen_247() {
    return open_247;
  }

  public void setOpen_247(boolean open_247) {
    this.open_247 = open_247;
  }

  public boolean isOnline() {
    return online;
  }

  public void setOnline(boolean online) {
    this.online = online;
  }

  public ClinicOpeningHours getOpeningHours() {
    return openingHours;
  }

  public void setOpeningHours(ClinicOpeningHours openingHours) {
    this.openingHours = openingHours;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.name);
    dest.writeString(this.address);
    dest.writeParcelable(this.openingHours, flags);
    dest.writeByte(this.within_network ? (byte) 1 : (byte) 0);
    dest.writeByte(this.open_247 ? (byte) 1 : (byte) 0);
    dest.writeByte(this.online ? (byte) 1 : (byte) 0);
  }
}
