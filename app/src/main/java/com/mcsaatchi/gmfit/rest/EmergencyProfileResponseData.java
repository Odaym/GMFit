package com.mcsaatchi.gmfit.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EmergencyProfileResponseData {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("body")
    @Expose
    private EmergencyProfileResponseBody body;
    @SerializedName("code")
    @Expose
    private Integer code;

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
    public EmergencyProfileResponseBody getBody() {
        return body;
    }

    /**
     * @param body The body
     */
    public void setBody(EmergencyProfileResponseBody body) {
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
