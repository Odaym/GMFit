package com.mcsaatchi.gmfit.architecture.countrypicker;

/**
 * Created by mukesh on 25/04/16.
 */
public interface CountryPickerListener {
  public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID);
}
