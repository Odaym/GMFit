package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AchievementsResponse {
  @SerializedName("data") @Expose private AchievementsResponseData data;

  public AchievementsResponseData getData() {
    return data;
  }

  public void setData(AchievementsResponseData data) {
    this.data = data;
  }
}
