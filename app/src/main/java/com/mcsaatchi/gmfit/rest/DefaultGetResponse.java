package com.mcsaatchi.gmfit.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DefaultGetResponse {

    @SerializedName("data")
    @Expose
    private DataGetResponseData data;

    /**
     * @return The data
     */
    public DataGetResponseData getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(DataGetResponseData data) {
        this.data = data;
    }
}
