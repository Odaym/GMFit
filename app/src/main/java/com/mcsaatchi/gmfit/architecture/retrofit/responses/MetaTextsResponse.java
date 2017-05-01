package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MetaTextsResponse {
  @SerializedName("data") @Expose private MetaTextsResponseData data;

  /**
   * @return The data
   */
  public MetaTextsResponseData getData() {
    return data;
  }

  /**
   * @param data The data
   */
  public void setData(MetaTextsResponseData data) {
    this.data = data;
  }
}
