package com.mcsaatchi.gmfit.insurance.activities.approval_request;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ApprovalRequestsStatusEmptyActivity extends BaseActivity {

  @Bind(R.id.toolbar) Toolbar toolbar;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_approval_requests_empty);
    ButterKnife.bind(this);
    setupToolbar(toolbar, "Approval Requests Status", true);
  }

  @OnClick(R.id.submitRequestBTN) public void submitRequest() {
    startActivity(new Intent(this, SubmitApprovalRequestsActivity.class));
    finish();
  }
}