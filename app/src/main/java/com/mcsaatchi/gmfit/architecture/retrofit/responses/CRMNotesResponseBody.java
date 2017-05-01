package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CRMNotesResponseBody {
  @SerializedName("data") @Expose private CRMNotesResponseInnerData data;

  public CRMNotesResponseInnerData getData() {
    return data;
  }

  public void setData(CRMNotesResponseInnerData data) {
    this.data = data;
  }
}
