package com.mcsaatchi.gmfit.insurance.activities.approval_request;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;

public class ApprovalRequestsEmptyActivity extends BaseActivity {

  @Bind(R.id.toolbar) Toolbar toolbar;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_approval_requests_empty);
    ButterKnife.bind(this);
    setupToolbar(getClass().getSimpleName(), toolbar, getString(R.string.approval_request_status_activity_title), true);
  }

  @OnClick(R.id.submitRequestBTN) public void submitRequest() {
    startActivity(new Intent(this, SubmitApprovalRequestActivity.class));
    finish();
  }
}