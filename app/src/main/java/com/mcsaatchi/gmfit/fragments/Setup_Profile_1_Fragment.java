package com.mcsaatchi.gmfit.fragments;

import android.content.Context;
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

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mukesh.countrypicker.fragments.CountryPicker;
import com.mukesh.countrypicker.interfaces.CountryPickerListener;
import com.mukesh.countrypicker.models.Country;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Setup_Profile_1_Fragment extends Fragment {
    @Bind(R.id.chooseCountryBTN)
    Button chooseCountryBTN;
    @Bind(R.id.countryFlagIV)
    ImageView countryFlagIV;
    @Bind(R.id.metricRdBTN)
    RadioButton metricRdBTN;
    @Bind(R.id.imperialRdBTN)
    RadioButton imperialRdBTN;

    private SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_setup_profile_1, container, false);

        ButterKnife.bind(this, fragmentView);

        prefs = getActivity().getSharedPreferences(Cons.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

        final CountryPicker picker = CountryPicker.newInstance(getString(R.string.choose_country_hint));

        Country userCountry = picker.getUserCountryInfo(getActivity());

        countryFlagIV.setImageResource(userCountry.getFlag());
        chooseCountryBTN.setText(userCountry.getName());

        /**
         * The defaults are set here
         */
        prefs.edit().putString(Cons.EXTRAS_USER_PROFILE_MEASUREMENT_SYSTEM, "Metric").apply();
        prefs.edit().putString(Cons.EXTRAS_USER_PROFILE_NATIONALITY, chooseCountryBTN.getText().toString()).apply();

        metricRdBTN.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked)
                    prefs.edit().putString(Cons.EXTRAS_USER_PROFILE_MEASUREMENT_SYSTEM, "Metric").apply();
            }
        });

        imperialRdBTN.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked)
                    prefs.edit().putString(Cons.EXTRAS_USER_PROFILE_MEASUREMENT_SYSTEM, "Imperial").apply();
            }
        });

        chooseCountryBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picker.show(getActivity().getSupportFragmentManager(), "COUNTRY_PICKER");
                picker.setListener(new CountryPickerListener() {
                    @Override
                    public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                        chooseCountryBTN.setText(name);
                        countryFlagIV.setImageResource(flagDrawableResID);

                        prefs.edit().putString(Cons.EXTRAS_USER_PROFILE_NATIONALITY, name).apply();

                        picker.dismiss();
                    }
                });
            }
        });

        return fragmentView;
    }
}
