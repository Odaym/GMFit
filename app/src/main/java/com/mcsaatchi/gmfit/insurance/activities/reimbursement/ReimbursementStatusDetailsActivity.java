package com.mcsaatchi.gmfit.insurance.activities.reimbursement;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.rest.ClaimListDetailsResponse;
import com.mcsaatchi.gmfit.architecture.rest.ClaimListDetailsResponseDatum;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.insurance.widget.CustomAttachmentPicker;
import com.mcsaatchi.gmfit.insurance.widget.ItemLabel;
import org.joda.time.LocalDate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ReimbursementStatusDetailsActivity extends BaseActivity {

  public static final String REIMBURSEMENT_REQUEST_ID = "reimbursement_request_id";

  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.status) ItemLabel status;
  @Bind(R.id.category) ItemLabel category;
  @Bind(R.id.subCategory) ItemLabel subCategory;
  @Bind(R.id.serviceDate) ItemLabel serviceDate;
  @Bind(R.id.amount) ItemLabel amount;
  @Bind(R.id.medicalReportImagesPicker) CustomAttachmentPicker medicalReportImagesPicker;
  @Bind(R.id.invoiceImagesPicker) CustomAttachmentPicker invoiceImagesPicker;
  @Bind(R.id.identityCardImagesPicker) CustomAttachmentPicker identityCardImagesPicker;
  @Bind(R.id.passportImagesPicker) CustomAttachmentPicker passportImagesPicker;
  @Bind(R.id.testResultsImagesPicker) CustomAttachmentPicker testResultsImagesPicker;
  @Bind(R.id.otherDocumentsImagesPicker) CustomAttachmentPicker otherDocumentsImagesPicker;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_reimbursement_status);

    ButterKnife.bind(this);

    Bundle incomingExtras = getIntent().getExtras();

    if (incomingExtras != null) {
      int requestId = incomingExtras.getInt(REIMBURSEMENT_REQUEST_ID);

      getReimbursementClaimDetails(requestId);
    }
  }

  private void getReimbursementClaimDetails(int claimId) {
    final ProgressDialog waitingDialog = new ProgressDialog(this);
    waitingDialog.setTitle(getResources().getString(R.string.loading_data_dialog_title));
    waitingDialog.setMessage(getResources().getString(R.string.please_wait_dialog_message));
    waitingDialog.setCancelable(false);
    waitingDialog.show();

    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle(R.string.loading_data_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.ok),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();

            if (waitingDialog.isShowing()) waitingDialog.dismiss();
          }
        });

    dataAccessHandler.getClaimslistDetails(
        prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""), "1",
        String.valueOf(claimId), new Callback<ClaimListDetailsResponse>() {
          @Override public void onResponse(Call<ClaimListDetailsResponse> call,
              Response<ClaimListDetailsResponse> response) {
            switch (response.code()) {
              case 200:
                waitingDialog.dismiss();

                ClaimListDetailsResponseDatum responseDatum =
                    response.body().getData().getBody().getData().get(0);

                setupToolbar(getClass().getSimpleName(), toolbar,
                    "Reimbursement #" + responseDatum.getId(), true);

                amount.setLabel("Amount", String.valueOf(responseDatum.getAmount()));
                serviceDate.setLabel("Service Date", Helpers.formatInsuranceDate(
                    new LocalDate(responseDatum.getDate().split(" ")[0])));
                subCategory.setLabel("Sub Category", responseDatum.getSubcategory());
                category.setLabel("Category", responseDatum.getCategory());
                status.setTextColor(Helpers.determineStatusColor(responseDatum.getStatus()));
                status.setLabel("Status", responseDatum.getStatus());

                for (int i = 0; i < responseDatum.getImages().size(); i++) {
                  switch (responseDatum.getImages().get(i).getDocumType()) {
                    case 1:
                      medicalReportImagesPicker.returnImagePicker(i)
                          .setImageBitmap(
                              turnBase64ToImage(responseDatum.getImages().get(i).getContent()));
                      break;
                    case 2:
                      invoiceImagesPicker.returnImagePicker(i)
                          .setImageBitmap(
                              turnBase64ToImage(responseDatum.getImages().get(i).getContent()));
                      break;
                    case 3:
                      identityCardImagesPicker.returnImagePicker(i)
                          .setImageBitmap(
                              turnBase64ToImage(responseDatum.getImages().get(i).getContent()));
                      break;
                    case 4:
                      passportImagesPicker.returnImagePicker(i)
                          .setImageBitmap(
                              turnBase64ToImage(responseDatum.getImages().get(i).getContent()));
                      break;
                    case 5:
                      testResultsImagesPicker.returnImagePicker(i)
                          .setImageBitmap(
                              turnBase64ToImage(responseDatum.getImages().get(i).getContent()));
                      break;
                    case 6:
                      otherDocumentsImagesPicker.returnImagePicker(i)
                          .setImageBitmap(
                              turnBase64ToImage(responseDatum.getImages().get(i).getContent()));
                      break;
                  }
                }
            }
          }

          @Override public void onFailure(Call<ClaimListDetailsResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            alertDialog.setMessage(t.getMessage());
            alertDialog.show();
          }
        });
  }

  private Bitmap turnBase64ToImage(String base64String) {
    byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
    return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
  }
}