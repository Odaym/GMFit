package com.mcsaatchi.gmfit.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChartsBySectionResponse {
    @SerializedName("data")
    @Expose
    private ChartsBySectionResponseData data;

    /**
     * @return The data
     */
    public ChartsBySectionResponseData getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(ChartsBySectionResponseData data) {
        this.data = data;
    }
}
