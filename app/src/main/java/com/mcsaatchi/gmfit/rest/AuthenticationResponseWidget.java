package com.mcsaatchi.gmfit.rest;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthenticationResponseWidget implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public AuthenticationResponseWidget createFromParcel(Parcel in) {
            return new AuthenticationResponseWidget(in);
        }

        public AuthenticationResponseWidget[] newArray(int size) {
            return new AuthenticationResponseWidget[size];
        }
    };
    @SerializedName("widget_id")
    @Expose
    private Integer widgetId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("slug")
    @Expose
    private String slug;
    @SerializedName("unit")
    @Expose
    private String unit;
    @SerializedName("position")
    @Expose
    private String position;
    @SerializedName("total")
    @Expose
    private String total;

    public AuthenticationResponseWidget(Parcel in) {
        widgetId = in.readInt();
        name = in.readString();
        slug = in.readString();
        unit = in.readString();
        position = in.readString();
        total = in.readString();
    }

    /**
     * @return The widgetId
     */
    public Integer getWidgetId() {
        return widgetId;
    }

    /**
     * @param widgetId The widget_id
     */
    public void setWidgetId(Integer widgetId) {
        this.widgetId = widgetId;
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
     * @return The slug
     */
    public String getSlug() {
        return slug;
    }

    /**
     * @param slug The slug
     */
    public void setSlug(String slug) {
        this.slug = slug;
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
     * @return The position
     */
    public String getPosition() {
        return position;
    }

    /**
     * @param position The position
     */
    public void setPosition(String position) {
        this.position = position;
    }

    /**
     * @return The total
     */
    public String getTotal() {
        return total;
    }

    /**
     * @param total The total
     */
    public void setTotal(String total) {
        this.total = total;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(widgetId);
        parcel.writeString(name);
        parcel.writeString(slug);
        parcel.writeString(unit);
        parcel.writeString(position);
        parcel.writeString(total);
    }
}
