package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserProfileResponseActivityLevel {
  @SerializedName("id") @Expose private String id;
  @SerializedName("name") @Expose private String name;
  @SerializedName("description") @Expose private String description;
  @SerializedName("selected") @Expose private String selected;

  /**
   * @return The id
   */
  public String getId() {
    return id;
  }

  /**
   * @param id The id
   */
  public void setId(String id) {
    this.id = id;
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
   * @return The description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description The description
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * @return The selected
   */
  public String getSelected() {
    return selected;
  }

  /**
   * @param selected The selected
   */
  public void setSelected(String selected) {
    this.selected = selected;
  }
}
