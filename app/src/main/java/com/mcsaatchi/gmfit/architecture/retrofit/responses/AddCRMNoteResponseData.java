package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddCRMNoteResponseData {
  @SerializedName("message") @Expose private String message;
  @SerializedName("body") @Expose private AddCRMNoteResponseBody body;
  @SerializedName("code") @Expose private Integer code;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public AddCRMNoteResponseBody getBody() {
    return body;
  }

  public void setBody(AddCRMNoteResponseBody body) {
    this.body = body;
  }

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }
}
