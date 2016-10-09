package com.mcsaatchi.gmfit.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class HealthWidgetsResponseBody {
    @SerializedName("data")
    @Expose
    private List<HealthWidgetsResponseDatum> data = new ArrayList<>();

    /**
     * @return The data
     */
    public List<HealthWidgetsResponseDatum> getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(List<HealthWidgetsResponseDatum> data) {
        this.data = data;
    }
}
