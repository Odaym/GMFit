package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class SearchMealItemResponseBody {
  @SerializedName("data") @Expose private List<SearchMealItemResponseDatum> data =
      new ArrayList<>();

  /**
   * @return The data
   */
  public List<SearchMealItemResponseDatum> getData() {
    return data;
  }

  /**
   * @param data The data
   */
  public void setData(List<SearchMealItemResponseDatum> data) {
    this.data = data;
  }
}
