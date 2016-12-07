package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ChartsBySectionResponseBody {
  @SerializedName("data") @Expose private List<ChartsBySectionResponseDatum> data =
      new ArrayList<>();

  /**
   * @return The data
   */
  public List<ChartsBySectionResponseDatum> getData() {
    return data;
  }

  /**
   * @param data The data
   */
  public void setData(List<ChartsBySectionResponseDatum> data) {
    this.data = data;
  }
}
