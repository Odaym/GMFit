package com.mcsaatchi.gmfit.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Setup_Profile_2_Fragment extends Fragment {

    @Bind(R.id.maintainWeightRdBTN)
    RadioButton maintainWeightRdBTN;
    @Bind(R.id.loseWeightRdBTN)
    RadioButton loseWeightRdBTN;
    @Bind(R.id.gainWeightRdBTN)
    RadioButton gainWeightRdBTN;

    private SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_setup_profile_2, container, false);

        ButterKnife.bind(this, fragmentView);

        prefs = getActivity().getSharedPreferences(Constants.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

        prefs.edit().putString(Constants.EXTRAS_USER_PROFILE_GOAL, "Lose weight").apply();

        loseWeightRdBTN.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked)
                    prefs.edit().putString(Constants.EXTRAS_USER_PROFILE_GOAL, "Lose weight").apply();
            }
        });

        maintainWeightRdBTN.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked)
                    prefs.edit().putString(Constants.EXTRAS_USER_PROFILE_GOAL, "Maintain weight").apply();
            }
        });


        gainWeightRdBTN.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked)
                    prefs.edit().putString(Constants.EXTRAS_USER_PROFILE_GOAL, "Gain weight").apply();
            }
        });

        return fragmentView;
    }
}
