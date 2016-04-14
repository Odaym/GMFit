package com.mcsaatchi.gmfit.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.CircleTransform;
import com.mcsaatchi.gmfit.countrypicker.CountryPicker;
import com.mcsaatchi.gmfit.countrypicker.CountryPickerListener;
import com.mcsaatchi.gmfit.logger.Log;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GetStarted_Activity extends Base_Activity {

    @Bind(R.id.getStartedIMG)
    ImageView getStartedIMG;
    @Bind(R.id.setup_profile_button)
    Button setupProfileBTN;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.get_started_activity);

        ButterKnife.bind(this);

        Picasso.with(this).load(R.drawable.fragment_intro_picture_1).transform(new CircleTransform()).into
                (getStartedIMG);

        setupProfileBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CountryPicker picker = CountryPicker.newInstance("Select Country");
                picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");

                picker.setListener(new CountryPickerListener() {

                    @Override
                    public void onSelectCountry(String name, String code) {
                        Log.toaster(GetStarted_Activity.this, "Country selected : " + name + "\nCode: " + code);
                    }
                });
            }
        });
    }
}
