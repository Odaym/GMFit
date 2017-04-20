package com.mcsaatchi.gmfit.insurance.activities.forgotpassword;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.andreabaccega.widget.FormEditText;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import java.util.ArrayList;

public class UpdateInsuranceDetailsActivity extends BaseActivity
    implements UpdateInsuranceDetailsActivityPresenter.UpdatePasswordActivityView {

  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.newPasswordET) FormEditText newPasswordET;
  @Bind(R.id.retypedPasswordET) FormEditText retypePasswordET;
  @Bind(R.id.emailAddressET) FormEditText emailAddressET;
  @Bind(R.id.phoneNumberET) FormEditText phoneNumberET;

  private ArrayList<FormEditText> allFields = new ArrayList<>();
  private UpdateInsuranceDetailsActivityPresenter presenter;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_insurance_update_details);

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar,
        getString(R.string.insurance_update_details_activity_title), true);

    presenter = new UpdateInsuranceDetailsActivityPresenter(this, dataAccessHandler);

    allFields.add(emailAddressET);
    allFields.add(phoneNumberET);
  }

  @OnClick(R.id.updateInfoBTN) public void handleUpdateInfo() {
    if (Helpers.validateFields(allFields)) {
      if (!newPasswordET.getText().toString().isEmpty() && !retypePasswordET.getText()
          .toString()
          .isEmpty()) {
        if (newPasswordET.getText().toString().equals(retypePasswordET.getText().toString())) {
          presenter.updateInsuranceDetails(
              prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""),
              newPasswordET.getText().toString(), retypePasswordET.getText().toString(),
              emailAddressET.getText().toString(), phoneNumberET.getText().toString());
        } else {
          AlertDialog alertDialog = new AlertDialog.Builder(this).create();
          alertDialog.setTitle(R.string.update_password_conflict_dialog_title);
          alertDialog.setMessage(getString(R.string.update_password_conflict_dialog_message));
          alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
              (dialog, which) -> dialog.dismiss());
          alertDialog.show();
        }
      } else {
        presenter.updateInsuranceDetails(
            prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""),
            prefs.getString(Constants.EXTRAS_INSURANCE_USER_PASSWORD, ""),
            prefs.getString(Constants.EXTRAS_INSURANCE_USER_PASSWORD, ""),
            emailAddressET.getText().toString(), phoneNumberET.getText().toString());
      }
    }
  }
}
