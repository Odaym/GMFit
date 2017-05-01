package com.mcsaatchi.gmfit.insurance.activities.inquiry;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.InquiriesListResponseInnerData;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;

public class InquiryDetailsActivity extends BaseActivity {
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.statusValueTV) TextView statusValueTV;
  @Bind(R.id.submittedOnTV) TextView submittedOnTV;
  @Bind(R.id.closedOnTV) TextView closedOnTV;
  @Bind(R.id.fullNameTV) TextView fullNameTV;
  @Bind(R.id.cardNumberTV) TextView cardNumberTV;
  @Bind(R.id.riskCarrierTV) TextView riskCarrierTV;
  @Bind(R.id.categoryTV) TextView categoryTV;
  @Bind(R.id.subcategoryTV) TextView subcategoryTV;
  @Bind(R.id.areaTV) TextView areaTV;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_inquiry_details);

    ButterKnife.bind(this);

    if (getIntent().getExtras() != null) {
      InquiriesListResponseInnerData inquiryItem =
          getIntent().getExtras().getParcelable("INQUIRY_OBJECT");

      if (inquiryItem != null) {
        setupToolbar(getClass().getSimpleName(), toolbar, inquiryItem.getTitle(), true);

        if (inquiryItem.getStatus() != null) {
          statusValueTV.setTextColor(
              getResources().getColor(Helpers.determineStatusColor(inquiryItem.getStatus())));
          statusValueTV.setText(inquiryItem.getStatus());
        }

        if (inquiryItem.getCreatedOn() != null) {
          submittedOnTV.setText(inquiryItem.getCreatedOn());
        }

        closedOnTV.setText("--");

        fullNameTV.setText(prefs.getString(Constants.EXTRAS_USER_FULL_NAME, ""));
        cardNumberTV.setText(prefs.getString(Constants.EXTRAS_INSURANCE_USER_USERNAME, ""));
        riskCarrierTV.setText(prefs.getString(Constants.EXTRAS_INSURANCE_COMPANY_NAME, ""));

        if (inquiryItem.getCategoryName() != null) {
          categoryTV.setText(inquiryItem.getCategoryName());
        }

        if (inquiryItem.getSubCategoryName() != null) {
          subcategoryTV.setText(inquiryItem.getSubCategoryName());
        }

        if (inquiryItem.getArea() != null) {
          areaTV.setText(inquiryItem.getArea());
        }
      }
    }
  }
}
