package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateNewRequestResponse {
  @SerializedName("data") @Expose private CreateNewRequestResponseData data;

  public CreateNewRequestResponseData getData() {
    return data;
  }

  public void setData(CreateNewRequestResponseData data) {
    this.data = data;
  }
}
