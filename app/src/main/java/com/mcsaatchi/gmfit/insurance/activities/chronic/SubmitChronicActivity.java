package com.mcsaatchi.gmfit.insurance.activities.chronic;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
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
import com.mcsaatchi.gmfit.insurance.activities.reimbursement.ReimbursementDetailsActivity;
import com.mcsaatchi.gmfit.insurance.widget.CustomAttachmentPicker;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import timber.log.Timber;

import static com.mcsaatchi.gmfit.insurance.widget.CustomAttachmentPicker.CAPTURE_NEW_PICTURE_REQUEST_CODE;
import static com.mcsaatchi.gmfit.insurance.widget.CustomAttachmentPicker.REQUEST_PICK_IMAGE_GALLERY;

public class SubmitChronicActivity extends BaseActivity
    implements SubmitChronicActivityPresenter.SubmitChronicActivityView {

  private static final int REQUEST_CAPTURE_PERMISSIONS = 123;
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.medicalReportImagesPicker) CustomAttachmentPicker medicalReportImagesPicker;
  @Bind(R.id.identityCardImagesPicker) CustomAttachmentPicker identityCardImagesPicker;
  @Bind(R.id.doctorConfirmationImagesPicker) CustomAttachmentPicker doctorConfirmationImagesPicker;
  @Bind(R.id.otherDocumentsImagesPicker) CustomAttachmentPicker otherDocumentsImagesPicker;

  private ProgressDialog waitingDialog;
  private File photoFile;
  private Uri photoFileUri;
  private ImageView currentImageView;
  private SubmitChronicActivityPresenter presenter;
  private ArrayList<String> imagePaths = new ArrayList<>();
  private ArrayList<String> imagePathsFinal = new ArrayList<>();
  private ArrayList<String> imagesDocumentTypes = new ArrayList<>();

  private int imageFilesSize = 0;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chronic_prescription_submit);
    ButterKnife.bind(this);
    setupToolbar(getClass().getSimpleName(), toolbar, "Submit Chronic Prescription", true);

    if (permChecker.lacksPermissions(Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
      ActivityCompat.requestPermissions(this,
          new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE },
          REQUEST_CAPTURE_PERMISSIONS);
    }

    presenter = new SubmitChronicActivityPresenter(this, dataAccessHandler);

    hookupImagesPickerImages(medicalReportImagesPicker, 1);
    hookupImagesPickerImages(identityCardImagesPicker, 2);
    hookupImagesPickerImages(doctorConfirmationImagesPicker, 3);
    hookupImagesPickerImages(otherDocumentsImagesPicker, 4);
  }

  @Override public void openChronicTrackActivity(Integer requestId) {
    Intent intent = new Intent(SubmitChronicActivity.this, ChronicTrackActivity.class);
    intent.putExtra(ReimbursementDetailsActivity.REIMBURSEMENT_REQUEST_ID, requestId);
    startActivity(intent);
    finish();
  }

  @Override public void dismissWaitingDialog() {
    waitingDialog.dismiss();
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    switch (requestCode) {
      case CAPTURE_NEW_PICTURE_REQUEST_CODE:
        if (photoFile != null) {
          if (photoFile.getTotalSpace() > 0) {
            Picasso.with(this).load(new File(photoFile.getAbsolutePath())).fit().into(currentImageView);

            imagePaths.add(photoFile.getAbsolutePath());
          } else {
            Timber.d("No picture was taken, photoFile size : %d", photoFile.getTotalSpace());
          }
        }
        break;
      case REQUEST_PICK_IMAGE_GALLERY:
        if (data != null) {
          Uri selectedImageUri = data.getData();
          String selectedImagePath = ImageHandler.getPhotoPathFromGallery(this, selectedImageUri);

          Picasso.with(this).load(new File(selectedImagePath)).fit().into(currentImageView);

          imagePaths.add(selectedImagePath);
        }
        break;
    }
  }

  @Override public void saveImagePath(String imagePath) {
    imagePathsFinal.add(imagePath);

    imageFilesSize++;

    if (imageFilesSize < imagePaths.size()) {
      startUploadImages(imageFilesSize);
    } else if (imagePaths.size() == imagePathsFinal.size()) {
      HashMap<String, RequestBody> attachments = constructSelectedImagesForRequest();

      presenter.submitChronicTreatment(
          prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""), attachments);
    }
  }

  @OnClick(R.id.submitChronicTreatmentBTN) public void handleSubmitChronicTreatment() {
    ArrayList<String> errorMessages = new ArrayList<>();

    if (imagePaths.isEmpty()) {
      errorMessages.add("You are required to attach some images.");
    }

    if (!errorMessages.isEmpty()) {
      String finalErrorMessage = "";

      for (String errorMessage : errorMessages) {
        finalErrorMessage += errorMessage + "\n\n";
      }

      final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
      alertDialog.setTitle(R.string.required_fields_dialog_title);
      alertDialog.setMessage(finalErrorMessage);
      alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.ok),
          (dialog, which) -> dialog.dismiss());
      alertDialog.show();
    } else {
      waitingDialog = new ProgressDialog(this);
      waitingDialog.setTitle(
          getResources().getString(R.string.submit_new_chronic_treatment_dialog_title));
      waitingDialog.setMessage(
          getResources().getString(R.string.uploading_attachments_dialog_message));
      waitingDialog.setOnShowListener(dialogInterface -> {
        startUploadImages(imageFilesSize);
      });

      waitingDialog.show();
    }
  }

  private void hookupImagesPickerImages(CustomAttachmentPicker imagePicker, int documentType) {
    LinearLayout parentLayout = (LinearLayout) imagePicker.getChildAt(0);
    final LinearLayout innerLayoutWithPickers = (LinearLayout) parentLayout.getChildAt(1);

    for (int i = 0; i < innerLayoutWithPickers.getChildCount(); i++) {
      if (innerLayoutWithPickers.getChildAt(i) instanceof ImageView) {
        final int finalI = i;

        innerLayoutWithPickers.getChildAt(i).setOnClickListener(view -> {
          imagesDocumentTypes.add(String.valueOf(documentType));

          ImageView imageView = innerLayoutWithPickers.findViewById(
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
                photoFileUri = FileProvider.getUriForFile(this,
                    getApplicationContext().getPackageName() + ".provider", photoFile);
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

  private void startUploadImages(int imageFilesIndex) {
    if (imagePaths.get(imageFilesIndex) != null) {
      HashMap<String, RequestBody> insuranceImages = new HashMap<>();

      File imageFile = new File(imagePaths.get(imageFilesIndex));

      RequestBody imageFilePart = RequestBody.create(MediaType.parse("image/*"), imageFile);

      insuranceImages.put("file\"; filename=\"" + imagePaths.get(imageFilesIndex), imageFilePart);

      presenter.uploadInsuranceImage(insuranceImages);
    }
  }

  private HashMap<String, RequestBody> constructSelectedImagesForRequest() {
    LinkedHashMap<String, RequestBody> imageParts = new LinkedHashMap<>();

    for (int i = 0; i < imagePaths.size(); i++) {
      if (imagePaths.get(i) != null) {
        imageParts.put("attachements[" + i + "][path]",
            Helpers.toRequestBody(imagePathsFinal.get(i)));
        imageParts.put("attachements[" + i + "][documType]",
            Helpers.toRequestBody(imagesDocumentTypes.get(i)));
        imageParts.put("attachements[" + i + "][filename]",
            Helpers.toRequestBody(imagePaths.get(i)));
        imageParts.put("attachements[" + i + "][id]", Helpers.toRequestBody(String.valueOf(i + 1)));
      }
    }

    return imageParts;
  }
}