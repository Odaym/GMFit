package com.mcsaatchi.gmfit.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.mcsaatchi.gmfit.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Setup_Profile_1_Fragment extends Fragment {
    @Bind(R.id.countrySpinner)
    Spinner citizenship;
    @Bind(R.id.measurementsSpinner)
    Spinner measurements;
    private boolean touched = false;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_setup_profile_1, container, false);

        ButterKnife.bind(this, fragmentView);

        setupCountriesSpinner(citizenship);

        setupMeasurementsSpinner(measurements);

        return fragmentView;
    }

    private void setupCountriesSpinner(final Spinner countriesSpinner) {
        TelephonyManager teleMgr = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        String localeCountry = new Locale("", teleMgr.getNetworkCountryIso()).getDisplayCountry();

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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, countries);
        countriesSpinner.setAdapter(adapter);

        countriesSpinner.setSelection(countries.indexOf(localeCountry));
    }

    private void setupMeasurementsSpinner(Spinner measurementsSpinner) {
        ArrayList<String> measurements = new ArrayList<String>();
        measurements.add("Metric");
        measurements.add("Imperial");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, measurements);
        measurementsSpinner.setAdapter(adapter);
    }
}
