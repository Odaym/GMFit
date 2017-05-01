package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserProfileResponseGoal implements Parcelable {
  public static final Creator<UserProfileResponseGoal> CREATOR =
      new Creator<UserProfileResponseGoal>() {
        @Override public UserProfileResponseGoal createFromParcel(Parcel source) {
          return new UserProfileResponseGoal(source);
        }

        @Override public UserProfileResponseGoal[] newArray(int size) {
          return new UserProfileResponseGoal[size];
        }
      };
  @SerializedName("id") @Expose private String id;
  @SerializedName("name") @Expose private String name;
  @SerializedName("selected") @Expose private String selected;

  protected UserProfileResponseGoal(Parcel in) {
    this.id = in.readString();
    this.name = in.readString();
    this.selected = in.readString();
  }

  /**
   * @return The id
   */
  public String getId() {
    return id;
  }

  /**
   * @param id The id
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @return The name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name The name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return The selected
   */
  public String getSelected() {
    return selected;
  }

  /**
   * @param selected The selected
   */
  public void setSelected(String selected) {
    this.selected = selected;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.id);
    dest.writeString(this.name);
    dest.writeString(this.selected);
  }
}
