package com.mcsaatchi.gmfit.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TakenMedicalTestsResponseBody {
    @SerializedName("instance_id")
    @Expose
    private Integer instanceId;
    @SerializedName("date_taken")
    @Expose
    private String dateTaken;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("images")
    @Expose
    private List<Object> images = new ArrayList<>();
    @SerializedName("metrics")
    @Expose
    private List<TakenMedicalTestsResponseMetricsDatum> metrics = new ArrayList<>();

    /**
     * @return The instanceId
     */
    public Integer getInstanceId() {
        return instanceId;
    }

    /**
     * @param instanceId The instance_id
     */
    public void setInstanceId(Integer instanceId) {
        this.instanceId = instanceId;
    }

    /**
     * @return The dateTaken
     */
    public String getDateTaken() {
        return dateTaken;
    }

    /**
     * @param dateTaken The date_taken
     */
    public void setDateTaken(String dateTaken) {
        this.dateTaken = dateTaken;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return The images
     */
    public List<Object> getImages() {
        return images;
    }

    /**
     * @param images The images
     */
    public void setImages(List<Object> images) {
        this.images = images;
    }

    /**
     * @return The metrics
     */
    public List<TakenMedicalTestsResponseMetricsDatum> getMetrics() {
        return metrics;
    }

    /**
     * @param metrics The metrics
     */
    public void setMetrics(List<TakenMedicalTestsResponseMetricsDatum> metrics) {
        this.metrics = metrics;
    }
}
