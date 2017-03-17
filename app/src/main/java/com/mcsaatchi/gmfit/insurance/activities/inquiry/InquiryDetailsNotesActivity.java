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
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.rest.AddCRMNoteResponse;
import com.mcsaatchi.gmfit.architecture.rest.CRMNotesResponse;
import com.mcsaatchi.gmfit.architecture.rest.CRMNotesResponseNoteAttribute;
import com.mcsaatchi.gmfit.architecture.rest.InquiriesListResponseInnerData;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import java.io.ByteArrayOutputStream;
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

public class InquiryDetailsNotesActivity extends BaseActivity {
  private static final int REQUEST_CAPTURE_PERMISSIONS = 123;

  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.mainNotesLayout) LinearLayout mainNotesLayout;
  @Bind(R.id.mainScrollView) ScrollView mainScrollView;
  @Bind(R.id.attachImageIV) ImageView attachImageIV;
  @Bind(R.id.yourReplyET) EditText yourReplyET;

  private String imageAttachment = null;
  private String mimeType = null;

  private File photoFile;
  private Uri photoFileUri;
  private InquiriesListResponseInnerData inquiryItem;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_inquiry_details_notes);

    ButterKnife.bind(this);

    if (getIntent().getExtras() != null) {
      inquiryItem = getIntent().getExtras().getParcelable("INQUIRY_OBJECT");

      if (inquiryItem != null) {
        setupToolbar(getClass().getSimpleName(), toolbar, inquiryItem.getTitle(), true);

        getCRMIncidentNotes(inquiryItem.getIncidentId());
      }
    }
  }

  @OnClick(R.id.sendMessageIV) public void handleSendMessage() {
    addCRMNote(inquiryItem.getIncidentId(), null, yourReplyET.getText().toString(), null, null,
        null);
  }

  @OnClick(R.id.attachImageIV) public void handleAttachImage() {
    if (permChecker.lacksPermissions(Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
      ActivityCompat.requestPermissions(this,
          new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE },
          REQUEST_CAPTURE_PERMISSIONS);
    } else {
      showImagePickerDialog();
    }
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.inquiry_details, menu);

    return super.onCreateOptionsMenu(menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.inquiryMoreDetailsBTN:
        Intent intent = new Intent(this, InquiryDetailsActivity.class);
        intent.putExtra("INQUIRY_OBJECT", inquiryItem);
        startActivity(intent);
        break;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    switch (requestCode) {
      case CAPTURE_NEW_PICTURE_REQUEST_CODE:
        if (photoFile.getTotalSpace() > 0) {
          imageAttachment = photoFile.getAbsolutePath();
        } else {
          Timber.d("No picture was taken, photoFile size : %d", photoFile.getTotalSpace());
        }

        break;
      case REQUEST_PICK_IMAGE_GALLERY:
        if (data != null) {
          Uri selectedImageUri = data.getData();
          String selectedImagePath = getPhotoPathFromGallery(selectedImageUri);

          imageAttachment = selectedImagePath;
        }
    }

    if (imageAttachment != null) {
      addCRMNote(inquiryItem.getIncidentId(), null, yourReplyET.getText().toString(), "image/jpeg",
          imageAttachment,
          Base64.encodeToString(turnImageToByteArray(imageAttachment), Base64.NO_WRAP));
    }
  }

  private void showImagePickerDialog() {
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

  private void addCRMNote(String incidentId, String subject, String noteText, String mimeType,
      String fileName, String documentBody) {
    final ProgressDialog waitingDialog = new ProgressDialog(this);
    waitingDialog.setTitle(getResources().getString(R.string.submitting_data_dialog_title));
    waitingDialog.setMessage(getResources().getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle(R.string.submitting_data_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.ok),
        (dialog, which) -> {
          dialog.dismiss();

          if (waitingDialog.isShowing()) waitingDialog.dismiss();
        });

    dataAccessHandler.addCRMNote(incidentId, subject, noteText, mimeType, fileName, documentBody,
        new Callback<AddCRMNoteResponse>() {

          @Override public void onResponse(Call<AddCRMNoteResponse> call,
              Response<AddCRMNoteResponse> response) {

            switch (response.code()) {
              case 200:
                mainNotesLayout.removeAllViews();

                getCRMIncidentNotes(inquiryItem.getIncidentId());

                yourReplyET.setText("");

                break;
              case 449:
                alertDialog.setMessage(Helpers.provideErrorStringFromJSON(response.errorBody()));
                alertDialog.show();
                break;
            }

            waitingDialog.dismiss();
          }

          @Override public void onFailure(Call<AddCRMNoteResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            alertDialog.setMessage(getString(R.string.server_error_got_returned));
            alertDialog.show();
          }
        });
  }

  private void getCRMIncidentNotes(String incidentId) {
    final ProgressDialog waitingDialog = new ProgressDialog(this);
    waitingDialog.setTitle(getResources().getString(R.string.loading_data_dialog_title));
    waitingDialog.setMessage(getResources().getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle(R.string.loading_data_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.ok),
        (dialog, which) -> {
          dialog.dismiss();

          if (waitingDialog.isShowing()) waitingDialog.dismiss();
        });

    dataAccessHandler.getCRMIncidentNotes(incidentId, new Callback<CRMNotesResponse>() {

      @Override
      public void onResponse(Call<CRMNotesResponse> call, Response<CRMNotesResponse> response) {

        switch (response.code()) {
          case 200:
            List<CRMNotesResponseNoteAttribute> noteAttributesList =
                response.body().getData().getBody().getData().getNoteAttributesLst();

            for (int i = 0; i < noteAttributesList.size(); i++) {
              LayoutInflater inflater = getLayoutInflater();

              View noteView = inflater.inflate(R.layout.individual_crm_note, null);

              TextView senderNameTV = (TextView) noteView.findViewById(R.id.senderNameTV);
              TextView sentDateTV = (TextView) noteView.findViewById(R.id.sentDateTV);
              TextView messageContentTV = (TextView) noteView.findViewById(R.id.messageContentTV);
              ImageView messageImageIV = (ImageView) noteView.findViewById(R.id.messageImageIV);

              if (noteAttributesList.get(i).getCreatedBy() != null) {
                senderNameTV.setText(noteAttributesList.get(i).getCreatedBy());
              }

              if (noteAttributesList.get(i).getNoteText() != null) {
                messageContentTV.setText(noteAttributesList.get(i).getNoteText());
              }

              if (noteAttributesList.get(i).getDocumentBody() != null) {
                messageImageIV.setImageBitmap(
                    turnBase64ToImage(noteAttributesList.get(i).getDocumentBody()));
              }

              LinearLayout.LayoutParams params =
                  new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                      LinearLayout.LayoutParams.WRAP_CONTENT);

              params.setMargins(0, 0, 0,
                  getResources().getDimensionPixelSize(R.dimen.default_margin_3));
              noteView.setLayoutParams(params);

              mainNotesLayout.addView(noteView);
            }

            final Handler handler = new Handler();

            handler.postDelayed(() -> mainScrollView.fullScroll(View.FOCUS_DOWN), 500);

            break;
          case 449:
            alertDialog.setMessage(Helpers.provideErrorStringFromJSON(response.errorBody()));
            alertDialog.show();
            break;
        }

        waitingDialog.dismiss();
      }

      @Override public void onFailure(Call<CRMNotesResponse> call, Throwable t) {
        Timber.d("Call failed with error : %s", t.getMessage());
        alertDialog.setMessage(getString(R.string.server_error_got_returned));
        alertDialog.show();
      }
    });
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

  private byte[] turnImageToByteArray(String imagePath) {
    Bitmap bm = BitmapFactory.decodeFile(imagePath);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);

    return baos.toByteArray();
  }

  private Bitmap turnBase64ToImage(String base64String) {
    byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
    return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
  }
}
