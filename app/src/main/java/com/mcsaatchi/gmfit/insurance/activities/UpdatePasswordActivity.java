package com.mcsaatchi.gmfit.insurance.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.andreabaccega.widget.FormEditText;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.rest.InsuranceLoginResponseInnerData;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.insurance.fragments.InsuranceLoginFragment;

public class UpdatePasswordActivity extends BaseActivity {

  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.newPasswordET) FormEditText newPasswordET;
  @Bind(R.id.retypedPasswordET) FormEditText retypePasswordET;
  @Bind(R.id.emailAddressET) FormEditText emailAddressET;
  @Bind(R.id.phoneNumberET) FormEditText phoneNumberET;

  private InsuranceLoginResponseInnerData insuranceUserData;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_insurance_update_password);

    ButterKnife.bind(this);

    setupToolbar(toolbar, getString(R.string.insurance_update_password_activity_title), true);

    if (getIntent().getExtras() != null) {
      insuranceUserData = (InsuranceLoginResponseInnerData) getIntent().getExtras()
          .get(Constants.BUNDLE_INSURANCE_USER_OBJECT);
    }
  }

  @OnClick(R.id.updateInfoBTN) public void handleUpdateInfo() {
    Intent intent = new Intent();
    intent.putExtra(Constants.BUNDLE_INSURANCE_USER_OBJECT, insuranceUserData);
    setResult(InsuranceLoginFragment.INFO_UPDATED_SUCCESSFULLY_AFTER_LOGIN, intent);
    finish();
  }
}
