package com.mcsaatchi.gmfit.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HealthWidgetsResponse {
    @SerializedName("data")
    @Expose
    private HealthWidgetsResponseData data;

    /**
     * @return The data
     */
    public HealthWidgetsResponseData getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(HealthWidgetsResponseData data) {
        this.data = data;
    }
}
