package com.mcsaatchi.gmfit.insurance.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.andreabaccega.widget.FormEditText;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.classes.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CountriesListResponseDatum;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.InsuranceLoginResponseInnerData;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.common.fragments.BaseFragment;
import com.mcsaatchi.gmfit.insurance.activities.forgotpassword.ForgotInsurancePasswordActivity;
import com.mcsaatchi.gmfit.insurance.activities.home.UpdatePasswordActivity;
import com.mcsaatchi.gmfit.insurance.widget.CustomCountryPicker;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class InsuranceLoginFragment extends BaseFragment
    implements InsuranceLoginFragmentPresenter.InsuranceLoginFragmentView {
  public static final int INFO_UPDATED_SUCCESSFULLY_AFTER_LOGIN = 537;
  @Bind(R.id.memberIdET) FormEditText memberIdET;
  @Bind(R.id.passwordET) FormEditText passwordET;
  @Bind(R.id.countryPicker) CustomCountryPicker countryPicker;
  @Bind(R.id.forgotPasswordTV) TextView forgotPasswordTV;

  @Inject DataAccessHandlerImpl dataAccessHandler;
  @Inject SharedPreferences prefs;

  private InsuranceLoginFragmentPresenter presenter;
  private boolean chosenCountry = false;

  private ArrayList<FormEditText> allFields = new ArrayList<>();

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View fragmentView = inflater.inflate(R.layout.fragment_insurance_login, container, false);

    ((GMFitApplication) getActivity().getApplication()).getAppComponent().inject(this);

    ButterKnife.bind(this, fragmentView);

    presenter = new InsuranceLoginFragmentPresenter(this, dataAccessHandler);

    allFields.add(memberIdET);
    allFields.add(passwordET);

    presenter.getCountriesList();

    forgotPasswordTV.setText(Html.fromHtml(getString(R.string.forgot_password_button)));

    return fragmentView;
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

  @Override public void saveInsuranceCredentials(String memberId, String password) {
    prefs.edit().putString(Constants.EXTRAS_INSURANCE_USER_PASSWORD, password).apply();
    prefs.edit().putString(Constants.EXTRAS_INSURANCE_USER_USERNAME, memberId).apply();
  }

  @Override public void openUpdatePasswordActivity(InsuranceLoginResponseInnerData userObject) {
    Intent intent = new Intent(getActivity(), UpdatePasswordActivity.class);
    intent.putExtra("OLD_PASSWORD", passwordET.getText().toString());
    intent.putExtra(Constants.BUNDLE_INSURANCE_USER_OBJECT, userObject);
    startActivityForResult(intent, INFO_UPDATED_SUCCESSFULLY_AFTER_LOGIN);
  }

  @Override
  public void openHomeFragment(InsuranceLoginResponseInnerData userObject, String memberId) {
    Bundle bundle = new Bundle();
    bundle.putParcelable(Constants.BUNDLE_INSURANCE_USER_OBJECT, userObject);
    bundle.putString("CARD_NUMBER", memberId);
    InsuranceHomeFragment insuranceHomeFragment = new InsuranceHomeFragment();
    insuranceHomeFragment.setArguments(bundle);

    getFragmentManager().beginTransaction()
        .replace(R.id.root_frame, insuranceHomeFragment)
        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        .commitAllowingStateLoss();
  }

  @Override
  public void populateCountriesDropdown(List<CountriesListResponseDatum> countriesResponse) {
    ArrayList<String> countries = new ArrayList<>();

    for (int i = 0; i < countriesResponse.size(); i++) {
      if (countriesResponse.get(i) != null) {
        countries.add(countriesResponse.get(i).getLabel());
      }
    }

    if (!countries.isEmpty()) {
      if (isAdded()) {
        countryPicker.setUpDropDown(getString(R.string.country_dropdown_title), "",
            countries.toArray(new String[countries.size()]), (index, selected) -> {
              chosenCountry = true;

              saveChosenCountry(countriesResponse, selected);
            });

        countryPicker.setSelectedItem(getString(R.string.country_dropdown_message));
      }
    }
  }

  @OnClick(R.id.loginBTN) public void handleUserLogin() {
    if (Helpers.validateFields(allFields)) {
      if (chosenCountry) {
        presenter.login(memberIdET.getText().toString(),
            prefs.getString(Constants.EXTRAS_INSURANCE_COUNTRY_ISO_CODE, ""), "2",
            passwordET.getText().toString());
      } else {
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(R.string.country_choice_dialog_title);
        alertDialog.setMessage(getResources().getString(R.string.no_country_chosen_dialog_message));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
            (dialog, which) -> dialog.dismiss());
        alertDialog.show();
      }
    }
  }

  @OnClick(R.id.forgotPasswordTV) public void handleForgotPasswordPressed() {
    Intent intent = new Intent(getActivity(), ForgotInsurancePasswordActivity.class);
    startActivity(intent);
  }

  private void saveChosenCountry(List<CountriesListResponseDatum> countriesResponse,
      String selectedCountryName) {
    for (int i = 0; i < countriesResponse.size(); i++) {
      if (countriesResponse.get(i).getLabel().equals(selectedCountryName)) {
        prefs.edit()
            .putString(Constants.EXTRAS_INSURANCE_COUNTRY_NAME, countriesResponse.get(i).getLabel())
            .apply();
        prefs.edit()
            .putString(Constants.EXTRAS_INSURANCE_COUNTRY_CRM_CODE,
                countriesResponse.get(i).getCrmCode())
            .apply();
        prefs.edit()
            .putString(Constants.EXTRAS_INSURANCE_COUNTRY_ISO_CODE,
                countriesResponse.get(i).getIsoCode())
            .apply();
      }
    }
  }
}
