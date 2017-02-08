package com.mcsaatchi.gmfit.insurance.activities.inquiry;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;

public class InquiryEmptyActivity extends BaseActivity {
  @Bind(R.id.toolbar) Toolbar toolbar;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_inquiry_empty);
    ButterKnife.bind(this);
    setupToolbar(getClass().getSimpleName(), toolbar, "Complaint/Inquiry Status", true);
  }

  @OnClick(R.id.submitInquiryBTN) public void submitInquiry() {
    startActivity(new Intent(this, SubmitInquiryActivity.class));
    finish();
  }
}
