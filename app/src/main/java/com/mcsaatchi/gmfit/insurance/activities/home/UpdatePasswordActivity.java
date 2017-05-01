package com.mcsaatchi.gmfit.insurance.activities.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.andreabaccega.widget.FormEditText;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.InsuranceLoginResponseInnerData;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.insurance.fragments.InsuranceLoginFragment;
import java.util.ArrayList;

public class UpdatePasswordActivity extends BaseActivity
    implements UpdatePasswordActivityPresenter.UpdatePasswordActivityView {

  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.newPasswordET) FormEditText newPasswordET;
  @Bind(R.id.retypedPasswordET) FormEditText retypePasswordET;
  @Bind(R.id.emailAddressET) FormEditText emailAddressET;
  @Bind(R.id.phoneNumberET) FormEditText phoneNumberET;

  private String old_password;
  private ArrayList<FormEditText> allFields = new ArrayList<>();
  private InsuranceLoginResponseInnerData insuranceUserData;
  private UpdatePasswordActivityPresenter presenter;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_insurance_update_password);

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar,
        getString(R.string.insurance_update_password_activity_title), true);

    presenter = new UpdatePasswordActivityPresenter(this, dataAccessHandler);

    allFields.add(newPasswordET);
    allFields.add(retypePasswordET);
    allFields.add(emailAddressET);
    allFields.add(phoneNumberET);

    if (getIntent().getExtras() != null) {
      insuranceUserData = (InsuranceLoginResponseInnerData) getIntent().getExtras()
          .get(Constants.BUNDLE_INSURANCE_USER_OBJECT);
      old_password = getIntent().getExtras().getString("OLD_PASSWORD");
    }
  }

  @OnClick(R.id.updateInfoBTN) public void handleUpdateInfo() {
    if (Helpers.validateFields(allFields)) {
      presenter.updateInsurancePassword(
          String.valueOf(insuranceUserData.getContracts().get(0).getNumber()), old_password,
          newPasswordET.getText().toString(), emailAddressET.getText().toString(),
          phoneNumberET.getText().toString());
    }
  }

  @Override public void sendResultBackAndCloseActivity(String newPassword) {
    Intent intent = new Intent();
    intent.putExtra(Constants.BUNDLE_INSURANCE_USER_OBJECT, insuranceUserData);
    intent.putExtra(Constants.EXTRAS_INSURANCE_USER_PASSWORD, newPassword);
    setResult(InsuranceLoginFragment.INFO_UPDATED_SUCCESSFULLY_AFTER_LOGIN, intent);
    finish();
  }
}
