package com.mcsaatchi.gmfit.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.countrypicker.CountryPicker;
import com.mcsaatchi.gmfit.countrypicker.CountryPickerListener;
import com.mcsaatchi.gmfit.logger.Log;

import java.util.ArrayList;
import java.util.Locale;

public class Setup_Profile_1_Fragment extends Fragment {
    private boolean touched = false;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_setup_profile_1, container, false);

        Spinner citizenship = (Spinner) fragmentView.findViewById(R.id.countrySpinner);

        Spinner measurements = (Spinner) fragmentView.findViewById(R.id.measurementsSpinner);

        setupCountriesSpinner(citizenship);

        setupMeasurementsSpinner(measurements);

        return fragmentView;
    }

    private void setupCountriesSpinner(Spinner countriesSpinner) {
        ArrayList<String> countries = new ArrayList<String>();
        TelephonyManager teleMgr = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        String localeCountry = teleMgr.getNetworkCountryIso();
        if (localeCountry != null) {
            Locale loc = new Locale("", localeCountry);
            countries.add(loc.getDisplayCountry());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, countries);
        countriesSpinner.setAdapter(adapter);

        countriesSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!touched) {
                    touched = true;

                    CountryPicker picker = CountryPicker.newInstance(getString(R.string.choose_country_hint));
                    picker.show(getActivity().getSupportFragmentManager(), "COUNTRY_PICKER");

                    picker.setListener(new CountryPickerListener() {
                        @Override
                        public void onSelectCountry(String name, String code) {
                            Log.toaster(getActivity(), "Country selected : " + name + "\nCode: " + code);
                        }

                        @Override
                        public void onDismissDialog() {
                            touched = false;
                        }
                    });
                }

                return true;
            }
        });
    }

    private void setupMeasurementsSpinner(Spinner measurementsSpinner) {
        ArrayList<String> measurements = new ArrayList<String>();
        measurements.add("Metric");
        measurements.add("Imperial");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, measurements);
        measurementsSpinner.setAdapter(adapter);
    }
}
