package com.mcsaatchi.gmfit.insurance.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.gson.Gson;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.insurance.adapters.MedicalInformationAdapter;
import com.mcsaatchi.gmfit.insurance.models.MedicalInformationModel;
import com.mcsaatchi.gmfit.insurance.models.ReimbursementModel;
import com.mcsaatchi.gmfit.insurance.widget.ItemLabel;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ReimbursementStatusActivity extends BaseActivity {

    public static final String REIMBURSEMENT_MODEL_KEY = "reimbursement_model";
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private Gson gson = new Gson();
    private ReimbursementModel reimbursementModel;

    @Bind(R.id.status)
    ItemLabel status;
    @Bind(R.id.category)
    ItemLabel category;
    @Bind(R.id.subCategory)
    ItemLabel subCategory;
    @Bind(R.id.serviceDate)
    ItemLabel serviceDate;
    @Bind(R.id.amount)
    ItemLabel amount;

    @Bind(R.id.medicationRecyclerView)
    RecyclerView medicationRecyclerView;
    MedicalInformationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reimbursement_status);
        ButterKnife.bind(this);
        Intent incomingIntent = getIntent();
        if (incomingIntent.hasExtra(REIMBURSEMENT_MODEL_KEY)) {
            String json = incomingIntent.getStringExtra(REIMBURSEMENT_MODEL_KEY);
            reimbursementModel = gson.fromJson(json, ReimbursementModel.class);
            setupToolbar(toolbar, "Reimbursement #" + reimbursementModel.getId(), true);
            amount.setLabel("Amount", reimbursementModel.getAmount());
            serviceDate.setLabel("Service Date", reimbursementModel.getServiceDate());
            subCategory.setLabel("Sub Category", reimbursementModel.getSubCategory());
            category.setLabel("Category", reimbursementModel.getCategory());
            status.setLabel("Status", reimbursementModel.getStatus());

            adapter = new MedicalInformationAdapter(reimbursementModel.getMedicines(), new MedicalInformationAdapter.OnClickListener() {
                @Override
                public void onClick(MedicalInformationModel medicalInformationModel, int index) {

                }
            });
            medicationRecyclerView.setNestedScrollingEnabled(false);
            medicationRecyclerView.setAdapter(adapter);
        }
    }
}