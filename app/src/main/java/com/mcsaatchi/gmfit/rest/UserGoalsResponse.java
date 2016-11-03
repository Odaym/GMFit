package com.mcsaatchi.gmfit.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserGoalsResponse {
    @SerializedName("data")
    @Expose
    private UserGoalsResponseData data;

    /**
     * @return The data
     */
    public UserGoalsResponseData getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(UserGoalsResponseData data) {
        this.data = data;
    }
}
