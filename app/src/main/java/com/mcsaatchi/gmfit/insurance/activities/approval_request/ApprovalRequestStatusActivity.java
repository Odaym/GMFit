package com.mcsaatchi.gmfit.insurance.activities.approval_request;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.insurance.adapters.MedicalInformationAdapter;
import com.mcsaatchi.gmfit.insurance.models.MedicalInformationModel;
import com.mcsaatchi.gmfit.insurance.models.ReimbursementModel;
import com.mcsaatchi.gmfit.insurance.widget.ItemLabel;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ApprovalRequestStatusActivity extends BaseActivity {
  public static final String REIMBURSEMENT_MODEL_KEY = "reimbursement_model";
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.status) ItemLabel status;
  @Bind(R.id.category) ItemLabel category;
  @Bind(R.id.subCategory) ItemLabel subCategory;
  @Bind(R.id.serviceDate) ItemLabel serviceDate;
  @Bind(R.id.medicationRecyclerView) RecyclerView medicationRecyclerView;
  MedicalInformationAdapter adapter;
  private ReimbursementModel reimbursementModel;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_approval_request_status);
    ButterKnife.bind(this);

    Bundle incomingExtras = getIntent().getExtras();

    if (incomingExtras != null) {
      reimbursementModel = incomingExtras.getParcelable(REIMBURSEMENT_MODEL_KEY);

      if (reimbursementModel != null) {
        setupToolbar(toolbar, "Reimbursement #" + reimbursementModel.getId(), true);

        serviceDate.setLabel("Service Date", reimbursementModel.getServiceDate());
        subCategory.setLabel("Sub Category", reimbursementModel.getSubCategory());
        category.setLabel("Category", reimbursementModel.getCategory());
        status.setLabel("Status", reimbursementModel.getStatus());

        adapter = new MedicalInformationAdapter(reimbursementModel.getMedicines(),
            new MedicalInformationAdapter.OnClickListener() {
              @Override
              public void onClick(MedicalInformationModel medicalInformationModel, int index) {

              }
            });
        medicationRecyclerView.setNestedScrollingEnabled(false);
        medicationRecyclerView.setAdapter(adapter);
      }
    }
  }
}
