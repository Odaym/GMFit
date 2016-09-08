package com.mcsaatchi.gmfit.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserMealsResponse {

    @SerializedName("data")
    @Expose
    private UserMealsResponseData data;

    /**
     *
     * @return
     * The data
     */
    public UserMealsResponseData getData() {
        return data;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setData(UserMealsResponseData data) {
        this.data = data;
    }
}
