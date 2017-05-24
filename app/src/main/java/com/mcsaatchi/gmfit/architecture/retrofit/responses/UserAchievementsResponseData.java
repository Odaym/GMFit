package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UserAchievementsResponseData {
  @SerializedName("message") @Expose private String message;
  @SerializedName("body") @Expose private List<UserAchievementsResponseBody> body = null;
  @SerializedName("code") @Expose private Integer code;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public List<UserAchievementsResponseBody> getBody() {
    return body;
  }

  public void setBody(List<UserAchievementsResponseBody> body) {
    this.body = body;
  }

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }
}
