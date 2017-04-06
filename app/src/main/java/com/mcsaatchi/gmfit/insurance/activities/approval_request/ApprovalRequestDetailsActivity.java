package com.mcsaatchi.gmfit.insurance.activities.approval_request;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.rest.ClaimListDetailsResponseDatum;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.common.classes.ImageHandler;
import com.mcsaatchi.gmfit.insurance.presenters.ApprovalRequestDetailsActivityPresenter;
import com.mcsaatchi.gmfit.insurance.widget.CustomAttachmentPicker;
import com.mcsaatchi.gmfit.insurance.widget.ItemLabel;
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

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_approval_request_status);
    ButterKnife.bind(this);

    Bundle incomingExtras = getIntent().getExtras();

    ApprovalRequestDetailsActivityPresenter presenter =
        new ApprovalRequestDetailsActivityPresenter(this, dataAccessHandler);

    if (incomingExtras != null) {
      int requestId = incomingExtras.getInt(APPROVAL_REQUEST_CLAIM_ID);

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
      switch (responseDatum.getImages().get(i).getDocumType()) {
        case 1:
          medicalReportImagesPicker.returnImagePicker(i)
              .setImageBitmap(ImageHandler.turnBase64ToImage(responseDatum.getImages().get(i).getContent()));
          break;
        case 2:
          invoiceImagesPicker.returnImagePicker(i)
              .setImageBitmap(ImageHandler.turnBase64ToImage(responseDatum.getImages().get(i).getContent()));
          break;
        case 3:
          identityCardImagesPicker.returnImagePicker(i)
              .setImageBitmap(ImageHandler.turnBase64ToImage(responseDatum.getImages().get(i).getContent()));
          break;
        case 4:
          passportImagesPicker.returnImagePicker(i)
              .setImageBitmap(ImageHandler.turnBase64ToImage(responseDatum.getImages().get(i).getContent()));
          break;
        case 5:
          testResultsImagesPicker.returnImagePicker(i)
              .setImageBitmap(ImageHandler.turnBase64ToImage(responseDatum.getImages().get(i).getContent()));
          break;
        case 6:
          otherDocumentsImagesPicker.returnImagePicker(i)
              .setImageBitmap(ImageHandler.turnBase64ToImage(responseDatum.getImages().get(i).getContent()));
          break;
      }
    }
  }
}
