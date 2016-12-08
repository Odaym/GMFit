package com.mcsaatchi.gmfit.insurance.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.activities.Base_Activity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Submit_Reimbursement_Activity extends Base_Activity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_reimbursement);
        ButterKnife.bind(this);
        setupToolbar(toolbar, "Submit Reimbursement", true);
    }
}
