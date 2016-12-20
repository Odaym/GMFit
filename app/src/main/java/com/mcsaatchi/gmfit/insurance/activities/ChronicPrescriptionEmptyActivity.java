package com.mcsaatchi.gmfit.insurance.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChronicPrescriptionEmptyActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chronic_prescription_empty);
        ButterKnife.bind(this);
        setupToolbar(toolbar, "Chronic Prescription Status", true);
    }

    @OnClick(R.id.submitChronicTreatmentBTN)
    public void submitChronicTreatment() {

    }
}