package com.mcsaatchi.gmfit.insurance.activities.approval_request;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.common.classes.ImageHandler;
import com.mcsaatchi.gmfit.insurance.presenters.SubmitApprovalRequestActivityPresenter;
import com.mcsaatchi.gmfit.insurance.widget.CustomAttachmentPicker;
import com.mcsaatchi.gmfit.insurance.widget.CustomPicker;
import com.mcsaatchi.gmfit.insurance.widget.CustomToggle;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import okhttp3.RequestBody;
import timber.log.Timber;

import static com.mcsaatchi.gmfit.insurance.widget.CustomAttachmentPicker.CAPTURE_NEW_PICTURE_REQUEST_CODE;
import static com.mcsaatchi.gmfit.insurance.widget.CustomAttachmentPicker.REQUEST_PICK_IMAGE_GALLERY;

public class SubmitApprovalRequestActivity extends BaseActivity
    implements SubmitApprovalRequestActivityPresenter.SubmitApprovalRequestActivityView {

  private static final int REQUEST_CAPTURE_PERMISSIONS = 123;
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.reimbursementSubcategory) CustomPicker subcategory;
  @Bind(R.id.reimbursementServiceDate) CustomPicker serviceDate;
  @Bind(R.id.categoryInOutToggle) CustomToggle categoryToggle;
  @Bind(R.id.submitApprovalRequestBTN) Button submitApprovalRequestBTN;
  @Bind(R.id.medicalReportImagesPicker) CustomAttachmentPicker medicalReportImagesPicker;
  @Bind(R.id.invoiceImagesPicker) CustomAttachmentPicker invoiceImagesPicker;
  @Bind(R.id.identityCardImagesPicker) CustomAttachmentPicker identityCardImagesPicker;
  @Bind(R.id.passportImagesPicker) CustomAttachmentPicker passportImagesPicker;
  @Bind(R.id.testResultsImagesPicker) CustomAttachmentPicker testResultsImagesPicker;
  @Bind(R.id.otherDocumentsImagesPicker) CustomAttachmentPicker otherDocumentsImagesPicker;
  @Bind(R.id.remarksET) EditText remarksET;

  private SubmitApprovalRequestActivityPresenter presenter;
  private ArrayList<String> imagePaths = new ArrayList<>();
  private ProgressDialog waitingDialog;
  private ImageView currentImageView;

  private File photoFile;
  private Uri photoFileUri;
  private String categoryValue = "Out";
  private String subCategoryValue = "Dental PCC";
  private String serviceDateValue;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_approval_requests_submit);

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar, "Submit Approval Request", true);

    presenter = new SubmitApprovalRequestActivityPresenter(this, dataAccessHandler);

    serviceDate.setUpDatePicker("Service Date", "Choose a date", (year, month, dayOfMonth) -> {
      Calendar calendar = Calendar.getInstance();
      calendar.set(Calendar.YEAR, year);
      calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
      calendar.set(Calendar.MONTH, month);

      Date d = new Date(calendar.getTimeInMillis());

      SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM, yyyy");
      serviceDateValue = dateFormatter.format(d);
      serviceDate.setSelectedItem(serviceDateValue);
    });

    categoryToggle.setUp("Category", "Out", "In", option -> categoryValue = option);

    subcategory.setUpDropDown("Subcategory", "Choose a subcategory",
        new String[] { "Dental PCC", "Ambulatory", "Doctors Visit", "PCP", "Dental" },
        (index, selected) -> subCategoryValue = selected);

    if (permChecker.lacksPermissions(Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
      ActivityCompat.requestPermissions(this,
          new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE },
          REQUEST_CAPTURE_PERMISSIONS);
    }

    hookupImagesPickerImages(medicalReportImagesPicker);
    hookupImagesPickerImages(invoiceImagesPicker);
    hookupImagesPickerImages(identityCardImagesPicker);
    hookupImagesPickerImages(passportImagesPicker);
    hookupImagesPickerImages(testResultsImagesPicker);
    hookupImagesPickerImages(otherDocumentsImagesPicker);
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    switch (requestCode) {
      case CAPTURE_NEW_PICTURE_REQUEST_CODE:
        if (photoFile.getTotalSpace() > 0) {
          Picasso.with(this)
              .load(new File(photoFile.getAbsolutePath()))
              .fit()
              .into(currentImageView);

          imagePaths.add(photoFile.getAbsolutePath());
        } else {
          Timber.d("No picture was taken, photoFile size : %d", photoFile.getTotalSpace());
        }

        break;
      case REQUEST_PICK_IMAGE_GALLERY:
        if (data != null) {
          Uri selectedImageUri = data.getData();
          String selectedImagePath = ImageHandler.getPhotoPathFromGallery(this, selectedImageUri);

          Picasso.with(this).load(new File(selectedImagePath)).fit().into(currentImageView);

          imagePaths.add(selectedImagePath);
        }
    }
  }

  @Override public void openApprovalRequestDetailsActivity(Integer claimId) {
    Intent intent =
        new Intent(SubmitApprovalRequestActivity.this, ApprovalRequestDetailsActivity.class);
    intent.putExtra(ApprovalRequestDetailsActivity.APPROVAL_REQUEST_CLAIM_ID, claimId);

    startActivity(intent);
  }

  @Override public void dismissWaitingDialog() {
    waitingDialog.dismiss();
  }

  @OnClick(R.id.submitApprovalRequestBTN) public void handleSubmitApprovalRequest() {
    waitingDialog = new ProgressDialog(this);
    waitingDialog.setTitle(getResources().getString(R.string.submit_new_approval_request));
    waitingDialog.setMessage(
        getResources().getString(R.string.uploading_attachments_dialog_message));
    waitingDialog.setOnShowListener(dialogInterface -> {
      HashMap<String, RequestBody> attachments = constructSelectedImagesForRequest();

      presenter.submitApprovalRequest(
          prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""),
          remarksET.getText().toString(), categoryValue, attachments);
    });

    waitingDialog.show();
  }

  private void hookupImagesPickerImages(CustomAttachmentPicker imagePicker) {
    LinearLayout parentLayout = (LinearLayout) imagePicker.getChildAt(0);
    final LinearLayout innerLayoutWithPickers = (LinearLayout) parentLayout.getChildAt(1);

    for (int i = 0; i < innerLayoutWithPickers.getChildCount(); i++) {
      if (innerLayoutWithPickers.getChildAt(i) instanceof ImageView) {
        final int finalI = i;
        innerLayoutWithPickers.getChildAt(i).setOnClickListener(view -> {
          ImageView imageView = (ImageView) innerLayoutWithPickers.findViewById(
              innerLayoutWithPickers.getChildAt(finalI).getId());
          showImagePickerDialog(imageView);
        });
      }
    }
  }

  private void showImagePickerDialog(ImageView view) {
    currentImageView = view;

    AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
    builderSingle.setTitle("Attach a picture");

    final ArrayAdapter<String> arrayAdapter =
        new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
    arrayAdapter.add(getResources().getString(R.string.choose_picture_from_gallery));
    arrayAdapter.add(getResources().getString(R.string.take_new_picture));

    builderSingle.setNegativeButton(R.string.decline_cancel, (dialog, which) -> dialog.dismiss());

    builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
      String strName = arrayAdapter.getItem(which);
      if (strName != null) {
        switch (strName) {
          case "Choose from gallery":
            Intent galleryIntent =
                new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, REQUEST_PICK_IMAGE_GALLERY);
            break;
          case "Take a new picture":
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
              photoFile = null;
              try {
                photoFile = ImageHandler.createImageFile(ImageHandler.constructImageFilename());
                photoFileUri = Uri.fromFile(photoFile);
              } catch (IOException ex) {
                ex.printStackTrace();
              }

              if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFileUri);
                startActivityForResult(takePictureIntent, CAPTURE_NEW_PICTURE_REQUEST_CODE);
              }
            }

            break;
        }
      }
    });
    builderSingle.show();
  }

  private HashMap<String, RequestBody> constructSelectedImagesForRequest() {
    LinkedHashMap<String, RequestBody> imageParts = new LinkedHashMap<>();

    for (int i = 0; i < imagePaths.size(); i++) {
      if (imagePaths.get(i) != null) {
        imageParts.put("attachements[" + i + "][content]", Helpers.toRequestBody(
            Base64.encodeToString(turnImageToByteArray(imagePaths.get(i)), Base64.NO_WRAP)));
        imageParts.put("attachements[" + i + "][documType]", Helpers.toRequestBody("2"));
        imageParts.put("attachements[" + i + "][name]", Helpers.toRequestBody(imagePaths.get(i)));
        imageParts.put("attachements[" + i + "][id]", Helpers.toRequestBody(String.valueOf(i + 1)));
      }
    }

    return imageParts;
  }

  private byte[] turnImageToByteArray(String imagePath) {
    Bitmap bm = BitmapFactory.decodeFile(imagePath);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);

    return baos.toByteArray();
  }
}
