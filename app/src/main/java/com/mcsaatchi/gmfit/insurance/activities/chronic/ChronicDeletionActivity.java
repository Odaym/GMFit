package com.mcsaatchi.gmfit.insurance.activities.chronic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ChronicTreatmentListInnerData;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.ImageHandler;
import com.mcsaatchi.gmfit.insurance.widget.CustomAttachmentPicker;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import timber.log.Timber;

import static com.mcsaatchi.gmfit.insurance.widget.CustomAttachmentPicker.CAPTURE_NEW_PICTURE_REQUEST_CODE;
import static com.mcsaatchi.gmfit.insurance.widget.CustomAttachmentPicker.REQUEST_PICK_IMAGE_GALLERY;

public class ChronicDeletionActivity extends BaseActivity
    implements ChronicDeletionActivityPresenter.ChronicDeletionActivityView {

  @Bind(R.id.doctorConfirmationImagePicker) CustomAttachmentPicker doctorConfirmationImagePicker;
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.passwordET) EditText passwordET;

  private ChronicDeletionActivityPresenter presenter;

  private ChronicTreatmentListInnerData chronicObject;

  private File photoFile;
  private Uri photoFileUri;
  private ImageView currentImageView;
  private ArrayList<String> imagePaths = new ArrayList<>();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chronic_request_deletion);
    ButterKnife.bind(this);
    setupToolbar(getClass().getSimpleName(), toolbar, "Request Deletion", true);

    presenter = new ChronicDeletionActivityPresenter(this, dataAccessHandler);

    if (getIntent().getExtras() != null) {
      chronicObject = getIntent().getExtras().getParcelable("CHRONIC_OBJECT");
    }

    hookupImagesPickerImages(doctorConfirmationImagePicker);
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

  @OnClick(R.id.submitDeletionRequestBTN) public void handleSubmitDeletionRequest() {
    if (passwordET.getText().toString().isEmpty()) {
      final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
      alertDialog.setMessage(getResources().getString(R.string.password_empty_error));
      alertDialog.show();
    } else if (!prefs.getString(Constants.EXTRAS_INSURANCE_USER_PASSWORD, "")
        .equals(passwordET.getText().toString())) {
      final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
      alertDialog.setMessage(getResources().getString(R.string.password_mismatch_chronic_deletion));
      alertDialog.show();
    } else {
      presenter.deleteChronicTreatment(
          prefs.getString(Constants.EXTRAS_INSURANCE_CONTRACT_NUMBER, ""),
          chronicObject.getRequestNbr(), "4");
    }
  }

  private void showImagePickerDialog(ImageView view) {
    currentImageView = view;

    AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
    builderSingle.setTitle(R.string.attach_picture_dialog_title);

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

  @Override public void successfullyDeletedChronicTreatment() {
    Toast.makeText(this, R.string.chronic_treatment_deleted_successfully, Toast.LENGTH_SHORT)
        .show();
    finish();
  }
}