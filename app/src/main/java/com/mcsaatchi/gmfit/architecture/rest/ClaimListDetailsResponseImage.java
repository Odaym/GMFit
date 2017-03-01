package com.mcsaatchi.gmfit.architecture.rest;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ClaimListDetailsResponseImage implements Parcelable {
  public static final Creator<ClaimListDetailsResponseImage> CREATOR =
      new Creator<ClaimListDetailsResponseImage>() {
        @Override public ClaimListDetailsResponseImage createFromParcel(Parcel source) {
          return new ClaimListDetailsResponseImage(source);
        }

        @Override public ClaimListDetailsResponseImage[] newArray(int size) {
          return new ClaimListDetailsResponseImage[size];
        }
      };
  @SerializedName("content") @Expose private String content;
  @SerializedName("documType") @Expose private Integer documType;
  @SerializedName("id") @Expose private String id;
  @SerializedName("name") @Expose private String name;
  @SerializedName("status") @Expose private String status;

  public ClaimListDetailsResponseImage() {
  }

  protected ClaimListDetailsResponseImage(Parcel in) {
    this.content = in.readString();
    this.documType = (Integer) in.readValue(Integer.class.getClassLoader());
    this.id = in.readString();
    this.name = in.readString();
    this.status = in.readString();
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Integer getDocumType() {
    return documType;
  }

  public void setDocumType(Integer documType) {
    this.documType = documType;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.content);
    dest.writeValue(this.documType);
    dest.writeString(this.id);
    dest.writeString(this.name);
    dest.writeString(this.status);
  }
}
