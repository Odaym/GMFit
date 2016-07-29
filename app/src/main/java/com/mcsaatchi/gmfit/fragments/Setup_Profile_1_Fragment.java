package com.mcsaatchi.gmfit.fragments;

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
import com.mcsaatchi.gmfit.activities.SetupProfile_Activity;
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

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_setup_profile_1, container, false);

        ButterKnife.bind(this, fragmentView);

        final CountryPicker picker = CountryPicker.newInstance(getString(R.string.choose_country_hint));

        Country userCountry = picker.getUserCountryInfo(getActivity());

        countryFlagIV.setImageResource(userCountry.getFlag());
        chooseCountryBTN.setText(userCountry.getName());

        chooseCountryBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picker.show(getActivity().getSupportFragmentManager(), "COUNTRY_PICKER");
                picker.setListener(new CountryPickerListener() {
                    @Override
                    public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                        chooseCountryBTN.setText(name);
                        countryFlagIV.setImageResource(flagDrawableResID);

                        ((SetupProfile_Activity) getActivity()).updateProfileFromFragment_1(name, );

                        picker.dismiss();
                    }
                });
            }
        });

        return fragmentView;
    }
}
