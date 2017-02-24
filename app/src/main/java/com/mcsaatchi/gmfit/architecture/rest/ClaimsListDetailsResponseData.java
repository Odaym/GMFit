package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ClaimsListDetailsResponseData {
  @SerializedName("message") @Expose private String message;
  @SerializedName("body") @Expose private ClaimsListDetailsResponseBody body;
  @SerializedName("code") @Expose private Integer code;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public ClaimsListDetailsResponseBody getBody() {
    return body;
  }

  public void setBody(ClaimsListDetailsResponseBody body) {
    this.body = body;
  }

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }
}
