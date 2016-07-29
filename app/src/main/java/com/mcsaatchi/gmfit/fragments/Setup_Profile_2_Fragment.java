package com.mcsaatchi.gmfit.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.activities.SetupProfile_Activity;
import com.mcsaatchi.gmfit.classes.Cons;
import com.mcsaatchi.gmfit.classes.EventBus_Poster;
import com.mcsaatchi.gmfit.classes.Profile;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Setup_Profile_2_Fragment extends Fragment {

    @Bind(R.id.maintainWeightRdBTN)
    RadioButton maintainWeightRdBTN;
    @Bind(R.id.loseWeightRdBTN)
    RadioButton loseWeightRdBTN;
    @Bind(R.id.gainWeightRdBTN)
    RadioButton gainWeightRdBTN;
    private Profile profile;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_setup_profile_2, container, false);

        ButterKnife.bind(this, fragmentView);

        maintainWeightRdBTN.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            }
        });

        return fragmentView;
    }

    @Subscribe
    public void handle_BusEvents(EventBus_Poster ebp) {
        String ebpMessage = ebp.getMessage();

        switch (ebpMessage) {
            case Cons.EVENT_USER_SETUP_PROFILE_STEP_1:

                break;
        }
    }
}
