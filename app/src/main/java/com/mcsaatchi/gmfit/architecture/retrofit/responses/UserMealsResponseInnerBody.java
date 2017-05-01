package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserMealsResponseInnerBody {
  @SerializedName("data") @Expose private UserMealsResponseInnerData data;

  /**
   * @return The data
   */
  public UserMealsResponseInnerData getData() {
    return data;
  }

  /**
   * @param data The data
   */
  public void setData(UserMealsResponseInnerData data) {
    this.data = data;
  }
}
