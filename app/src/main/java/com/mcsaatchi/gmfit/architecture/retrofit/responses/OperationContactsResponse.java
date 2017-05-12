package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OperationContactsResponse {
  @SerializedName("data") @Expose private OperationContactsResponseData data;

  public OperationContactsResponseData getData() {
    return data;
  }

  public void setData(OperationContactsResponseData data) {
    this.data = data;
  }
}
