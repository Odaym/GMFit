package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ActivitiesListResponseData {
  @SerializedName("message") @Expose private String message;
  @SerializedName("body") @Expose private List<ActivitiesListResponseBody> body = null;
  @SerializedName("code") @Expose private Integer code;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public List<ActivitiesListResponseBody> getBody() {
    return body;
  }

  public void setBody(List<ActivitiesListResponseBody> body) {
    this.body = body;
  }

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }
}
