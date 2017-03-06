package com.mcsaatchi.gmfit.insurance.activities.reimbursement;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.rest.CreateNewRequestResponse;
import com.mcsaatchi.gmfit.architecture.rest.SubCategoriesResponse;
import com.mcsaatchi.gmfit.architecture.rest.SubCategoriesResponseDatum;
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
import java.util.Calendar;
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

public class SubmitReimbursementActivity extends BaseActivity {

  private static final int REQUEST_CAPTURE_PERMISSIONS = 123;
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.reimbursementSubcategory) CustomPicker subcategoryPicker;
  @Bind(R.id.reimbursementServiceDate) CustomPicker serviceDate;
  @Bind(R.id.categoryInOutToggle) CustomToggle categoryToggle;
  @Bind(R.id.medicalReportImagesPicker) CustomAttachmentPicker medicalReportImagesPicker;
  @Bind(R.id.invoiceImagesPicker) CustomAttachmentPicker invoiceImagesPicker;
  @Bind(R.id.identityCardImagesPicker) CustomAttachmentPicker identityCardImagesPicker;
  @Bind(R.id.passportImagesPicker) CustomAttachmentPicker passportImagesPicker;
  @Bind(R.id.testResultsImagesPicker) CustomAttachmentPicker testResultsImagesPicker;
  @Bind(R.id.otherDocumentsImagesPicker) CustomAttachmentPicker otherDocumentsImagesPicker;
  @Bind(R.id.currencyLayout) LinearLayout currencyLayout;
  @Bind(R.id.currencyLabel) TextView currencyLabel;
  @Bind(R.id.amountClaimedET) EditText amountClaimedET;
  @Bind(R.id.remarksET) EditText remarksET;

  private ArrayList<String> imagePaths = new ArrayList<>();
  private List<SubCategoriesResponseDatum> subCategoriesList;

  private String categoryValue = "";
  private String serviceDateValue = "";
  private String amountValue = "";
  private String subCategoryId = "";
  private String requestTypeId = "1";

  private File photoFile;
  private Uri photoFileUri;
  private ImageView currentImageView;

  public static RequestBody toRequestBody(String value) {
    return RequestBody.create(MediaType.parse("text/plain"), value);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_reimbursement_submit);

    ButterKnife.bind(this);

    setupToolbar(getClass().getSimpleName(), toolbar, "Submit Reimbursement", true);

    getSubCategories();

    currencyLayout.setOnClickListener(view -> {
      final String[] items = new String[] { "LBP", "USD" };

      AlertDialog.Builder builder = new AlertDialog.Builder(SubmitReimbursementActivity.this);
      builder.setTitle("Pick currency").setItems(items,
          (dialogInterface, i) -> currencyLabel.setText(items[i]));
      builder.create();
      builder.show();
    });

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

  @OnClick(R.id.submitReimbursementBTN) public void handleSubmitReimbursement() {
    final ProgressDialog waitingDialog = new ProgressDialog(this);
    waitingDialog.setTitle(getResources().getString(R.string.submit_new_reimbursement));
    waitingDialog.setCancelable(false);
    waitingDialog.setMessage(
        getResources().getString(R.string.uploading_attachments_dialog_message));
    waitingDialog.setOnShowListener(dialogInterface -> {
      HashMap<String, RequestBody> attachments = constructSelectedImagesForRequest();

      submitReimbursement(attachments, waitingDialog);
    });

    waitingDialog.show();
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
    builderSingle.setTitle("Set profile picture");

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
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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

  private File createImageFile(String imagePath) throws IOException {
    return new File(imagePath);
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

  private void submitReimbursement(HashMap<String, RequestBody> attachements,
      final ProgressDialog waitingDialog) {
    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle(R.string.submit_new_reimbursement);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.ok),
        (dialog, which) -> {
          dialog.dismiss();

          if (waitingDialog.isShowing()) waitingDialog.dismiss();
        });

    dataAccessHandler.createNewRequest(
        toRequestBody(prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, "")),
        toRequestBody(categoryValue), toRequestBody(subCategoryId), toRequestBody(requestTypeId),
        toRequestBody(amountValue), toRequestBody("2"), toRequestBody(
            Helpers.getFormatServiceDate() + "T" + Helpers.getFormatServiceTime() + "+02:00"),
        toRequestBody("D"), toRequestBody(remarksET.getText().toString()), attachements,
        new Callback<CreateNewRequestResponse>() {
          @Override public void onResponse(Call<CreateNewRequestResponse> call,
              Response<CreateNewRequestResponse> response) {
            switch (response.code()) {
              case 200:
                Intent intent = new Intent(SubmitReimbursementActivity.this,
                    ReimbursementStatusDetailsActivity.class);
                intent.putExtra(ReimbursementStatusDetailsActivity.REIMBURSEMENT_REQUEST_ID,
                    response.body().getData().getBody().getData().getRequestId());

                startActivity(intent);
                break;
              case 449:
                alertDialog.setMessage(Helpers.provideErrorStringFromJSON(response.errorBody()));
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

  private void getSubCategories() {
    final ProgressDialog waitingDialog = new ProgressDialog(this);
    waitingDialog.setTitle(getResources().getString(R.string.loading_data_dialog_title));
    waitingDialog.setMessage(getResources().getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    dataAccessHandler.getSubCategories(
        prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""),
        new Callback<SubCategoriesResponse>() {
          @Override public void onResponse(Call<SubCategoriesResponse> call,
              Response<SubCategoriesResponse> response) {
            switch (response.code()) {
              case 200:
                waitingDialog.dismiss();

                subCategoriesList = response.body().getData().getBody().getData();
                ArrayList<String> finalCategoryNames = new ArrayList<>();

                for (int i = 0; i < subCategoriesList.size(); i++) {
                  if (subCategoriesList.get(i).getName() != null) {
                    finalCategoryNames.add(subCategoriesList.get(i).getName());
                  }
                }

                subcategoryPicker.setUpDropDown("Subcategory", "Choose a subcategory",
                    finalCategoryNames.toArray(new String[finalCategoryNames.size()]),
                    (index, selected) -> {
                      for (SubCategoriesResponseDatum subCategoriesResponseDatum : subCategoriesList) {
                        if (subCategoriesResponseDatum.getName() != null
                            && subCategoriesResponseDatum.getName().equals(selected)) {
                          subCategoryId = subCategoriesResponseDatum.getId();
                        }
                      }
                    });
            }
          }

          @Override public void onFailure(Call<SubCategoriesResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
          }
        });
  }
}
