package com.mcsaatchi.gmfit.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class UserProfileResponseDatum {
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("birthday")
    @Expose
    private String birthday;
    @SerializedName("blood_type")
    @Expose
    private String bloodType;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("weight")
    @Expose
    private String weight;
    @SerializedName("height")
    @Expose
    private String height;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("metric_system")
    @Expose
    private String metricSystem;
    @SerializedName("body_mass_index")
    @Expose
    private String bodyMassIndex;
    @SerializedName("body_fat_percentage")
    @Expose
    private String bodyFatPercentage;
    @SerializedName("lean_body_mass")
    @Expose
    private String leanBodyMass;
    @SerializedName("verified")
    @Expose
    private Boolean verified;
    @SerializedName("medical_conditions")
    @Expose
    private List<UserProfileResponseMedicalCondition> medicalConditions = new ArrayList<UserProfileResponseMedicalCondition>();
    @SerializedName("profile_picture")
    @Expose
    private String profile_picture;

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
     * @return The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return The birthday
     */
    public String getBirthday() {
        return birthday;
    }

    /**
     * @param birthday The birthday
     */
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    /**
     * @return The bloodType
     */
    public String getBloodType() {
        return bloodType;
    }

    /**
     * @param bloodType The blood_type
     */
    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    /**
     * @return The gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * @param gender The gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * @return The weight
     */
    public String getWeight() {
        return weight;
    }

    /**
     * @param weight The weight
     */
    public void setWeight(String weight) {
        this.weight = weight;
    }

    /**
     * @return The height
     */
    public String getHeight() {
        return height;
    }

    /**
     * @param height The height
     */
    public void setHeight(String height) {
        this.height = height;
    }

    /**
     * @return The country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country The country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * @return The metricSystem
     */
    public String getMetricSystem() {
        return metricSystem;
    }

    /**
     * @param metricSystem The metric_system
     */
    public void setMetricSystem(String metricSystem) {
        this.metricSystem = metricSystem;
    }

    /**
     * @return The bodyMassIndex
     */
    public String getBodyMassIndex() {
        return bodyMassIndex;
    }

    /**
     * @param bodyMassIndex The body_mass_index
     */
    public void setBodyMassIndex(String bodyMassIndex) {
        this.bodyMassIndex = bodyMassIndex;
    }

    /**
     * @return The bodyFatPercentage
     */
    public String getBodyFatPercentage() {
        return bodyFatPercentage;
    }

    /**
     * @param bodyFatPercentage The body_fat_percentage
     */
    public void setBodyFatPercentage(String bodyFatPercentage) {
        this.bodyFatPercentage = bodyFatPercentage;
    }

    /**
     * @return The leanBodyMass
     */
    public String getLeanBodyMass() {
        return leanBodyMass;
    }

    /**
     * @param leanBodyMass The lean_body_mass
     */
    public void setLeanBodyMass(String leanBodyMass) {
        this.leanBodyMass = leanBodyMass;
    }

    /**
     * @return The verified
     */
    public Boolean getVerified() {
        return verified;
    }

    /**
     * @param verified The verified
     */
    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public List<UserProfileResponseMedicalCondition> getMedicalConditions() {
        return medicalConditions;
    }

    public void setMedicalConditions(List<UserProfileResponseMedicalCondition> medicalConditions) {
        this.medicalConditions = medicalConditions;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }
}
