package com.mcsaatchi.gmfit.insurance.models;

public class MedicalInformationModel {
    private String medicineName;
    private String status;
    private String tabletCount;
    private String frequency;
    private String duration;

    public MedicalInformationModel(String medicineName, String status, String tabletCount, String frequency, String duration) {
        this.medicineName = medicineName;
        this.status = status;
        this.tabletCount = tabletCount;
        this.frequency = frequency;
        this.duration = duration;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTabletCount() {
        return tabletCount;
    }

    public void setTabletCount(String tabletCount) {
        this.tabletCount = tabletCount;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}