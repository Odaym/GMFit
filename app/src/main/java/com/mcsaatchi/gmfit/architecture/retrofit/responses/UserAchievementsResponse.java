package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserAchievementsResponse {
  @SerializedName("data") @Expose private UserAchievementsResponseData data;

  public UserAchievementsResponseData getData() {
    return data;
  }

  public void setData(UserAchievementsResponseData data) {
    this.data = data;
  }
}
