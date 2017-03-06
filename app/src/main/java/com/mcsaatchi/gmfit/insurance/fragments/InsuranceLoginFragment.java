package com.mcsaatchi.gmfit.insurance.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.andreabaccega.widget.FormEditText;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.rest.CountriesListResponse;
import com.mcsaatchi.gmfit.architecture.rest.InsuranceLoginResponse;
import com.mcsaatchi.gmfit.architecture.rest.InsuranceLoginResponseInnerData;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.insurance.activities.home.UpdatePasswordActivity;
import java.util.ArrayList;
import javax.inject.Inject;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class InsuranceLoginFragment extends Fragment {
  public static final int INFO_UPDATED_SUCCESSFULLY_AFTER_LOGIN = 537;
  @Bind(R.id.memberImageIV) ImageView memberImageIV;
  @Bind(R.id.memberIdET) FormEditText memberIdET;
  @Bind(R.id.passwordET) FormEditText passwordET;

  @Inject DataAccessHandler dataAccessHandler;
  @Inject SharedPreferences prefs;

  private ArrayList<FormEditText> allFields = new ArrayList<>();

  public static RequestBody toRequestBody(String value) {
    return RequestBody.create(MediaType.parse("text/plain"), value);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View fragmentView = inflater.inflate(R.layout.fragment_insurance_login, container, false);

    ((GMFitApplication) getActivity().getApplication()).getAppComponent().inject(this);

    ButterKnife.bind(this, fragmentView);

    memberIdET.setText("91071403");
    passwordET.setText("1982");

    allFields.add(memberIdET);
    allFields.add(passwordET);

    getCountriesList();

    return fragmentView;
  }

  @OnClick(R.id.loginBTN) public void handleUserLogin() {
    if (Helpers.validateFields(allFields)) {
      insuranceUserLogin();
    }
  }

  private void insuranceUserLogin() {
    final ProgressDialog waitingDialog = new ProgressDialog(getActivity());
    waitingDialog.setTitle(getString(R.string.signing_in_dialog_title));
    waitingDialog.setMessage(getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
    alertDialog.setTitle(R.string.signing_in_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) -> {
      dialog.dismiss();

      if (waitingDialog.isShowing()) waitingDialog.dismiss();
    });

    dataAccessHandler.insuranceUserLogin(memberIdET.getText().toString(), "422", "2",
        passwordET.getText().toString(), new Callback<InsuranceLoginResponse>() {
          @Override public void onResponse(Call<InsuranceLoginResponse> call,
              Response<InsuranceLoginResponse> response) {
            switch (response.code()) {
              case 200:
                prefs.edit()
                    .putString(Constants.EXTRAS_INSURANCE_USER_PASSWORD,
                        passwordET.getText().toString())
                    .apply();
                prefs.edit()
                    .putString(Constants.EXTRAS_INSURANCE_USER_USERNAME,
                        memberIdET.getText().toString())
                    .apply();

                /**
                 * First time logging in, prompt them to change password
                 */
                if (response.body().getData().getBody().getData().getIsFirstLogin()) {
                  Intent intent = new Intent(getActivity(), UpdatePasswordActivity.class);
                  intent.putExtra("OLD_PASSWORD", passwordET.getText().toString());
                  intent.putExtra(Constants.BUNDLE_INSURANCE_USER_OBJECT,
                      response.body().getData().getBody().getData());
                  startActivityForResult(intent, INFO_UPDATED_SUCCESSFULLY_AFTER_LOGIN);
                } else {
                  /**
                   * Not the first time logging in, assume password should have been changed.
                   * Send them to where they were originally suppposed to go (InsuranceHomeFragment)
                   */
                  Bundle bundle = new Bundle();
                  bundle.putParcelable(Constants.BUNDLE_INSURANCE_USER_OBJECT,
                      response.body().getData().getBody().getData());
                  bundle.putString("CARD_NUMBER", memberIdET.getText().toString());
                  InsuranceHomeFragment insuranceHomeFragment = new InsuranceHomeFragment();
                  insuranceHomeFragment.setArguments(bundle);

                  getFragmentManager().beginTransaction()
                      .replace(R.id.root_frame, insuranceHomeFragment)
                      .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                      .commitAllowingStateLoss();
                }

                break;
              case 449:
                alertDialog.setMessage(getString(R.string.login_failed_wrong_credentials));
                alertDialog.show();
                break;
            }

            waitingDialog.dismiss();
          }

          @Override public void onFailure(Call<InsuranceLoginResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setMessage(getString(R.string.server_error_got_returned));
            alertDialog.show();
          }
        });
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    super.onActivityResult(requestCode, resultCode, intent);

    switch (resultCode) {
      case INFO_UPDATED_SUCCESSFULLY_AFTER_LOGIN:
        if (intent.getExtras() != null) {
          InsuranceLoginResponseInnerData insuranceUserData =
              (InsuranceLoginResponseInnerData) intent.getExtras()
                  .get(Constants.BUNDLE_INSURANCE_USER_OBJECT);
          Bundle bundle = new Bundle();
          bundle.putParcelable(Constants.BUNDLE_INSURANCE_USER_OBJECT, insuranceUserData);
          bundle.putString("CARD_NUMBER", memberIdET.getText().toString());
          InsuranceHomeFragment insuranceHomeFragment = new InsuranceHomeFragment();
          insuranceHomeFragment.setArguments(bundle);

          getFragmentManager().beginTransaction()
              .replace(R.id.root_frame, insuranceHomeFragment)
              .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
              .commitAllowingStateLoss();
        }

        break;
    }
  }

  private void getCountriesList() {
    final ProgressDialog waitingDialog = new ProgressDialog(getActivity());
    waitingDialog.setTitle(getResources().getString(R.string.loading_data_dialog_title));
    waitingDialog.setMessage(getResources().getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    dataAccessHandler.getCountriesList(
        toRequestBody(prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, "")),
        new Callback<CountriesListResponse>() {
          @Override public void onResponse(Call<CountriesListResponse> call,
              Response<CountriesListResponse> response) {
            switch (response.code()) {
              case 200:
                waitingDialog.dismiss();



                //Timber.d(response.body().getData().getBody().getData().get);
            }
          }

          @Override public void onFailure(Call<CountriesListResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
          }
        });
  }
}
