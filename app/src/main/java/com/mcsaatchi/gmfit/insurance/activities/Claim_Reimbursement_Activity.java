package com.mcsaatchi.gmfit.insurance.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.activities.Base_Activity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Claim_Reimbursement_Activity extends Base_Activity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim__reimbursment_);
        ButterKnife.bind(this);
        setupToolbar(toolbar, "Reimbursement Status", true);
    }

    @OnClick(R.id.submitReimbursement)
    public void submitReimbursement() {
        Toast.makeText(this, "submit", Toast.LENGTH_SHORT).show();
    }
}
