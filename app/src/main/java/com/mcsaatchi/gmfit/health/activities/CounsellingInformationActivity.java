package com.mcsaatchi.gmfit.health.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;

public class CounsellingInformationActivity extends BaseActivity
    implements CounsellingInformationActivityPresenter.CounsellingInformationActivityView {
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.counsellingInformationTV) TextView counsellingInformationTV;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_counselling_information);

    ButterKnife.bind(this);

    if (getIntent().getExtras() != null) {
      String medCode = getIntent().getExtras().getString("MEDICATION_CODE");

      String compatibilityCheckResult =
          getIntent().getExtras().getString("COMPATIBILITY_CHECK_RESULT");

      if (medCode != null) {
        setupToolbar(getClass().getSimpleName(), toolbar,
            getResources().getString(R.string.counselling_information_activity_title), true);

        CounsellingInformationActivityPresenter presenter =
            new CounsellingInformationActivityPresenter(this, dataAccessHandler);

        presenter.getCounsellingInformation(medCode);
      } else if (compatibilityCheckResult != null) {
        setupToolbar(getClass().getSimpleName(), toolbar,
            getResources().getString(R.string.check_compatibility_activity_title), true);
        counsellingInformationTV.setText(compatibilityCheckResult);
      }
    }
  }

  @Override public void displayCounsellingInformation(String compatibilityDescription) {
    counsellingInformationTV.setText(compatibilityDescription);
  }
}
