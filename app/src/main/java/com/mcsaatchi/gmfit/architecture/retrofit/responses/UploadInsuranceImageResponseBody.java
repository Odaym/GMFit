package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UploadInsuranceImageResponseBody {
  @SerializedName("path") @Expose private String path;

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }
}
