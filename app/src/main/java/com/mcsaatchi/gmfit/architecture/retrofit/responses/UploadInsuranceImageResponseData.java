package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UploadInsuranceImageResponseData {
  @SerializedName("message") @Expose private String message;
  @SerializedName("body") @Expose private UploadInsuranceImageResponseBody body;
  @SerializedName("code") @Expose private Integer code;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public UploadInsuranceImageResponseBody getBody() {
    return body;
  }

  public void setBody(UploadInsuranceImageResponseBody body) {
    this.body = body;
  }

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }
}
