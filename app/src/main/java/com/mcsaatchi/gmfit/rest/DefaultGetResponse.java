package com.mcsaatchi.gmfit.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DefaultGetResponse {

    @SerializedName("data")
    @Expose
    private DataGETResponse_Contents data;

    /**
     * @return The data
     */
    public DataGETResponse_Contents getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(DataGETResponse_Contents data) {
        this.data = data;
    }
}
