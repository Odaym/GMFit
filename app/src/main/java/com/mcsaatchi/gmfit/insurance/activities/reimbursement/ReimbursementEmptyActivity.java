package com.mcsaatchi.gmfit.insurance.activities.reimbursement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;

public class ReimbursementEmptyActivity extends BaseActivity {

  @Bind(R.id.toolbar) Toolbar toolbar;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_reimbursement_empty);
    ButterKnife.bind(this);
    setupToolbar(toolbar, "Reimbursement Status", true);
  }

  @OnClick(R.id.submitReimbursement) public void submitReimbursement() {
    startActivity(new Intent(this, SubmitReimbursementActivity.class));
    finish();
  }
}