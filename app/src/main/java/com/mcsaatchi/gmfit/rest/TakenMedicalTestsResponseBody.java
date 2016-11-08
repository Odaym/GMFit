package com.mcsaatchi.gmfit.rest;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TakenMedicalTestsResponseBody implements Parcelable {
  public static final Creator<TakenMedicalTestsResponseBody> CREATOR =
      new Creator<TakenMedicalTestsResponseBody>() {
        @Override public TakenMedicalTestsResponseBody createFromParcel(Parcel source) {
          return new TakenMedicalTestsResponseBody(source);
        }

        @Override public TakenMedicalTestsResponseBody[] newArray(int size) {
          return new TakenMedicalTestsResponseBody[size];
        }
      };
  @SerializedName("instance_id") @Expose private Integer instanceId;
  @SerializedName("date_taken") @Expose private String dateTaken;
  @SerializedName("name") @Expose private String name;
  @SerializedName("type") @Expose private String type;
  @SerializedName("images") @Expose private List<TakenMedicalTestsResponseImagesDatum> images =
      new ArrayList<>();
  @SerializedName("metrics") @Expose private List<TakenMedicalTestsResponseMetricsDatum> metrics =
      new ArrayList<>();

  protected TakenMedicalTestsResponseBody(Parcel in) {
    this.instanceId = (Integer) in.readValue(Integer.class.getClassLoader());
    this.dateTaken = in.readString();
    this.name = in.readString();
    this.type = in.readString();
    this.images = new ArrayList<TakenMedicalTestsResponseImagesDatum>();
    in.readList(this.images, Object.class.getClassLoader());
    this.metrics = new ArrayList<TakenMedicalTestsResponseMetricsDatum>();
    in.readList(this.metrics, TakenMedicalTestsResponseMetricsDatum.class.getClassLoader());
  }

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
  public List<TakenMedicalTestsResponseImagesDatum> getImages() {
    return images;
  }

  /**
   * @param images The images
   */
  public void setImages(List<TakenMedicalTestsResponseImagesDatum> images) {
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

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeValue(this.instanceId);
    dest.writeString(this.dateTaken);
    dest.writeString(this.name);
    dest.writeString(this.type);
    dest.writeList(this.images);
    dest.writeList(this.metrics);
  }
}
