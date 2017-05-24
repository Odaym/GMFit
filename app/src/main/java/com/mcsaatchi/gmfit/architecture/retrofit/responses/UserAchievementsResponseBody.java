package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserAchievementsResponseBody {
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
}
