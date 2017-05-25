package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AchievementsResponseBody implements Parcelable {
  @SerializedName("id") @Expose private Integer id;
  @SerializedName("name") @Expose private String name;
  @SerializedName("description") @Expose private String description;
  @SerializedName("image") @Expose private String image;
  @SerializedName("progress") @Expose private Integer progress;
  @SerializedName("is_done") @Expose private Boolean isDone;
  @SerializedName("finishes") @Expose private Integer finishes;
  @SerializedName("last_finish") @Expose private String lastFinish;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
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

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public Integer getProgress() {
    return progress;
  }

  public void setProgress(Integer progress) {
    this.progress = progress;
  }

  public Boolean getIsDone() {
    return isDone;
  }

  public void setIsDone(Boolean isDone) {
    this.isDone = isDone;
  }

  public Integer getFinishes() {
    return finishes;
  }

  public void setFinishes(Integer finishes) {
    this.finishes = finishes;
  }

  public String getLastFinish() {
    return lastFinish;
  }

  public void setLastFinish(String lastFinish) {
    this.lastFinish = lastFinish;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeValue(this.id);
    dest.writeString(this.name);
    dest.writeString(this.description);
    dest.writeString(this.image);
    dest.writeValue(this.progress);
    dest.writeValue(this.isDone);
    dest.writeValue(this.finishes);
    dest.writeString(this.lastFinish);
  }

  public AchievementsResponseBody() {
  }

  protected AchievementsResponseBody(Parcel in) {
    this.id = (Integer) in.readValue(Integer.class.getClassLoader());
    this.name = in.readString();
    this.description = in.readString();
    this.image = in.readString();
    this.progress = (Integer) in.readValue(Integer.class.getClassLoader());
    this.isDone = (Boolean) in.readValue(Boolean.class.getClassLoader());
    this.finishes = (Integer) in.readValue(Integer.class.getClassLoader());
    this.lastFinish = in.readString();
  }

  public static final Creator<AchievementsResponseBody> CREATOR =
      new Creator<AchievementsResponseBody>() {
        @Override public AchievementsResponseBody createFromParcel(Parcel source) {
          return new AchievementsResponseBody(source);
        }

        @Override public AchievementsResponseBody[] newArray(int size) {
          return new AchievementsResponseBody[size];
        }
      };
}
