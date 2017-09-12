package com.mcsaatchi.gmfit.insurance.activities.inquiry;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.andreabaccega.widget.FormEditText;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.CRMNotesResponseNoteAttribute;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.InquiriesListResponseInnerData;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.common.classes.ImageHandler;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

import static com.mcsaatchi.gmfit.insurance.widget.CustomAttachmentPicker.CAPTURE_NEW_PICTURE_REQUEST_CODE;
import static com.mcsaatchi.gmfit.insurance.widget.CustomAttachmentPicker.REQUEST_PICK_IMAGE_GALLERY;

public class InquiryNotesActivity extends BaseActivity
    implements InquiryNotesActivityPresenter.InquiryNotesActivityView {
  private static final int REQUEST_CAPTURE_PERMISSIONS = 123;

  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.mainNotesLayout) LinearLayout mainNotesLayout;
  @Bind(R.id.mainScrollView) ScrollView mainScrollView;
  @Bind(R.id.attachImageIV) ImageView attachImageIV;
  @Bind(R.id.imagePlaceHolderIV) ImageView imagePlaceHolderIV;
  @Bind(R.id.yourReplyET) FormEditText yourReplyET;

  private ProgressDialog waitingDialog;
  private String imageAttachment = null;
  private String mimeType = null;
  private String documentBody = null;

  private ArrayList<FormEditText> allFields = new ArrayList<>();
  private InquiryNotesActivityPresenter presenter;

  private File photoFile;
  private Uri photoFileUri;
  private InquiriesListResponseInnerData inquiryItem;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_inquiry_details_notes);

    ButterKnife.bind(this);

    allFields.add(yourReplyET);

    presenter = new InquiryNotesActivityPresenter(this, dataAccessHandler);

    if (getIntent().getExtras() != null) {
      inquiryItem = getIntent().getExtras().getParcelable("INQUIRY_OBJECT");

      if (inquiryItem != null) {
        setupToolbar(getClass().getSimpleName(), toolbar, inquiryItem.getTitle(), true);

        presenter.getCRMIncidentNotes(inquiryItem.getIncidentId(),
            prefs.getString(Constants.EXTRAS_INSURANCE_COUNTRY_CRM_CODE, ""));
      }
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

        imagePlaceHolderIV.setVisibility(View.VISIBLE);

        Picasso.with(this)
            .load(new File(photoFile.getAbsolutePath()))
            .fit()
            .into(imagePlaceHolderIV);

        break;
      case REQUEST_PICK_IMAGE_GALLERY:
        if (data != null) {
          Uri selectedImageUri = data.getData();
          String selectedImagePath = ImageHandler.getPhotoPathFromGallery(this, selectedImageUri);

          imagePlaceHolderIV.setVisibility(View.VISIBLE);

          imageAttachment = selectedImagePath;

          Picasso.with(this).load(new File(selectedImagePath)).fit().into(imagePlaceHolderIV);
        }
    }
  }

  @Override
  public void displayCRMIncidentNotes(List<CRMNotesResponseNoteAttribute> noteAttributesList) {
    for (int i = 0; i < noteAttributesList.size(); i++) {
      LayoutInflater inflater = getLayoutInflater();

      View noteView = inflater.inflate(R.layout.individual_crm_note, null);

      TextView senderNameTV = noteView.findViewById(R.id.senderNameTV);
      TextView sentDateTV = noteView.findViewById(R.id.sentDateTV);
      TextView messageContentTV = noteView.findViewById(R.id.messageContentTV);
      ImageView messageImageIV = noteView.findViewById(R.id.messageImageIV);

      if (noteAttributesList.get(i).getCreatedBy() != null) {
        senderNameTV.setText(noteAttributesList.get(i).getCreatedBy());
      }

      if (noteAttributesList.get(i).getNoteText() != null) {
        messageContentTV.setText(noteAttributesList.get(i).getNoteText());
      }

      if (noteAttributesList.get(i).getDocumentBody() != null) {
        messageImageIV.setImageBitmap(
            ImageHandler.turnBase64ToImage(noteAttributesList.get(i).getDocumentBody()));
      }

      LinearLayout.LayoutParams params =
          new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
              LinearLayout.LayoutParams.WRAP_CONTENT);

      params.setMargins(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.default_margin_3));
      noteView.setLayoutParams(params);

      mainNotesLayout.addView(noteView);
    }

    final Handler handler = new Handler();

    handler.postDelayed(() -> mainScrollView.fullScroll(View.FOCUS_DOWN), 500);
  }

  @Override public void dismissWaitingDialog() {
    waitingDialog.dismiss();
  }

  @Override public void clearViews() {
    mainNotesLayout.removeAllViews();
    yourReplyET.setText("");
    hideImagePlaceHolder();
  }

  @OnClick(R.id.sendMessageIV) public void handleSendMessage() {
    if (Helpers.validateFields(allFields)) {
      if (imageAttachment != null) {
        documentBody = Base64.encodeToString(ImageHandler.turnImageToByteArray(imageAttachment),
            Base64.NO_WRAP);
      }

      addCRMNote(inquiryItem.getIncidentId(), null, yourReplyET.getText().toString(), "image/jpeg",
          imageAttachment, documentBody,
          prefs.getString(Constants.EXTRAS_INSURANCE_COUNTRY_CRM_CODE, ""));
    }
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

  @OnClick(R.id.imagePlaceHolderIV) public void handlePlaceHolderClicked() {
    showImagePickerDialog();
  }

  private void showImagePickerDialog() {
    AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
    builderSingle.setTitle("Attach a picture");

    final ArrayAdapter<String> arrayAdapter =
        new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
    arrayAdapter.add(getResources().getString(R.string.choose_picture_from_gallery));
    arrayAdapter.add(getResources().getString(R.string.take_new_picture));
    arrayAdapter.add(getResources().getString(R.string.remove_picture_chosen));

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
          case "Remove picture":
            hideImagePlaceHolder();
            break;
        }
      }
    });
    builderSingle.show();
  }

  private void addCRMNote(String incidentId, String subject, String noteText, String mimeType,
      String fileName, String documentBody, String dbCountry) {
    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle(R.string.submitting_data_dialog_title);

    waitingDialog = new ProgressDialog(this);
    waitingDialog.setTitle(getResources().getString(R.string.submitting_data_dialog_title));
    waitingDialog.setMessage(getResources().getString(R.string.please_wait_dialog_message));

    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.ok),
        (dialog, which) -> {
          dialog.dismiss();

          if (waitingDialog.isShowing()) waitingDialog.dismiss();
        });

    waitingDialog.setOnShowListener(
        dialogInterface -> presenter.addCRMNote(incidentId, subject, noteText, mimeType, fileName,
            documentBody, dbCountry));

    waitingDialog.show();
  }

  private void hideImagePlaceHolder() {
    imageAttachment = null;
    imagePlaceHolderIV.setImageResource(0);
    imagePlaceHolderIV.setVisibility(View.GONE);
  }
}
