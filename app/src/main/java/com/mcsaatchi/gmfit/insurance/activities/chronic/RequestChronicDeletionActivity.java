package com.mcsaatchi.gmfit.insurance.activities.chronic;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.insurance.widget.CustomAttachmentPicker;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import timber.log.Timber;

import static com.mcsaatchi.gmfit.insurance.widget.CustomAttachmentPicker.CAPTURE_NEW_PICTURE_REQUEST_CODE;
import static com.mcsaatchi.gmfit.insurance.widget.CustomAttachmentPicker.REQUEST_PICK_IMAGE_GALLERY;

public class RequestChronicDeletionActivity extends BaseActivity {

  @Bind(R.id.doctorConfirmationImagePicker) CustomAttachmentPicker doctorConfirmationImagePicker;
  @Bind(R.id.toolbar) Toolbar toolbar;

  private File photoFile;
  private Uri photoFileUri;
  private ImageView currentImageView;
  private ArrayList<String> imagePaths = new ArrayList<>();

  public static RequestBody toRequestBody(String value) {
    return RequestBody.create(MediaType.parse("text/plain"), value);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chronic_request_deletion);
    ButterKnife.bind(this);
    setupToolbar(getClass().getSimpleName(), toolbar, "Request Deletion", true);

    hookupImagesPickerImages(doctorConfirmationImagePicker);
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

  @OnClick(R.id.submitDeletionRequestBTN) public void handleSubmitDeletionRequest() {
    Toast.makeText(this, "Deletion request button not hooked up yet!", Toast.LENGTH_SHORT).show();
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
}