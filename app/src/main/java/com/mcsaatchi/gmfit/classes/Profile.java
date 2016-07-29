package com.mcsaatchi.gmfit.classes;

public class Profile {
    private String birthday;
    private String bloodType;
    private String nationality;
    private String measurementSystem;
    private String goal;
    private int gender;
    private double height;
    private double weight;
    private double BMI;

    public Profile() {
    }

    public Profile(String birthday, String bloodType, String nationality, String measurementSystem, String goal, int gender, double height, double weight, double BMI) {
        this.birthday = birthday;
        this.bloodType = bloodType;
        this.nationality = nationality;
        this.measurementSystem = measurementSystem;
        this.goal = goal;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.BMI = BMI;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getMeasurementSystem() {
        return measurementSystem;
    }

    public void setMeasurementSystem(String measurementSystem) {
        this.measurementSystem = measurementSystem;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getBMI() {
        return BMI;
    }

    public void setBMI(double BMI) {
        this.BMI = BMI;
    }
}
