package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ChronicTreatmentListResponseBody {
  @SerializedName("data") @Expose private List<ChronicTreatmentListInnerData> data;

  public List<ChronicTreatmentListInnerData> getData() {
    return data;
  }

  public void setData(List<ChronicTreatmentListInnerData> data) {
    this.data = data;
  }
}
