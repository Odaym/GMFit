package com.mcsaatchi.gmfit.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "NutritionWidget")
public class NutritionWidget implements Parcelable {
    public static final Creator<NutritionWidget> CREATOR = new Creator<NutritionWidget>() {
        @Override
        public NutritionWidget createFromParcel(Parcel source) {
            return new NutritionWidget(source);
        }

        @Override
        public NutritionWidget[] newArray(int size) {
            return new NutritionWidget[size];
        }
    };
    @DatabaseField(generatedId = true, index = true)
    int id;
    @DatabaseField
    double value;
    @DatabaseField
    String title;
    @DatabaseField
    String measurementUnit;
    @DatabaseField
    double percentage;
    @DatabaseField
    int position;

    public NutritionWidget() {
    }

    public NutritionWidget(double value, String title, String measurementUnit, double percentage, int position) {
        this.value = value;
        this.title = title;
        this.measurementUnit = measurementUnit;
        this.percentage = percentage;
        this.position = position;
    }

    protected NutritionWidget(Parcel in) {
        this.id = in.readInt();
        this.value = in.readDouble();
        this.title = in.readString();
        this.measurementUnit = in.readString();
        this.percentage = in.readDouble();
        this.position = in.readInt();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMeasurementUnit() {
        return measurementUnit;
    }

    public void setMeasurementUnit(String measurementUnit) {
        this.measurementUnit = measurementUnit;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeDouble(this.value);
        dest.writeString(this.title);
        dest.writeString(this.measurementUnit);
        dest.writeDouble(this.percentage);
        dest.writeInt(this.position);
    }
}
