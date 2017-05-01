package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SlugBreakdownResponseBody {
  @SerializedName("data") @Expose private SlugBreakdownResponseInnerData data;

  /**
   * @return The data
   */
  public SlugBreakdownResponseInnerData getData() {
    return data;
  }

  /**
   * @param data The data
   */
  public void setData(SlugBreakdownResponseInnerData data) {
    this.data = data;
  }
}
