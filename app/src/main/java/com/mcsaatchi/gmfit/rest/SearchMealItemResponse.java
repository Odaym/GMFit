package com.mcsaatchi.gmfit.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchMealItemResponse {
    @SerializedName("data")
    @Expose
    private SearchMealItemResponseData data;

    /**
     *
     * @return
     * The data
     */
    public SearchMealItemResponseData getData() {
        return data;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setData(SearchMealItemResponseData data) {
        this.data = data;
    }
}
