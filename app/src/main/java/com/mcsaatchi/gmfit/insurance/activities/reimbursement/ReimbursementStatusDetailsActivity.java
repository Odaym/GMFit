package com.mcsaatchi.gmfit.insurance.activities.reimbursement;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.rest.ClaimsListResponseDatum;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.insurance.widget.CustomAttachmentPicker;
import com.mcsaatchi.gmfit.insurance.widget.ItemLabel;

public class ReimbursementStatusDetailsActivity extends BaseActivity {

  public static final String REIMBURSEMENT_MODEL_KEY = "reimbursement_model";
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.status) ItemLabel status;
  @Bind(R.id.category) ItemLabel category;
  @Bind(R.id.subCategory) ItemLabel subCategory;
  @Bind(R.id.serviceDate) ItemLabel serviceDate;
  @Bind(R.id.amount) ItemLabel amount;

  @Bind(R.id.medicalReportImagesPicker) CustomAttachmentPicker medicalReportImagesPicker;
  @Bind(R.id.invoiceImagesPicker) CustomAttachmentPicker invoiceImagesPicker;
  @Bind(R.id.identityCardImagesPicker) CustomAttachmentPicker identityCardImagesPicker;
  @Bind(R.id.passportImagesPicker) CustomAttachmentPicker passportImagesPicker;
  @Bind(R.id.testResultsImagesPicker) CustomAttachmentPicker testResultsImagesPicker;
  @Bind(R.id.otherDocumentsImagesPicker) CustomAttachmentPicker otherDocumentsImagesPicker;

  private ClaimsListResponseDatum reimbursementModel;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_reimbursement_status);

    ButterKnife.bind(this);

    Bundle incomingExtras = getIntent().getExtras();

    if (incomingExtras != null) {
      reimbursementModel = incomingExtras.getParcelable(REIMBURSEMENT_MODEL_KEY);

      if (reimbursementModel != null) {
        setupToolbar(getClass().getSimpleName(), toolbar,
            "Reimbursement #" + reimbursementModel.getId(), true);

        amount.setLabel("Amount", String.valueOf(reimbursementModel.getAmount()));
        serviceDate.setLabel("Service Date", reimbursementModel.getDate());
        subCategory.setLabel("Sub Category", reimbursementModel.getSubcategory());
        category.setLabel("Category", reimbursementModel.getCategory());
        status.setLabel("Status", reimbursementModel.getStatus());
      }
    }
  }
}