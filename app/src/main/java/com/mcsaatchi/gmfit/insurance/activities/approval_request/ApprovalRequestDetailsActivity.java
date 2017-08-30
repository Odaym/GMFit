package com.mcsaatchi.gmfit.insurance.activities.approval_request;

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
import java.util.ArrayList;
import org.joda.time.LocalDate;

public class ApprovalRequestDetailsActivity extends BaseActivity
    implements ApprovalRequestDetailsActivityPresenter.ApprovalRequestDetailsActivityView {
  public static final String APPROVAL_REQUEST_CLAIM_ID = "approval_request_claim_id";
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.status) ItemLabel status;
  @Bind(R.id.category) ItemLabel category;
  @Bind(R.id.subCategory) ItemLabel subCategory;
  @Bind(R.id.serviceDate) ItemLabel serviceDate;
  @Bind(R.id.medicalReportImagesPicker) CustomAttachmentPicker medicalReportImagesPicker;
  @Bind(R.id.invoiceImagesPicker) CustomAttachmentPicker invoiceImagesPicker;
  @Bind(R.id.identityCardImagesPicker) CustomAttachmentPicker identityCardImagesPicker;
  @Bind(R.id.passportImagesPicker) CustomAttachmentPicker passportImagesPicker;
  @Bind(R.id.testResultsImagesPicker) CustomAttachmentPicker testResultsImagesPicker;
  @Bind(R.id.otherDocumentsImagesPicker) CustomAttachmentPicker otherDocumentsImagesPicker;

  private ArrayList<Integer> medicalReportImagesPlacement, invoiceImagesPlacement,
      identityCardImagesPlacement, passportImagesPlacement, testResultsImagesPlacement,
      otherDocumentsImagesPlacement = new ArrayList<>();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_approval_request_status);
    ButterKnife.bind(this);

    Bundle incomingExtras = getIntent().getExtras();

    ApprovalRequestDetailsActivityPresenter presenter =
        new ApprovalRequestDetailsActivityPresenter(this, dataAccessHandler);

    if (incomingExtras != null) {
      int requestId = incomingExtras.getInt(APPROVAL_REQUEST_CLAIM_ID);

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

      if (incomingExtras.getIntegerArrayList("passportImagesPlacement") != null) {
        passportImagesPlacement = incomingExtras.getIntegerArrayList("passportImagesPlacement");
      }

      if (incomingExtras.getIntegerArrayList("testResultsImagesPlacement") != null) {
        testResultsImagesPlacement =
            incomingExtras.getIntegerArrayList("testResultsImagesPlacement");
      }

      if (incomingExtras.getIntegerArrayList("otherDocumentsImagesPlacement") != null) {
        otherDocumentsImagesPlacement =
            incomingExtras.getIntegerArrayList("otherDocumentsImagesPlacement");
      }

      presenter.getClaimDetailsList(prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""),
          "2", requestId);
    }
  }

  @Override public void displayClaimDetails(ClaimListDetailsResponseDatum responseDatum) {
    setupToolbar(getClass().getSimpleName(), toolbar, "#" + responseDatum.getId(), true);

    serviceDate.setLabel("Service Date",
        Helpers.formatInsuranceDate(new LocalDate(responseDatum.getDate().split(" ")[0])));
    subCategory.setLabel("Sub Category", responseDatum.getSubcategory());
    category.setLabel("Category", responseDatum.getCategory());
    status.setTextColor(Helpers.determineStatusColor(responseDatum.getStatus()));
    status.setLabel("Status", responseDatum.getStatus());

    for (int i = 0; i < responseDatum.getImages().size(); i++) {

      String imageContents = responseDatum.getImages().get(i).getContent();

      try {
        switch (responseDatum.getImages().get(i).getDocumType()) {
          case 1:
            if (medicalReportImagesPlacement != null) {
              for (int k = 0; k < medicalReportImagesPlacement.size(); k++) {
                medicalReportImagesPicker.returnImagePicker(medicalReportImagesPlacement.get(k))
                    .setImageBitmap(Helpers.getResizedBitmap(Helpers.convertToBase64(imageContents),
                        Constants.BITMAP_RESIZE_DIMENS_WIDTH,
                        Constants.BITMAP_RESIZE_DIMENS_HEIGHT));
              }
            } else {
              medicalReportImagesPicker.returnImagePicker(i)
                  .setImageBitmap(Helpers.getResizedBitmap(Helpers.convertToBase64(imageContents),
                      Constants.BITMAP_RESIZE_DIMENS_WIDTH, Constants.BITMAP_RESIZE_DIMENS_HEIGHT));
            }
            break;
          case 2:
            if (invoiceImagesPlacement != null) {
              for (int k = 0; k < invoiceImagesPlacement.size(); k++) {
                invoiceImagesPicker.returnImagePicker(invoiceImagesPlacement.get(k))
                    .setImageBitmap(Helpers.getResizedBitmap(Helpers.convertToBase64(imageContents),
                        Constants.BITMAP_RESIZE_DIMENS_WIDTH,
                        Constants.BITMAP_RESIZE_DIMENS_HEIGHT));
              }
            } else {
              invoiceImagesPicker.returnImagePicker(i)
                  .setImageBitmap(Helpers.getResizedBitmap(Helpers.convertToBase64(imageContents),
                      Constants.BITMAP_RESIZE_DIMENS_WIDTH, Constants.BITMAP_RESIZE_DIMENS_HEIGHT));
            }
            break;
          case 3:
            if (identityCardImagesPlacement != null) {
              for (int k = 0; k < identityCardImagesPlacement.size(); k++) {
                identityCardImagesPicker.returnImagePicker(identityCardImagesPlacement.get(k))
                    .setImageBitmap(Helpers.getResizedBitmap(Helpers.convertToBase64(imageContents),
                        Constants.BITMAP_RESIZE_DIMENS_WIDTH,
                        Constants.BITMAP_RESIZE_DIMENS_HEIGHT));
              }
            } else {
              identityCardImagesPicker.returnImagePicker(i)
                  .setImageBitmap(Helpers.getResizedBitmap(Helpers.convertToBase64(imageContents),
                      Constants.BITMAP_RESIZE_DIMENS_WIDTH, Constants.BITMAP_RESIZE_DIMENS_HEIGHT));
            }
            break;
          case 4:
            if (passportImagesPlacement != null) {
              for (int k = 0; k < passportImagesPlacement.size(); k++) {
                passportImagesPicker.returnImagePicker(passportImagesPlacement.get(k))
                    .setImageBitmap(Helpers.getResizedBitmap(Helpers.convertToBase64(imageContents),
                        Constants.BITMAP_RESIZE_DIMENS_WIDTH,
                        Constants.BITMAP_RESIZE_DIMENS_HEIGHT));
              }
            } else {
              passportImagesPicker.returnImagePicker(i)
                  .setImageBitmap(Helpers.getResizedBitmap(Helpers.convertToBase64(imageContents),
                      Constants.BITMAP_RESIZE_DIMENS_WIDTH, Constants.BITMAP_RESIZE_DIMENS_HEIGHT));
            }
            break;
          case 5:
            if (testResultsImagesPlacement != null) {
              for (int k = 0; k < testResultsImagesPlacement.size(); k++) {
                testResultsImagesPicker.returnImagePicker(testResultsImagesPlacement.get(k))
                    .setImageBitmap(Helpers.getResizedBitmap(Helpers.convertToBase64(imageContents),
                        Constants.BITMAP_RESIZE_DIMENS_WIDTH,
                        Constants.BITMAP_RESIZE_DIMENS_HEIGHT));
              }
            } else {
              testResultsImagesPicker.returnImagePicker(i)
                  .setImageBitmap(Helpers.getResizedBitmap(Helpers.convertToBase64(imageContents),
                      Constants.BITMAP_RESIZE_DIMENS_WIDTH, Constants.BITMAP_RESIZE_DIMENS_HEIGHT));
            }
            break;
          case 6:
            if (otherDocumentsImagesPlacement != null) {
              for (int k = 0; k < otherDocumentsImagesPlacement.size(); k++) {
                otherDocumentsImagesPicker.returnImagePicker(otherDocumentsImagesPlacement.get(k))
                    .setImageBitmap(Helpers.getResizedBitmap(Helpers.convertToBase64(imageContents),
                        Constants.BITMAP_RESIZE_DIMENS_WIDTH,
                        Constants.BITMAP_RESIZE_DIMENS_HEIGHT));
              }
            } else {
              otherDocumentsImagesPicker.returnImagePicker(i)
                  .setImageBitmap(Helpers.getResizedBitmap(Helpers.convertToBase64(imageContents),
                      Constants.BITMAP_RESIZE_DIMENS_WIDTH, Constants.BITMAP_RESIZE_DIMENS_HEIGHT));
            }
            break;
        }
      } catch (NullPointerException ignored) {
      }
    }
  }
}
