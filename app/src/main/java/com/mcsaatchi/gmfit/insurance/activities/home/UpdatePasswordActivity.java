package com.mcsaatchi.gmfit.insurance.activities.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.andreabaccega.widget.FormEditText;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.rest.InsuranceLoginResponseInnerData;
import com.mcsaatchi.gmfit.architecture.rest.UpdateInsurancePasswordResponse;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.insurance.fragments.InsuranceLoginFragment;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class UpdatePasswordActivity extends BaseActivity {

  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.newPasswordET) FormEditText newPasswordET;
  @Bind(R.id.retypedPasswordET) FormEditText retypePasswordET;
  @Bind(R.id.emailAddressET) FormEditText emailAddressET;
  @Bind(R.id.phoneNumberET) FormEditText phoneNumberET;

  private String old_password;

  private ArrayList<FormEditText> allFields = new ArrayList<>();

  private InsuranceLoginResponseInnerData insuranceUserData;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_insurance_update_password);

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar,
        getString(R.string.insurance_update_password_activity_title), true);

    allFields.add(newPasswordET);
    allFields.add(retypePasswordET);

    if (getIntent().getExtras() != null) {
      insuranceUserData = (InsuranceLoginResponseInnerData) getIntent().getExtras()
          .get(Constants.BUNDLE_INSURANCE_USER_OBJECT);
      old_password = getIntent().getExtras().getString("OLD_PASSWORD");
    }
  }

  @OnClick(R.id.updateInfoBTN) public void handleUpdateInfo() {
    if (Helpers.validateFields(allFields)) {
      updateInsurancePassword(String.valueOf(insuranceUserData.getContracts().get(0).getNumber()),
          old_password, newPasswordET.getText().toString(), insuranceUserData.getEmail(),
          insuranceUserData.getMobile());
    }
  }

  private void updateInsurancePassword(String contractNumber, String oldPassword,
      final String newPassword, String email, String mobileNumber) {
    final ProgressDialog waitingDialog = new ProgressDialog(this);
    waitingDialog.setTitle(getResources().getString(R.string.loading_data_dialog_title));
    waitingDialog.setMessage(getResources().getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle(R.string.updating_password_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.ok),
        (dialog, which) -> {
          dialog.dismiss();

          if (waitingDialog.isShowing()) waitingDialog.dismiss();
        });

    dataAccessHandler.updateInsurancePassword(contractNumber, oldPassword, newPassword, email,
        mobileNumber, new Callback<UpdateInsurancePasswordResponse>() {
          @Override public void onResponse(Call<UpdateInsurancePasswordResponse> call,
              Response<UpdateInsurancePasswordResponse> response) {

            switch (response.code()) {
              case 200:
                waitingDialog.dismiss();

                Intent intent = new Intent();
                intent.putExtra(Constants.BUNDLE_INSURANCE_USER_OBJECT, insuranceUserData);
                intent.putExtra(Constants.EXTRAS_INSURANCE_USER_PASSWORD, newPassword);
                setResult(InsuranceLoginFragment.INFO_UPDATED_SUCCESSFULLY_AFTER_LOGIN, intent);
                finish();
            }
          }

          @Override public void onFailure(Call<UpdateInsurancePasswordResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            alertDialog.setMessage(getString(R.string.server_error_got_returned));
            alertDialog.show();
          }
        });
  }
}
