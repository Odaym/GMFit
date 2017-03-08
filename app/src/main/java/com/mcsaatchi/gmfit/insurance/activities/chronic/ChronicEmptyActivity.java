package com.mcsaatchi.gmfit.insurance.activities.chronic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;

public class ChronicEmptyActivity extends BaseActivity {
  @Bind(R.id.toolbar) Toolbar toolbar;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chronic_prescription_empty);
    ButterKnife.bind(this);
    setupToolbar(getClass().getSimpleName(), toolbar, "Chronic Prescription Status", true);
  }

  @OnClick(R.id.submitChronicTreatmentBTN) public void submitChronicTreatment() {
    startActivity(new Intent(this, SubmitChronicActivity.class));
  }
}