package com.mcsaatchi.gmfit.rest;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TakenMedicalTestsResponseImagesDatum implements Parcelable{
    public static final Creator<TakenMedicalTestsResponseImagesDatum> CREATOR = new Creator<TakenMedicalTestsResponseImagesDatum>() {
        @Override
        public TakenMedicalTestsResponseImagesDatum createFromParcel(Parcel source) {
            return new TakenMedicalTestsResponseImagesDatum(source);
        }

        @Override
        public TakenMedicalTestsResponseImagesDatum[] newArray(int size) {
            return new TakenMedicalTestsResponseImagesDatum[size];
        }
    };
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("image")
    @Expose
    private String image;

    public TakenMedicalTestsResponseImagesDatum() {
    }

    protected TakenMedicalTestsResponseImagesDatum(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.image = in.readString();
    }

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The image
     */
    public String getImage() {
        return image;
    }

    /**
     *
     * @param image
     * The image
     */
    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.image);
    }
}
