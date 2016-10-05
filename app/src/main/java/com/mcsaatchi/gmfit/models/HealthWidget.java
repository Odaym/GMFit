package com.mcsaatchi.gmfit.models;

import android.os.Parcel;
import android.os.Parcelable;

public class HealthWidget implements Parcelable {

    public static final Creator<HealthWidget> CREATOR = new Creator<HealthWidget>() {
        @Override
        public HealthWidget createFromParcel(Parcel source) {
            return new HealthWidget(source);
        }

        @Override
        public HealthWidget[] newArray(int size) {
            return new HealthWidget[size];
        }
    };
    private int id;
    private double value;
    private String title;
    private String measurementUnit;
    private double percentage;
    private int position;
    private int widget_id;

    public HealthWidget() {
    }

    protected HealthWidget(Parcel in) {
        this.id = in.readInt();
        this.value = in.readDouble();
        this.title = in.readString();
        this.measurementUnit = in.readString();
        this.percentage = in.readDouble();
        this.position = in.readInt();
        this.widget_id = in.readInt();
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

    public int getWidget_id() {
        return widget_id;
    }

    public void setWidget_id(int widget_id) {
        this.widget_id = widget_id;
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
        dest.writeInt(this.widget_id);
    }
}
