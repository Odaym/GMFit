package com.mcsaatchi.gmfit.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChartMetricBreakdownResponse {
    @SerializedName("data")
    @Expose
    private ChartMetricBreakdownResponseData data;

    /**
     * @return The data
     */
    public ChartMetricBreakdownResponseData getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(ChartMetricBreakdownResponseData data) {
        this.data = data;
    }
}
