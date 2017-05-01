package com.mcsaatchi.gmfit.common.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.AuthenticationResponseChartData;
import java.util.ArrayList;

@DatabaseTable(tableName = "DataChart") public class DataChart implements Parcelable {

  public static final Creator<DataChart> CREATOR = new Creator<DataChart>() {
    @Override public DataChart createFromParcel(Parcel source) {
      return new DataChart(source);
    }

    @Override public DataChart[] newArray(int size) {
      return new DataChart[size];
    }
  };

  @DatabaseField(generatedId = true) private int id;
  @DatabaseField private String name;
  @DatabaseField private String type;
  @DatabaseField private String measurementUnit;
  @DatabaseField private int position;
  @DatabaseField private String username;
  @DatabaseField private int chart_id;
  @DatabaseField(dataType = DataType.SERIALIZABLE)
  private ArrayList<AuthenticationResponseChartData> chartData;

  /**
   * 1 = FITNESS
   * 2 = NUTRITION
   * 3 = HEALTH
   */
  @DatabaseField private String whichFragment;

  public DataChart() {
  }

  public DataChart(String name, String type) {
    this.name = name;
    this.type = type;
  }

  protected DataChart(Parcel in) {
    this.id = in.readInt();
    this.name = in.readString();
    this.type = in.readString();
    this.measurementUnit = in.readString();
    this.position = in.readInt();
    this.username = in.readString();
    this.chart_id = in.readInt();
    this.chartData = in.createTypedArrayList(AuthenticationResponseChartData.CREATOR);
    this.whichFragment = in.readString();
  }

  public int getChart_id() {
    return chart_id;
  }

  public void setChart_id(int chart_id) {
    this.chart_id = chart_id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getWhichFragment() {
    return whichFragment;
  }

  public void setWhichFragment(String whichFragment) {
    this.whichFragment = whichFragment;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public ArrayList<AuthenticationResponseChartData> getChartData() {
    return chartData;
  }

  public void setChartData(ArrayList<AuthenticationResponseChartData> chartData) {
    this.chartData = chartData;
  }

  public String getMeasurementUnit() {
    return measurementUnit;
  }

  public void setMeasurementUnit(String measurementUnit) {
    this.measurementUnit = measurementUnit;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.id);
    dest.writeString(this.name);
    dest.writeString(this.type);
    dest.writeString(this.measurementUnit);
    dest.writeInt(this.position);
    dest.writeString(this.username);
    dest.writeInt(this.chart_id);
    dest.writeTypedList(this.chartData);
    dest.writeString(this.whichFragment);
  }

  @Override public String toString() {
    return "DataChart{"
        + "name='"
        + name
        + '\''
        + ", type='"
        + type
        + '\''
        + ", chart_id="
        + chart_id
        + '}';
  }
}
