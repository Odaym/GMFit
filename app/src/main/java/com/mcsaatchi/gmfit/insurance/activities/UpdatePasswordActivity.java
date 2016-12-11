package com.mcsaatchi.gmfit.insurance.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.andreabaccega.widget.FormEditText;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.activities.Base_Activity;
import com.mcsaatchi.gmfit.insurance.fragments.InsuranceLoginFragment;

public class UpdatePasswordActivity extends Base_Activity {

  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.newPasswordET) FormEditText newPasswordET;
  @Bind(R.id.retypedPasswordET) FormEditText retypePasswordET;
  @Bind(R.id.emailAddressET) FormEditText emailAddressET;
  @Bind(R.id.phoneNumberET) FormEditText phoneNumberET;
  @Bind(R.id.updateInfoBTN) Button updateInfoBTN;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_insurance_update_password);

    ButterKnife.bind(this);

    setupToolbar(toolbar, getString(R.string.insurance_update_password_activity_title), true);

    updateInfoBTN.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        setResult(InsuranceLoginFragment.INFO_UPDATED_SUCCESSFULLY_AFTER_LOGIN);
        finish();
      }
    });
  }
}
