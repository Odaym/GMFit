package com.mcsaatchi.gmfit.rest;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MedicalTestsResponseBody implements Parcelable {
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("slug")
    @Expose
    private String slug;
    @SerializedName("metrics")
    @Expose
    private List<MedicalTestsResponseDatum> metrics = new ArrayList<>();

    private MedicalTestsResponseBody(Parcel in) {
        name = in.readString();
        type = in.readString();
        slug = in.readString();
    }

    public static final Creator<MedicalTestsResponseBody> CREATOR = new Creator<MedicalTestsResponseBody>() {
        @Override
        public MedicalTestsResponseBody createFromParcel(Parcel in) {
            return new MedicalTestsResponseBody(in);
        }

        @Override
        public MedicalTestsResponseBody[] newArray(int size) {
            return new MedicalTestsResponseBody[size];
        }
    };

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The type
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     * The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return
     * The slug
     */
    public String getSlug() {
        return slug;
    }

    /**
     *
     * @param slug
     * The slug
     */
    public void setSlug(String slug) {
        this.slug = slug;
    }

    /**
     *
     * @return
     * The metrics
     */
    public List<MedicalTestsResponseDatum> getMetrics() {
        return metrics;
    }

    /**
     *
     * @param metrics
     * The metrics
     */
    public void setMetrics(List<MedicalTestsResponseDatum> metrics) {
        this.metrics = metrics;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(type);
        parcel.writeString(slug);
    }
}
