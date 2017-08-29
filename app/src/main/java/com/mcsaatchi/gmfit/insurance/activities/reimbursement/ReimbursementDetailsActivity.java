package com.mcsaatchi.gmfit.insurance.activities.reimbursement;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ClaimListDetailsResponseDatum;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.insurance.widget.CustomAttachmentPicker;
import com.mcsaatchi.gmfit.insurance.widget.ItemLabel;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import org.joda.time.LocalDate;

public class ReimbursementDetailsActivity extends BaseActivity
    implements ReimbursementDetailsActivityPresenter.ReimbursementDetailsActivityView {

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
  @Bind(R.id.testResultsImagesPicker) CustomAttachmentPicker testResultsImagesPicker;
  @Bind(R.id.originalReceiptImagesPicker) CustomAttachmentPicker originalReceiptImagesPicker;
  @Bind(R.id.otherDocumentsImagesPicker) CustomAttachmentPicker otherDocumentsImagesPicker;

  private ArrayList<Integer> medicalReportImagesPlacement, invoiceImagesPlacement,
      identityCardImagesPlacement, testResultsImagesPlacement, originalReceiptImagesPlacement,
      otherDocumentsImagesPlacement = new ArrayList<>();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_reimbursement_status);

    ButterKnife.bind(this);

    ReimbursementDetailsActivityPresenter presenter =
        new ReimbursementDetailsActivityPresenter(this, dataAccessHandler);

    Bundle incomingExtras = getIntent().getExtras();

    if (incomingExtras != null) {
      int requestId = incomingExtras.getInt(REIMBURSEMENT_REQUEST_ID);

      if (incomingExtras.getIntegerArrayList("medicalReportImagesPlacement") != null) {
        medicalReportImagesPlacement =
            incomingExtras.getIntegerArrayList("medicalReportImagesPlacement");
      }

      if (incomingExtras.getIntegerArrayList("invoiceImagesPlacement") != null) {
        invoiceImagesPlacement = incomingExtras.getIntegerArrayList("invoiceImagesPlacement");
      }

      if (incomingExtras.getIntegerArrayList("identityCardImagesPlacement") != null) {
        identityCardImagesPlacement =
            incomingExtras.getIntegerArrayList("identityCardImagesPlacement");
      }

      if (incomingExtras.getIntegerArrayList("testResultsImagesPlacement") != null) {
        testResultsImagesPlacement =
            incomingExtras.getIntegerArrayList("testResultsImagesPlacement");
      }

      if (incomingExtras.getIntegerArrayList("originalReceiptImagesPlacement") != null) {
        originalReceiptImagesPlacement =
            incomingExtras.getIntegerArrayList("originalReceiptImagesPlacement");
      }

      if (incomingExtras.getIntegerArrayList("otherDocumentsImagesPlacement") != null) {
        otherDocumentsImagesPlacement =
            incomingExtras.getIntegerArrayList("otherDocumentsImagesPlacement");
      }

      presenter.getReimbursementClaimDetails(
          prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""), requestId);
    }
  }

  @Override public void populateClaimDetails(ClaimListDetailsResponseDatum claimDetails) {
    setupToolbar(getClass().getSimpleName(), toolbar, "Reimbursement #" + claimDetails.getId(),
        true);

    amount.setLabel("Amount", String.valueOf(claimDetails.getAmount()));
    serviceDate.setLabel("Service Date",
        Helpers.formatInsuranceDate(new LocalDate(claimDetails.getDate().split(" ")[0])));
    subCategory.setLabel("Sub Category", claimDetails.getSubcategory());
    category.setLabel("Category", claimDetails.getCategory());
    status.setTextColor(Helpers.determineStatusColor(claimDetails.getStatus()));
    status.setLabel("Status", claimDetails.getStatus());

    for (int i = 0; i < claimDetails.getImages().size(); i++) {
      try {
        switch (claimDetails.getImages().get(i).getDocumType()) {
          case 1:
            if (medicalReportImagesPlacement != null) {
              for (int k = 0; k < medicalReportImagesPlacement.size(); k++) {
                Picasso.with(this)
                    .load(Constants.BASE_IMAGES_URL + claimDetails.getImages()
                        .get(i)
                        .getContent()
                        .replace("/jpg", ".jpg"))
                    .resize(100, 100)
                    .into(medicalReportImagesPicker.returnImagePicker(
                        medicalReportImagesPlacement.get(k)));
              }
            } else {
              Picasso.with(this)
                  .load(Constants.BASE_IMAGES_URL + claimDetails.getImages()
                      .get(i)
                      .getContent()
                      .replace("/jpg", ".jpg"))
                  .resize(100, 100)
                  .into(medicalReportImagesPicker.returnImagePicker(i));
            }
            break;
          case 2:
            if (invoiceImagesPlacement != null) {
              for (int k = 0; k < invoiceImagesPlacement.size(); k++) {
                Picasso.with(this)
                    .load(Constants.BASE_IMAGES_URL + claimDetails.getImages()
                        .get(i)
                        .getContent()
                        .replace("/jpg", ".jpg"))
                    .resize(100, 100)
                    .into(invoiceImagesPicker.returnImagePicker(invoiceImagesPlacement.get(k)));
              }
            } else {
              Picasso.with(this)
                  .load(Constants.BASE_IMAGES_URL + claimDetails.getImages()
                      .get(i)
                      .getContent()
                      .replace("/jpg", ".jpg"))
                  .resize(100, 100)
                  .into(invoiceImagesPicker.returnImagePicker(i));
            }
            break;
          case 3:
            if (identityCardImagesPlacement != null) {
              for (int k = 0; k < identityCardImagesPlacement.size(); k++) {
                Picasso.with(this)
                    .load(Constants.BASE_IMAGES_URL + claimDetails.getImages()
                        .get(i)
                        .getContent()
                        .replace("/jpg", ".jpg"))
                    .resize(100, 100)
                    .into(originalReceiptImagesPicker.returnImagePicker(
                        identityCardImagesPlacement.get(k)));
              }
            } else {
              Picasso.with(this)
                  .load(Constants.BASE_IMAGES_URL + claimDetails.getImages()
                      .get(i)
                      .getContent()
                      .replace("/jpg", ".jpg"))
                  .resize(100, 100)
                  .into(originalReceiptImagesPicker.returnImagePicker(i));
            }
            break;
          case 4:
            if (testResultsImagesPlacement != null) {
              for (int k = 0; k < testResultsImagesPlacement.size(); k++) {
                Picasso.with(this)
                    .load(Constants.BASE_IMAGES_URL + claimDetails.getImages()
                        .get(i)
                        .getContent()
                        .replace("/jpg", ".jpg"))
                    .resize(100, 100)
                    .into(identityCardImagesPicker.returnImagePicker(
                        testResultsImagesPlacement.get(k)));
              }
            } else {
              Picasso.with(this)
                  .load(Constants.BASE_IMAGES_URL + claimDetails.getImages()
                      .get(i)
                      .getContent()
                      .replace("/jpg", ".jpg"))
                  .resize(100, 100)
                  .into(identityCardImagesPicker.returnImagePicker(i));
            }
            break;
          case 5:
            if (originalReceiptImagesPlacement != null) {
              for (int k = 0; k < originalReceiptImagesPlacement.size(); k++) {
                Picasso.with(this)
                    .load(Constants.BASE_IMAGES_URL + claimDetails.getImages()
                        .get(i)
                        .getContent()
                        .replace("/jpg", ".jpg"))
                    .resize(100, 100)
                    .into(testResultsImagesPicker.returnImagePicker(
                        originalReceiptImagesPlacement.get(k)));
              }
            } else {
              Picasso.with(this)
                  .load(Constants.BASE_IMAGES_URL + claimDetails.getImages()
                      .get(i)
                      .getContent()
                      .replace("/jpg", ".jpg"))
                  .resize(100, 100)
                  .into(testResultsImagesPicker.returnImagePicker(i));
            }
            break;
          case 6:
            if (otherDocumentsImagesPlacement != null) {
              for (int k = 0; k < otherDocumentsImagesPlacement.size(); k++) {
                Picasso.with(this)
                    .load(Constants.BASE_IMAGES_URL + claimDetails.getImages()
                        .get(i)
                        .getContent()
                        .replace("/jpg", ".jpg"))
                    .resize(100, 100)
                    .into(otherDocumentsImagesPicker.returnImagePicker(
                        otherDocumentsImagesPlacement.get(k)));
              }
            } else {
              Picasso.with(this)
                  .load(Constants.BASE_IMAGES_URL + claimDetails.getImages()
                      .get(i)
                      .getContent()
                      .replace("/jpg", ".jpg"))
                  .resize(100, 100)
                  .into(otherDocumentsImagesPicker.returnImagePicker(i));
            }
            break;
        }
      } catch (NullPointerException ignored) {
      }
    }
  }
}