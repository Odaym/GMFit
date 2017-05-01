package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ClaimsListResponseBody {
  @SerializedName("data") @Expose private List<ClaimsListResponseDatum> data = null;

  public List<ClaimsListResponseDatum> getData() {
    return data;
  }

  public void setData(List<ClaimsListResponseDatum> data) {
    this.data = data;
  }
}
