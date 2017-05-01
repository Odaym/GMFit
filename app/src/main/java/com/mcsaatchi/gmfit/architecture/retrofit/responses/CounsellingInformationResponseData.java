package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CounsellingInformationResponseData {
  @SerializedName("message") @Expose private String message;
  @SerializedName("body") @Expose private CounsellingInformationResponseBody body;
  @SerializedName("code") @Expose private Integer code;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public CounsellingInformationResponseBody getBody() {
    return body;
  }

  public void setBody(CounsellingInformationResponseBody body) {
    this.body = body;
  }

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }
}
