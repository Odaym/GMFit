package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class InquiriesListResponseBody {
  @SerializedName("data") @Expose private ArrayList<InquiriesListResponseInnerData> data;

  public ArrayList<InquiriesListResponseInnerData> getData() {
    return data;
  }

  public void setData(ArrayList<InquiriesListResponseInnerData> data) {
    this.data = data;
  }
}
