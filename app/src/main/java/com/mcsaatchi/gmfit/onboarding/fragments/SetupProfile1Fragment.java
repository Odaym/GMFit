package com.mcsaatchi.gmfit.onboarding.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.GMFitApplication;
import com.mcsaatchi.gmfit.common.Constants;
import com.mukesh.countrypicker.fragments.CountryPicker;
import com.mukesh.countrypicker.interfaces.CountryPickerListener;
import com.mukesh.countrypicker.models.Country;
import javax.inject.Inject;

public class SetupProfile1Fragment extends Fragment {
  @Bind(R.id.chooseCountryBTN) Button chooseCountryBTN;
  @Bind(R.id.countryFlagIV) ImageView countryFlagIV;
  @Bind(R.id.metricRdBTN) RadioButton metricRdBTN;

  @Inject SharedPreferences prefs;

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View fragmentView = inflater.inflate(R.layout.fragment_setup_profile_1, container, false);

    ButterKnife.bind(this, fragmentView);
    ((GMFitApplication) getActivity().getApplication()).getAppComponent().inject(this);

    final CountryPicker picker = CountryPicker.newInstance(getString(R.string.choose_country_hint));

    Country userCountry = picker.getUserCountryInfo(getActivity());

    countryFlagIV.setImageResource(userCountry.getFlag());
    chooseCountryBTN.setText(userCountry.getName());

    /**
     * The defaults are set here
     */
    prefs.edit().putString(Constants.EXTRAS_USER_PROFILE_MEASUREMENT_SYSTEM, "metric").apply();
    prefs.edit()
        .putString(Constants.EXTRAS_USER_PROFILE_NATIONALITY, chooseCountryBTN.getText().toString())
        .apply();

    metricRdBTN.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        if (checked) {
          prefs.edit()
              .putString(Constants.EXTRAS_USER_PROFILE_MEASUREMENT_SYSTEM, "metric")
              .apply();
        }
      }
    });

    chooseCountryBTN.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        picker.show(getActivity().getSupportFragmentManager(), "COUNTRY_PICKER");
        picker.setListener(new CountryPickerListener() {
          @Override public void onSelectCountry(String name, String code, String dialCode,
              int flagDrawableResID) {
            chooseCountryBTN.setText(name);
            countryFlagIV.setImageResource(flagDrawableResID);

            prefs.edit().putString(Constants.EXTRAS_USER_PROFILE_NATIONALITY, name).apply();

            picker.dismiss();
          }
        });
      }
    });

    return fragmentView;
  }
}
