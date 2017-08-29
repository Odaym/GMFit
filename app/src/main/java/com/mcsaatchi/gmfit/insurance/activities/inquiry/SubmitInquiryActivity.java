package com.mcsaatchi.gmfit.insurance.activities.inquiry;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.andreabaccega.widget.FormEditText;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CRMCategoriesResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CRMCategoriesResponseDatum;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CreateNewRequestResponse;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.ImageHandler;
import com.mcsaatchi.gmfit.insurance.widget.CustomAttachmentPicker;
import com.mcsaatchi.gmfit.insurance.widget.CustomPicker;
import com.mcsaatchi.gmfit.insurance.widget.CustomToggle;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.mcsaatchi.gmfit.insurance.widget.CustomAttachmentPicker.CAPTURE_NEW_PICTURE_REQUEST_CODE;
import static com.mcsaatchi.gmfit.insurance.widget.CustomAttachmentPicker.REQUEST_PICK_IMAGE_GALLERY;

public class SubmitInquiryActivity extends BaseActivity {

  private static final int REQUEST_CAPTURE_PERMISSIONS = 123;
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.categoryPicker) CustomPicker categoryPicker;
  @Bind(R.id.subCategoryPicker) CustomPicker subCategoryPicker;
  @Bind(R.id.areaToggle) CustomToggle areaToggle;
  @Bind(R.id.fullNameET) EditText fullNameET;
  @Bind(R.id.riskCarrierET) EditText riskCarrierET;
  @Bind(R.id.cardNumberET) EditText cardNumberET;
  @Bind(R.id.requestTitleTV) FormEditText requestTitleTV;
  @Bind(R.id.medicalReportImagesPicker) CustomAttachmentPicker medicalReportImagesPicker;
  private File photoFile;
  private Uri photoFileUri;

  private ImageView currentImageView;

  private ArrayList<String> imagePaths = new ArrayList<>();
  private ArrayList<String> imagesDocumentTypes = new ArrayList<>();
  private List<CRMCategoriesResponseDatum> categoriesList;

  private String category = "";
  private String subcategory = "";
  private String area = "local";

  public static RequestBody toRequestBody(String value) {
    return RequestBody.create(MediaType.parse("text/plain"), value);
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_inquiry_submit);
    ButterKnife.bind(this);
    setupToolbar(getClass().getSimpleName(), toolbar, "Submit Complaint/Inquiry", true);

    getCRMCategories();

    cardNumberET.setText(prefs.getString(Constants.EXTRAS_INSURANCE_USER_USERNAME, ""));
    fullNameET.setText(prefs.getString(Constants.EXTRAS_INSURANCE_FULL_NAME, ""));
    riskCarrierET.setText(prefs.getString(Constants.EXTRAS_INSURANCE_COMPANY_NAME, ""));

    subCategoryPicker.setUpDropDown("Subcategory", "Choose a subcategory",
        new String[] { "No category loaded" }, (index, selected) -> {
        });

    areaToggle.setUp("Area", "Local", "Cross Border", option -> area = option);

    if (permChecker.lacksPermissions(Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
      ActivityCompat.requestPermissions(this,
          new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE },
          REQUEST_CAPTURE_PERMISSIONS);
    }

    hookupImagesPickerImages(medicalReportImagesPicker, 1);
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

  @OnClick(R.id.submitInquiryBTN) public void handleSubmitInquiry() {
    ArrayList<String> errorMessages = new ArrayList<>();

    if (category.isEmpty()) {
      errorMessages.add("The Category field is required.");
    }
    if (subcategory.isEmpty()) {
      errorMessages.add("The Subcategory field is required.");
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
      final ProgressDialog waitingDialog = new ProgressDialog(this);
      waitingDialog.setTitle(getResources().getString(R.string.submit_new_inquiry));
      waitingDialog.setCancelable(false);
      waitingDialog.setMessage(
          getResources().getString(R.string.uploading_attachments_dialog_message));
      waitingDialog.setOnShowListener(dialogInterface -> {
        HashMap<String, RequestBody> attachments = constructSelectedImagesForRequest();

        submitInquiryComplaint(attachments, waitingDialog);
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

  private HashMap<String, RequestBody> constructSelectedImagesForRequest() {
    LinkedHashMap<String, RequestBody> imageParts = new LinkedHashMap<>();

    for (int i = 0; i < imagePaths.size(); i++) {
      if (imagePaths.get(i) != null) {
        imageParts.put("attachements[" + i + "][content]", toRequestBody(
            Base64.encodeToString(ImageHandler.turnImageToByteArray(imagePaths.get(i)),
                Base64.NO_WRAP)));
        imageParts.put("attachements[" + i + "][documType]",
            toRequestBody(imagesDocumentTypes.get(i)));
        imageParts.put("attachements[" + i + "][name]", toRequestBody(imagePaths.get(i)));
        imageParts.put("attachements[" + i + "][id]", toRequestBody(String.valueOf(i + 1)));
      }
    }

    return imageParts;
  }

  private void submitInquiryComplaint(HashMap<String, RequestBody> attachements,
      final ProgressDialog waitingDialog) {
    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle(R.string.submit_new_inquiry);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.ok),
        (dialog, which) -> {
          dialog.dismiss();

          if (waitingDialog.isShowing()) waitingDialog.dismiss();
        });

    dataAccessHandler.createNewInquiryComplaint(
        toRequestBody(prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, "")),
        toRequestBody(category), toRequestBody(subcategory),
        toRequestBody(requestTitleTV.getText().toString()), toRequestBody(area.toLowerCase()),
        toRequestBody(prefs.getString(Constants.EXTRAS_INSURANCE_COUNTRY_CRM_CODE, "")),
        attachements, new Callback<CreateNewRequestResponse>() {
          @Override public void onResponse(Call<CreateNewRequestResponse> call,
              Response<CreateNewRequestResponse> response) {
            switch (response.code()) {
              case 200:
                alertDialog.setMessage(getString(R.string.submission_successful_dialog_title));
                alertDialog.show();

                alertDialog.setOnDismissListener(dialogInterface -> finish());

                //Intent intent = new Intent(SubmitInquiryActivity.this,
                //    ReimbursementDetailsActivity.class);
                //intent.putExtra(ReimbursementDetailsActivity.REIMBURSEMENT_REQUEST_ID,
                //    response.body().getData().getBody().getData().getRequestId());
                //
                //startActivity(intent);
                break;
              case 449:
                //view.displayRequestErrorDialog(Helpers.provideErrorStringFromJSON(response.errorBody()));
                break;
            }

            waitingDialog.dismiss();
          }

          @Override public void onFailure(Call<CreateNewRequestResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
          }
        });
  }

  private void getCRMCategories() {
    final ProgressDialog waitingDialog = new ProgressDialog(this);
    waitingDialog.setTitle(getResources().getString(R.string.loading_data_dialog_title));
    waitingDialog.setMessage(getResources().getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    dataAccessHandler.getCRMCategories(
        toRequestBody(prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, "")),
        toRequestBody(prefs.getString(Constants.EXTRAS_INSURANCE_COUNTRY_CRM_CODE, "")),
        new Callback<CRMCategoriesResponse>() {
          @Override public void onResponse(Call<CRMCategoriesResponse> call,
              Response<CRMCategoriesResponse> response) {
            switch (response.code()) {
              case 200:
                waitingDialog.dismiss();

                categoriesList = response.body().getData().getBody().getData();
                ArrayList<String> finalCategoryNames = new ArrayList<>();

                for (int i = 0; i < categoriesList.size(); i++) {
                  if (categoriesList.get(i).getName() != null) {
                    finalCategoryNames.add(categoriesList.get(i).getName());
                  }
                }

                categoryPicker.setUpDropDown("Category", "Choose a category",
                    finalCategoryNames.toArray(new String[finalCategoryNames.size()]),
                    (index, selected) -> {
                      for (final CRMCategoriesResponseDatum categoriesResponseDatum : categoriesList) {
                        if (categoriesResponseDatum.getName() != null
                            && categoriesResponseDatum.getName().equals(selected)) {
                          category = categoriesResponseDatum.getId();

                          List<String> subCategoryNames = new ArrayList<>();

                          for (int i = 0; i < categoriesResponseDatum.getSubs().size(); i++) {
                            if (categoriesResponseDatum.getSubs().get(i).getName() != null) {
                              subCategoryNames.add(
                                  categoriesResponseDatum.getSubs().get(i).getName());
                            }
                          }

                          subCategoryPicker.setUpDropDown("SubCategory", "Choose a subcategory",
                              subCategoryNames.toArray(new String[subCategoryNames.size()]),
                              (index1, selected1) -> {
                                for (int i = 0; i < categoriesResponseDatum.getSubs().size(); i++) {
                                  if (categoriesResponseDatum.getSubs().get(i).getName() != null
                                      && categoriesResponseDatum.getSubs()
                                      .get(i)
                                      .getName()
                                      .equals(selected1)) {
                                    subcategory = categoriesResponseDatum.getSubs().get(i).getId();
                                  }
                                }
                              });
                        }
                      }
                    });
            }
          }

          @Override public void onFailure(Call<CRMCategoriesResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
          }
        });
  }
}
