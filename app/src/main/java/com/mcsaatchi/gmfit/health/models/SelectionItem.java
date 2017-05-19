package com.mcsaatchi.gmfit.health.models;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;

public class SelectionItem implements Parcelable, Serializable {
  public static final Creator<SelectionItem> CREATOR = new Creator<SelectionItem>() {
    @Override public SelectionItem createFromParcel(Parcel in) {
      return new SelectionItem(in);
    }

    @Override public SelectionItem[] newArray(int size) {
      return new SelectionItem[size];
    }
  };
  private String selectionName;
  private boolean itemSelected;
  private int id;

  public SelectionItem(String selectionName, boolean itemSelected) {
    this.selectionName = selectionName;
    this.itemSelected = itemSelected;
  }

  protected SelectionItem(Parcel in) {
    id = in.readInt();
    selectionName = in.readString();
    itemSelected = in.readByte() != 0;
  }

  public String getSelectionName() {
    return selectionName;
  }

  public void setSelectionName(String selectionName) {
    this.selectionName = selectionName;
  }

  public void setItemName(String selectionName) {
    this.selectionName = selectionName;
  }

  public boolean isItemSelected() {
    return itemSelected;
  }

  public void setItemSelected(boolean itemSelected) {
    this.itemSelected = itemSelected;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel parcel, int i) {
    parcel.writeInt(id);
    parcel.writeString(selectionName);
    parcel.writeByte((byte) (itemSelected ? 1 : 0));
  }
}
