package com.mcsaatchi.gmfit.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class UserMealsResponseInnerData {
  @SerializedName("breakfast") @Expose private List<UserMealsResponseBreakfast> breakfast =
      new ArrayList<>();
  @SerializedName("lunch") @Expose private List<UserMealsResponseLunch> lunch = new ArrayList<>();
  @SerializedName("dinner") @Expose private List<UserMealsResponseDinner> dinner =
      new ArrayList<>();
  @SerializedName("snack") @Expose private List<UserMealsResponseSnack> snack = new ArrayList<>();

  /**
   * @return The breakfast
   */
  public List<UserMealsResponseBreakfast> getBreakfast() {
    return breakfast;
  }

  /**
   * @param breakfast The breakfast
   */
  public void setBreakfast(List<UserMealsResponseBreakfast> breakfast) {
    this.breakfast = breakfast;
  }

  /**
   * @return The lunch
   */
  public List<UserMealsResponseLunch> getLunch() {
    return lunch;
  }

  /**
   * @param lunch The lunch
   */
  public void setLunch(List<UserMealsResponseLunch> lunch) {
    this.lunch = lunch;
  }

  /**
   * @return The dinner
   */
  public List<UserMealsResponseDinner> getDinner() {
    return dinner;
  }

  /**
   * @param dinner The dinner
   */
  public void setDinner(List<UserMealsResponseDinner> dinner) {
    this.dinner = dinner;
  }

  /**
   * @return The snack
   */
  public List<UserMealsResponseSnack> getSnack() {
    return snack;
  }

  /**
   * @param snack The snack
   */
  public void setSnack(List<UserMealsResponseSnack> snack) {
    this.snack = snack;
  }
}
