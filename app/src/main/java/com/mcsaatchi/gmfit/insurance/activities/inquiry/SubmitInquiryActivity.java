package com.mcsaatchi.gmfit.insurance.activities.inquiry;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.rest.SubCategoriesResponse;
import com.mcsaatchi.gmfit.architecture.rest.SubCategoriesResponseDatum;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.insurance.widget.CustomAttachmentPicker;
import com.mcsaatchi.gmfit.insurance.widget.CustomPicker;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
  @Bind(R.id.areaPicker) CustomPicker areaPicker;
  @Bind(R.id.medicalReportImagesPicker) CustomAttachmentPicker medicalReportImagesPicker;
  private File photoFile;
  private Uri photoFileUri;
  private ImageView currentImageView;
  private List<SubCategoriesResponseDatum> subCategoriesList;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_inquiry_submit);
    ButterKnife.bind(this);
    setupToolbar(getClass().getSimpleName(), toolbar, "Submit Complaint/Inquiry", true);

    getSubCategories();

    categoryPicker.setUpDropDown("Category", "Choose a category",
        new String[] { "item 1", "item 2", "item 3" }, new CustomPicker.OnDropDownClickListener() {
          @Override public void onClick(int index, String selected) {

          }
        });

    areaPicker.setUpDropDown("Area", "Choose an area",
        new String[] { "item 1", "item 2", "item 3" }, new CustomPicker.OnDropDownClickListener() {
          @Override public void onClick(int index, String selected) {

          }
        });

    if (permChecker.lacksPermissions(Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
      ActivityCompat.requestPermissions(this,
          new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE },
          REQUEST_CAPTURE_PERMISSIONS);
    }

    hookupImagesPickerImages(medicalReportImagesPicker);
  }

  private void hookupImagesPickerImages(CustomAttachmentPicker imagePicker) {
    LinearLayout parentLayout = (LinearLayout) imagePicker.getChildAt(0);
    final LinearLayout innerLayoutWithPickers = (LinearLayout) parentLayout.getChildAt(1);

    for (int i = 0; i < innerLayoutWithPickers.getChildCount(); i++) {
      if (innerLayoutWithPickers.getChildAt(i) instanceof ImageView) {
        final int finalI = i;
        innerLayoutWithPickers.getChildAt(i).setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View view) {
            ImageView imageView = (ImageView) innerLayoutWithPickers.findViewById(
                innerLayoutWithPickers.getChildAt(finalI).getId());
            showImagePickerDialog(imageView);
          }
        });
      }
    }
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    switch (requestCode) {
      case CAPTURE_NEW_PICTURE_REQUEST_CODE:
        if (photoFile.getTotalSpace() > 0) {
          Picasso.with(this)
              .load(new File(photoFile.getAbsolutePath()))
              .resize(getResources().getDimensionPixelSize(R.dimen.attached_images_dimens),
                  getResources().getDimensionPixelSize(R.dimen.attached_images_dimens))
              .centerInside()
              .into(currentImageView);
        } else {
          Timber.d("No picture was taken, photoFile size : %d", photoFile.getTotalSpace());
        }

        break;
      case REQUEST_PICK_IMAGE_GALLERY:
        if (data != null) {
          Uri selectedImageUri = data.getData();
          String selectedImagePath = getPhotoPathFromGallery(selectedImageUri);

          Picasso.with(this)
              .load(new File(selectedImagePath))
              .resize(getResources().getDimensionPixelSize(R.dimen.attached_images_dimens),
                  getResources().getDimensionPixelSize(R.dimen.attached_images_dimens))
              .centerInside()
              .into(currentImageView);
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

    builderSingle.setNegativeButton(R.string.decline_cancel, new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });

    builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialog, int which) {
        String strName = arrayAdapter.getItem(which);
        if (strName != null) {
          switch (strName) {
            case "Choose from gallery":
              Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                  android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
                String[] finalCategoryNames = new String[subCategoriesList.size()];

                for (int i = 0; i < subCategoriesList.size(); i++) {
                  finalCategoryNames[i] = subCategoriesList.get(i).getName();
                }

                subCategoryPicker.setUpDropDown("Subcategory", "Choose a subcategory",
                    finalCategoryNames, new CustomPicker.OnDropDownClickListener() {
                      @Override public void onClick(int index, String selected) {

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
