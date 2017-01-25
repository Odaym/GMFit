package com.mcsaatchi.gmfit.insurance.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.mcsaatchi.gmfit.architecture.picasso.CircleTransform;
import com.mcsaatchi.gmfit.architecture.rest.InsuranceLoginResponse;
import com.mcsaatchi.gmfit.architecture.rest.InsuranceLoginResponseInnerData;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.insurance.activities.UpdatePasswordActivity;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import javax.inject.Inject;
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

  private ArrayList<FormEditText> allFields = new ArrayList<>();

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View fragmentView = inflater.inflate(R.layout.fragment_insurance_login, container, false);

    ((GMFitApplication) getActivity().getApplication()).getAppComponent().inject(this);

    ButterKnife.bind(this, fragmentView);

    Picasso.with(getActivity())
        .load(R.drawable.welcome_slider_banner)
        .transform(new CircleTransform())
        .into(memberImageIV);

    memberIdET.setText("2012250");
    passwordET.setText("walid123");

    allFields.add(memberIdET);
    allFields.add(passwordET);

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
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();

            if (waitingDialog.isShowing()) waitingDialog.dismiss();
          }
        });

    dataAccessHandler.insuranceUserLogin(memberIdET.getText().toString(), "1892870", "422", "2",
        passwordET.getText().toString(), new Callback<InsuranceLoginResponse>() {
          @Override public void onResponse(Call<InsuranceLoginResponse> call,
              Response<InsuranceLoginResponse> response) {
            switch (response.code()) {
              case 200:
                waitingDialog.dismiss();

                Intent intent = new Intent(getActivity(), UpdatePasswordActivity.class);
                intent.putExtra(Constants.BUNDLE_INSURANCE_USER_OBJECT,
                    response.body().getData().getBody().getData());
                startActivityForResult(intent, INFO_UPDATED_SUCCESSFULLY_AFTER_LOGIN);

                break;
            }
          }

          @Override public void onFailure(Call<InsuranceLoginResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
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
          InsuranceHomeFragment insuranceHomeFragment = new InsuranceHomeFragment();
          insuranceHomeFragment.setArguments(bundle);

          getFragmentManager().beginTransaction()
              .replace(R.id.root_frame, insuranceHomeFragment)
              .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
              .addToBackStack(null)
              .commitAllowingStateLoss();
        }

        break;
    }
  }
}
