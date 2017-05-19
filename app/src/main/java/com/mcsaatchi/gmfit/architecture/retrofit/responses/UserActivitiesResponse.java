package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserActivitiesResponse {
  @SerializedName("data") @Expose private UserActivitiesResponseData data;

  public UserActivitiesResponseData getData() {
    return data;
  }

  public void setData(UserActivitiesResponseData data) {
    this.data = data;
  }
}
