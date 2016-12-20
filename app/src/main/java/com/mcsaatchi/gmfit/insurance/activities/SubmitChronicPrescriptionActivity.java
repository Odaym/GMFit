package com.mcsaatchi.gmfit.insurance.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SubmitChronicPrescriptionActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_chronic_prescription);
        ButterKnife.bind(this);
        setupToolbar(toolbar, "Submit Chronic Prescription", true);
    }
}