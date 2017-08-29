package com.mcsaatchi.gmfit.insurance.activities.reimbursement;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.SubCategoriesResponseDatum;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.common.classes.ImageHandler;
import com.mcsaatchi.gmfit.insurance.widget.CustomAttachmentPicker;
import com.mcsaatchi.gmfit.insurance.widget.CustomPicker;
import com.mcsaatchi.gmfit.insurance.widget.CustomToggle;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import timber.log.Timber;

import static com.mcsaatchi.gmfit.insurance.widget.CustomAttachmentPicker.CAPTURE_NEW_PICTURE_REQUEST_CODE;
import static com.mcsaatchi.gmfit.insurance.widget.CustomAttachmentPicker.REQUEST_PICK_IMAGE_GALLERY;

public class SubmitReimbursementActivity extends BaseActivity
    implements SubmitReimbursementActivityPresenter.SubmitReimbursementActivityView {

  private static final int REQUEST_CAPTURE_PERMISSIONS = 123;
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.reimbursementSubcategory) CustomPicker subcategoryPicker;
  @Bind(R.id.reimbursementServiceDate) CustomPicker serviceDate;
  @Bind(R.id.categoryInOutToggle) CustomToggle categoryToggle;
  @Bind(R.id.medicalReportImagesPicker) CustomAttachmentPicker medicalReportImagesPicker;
  @Bind(R.id.invoiceImagesPicker) CustomAttachmentPicker invoiceImagesPicker;
  @Bind(R.id.originalReceiptImagesPicker) CustomAttachmentPicker originalReceiptImagesPicker;
  @Bind(R.id.identityCardImagesPicker) CustomAttachmentPicker identityCardImagesPicker;
  @Bind(R.id.testResultsImagesPicker) CustomAttachmentPicker testResultsImagesPicker;
  @Bind(R.id.otherDocumentsImagesPicker) CustomAttachmentPicker otherDocumentsImagesPicker;
  @Bind(R.id.currencyLayout) LinearLayout currencyLayout;
  @Bind(R.id.currencyLabel) TextView currencyLabel;
  @Bind(R.id.amountClaimedET) EditText amountClaimedET;
  @Bind(R.id.remarksET) EditText remarksET;
  @Bind(R.id.submitReimbursementBTN) Button submitReimbursementBTN;

  private ProgressDialog waitingDialog;

  private ArrayList<String> imagePaths = new ArrayList<>();
  private ArrayList<String> imagePathsFinal = new ArrayList<>();
  private ArrayList<String> imagesDocumentType = new ArrayList<>();

  private ArrayList<Integer> medicalReportImagesPlacement = new ArrayList<>();
  private ArrayList<Integer> invoiceImagesPlacement = new ArrayList<>();
  private ArrayList<Integer> originalReceiptImagesPlacement = new ArrayList<>();
  private ArrayList<Integer> identityCardImagesPlacement = new ArrayList<>();
  private ArrayList<Integer> testResultsImagesPlacement = new ArrayList<>();
  private ArrayList<Integer> otherDocumentsImagesPlacement = new ArrayList<>();

  private SubmitReimbursementActivityPresenter presenter;

  private File photoFile;
  private Uri photoFileUri;

  private int imageFilesSize = 0;

  private String categoryValue = "Out";
  private String currencyValue = "2";
  private String serviceDateValue = "";
  private String subCategoryId = "";
  private String requestTypeId = "1";

  private ImageView currentImageView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_reimbursement_submit);

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar, "Submit Reimbursement", true);

    presenter = new SubmitReimbursementActivityPresenter(this, dataAccessHandler);

    presenter.getSubCategories(prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""));

    currencyLayout.setOnClickListener(view -> {
      final String[] items = new String[] { "LBP", "USD" };

      AlertDialog.Builder builder = new AlertDialog.Builder(SubmitReimbursementActivity.this);
      builder.setTitle("Pick currency").setItems(items, (dialogInterface, i) -> {
        switch (items[i]) {
          case "USD":
            currencyValue = "2";
            break;
          case "LBP":
            currencyValue = "1";
            break;
        }

        currencyLabel.setText(items[i]);
      });
      builder.create();
      builder.show();
    });

    serviceDate.setUpDatePicker("Service Date", "Choose a date", (year, month, dayOfMonth) -> {
      Calendar calendar = Calendar.getInstance();
      calendar.set(Calendar.YEAR, year);
      calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
      calendar.set(Calendar.MONTH, month);

      Date d = new Date(calendar.getTimeInMillis());

      SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault());
      serviceDateValue = dateFormatter.format(d);
      serviceDate.setSelectedItem(serviceDateValue);
    });

    categoryToggle.setUp("Category", "Out", "In", option -> categoryValue = option);

    if (permChecker.lacksPermissions(Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
      ActivityCompat.requestPermissions(this,
          new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE },
          REQUEST_CAPTURE_PERMISSIONS);
    }

    hookupImagesPickerImages(medicalReportImagesPicker, 1);
    hookupImagesPickerImages(invoiceImagesPicker, 2);
    hookupImagesPickerImages(originalReceiptImagesPicker, 3);
    hookupImagesPickerImages(identityCardImagesPicker, 4);
    hookupImagesPickerImages(testResultsImagesPicker, 5);
    hookupImagesPickerImages(otherDocumentsImagesPicker, 6);
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    String finalPath = null;

    switch (requestCode) {
      case CAPTURE_NEW_PICTURE_REQUEST_CODE:
        if (photoFile.getTotalSpace() > 0) {
          finalPath = photoFile.getAbsolutePath();

          Picasso.with(this).load(new File(finalPath)).fit().into(currentImageView);
        } else {
          Timber.d("No picture was taken, photoFile size : %d", photoFile.getTotalSpace());
        }

        break;
      case REQUEST_PICK_IMAGE_GALLERY:
        if (data != null) {
          Uri selectedImageUri = data.getData();

          finalPath = ImageHandler.getPhotoPathFromGallery(this, selectedImageUri);

          Picasso.with(this).load(new File(finalPath)).fit().into(currentImageView);
        }

        imagePaths.add(finalPath);
    }
  }

  @Override public void populateSubCategories(List<SubCategoriesResponseDatum> subCategoriesList) {
    ArrayList<String> finalCategoryNames = new ArrayList<>();

    for (int i = 0; i < subCategoriesList.size(); i++) {
      if (subCategoriesList.get(i).getName() != null) {
        finalCategoryNames.add(subCategoriesList.get(i).getName());
      }
    }

    subcategoryPicker.setUpDropDown("Subcategory", "Choose a subcategory",
        finalCategoryNames.toArray(new String[finalCategoryNames.size()]), (index, selected) -> {
          for (SubCategoriesResponseDatum subCategoriesResponseDatum : subCategoriesList) {
            if (subCategoriesResponseDatum.getName() != null && subCategoriesResponseDatum.getName()
                .equals(selected)) {
              subCategoryId = subCategoriesResponseDatum.getId();
            }
          }
        });
  }

  @Override public void openReimbursementDetailsActivity(Integer claimId) {
    Intent intent =
        new Intent(SubmitReimbursementActivity.this, ReimbursementDetailsActivity.class);
    intent.putExtra(ReimbursementDetailsActivity.REIMBURSEMENT_REQUEST_ID, claimId);
    intent.putExtra("medicalReportImagesPlacement", medicalReportImagesPlacement);
    intent.putExtra("invoiceImagesPlacement", invoiceImagesPlacement);
    intent.putExtra("originalReceiptImagesPlacement", originalReceiptImagesPlacement);
    intent.putExtra("identityCardImagesPlacement", identityCardImagesPlacement);
    intent.putExtra("testResultsImagesPlacement", testResultsImagesPlacement);
    intent.putExtra("otherDocumentsImagesPlacement", otherDocumentsImagesPlacement);
    startActivity(intent);
    finish();
  }

  @Override public void saveImagePath(String imagePath) {
    imagePathsFinal.add(imagePath);

    imageFilesSize++;

    if (imageFilesSize < imagePaths.size()) {
      startUploadImages(imageFilesSize);
    } else if (imagePaths.size() == imagePathsFinal.size()) {
      Timber.d("Images and their paths are matching, upload");

      HashMap<String, RequestBody> attachments = constructSelectedImagesForRequest();

      presenter.submitReimbursement(prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""),
          categoryValue, subCategoryId, Helpers.formatActivitiesDate(serviceDateValue),
          requestTypeId, amountClaimedET.getText().toString(), currencyValue,
          remarksET.getText().toString(), attachments);

      submitReimbursementBTN.setEnabled(true);
    }
  }

  @Override public void dismissWaitingDialog() {
    waitingDialog.dismiss();
  }

  @OnClick(R.id.submitReimbursementBTN) public void handleSubmitReimbursement() {
    ArrayList<String> errorMessages = new ArrayList<>();

    imageFilesSize = 0;

    if (amountClaimedET.getText().toString().isEmpty()
        || Double.parseDouble(amountClaimedET.getText().toString()) == 0) {
      errorMessages.add("The Amount field is required, cannot be zero.");
    }
    if (serviceDateValue.isEmpty()) {
      errorMessages.add("The Service Date field is required.");
    }
    if (subCategoryId.isEmpty()) {
      errorMessages.add("The Subcategory field is required.");
    }
    if (imagePaths.isEmpty()) {
      errorMessages.add("You are required to attach some images.");
    }
    if (medicalReportImagesPlacement.isEmpty()
        || invoiceImagesPlacement.isEmpty()
        || originalReceiptImagesPlacement.isEmpty()
        || identityCardImagesPlacement.isEmpty()) {
      errorMessages.add("Please populate all the attachment categories");
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
      waitingDialog.setTitle(getResources().getString(R.string.submit_new_reimbursement));
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
          imagesDocumentType.add(String.valueOf(documentType));

          switch (documentType) {
            case 1:
              medicalReportImagesPlacement.add(finalI);
              break;
            case 2:
              invoiceImagesPlacement.add(finalI);
              break;
            case 3:
              originalReceiptImagesPlacement.add(finalI);
              break;
            case 4:
              identityCardImagesPlacement.add(finalI);
              break;
            case 5:
              testResultsImagesPlacement.add(finalI);
              break;
            case 6:
              otherDocumentsImagesPlacement.add(finalI);
              break;
          }

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
            Helpers.toRequestBody(imagesDocumentType.get(i)));
        imageParts.put("attachements[" + i + "][filename]",
            Helpers.toRequestBody(imagePaths.get(i)));
        imageParts.put("attachements[" + i + "][id]", Helpers.toRequestBody(String.valueOf(i + 1)));
      }
    }

    return imageParts;
  }
}
