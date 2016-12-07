package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class UserGoalsResponseData {
  @SerializedName("message") @Expose private String message;
  @SerializedName("body") @Expose private List<UserGoalsResponseBody> body = new ArrayList<>();
  @SerializedName("code") @Expose private Integer code;

  /**
   * @return The message
   */
  public String getMessage() {
    return message;
  }

  /**
   * @param message The message
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * @return The body
   */
  public List<UserGoalsResponseBody> getBody() {
    return body;
  }

  /**
   * @param body The body
   */
  public void setBody(List<UserGoalsResponseBody> body) {
    this.body = body;
  }

  /**
   * @return The code
   */
  public Integer getCode() {
    return code;
  }

  /**
   * @param code The code
   */
  public void setCode(Integer code) {
    this.code = code;
  }
}
