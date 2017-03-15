package com.mcsaatchi.gmfit.insurance.fragments;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.PermissionsChecker;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.rest.CertainPDFResponse;
import com.mcsaatchi.gmfit.architecture.rest.InsuranceLoginResponseInnerData;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.insurance.activities.home.ContractsChoiceView;
import com.mcsaatchi.gmfit.insurance.activities.home.PDFViewerActivity;
import com.mcsaatchi.gmfit.insurance.adapters.InsuranceOperationWidgetsGridAdapter;
import com.mcsaatchi.gmfit.insurance.models.InsuranceContract;
import com.mcsaatchi.gmfit.insurance.models.InsuranceOperationWidget;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class InsuranceHomeFragment extends Fragment {

  private static final int REQUEST_CAMERA_AND_STORAGE_PERMISSIONS = 123;
  @Bind(R.id.insurancePathsGridView) RecyclerView insurancePathsGridView;
  @Bind(R.id.parentLayout) RelativeLayout parentLayout;
  @Bind(R.id.cardOwnerTV) TextView cardOwnerTV;
  @Bind(R.id.bankNameTV) TextView bankNameTV;
  @Bind(R.id.cardNumberTV) TextView cardNumberTV;
  @Inject DataAccessHandler dataAccessHandler;
  @Inject PermissionsChecker permChecker;
  @Inject SharedPreferences prefs;

  private ViewGroup parentFragmentView;
  private ImageView contractSelectorBTN;
  private String cardNumber;

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View fragmentView = inflater.inflate(R.layout.fragment_insurance_home, container, false);

    ButterKnife.bind(this, fragmentView);

    ((GMFitApplication) getActivity().getApplication()).getAppComponent().inject(this);

    parentFragmentView = ((ViewGroup) getParentFragment().getView());

    List<InsuranceContract> insuranceContracts = new ArrayList<>();

    if (getArguments() != null) {
      InsuranceLoginResponseInnerData insuranceUserData =
          (InsuranceLoginResponseInnerData) getArguments().get(
              Constants.BUNDLE_INSURANCE_USER_OBJECT);
      cardNumber = getArguments().getString("CARD_NUMBER");

      cardNumberTV.setText("Card Number: " + cardNumber);

      cardOwnerTV.setText(insuranceUserData.getUsername());
      bankNameTV.setText(insuranceUserData.getContracts().get(0).getCompany());

      for (int i = 0; i < insuranceUserData.getContracts().size(); i++) {
        InsuranceContract contract = new InsuranceContract();
        contract.setFullName(insuranceUserData.getUsername());
        contract.setNumber(String.valueOf(insuranceUserData.getContracts().get(i).getNumber()));
        contract.setInsuranceCompany(insuranceUserData.getContracts().get(i).getCompany());
        contract.setHoldername(insuranceUserData.getContracts().get(i).getHoldername());
        contract.setExpiryDate(insuranceUserData.getContracts().get(i).getContract_expiry());

        insuranceContracts.add(contract);
      }

      setupContractSelectorButton(insuranceContracts);

      setupInsurancePathsGrid(new ArrayList<InsuranceOperationWidget>() {{
        add(new InsuranceOperationWidget(R.drawable.ic_insurance_operations_submit,
            getString(R.string.widget_submit)));
        add(new InsuranceOperationWidget(R.drawable.ic_insurance_operation_track,
            getString(R.string.widget_track)));
        add(new InsuranceOperationWidget(R.drawable.ic_insurance_operation_member_guide,
            getString(R.string.widget_members_guide)));
        add(new InsuranceOperationWidget(R.drawable.ic_insurance_operation_coverage_desc,
            getString(R.string.widget_coverage_description)));
        add(new InsuranceOperationWidget(R.drawable.ic_insurance_operation_snapshot,
            getString(R.string.widget_snapshot)));
        add(new InsuranceOperationWidget(R.drawable.ic_insurance_operation_policy,
            getString(R.string.widget_insurance_policy)));
      }});
    }

    if (permChecker.lacksPermissions(Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
      requestPermissions(
          new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE },
          REQUEST_CAMERA_AND_STORAGE_PERMISSIONS);
    }

    return fragmentView;
  }

  private void setupInsurancePathsGrid(
      ArrayList<InsuranceOperationWidget> insuranceOperationWidgets) {

    InsuranceOperationWidgetsGridAdapter widgetsAdapter =
        new InsuranceOperationWidgetsGridAdapter(getActivity(), getActivity().getApplication(),
            insuranceOperationWidgets);

    GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
    gridLayoutManager.setSpanCount(4);
    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
      @Override public int getSpanSize(int position) {
        return 2;
      }
    });

    insurancePathsGridView.setHasFixedSize(true);
    insurancePathsGridView.setLayoutManager(gridLayoutManager);
    insurancePathsGridView.setAdapter(widgetsAdapter);
  }

  @OnClick(R.id.cardDetailsLayout) public void handleCardDetailsClicked() {
    getCardDetails();
  }

  private void getCardDetails() {
    final ProgressDialog waitingDialog = new ProgressDialog(getActivity());
    waitingDialog.setTitle(getResources().getString(R.string.loading_data_dialog_title));
    waitingDialog.setMessage(getResources().getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
    alertDialog.setTitle(R.string.loading_data_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.ok),
        (dialog, which) -> {
          dialog.dismiss();

          if (waitingDialog.isShowing()) waitingDialog.dismiss();
        });

    dataAccessHandler.getCardDetails(
        prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""),
        new Callback<CertainPDFResponse>() {
          @Override public void onResponse(Call<CertainPDFResponse> call,
              Response<CertainPDFResponse> response) {

            switch (response.code()) {
              case 200:
                Intent intent = new Intent(getActivity(), PDFViewerActivity.class);
                intent.putExtra("TITLE", "Card Details");
                intent.putExtra("PDF",
                    response.body().getData().getBody().getData().replace("\\", ""));

                startActivity(intent);
                break;
              case 449:
                alertDialog.setMessage(Helpers.provideErrorStringFromJSON(response.errorBody()));
                alertDialog.show();
                break;
            }

            waitingDialog.dismiss();
          }

          @Override public void onFailure(Call<CertainPDFResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            alertDialog.setMessage(getString(R.string.server_error_got_returned));
            alertDialog.show();
          }
        });
  }

  private void setupContractSelectorButton(final List<InsuranceContract> insuranceContracts) {
    if (parentFragmentView != null) {
      final ContractsChoiceView contractsChoiceView =
          new ContractsChoiceView((GMFitApplication) getActivity().getApplication(), getActivity(),
              insuranceContracts);

      Button logoutContractButton =
          (Button) contractsChoiceView.findViewById(R.id.logoutContractBTN);

      final Dialog contractChooserDialog = new Dialog(getActivity());
      contractChooserDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
      contractChooserDialog.setContentView(contractsChoiceView);
      contractChooserDialog.setCancelable(false);

      contractsChoiceView.setParentDialog(contractChooserDialog);

      WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
      lp.copyFrom(contractChooserDialog.getWindow().getAttributes());
      lp.width = WindowManager.LayoutParams.MATCH_PARENT;
      lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

      contractChooserDialog.getWindow().setAttributes(lp);

      contractSelectorBTN = (ImageView) parentFragmentView.findViewById(R.id.contractChooserBTN);

      contractSelectorBTN.setVisibility(View.VISIBLE);

      contractSelectorBTN.setOnClickListener(view -> {
        contractSelectorBTN.setImageResource(R.drawable.ic_contract_chooser_active);
        contractChooserDialog.show();
      });

      if (prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, "").isEmpty()) {
        contractSelectorBTN.setImageResource(R.drawable.ic_contract_chooser_active);
        contractChooserDialog.show();
      }

      logoutContractButton.setOnClickListener(view -> {
        prefs.edit().putString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, "").apply();
        prefs.edit().putString(Constants.EXTRAS_INSURANCE_USER_USERNAME, "").apply();
        prefs.edit().putString(Constants.EXTRAS_INSURANCE_USER_PASSWORD, "").apply();

        getFragmentManager().beginTransaction()
            .hide(InsuranceHomeFragment.this)
            .replace(R.id.root_frame, new InsuranceLoginFragment())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commitAllowingStateLoss();

        contractChooserDialog.dismiss();

        contractSelectorBTN.setVisibility(View.GONE);
      });
    }
  }
}
