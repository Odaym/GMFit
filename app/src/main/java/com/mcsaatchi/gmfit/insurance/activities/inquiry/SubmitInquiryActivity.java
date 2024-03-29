package com.mcsaatchi.gmfit.insurance.activities.inquiry;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.andreabaccega.widget.FormEditText;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CRMCategoriesResponseDatum;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import timber.log.Timber;

import static com.mcsaatchi.gmfit.common.classes.Helpers.toRequestBody;
import static com.mcsaatchi.gmfit.insurance.widget.CustomAttachmentPicker.CAPTURE_NEW_PICTURE_REQUEST_CODE;
import static com.mcsaatchi.gmfit.insurance.widget.CustomAttachmentPicker.REQUEST_PICK_IMAGE_GALLERY;

public class SubmitInquiryActivity extends BaseActivity
    implements SubmitInquiryActivityPresenter.SubmitInquiryActivityView {

  private static final int REQUEST_CAPTURE_PERMISSIONS = 123;
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.categoryPicker) CustomPicker categoryPicker;
  @Bind(R.id.subCategoryPicker) CustomPicker subCategoryPicker;
  @Bind(R.id.areaToggle) CustomToggle areaToggle;
  @Bind(R.id.fullNameET) EditText fullNameET;
  @Bind(R.id.riskCarrierET) EditText riskCarrierET;
  @Bind(R.id.cardNumberET) EditText cardNumberET;
  @Bind(R.id.requestTitleET) FormEditText requestTitleET;
  @Bind(R.id.requestDescriptionET) FormEditText requestDescriptionET;
  @Bind(R.id.optionalImageImagesPicker) CustomAttachmentPicker optionalImageImagesPicker;
  private File photoFile;
  private Uri photoFileUri;

  private ImageView currentImageView;

  private SubmitInquiryActivityPresenter presenter;

  private ArrayList<String> imagePaths = new ArrayList<>();

  private List<CRMCategoriesResponseDatum> categoriesList;
  private ArrayList<String> finalCategoryNames = new ArrayList<>();

  private String category = "";
  private String subcategory = "";
  private String area = "local";

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_inquiry_submit);
    ButterKnife.bind(this);
    setupToolbar(getClass().getSimpleName(), toolbar,
        getString(R.string.submit_complaint_inquiry_activity_title), true);

    optionalImageImagesPicker.hideRemainingImages();

    presenter = new SubmitInquiryActivityPresenter(this, dataAccessHandler);

    presenter.getCRMCategories(
        Helpers.toRequestBody(prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, "")),
        Helpers.toRequestBody(prefs.getString(Constants.EXTRAS_INSURANCE_COUNTRY_CRM_CODE, "")));

    cardNumberET.setText(prefs.getString(Constants.EXTRAS_INSURANCE_USER_USERNAME, ""));
    fullNameET.setText(prefs.getString(Constants.EXTRAS_INSURANCE_FULL_NAME, ""));
    riskCarrierET.setText(prefs.getString(Constants.EXTRAS_INSURANCE_COMPANY_NAME, ""));

    subCategoryPicker.setUpDropDown(getString(R.string.sub_category_picker_title),
        getString(R.string.sub_category_picker_message), new String[] { "No category loaded" },
        (index, selected) -> {
        });

    areaToggle.setUp("Area", "Local", "Cross Border", option -> area = option);

    if (permChecker.lacksPermissions(Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
      ActivityCompat.requestPermissions(this,
          new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE },
          REQUEST_CAPTURE_PERMISSIONS);
    }

    hookupImagesPickerImages(optionalImageImagesPicker);
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    switch (requestCode) {
      case CAPTURE_NEW_PICTURE_REQUEST_CODE:
        if (photoFile != null) {
          if (photoFile.getTotalSpace() > 0) {
            Picasso.with(this)
                .load(new File(photoFile.getAbsolutePath()))
                .fit()
                .into(currentImageView);

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
    }
  }

  @Override public void startSubmitInquiryComplaint(String imagePath) {
    if (imagePath == null) {
      presenter.submitInquiryComplaintWithoutImage(
          toRequestBody(prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, "")),
          toRequestBody(prefs.getString(Constants.EXTRAS_INSURANCE_COUNTRY_CRM_CODE, "")),
          toRequestBody(category), toRequestBody(subcategory), toRequestBody(area.toLowerCase()),
          toRequestBody(requestTitleET.getText().toString()),
          toRequestBody(requestDescriptionET.getText().toString()));
    } else {
      presenter.submitInquiryComplaint(
          toRequestBody(prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, "")),
          toRequestBody(prefs.getString(Constants.EXTRAS_INSURANCE_COUNTRY_CRM_CODE, "")),
          toRequestBody(category), toRequestBody(subcategory), toRequestBody(area.toLowerCase()),
          toRequestBody(requestTitleET.getText().toString()),
          toRequestBody(requestDescriptionET.getText().toString()), toRequestBody(imagePath));
    }
  }

  @Override public void setupCategoryAndSubCategoryPickerDropdown(
      List<CRMCategoriesResponseDatum> crmCategoriesResponseDatumList) {
    categoriesList = crmCategoriesResponseDatumList;

    for (int i = 0; i < categoriesList.size(); i++) {
      if (categoriesList.get(i).getName() != null) {
        finalCategoryNames.add(categoriesList.get(i).getName());
      }
    }

    categoryPicker.setUpDropDown(getString(R.string.category_picker_title),
        getString(R.string.category_picker_message),
        finalCategoryNames.toArray(new String[finalCategoryNames.size()]), (index, selected) -> {
          for (final CRMCategoriesResponseDatum categoriesResponseDatum : categoriesList) {
            if (categoriesResponseDatum.getName() != null && categoriesResponseDatum.getName()
                .equals(selected)) {
              category = categoriesResponseDatum.getId();

              List<String> subCategoryNames = new ArrayList<>();

              for (int i = 0; i < categoriesResponseDatum.getSubs().size(); i++) {
                if (categoriesResponseDatum.getSubs().get(i).getName() != null) {
                  subCategoryNames.add(categoriesResponseDatum.getSubs().get(i).getName());
                }
              }

              subCategoryPicker.setUpDropDown(getString(R.string.sub_category_picker_title),
                  getString(R.string.sub_category_picker_message),
                  subCategoryNames.toArray(new String[subCategoryNames.size()]),
                  (index1, selected1) -> {
                    for (int i = 0; i < categoriesResponseDatum.getSubs().size(); i++) {
                      if (categoriesResponseDatum.getSubs().get(i).getName() != null
                          && categoriesResponseDatum.getSubs().get(i).getName().equals(selected1)) {
                        subcategory = categoriesResponseDatum.getSubs().get(i).getId();
                      }
                    }
                  });
            }
          }
        });
  }

  @OnClick(R.id.submitInquiryBTN) public void handleSubmitInquiry() {
    ArrayList<String> errorMessages = new ArrayList<>();

    if (category.isEmpty()) {
      errorMessages.add(getString(R.string.error_message_category_required));
    }
    if (subcategory.isEmpty()) {
      errorMessages.add(getString(R.string.error_message_subcategory_required));
    }
    if (requestTitleET.getText().toString().isEmpty()) {
      errorMessages.add(getString(R.string.error_message_title_description_required));
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
      startUploadImages(0);
    }
  }

  private void hookupImagesPickerImages(CustomAttachmentPicker imagePicker) {
    LinearLayout parentLayout = (LinearLayout) imagePicker.getChildAt(0);
    final LinearLayout innerLayoutWithPickers = (LinearLayout) parentLayout.getChildAt(1);

    for (int i = 0; i < innerLayoutWithPickers.getChildCount(); i++) {
      if (innerLayoutWithPickers.getChildAt(i) instanceof ImageView) {
        final int finalI = i;
        innerLayoutWithPickers.getChildAt(i).setOnClickListener(view -> {
          ImageView imageView = innerLayoutWithPickers.findViewById(
              innerLayoutWithPickers.getChildAt(finalI).getId());
          showImagePickerDialog(imageView);
        });
      }
    }
  }

  private void startUploadImages(int imageFilesIndex) {
    if (!imagePaths.isEmpty() && imagePaths.get(imageFilesIndex) != null) {
      HashMap<String, RequestBody> insuranceImages = new HashMap<>();

      File imageFile = new File(imagePaths.get(imageFilesIndex));

      RequestBody imageFilePart = RequestBody.create(MediaType.parse("image/*"), imageFile);

      insuranceImages.put("file\"; filename=\"" + imagePaths.get(imageFilesIndex), imageFilePart);

      presenter.uploadInsuranceImage(insuranceImages);
    } else {
      startSubmitInquiryComplaint(null);
    }
  }

  private void showImagePickerDialog(ImageView view) {
    currentImageView = view;

    AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
    builderSingle.setTitle(getString(R.string.attach_picture_dialog_title));

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
}
