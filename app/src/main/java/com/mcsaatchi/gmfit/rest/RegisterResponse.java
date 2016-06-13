package com.mcsaatchi.gmfit.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegisterResponse {

    @SerializedName("data")
    @Expose
    private RegisterResponseData data;

    /**
     * @return The data
     */
    public RegisterResponseData getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(RegisterResponseData data) {
        this.data = data;
    }
}
