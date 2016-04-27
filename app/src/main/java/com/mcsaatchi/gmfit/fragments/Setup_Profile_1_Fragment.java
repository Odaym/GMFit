package com.mcsaatchi.gmfit.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.countrypicker.CountryPicker;
import com.mcsaatchi.gmfit.countrypicker.CountryPickerListener;
import com.mcsaatchi.gmfit.logger.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class Setup_Profile_1_Fragment extends Fragment {
    private boolean touched = false;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_setup_profile_1, container, false);

        Locale[] locale = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();
        String country;
        for (Locale loc : locale) {
            country = loc.getDisplayCountry();
            if (country.length() > 0 && !countries.contains(country)) {
                countries.add(country);
            }
        }

        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);

        Spinner citizenship = (Spinner) fragmentView.findViewById(R.id.countrySpinner);
        citizenship.setPrompt("Select your country");
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, countries);
//        citizenship.setAdapter(adapter);

        citizenship.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!touched) {
                    touched = true;

                    CountryPicker picker = CountryPicker.newInstance("Select Country");
                    picker.show(getActivity().getSupportFragmentManager(), "COUNTRY_PICKER");

                    picker.setListener(new CountryPickerListener() {
                        @Override
                        public void onSelectCountry(String name, String code) {
                            Log.toaster(getActivity(), "Country selected : " + name + "\nCode: " + code);
                        }
                    });
                }

                return true;
            }
        });
        return fragmentView;
    }

}
