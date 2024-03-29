package com.mcsaatchi.gmfit.onboarding.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.facebook.FacebookSdk;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.classes.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.countrypicker.Country;
import com.mcsaatchi.gmfit.architecture.countrypicker.CountryPicker;
import com.mcsaatchi.gmfit.common.Constants;
import javax.inject.Inject;

import static com.facebook.FacebookSdk.getApplicationContext;

public class SetupProfile1Fragment extends Fragment {
  @Bind(R.id.chooseCountryBTN) Button chooseCountryBTN;
  @Bind(R.id.countryFlagIV) ImageView countryFlagIV;
  @Bind(R.id.metricRdBTN) RadioButton metricRdBTN;

  @Inject SharedPreferences prefs;

  private CountryPicker picker;

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View fragmentView = inflater.inflate(R.layout.fragment_setup_profile_1, container, false);

    ButterKnife.bind(this, fragmentView);

    ((GMFitApplication) getActivity().getApplication()).getAppComponent().inject(this);

    FacebookSdk.sdkInitialize(getActivity());

    picker = CountryPicker.newInstance(getString(R.string.choose_country_hint));

    Country userCountry = Country.getCountryFromSIM(getApplicationContext());

    if (userCountry != null) {
      countryFlagIV.setImageResource(userCountry.getFlag());
      chooseCountryBTN.setText(userCountry.getName());
    }

    prefs.edit().putString(Constants.EXTRAS_USER_PROFILE_MEASUREMENT_SYSTEM, "metric").apply();
    prefs.edit()
        .putString(Constants.EXTRAS_USER_PROFILE_NATIONALITY, chooseCountryBTN.getText().toString())
        .apply();

    metricRdBTN.setOnCheckedChangeListener((compoundButton, checked) -> {
      if (checked) {
        prefs.edit().putString(Constants.EXTRAS_USER_PROFILE_MEASUREMENT_SYSTEM, "metric").apply();
      }
    });

    return fragmentView;
  }

  @OnClick(R.id.chooseCountryBTN) public void handleChooseCountry() {
    picker.show(getActivity().getSupportFragmentManager(), "COUNTRY_PICKER");
    picker.setListener((name, code, dialCode, flagDrawableResID) -> {
      chooseCountryBTN.setText(name);
      countryFlagIV.setImageResource(flagDrawableResID);

      prefs.edit().putString(Constants.EXTRAS_USER_PROFILE_NATIONALITY, name).apply();

      picker.dismiss();
    });
  }
}
