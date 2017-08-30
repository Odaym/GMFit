package com.mcsaatchi.gmfit.architecture.retrofit.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class UserMealsResponseInnerData {
  @SerializedName("breakfast") @Expose private List<UserMealsResponseInner> breakfast =
      new ArrayList<>();
  @SerializedName("lunch") @Expose private List<UserMealsResponseInner> lunch = new ArrayList<>();
  @SerializedName("dinner") @Expose private List<UserMealsResponseInner> dinner = new ArrayList<>();
  @SerializedName("snack") @Expose private List<UserMealsResponseInner> snack = new ArrayList<>();

  /**
   * @return The breakfast
   */
  public List<UserMealsResponseInner> getBreakfast() {
    return breakfast;
  }

  /**
   * @param breakfast The breakfast
   */
  public void setBreakfast(List<UserMealsResponseInner> breakfast) {
    this.breakfast = breakfast;
  }

  /**
   * @return The lunch
   */
  public List<UserMealsResponseInner> getLunch() {
    return lunch;
  }

  /**
   * @param lunch The lunch
   */
  public void setLunch(List<UserMealsResponseInner> lunch) {
    this.lunch = lunch;
  }

  /**
   * @return The dinner
   */
  public List<UserMealsResponseInner> getDinner() {
    return dinner;
  }

  /**
   * @param dinner The dinner
   */
  public void setDinner(List<UserMealsResponseInner> dinner) {
    this.dinner = dinner;
  }

  /**
   * @return The snack
   */
  public List<UserMealsResponseInner> getSnack() {
    return snack;
  }

  /**
   * @param snack The snack
   */
  public void setSnack(List<UserMealsResponseInner> snack) {
    this.snack = snack;
  }
}
