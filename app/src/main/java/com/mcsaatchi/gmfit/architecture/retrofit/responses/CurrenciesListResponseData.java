package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CurrenciesListResponseData {
  @Expose private String message;
  @SerializedName("body") @Expose private List<CurrenciesListResponseBody> body = null;
  @SerializedName("code") @Expose private Integer code;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public List<CurrenciesListResponseBody> getBody() {
    return body;
  }

  public void setBody(List<CurrenciesListResponseBody> body) {
    this.body = body;
  }

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }
}
