package com.mcsaatchi.gmfit.insurance.activities.inquiry;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.andreabaccega.widget.FormEditText;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.rest.CRMCategoriesResponse;
import com.mcsaatchi.gmfit.architecture.rest.CRMCategoriesResponseDatum;
import com.mcsaatchi.gmfit.architecture.rest.CreateNewRequestResponse;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.insurance.widget.CustomAttachmentPicker;
import com.mcsaatchi.gmfit.insurance.widget.CustomPicker;
import com.mcsaatchi.gmfit.insurance.widget.CustomToggle;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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

  private ArrayList<FormEditText> allFields = new ArrayList<>();

  private ImageView currentImageView;

  private ArrayList<String> imagePaths = new ArrayList<>();
  private List<CRMCategoriesResponseDatum> categoriesList;

  private String category = "ecd80d97-27da-e511-80e3-005056983dee";
  private String subcategory = "8402c6b4-27da-e511-80e3-005056983dee";
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

    allFields.add(requestTitleTV);

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

    hookupImagesPickerImages(medicalReportImagesPicker);
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
          String selectedImagePath = getPhotoPathFromGallery(selectedImageUri);

          Picasso.with(this).load(new File(selectedImagePath)).fit().into(currentImageView);

          imagePaths.add(selectedImagePath);
        }
    }
  }

  @OnClick(R.id.submitInquiryBTN) public void handleSubmitInquiry() {
    if (Helpers.validateFields(allFields)) {
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
                photoFile = createImageFile(constructImageFilename());
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

  private String getPhotoPathFromGallery(Uri uri) {
    if (uri == null) {
      // TODO perform some logging or show user feedback
      return null;
    }

    String[] projection = { MediaStore.Images.Media.DATA };
    Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
    if (cursor != null) {
      int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
      cursor.moveToFirst();
      return cursor.getString(column_index);
    }

    return uri.getPath();
  }

  private String constructImageFilename() {
    String timeStamp =
        new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    String imageFileName = "JPEG_" + timeStamp;

    File mediaStorageDir =
        new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "GMFit");

    if (!mediaStorageDir.exists()) {
      if (!mediaStorageDir.mkdirs()) {
        Log.d("Constants.DEBUG_TAG", "failed to create directory");
        return null;
      }
    }

    return mediaStorageDir.getPath() + File.separator + imageFileName;
  }

  private File createImageFile(String imagePath) throws IOException {
    return new File(imagePath);
  }

  private HashMap<String, RequestBody> constructSelectedImagesForRequest() {
    LinkedHashMap<String, RequestBody> imageParts = new LinkedHashMap<>();

    for (int i = 0; i < imagePaths.size(); i++) {
      if (imagePaths.get(i) != null) {
        imageParts.put("attachements[" + i + "][content]", toRequestBody(
            Base64.encodeToString(turnImageToByteArray(imagePaths.get(i)), Base64.NO_WRAP)));
        imageParts.put("attachements[" + i + "][documType]", toRequestBody("2"));
        imageParts.put("attachements[" + i + "][name]", toRequestBody(imagePaths.get(i)));
        imageParts.put("attachements[" + i + "][id]", toRequestBody(String.valueOf(i + 1)));
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
                //    ReimbursementStatusDetailsActivity.class);
                //intent.putExtra(ReimbursementStatusDetailsActivity.REIMBURSEMENT_REQUEST_ID,
                //    response.body().getData().getBody().getData().getRequestId());
                //
                //startActivity(intent);
                break;
              case 449:
                alertDialog.setMessage(getString(R.string.server_error_got_returned));
                alertDialog.show();
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
    waitingDialog.setCancelable(false);
    waitingDialog.show();

    dataAccessHandler.getCRMCategories(
        toRequestBody(prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, "")),
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
