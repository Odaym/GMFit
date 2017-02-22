package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateNewRequestResponseBody {
  @SerializedName("data") @Expose private CreateNewRequestResponseInnerData data;

  public CreateNewRequestResponseInnerData getData() {
    return data;
  }

  public void setData(CreateNewRequestResponseInnerData data) {
    this.data = data;
  }
}
