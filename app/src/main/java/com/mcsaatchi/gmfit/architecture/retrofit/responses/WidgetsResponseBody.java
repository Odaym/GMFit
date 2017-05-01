package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class WidgetsResponseBody {
  @SerializedName("data") @Expose private List<WidgetsResponseDatum> data = new ArrayList<>();

  /**
   * @return The data
   */
  public List<WidgetsResponseDatum> getData() {
    return data;
  }

  /**
   * @param data The data
   */
  public void setData(List<WidgetsResponseDatum> data) {
    this.data = data;
  }
}
