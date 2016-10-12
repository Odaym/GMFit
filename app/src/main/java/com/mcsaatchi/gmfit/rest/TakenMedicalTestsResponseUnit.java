package com.mcsaatchi.gmfit.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TakenMedicalTestsResponseUnit {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("unit")
    @Expose
    private String unit;
    @SerializedName("selected")
    @Expose
    private Boolean selected;

    /**
     * @return The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * @param unit The unit
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * @return The selected
     */
    public Boolean getSelected() {
        return selected;
    }

    /**
     * @param selected The selected
     */
    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
