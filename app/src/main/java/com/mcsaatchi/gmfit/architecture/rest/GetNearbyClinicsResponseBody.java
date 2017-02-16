package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GetNearbyClinicsResponseBody {
  @SerializedName("data") @Expose private List<GetNearbyClinicsResponseDatum> data = null;

  public List<GetNearbyClinicsResponseDatum> getData() {
    return data;
  }

  public void setData(List<GetNearbyClinicsResponseDatum> data) {
    this.data = data;
  }
}
