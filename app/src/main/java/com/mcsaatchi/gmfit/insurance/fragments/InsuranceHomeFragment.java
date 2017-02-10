package com.mcsaatchi.gmfit.insurance.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.rest.InsuranceLoginResponseInnerData;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.insurance.adapters.InsuranceOperationWidgetsGridAdapter;
import com.mcsaatchi.gmfit.insurance.models.InsuranceOperationWidget;
import java.util.ArrayList;
import javax.inject.Inject;

public class InsuranceHomeFragment extends Fragment {

  @Bind(R.id.insurancePathsGridView) RecyclerView insurancePathsGridView;
  @Bind(R.id.cardOwnerTV) TextView cardOwnerTV;
  @Bind(R.id.bankNameTV) TextView bankNameTV;
  @Bind(R.id.cardNumberTV) TextView cardNumberTV;

  @Inject DataAccessHandler dataAccessHandler;

  private InsuranceLoginResponseInnerData insuranceUserData;

  private String cardNumber;

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View fragmentView = inflater.inflate(R.layout.fragment_insurance_home, container, false);

    ButterKnife.bind(this, fragmentView);

    ((GMFitApplication) getActivity().getApplication()).getAppComponent().inject(this);

    if (getArguments() != null) {
      insuranceUserData = (InsuranceLoginResponseInnerData) getArguments().get(
          Constants.BUNDLE_INSURANCE_USER_OBJECT);
      cardNumber = getArguments().getString("CARD_NUMBER");

      cardNumberTV.setText("Card Number: " + cardNumber);

      cardOwnerTV.setText(insuranceUserData.getUsername());
      bankNameTV.setText(insuranceUserData.getContracts().get(0).getCompany());

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
      }});
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

  //@OnClick(R.id.cardDetailsLayout) public void handleCardDetailsClicked() {
  //  getCardDetails();
  //}

  //private void getCardDetails() {
  //  final ProgressDialog waitingDialog = new ProgressDialog(getActivity());
  //  waitingDialog.setTitle(getResources().getString(R.string.loading_data_dialog_title));
  //  waitingDialog.setMessage(getResources().getString(R.string.please_wait_dialog_message));
  //  waitingDialog.show();
  //
  //  final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
  //  alertDialog.setTitle(R.string.loading_data_dialog_title);
  //  alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.ok),
  //      new DialogInterface.OnClickListener() {
  //        public void onClick(DialogInterface dialog, int which) {
  //          dialog.dismiss();
  //
  //          if (waitingDialog.isShowing()) waitingDialog.dismiss();
  //        }
  //      });
  //
  //  dataAccessHandler.getCardDetails("2012250", "1892870", "422", "2", "walid123",
  //      new Callback<InsuranceLoginResponse>() {
  //        @Override public void onResponse(Call<InsuranceLoginResponse> call,
  //            Response<InsuranceLoginResponse> response) {
  //
  //          switch (response.code()) {
  //            case 200:
  //              waitingDialog.dismiss();
  //
  //              Intent intent = new Intent(getActivity(), CardDetailsActivity.class);
  //              intent.putExtra("PDF",
  //                  response.body().getData().getBody().getData().replace("\\", ""));
  //
  //              startActivity(intent);
  //          }
  //        }
  //
  //        @Override public void onFailure(Call<InsuranceLoginResponse> call, Throwable t) {
  //          Timber.d("Call failed with error : %s", t.getMessage());
  //          alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
  //          alertDialog.show();
  //        }
  //      });
  //}
}