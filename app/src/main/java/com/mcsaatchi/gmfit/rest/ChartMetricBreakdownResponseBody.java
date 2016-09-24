package com.mcsaatchi.gmfit.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ChartMetricBreakdownResponseBody {
    @SerializedName("data")
    @Expose
    private List<ChartMetricBreakdownResponseDatum> data = new ArrayList<>();

    /**
     * @return The data
     */
    public List<ChartMetricBreakdownResponseDatum> getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(List<ChartMetricBreakdownResponseDatum> data) {
        this.data = data;
    }
}
