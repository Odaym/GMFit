package com.mcsaatchi.gmfit.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.classes.Helpers;

public class LearnMoreGoogleFit_Activity extends Base_Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(Helpers.createActivityBundleWithProperties(R.string.about_google_fit_acitivity_title, true));

        setContentView(R.layout.activity_about_google_fit);
    }
}
