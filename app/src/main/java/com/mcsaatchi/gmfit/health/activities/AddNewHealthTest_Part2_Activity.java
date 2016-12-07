package com.mcsaatchi.gmfit.health.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.andreabaccega.widget.FormEditText;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.otto.EventBus_Poster;
import com.mcsaatchi.gmfit.architecture.otto.EventBus_Singleton;
import com.mcsaatchi.gmfit.architecture.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.architecture.rest.MedicalTestMetricsResponseBody;
import com.mcsaatchi.gmfit.architecture.rest.TakenMedicalTestsResponseBody;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.Base_Activity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.joda.time.DateTime;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.LocalDate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class AddNewHealthTest_Part2_Activity extends Base_Activity
    implements CalendarDatePickerDialogFragment.OnDateSetListener {
  private static final int ASK_CAMERA_AND_STORAGE_PERMISSION = 834;
  private static final int REQUEST_PICK_IMAGE_GALLERY = 329;
  private static final int CAPTURE_NEW_PICTURE_REQUEST_CODE = 871;
  private static final String ACTIVITY_TAG_TIME_PICKER = "activity_tag_time_picker";
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.dateTakenTV) TextView dateTakenTV;
  @Bind(R.id.testNameET) FormEditText testNameET;
  @Bind(R.id.addPic1) ImageView addPic1;
  @Bind(R.id.addPic2) ImageView addPic2;
  @Bind(R.id.addPic3) ImageView addPic3;
  @Bind(R.id.addPic4) ImageView addPic4;
  @Bind(R.id.addPic5) ImageView addPic5;

  @Bind(R.id.deletePic1) ImageView deletePic1;
  @Bind(R.id.deletePic2) ImageView deletePic2;
  @Bind(R.id.deletePic3) ImageView deletePic3;
  @Bind(R.id.deletePic4) ImageView deletePic4;
  @Bind(R.id.deletePic5) ImageView deletePic5;

  private ArrayList<Integer> deletedImages = new ArrayList<>();

  private ArrayList<String> picturePaths = new ArrayList<String>() {{
    add(null);
    add(null);
    add(null);
    add(null);
    add(null);
  }};

  private ArrayList<ImageView> imageViewElements;
  private ArrayList<ImageView> deleteButtonElements;

  private File photoFile;
  private Uri photoFileUri;
  private String dateTakenForRequest;
  private ProgressDialog waitingDialog;
  private AlertDialog alertDialog;
  private int viewIdForTestPicture;
  private ArrayList<FormEditText> allFields = new ArrayList<>();

  private ArrayList<MedicalTestMetricsResponseBody> testicularMetrics = new ArrayList<>();
  private TakenMedicalTestsResponseBody existingMedicaltest;

  public static RequestBody toRequestBody(String value) {
    return RequestBody.create(MediaType.parse("text/plain"), value);
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_add_new_test_part_2);

    ButterKnife.bind(this);

    EventBus_Singleton.getInstance().register(this);

    setupToolbar(toolbar, getResources().getString(R.string.add_new_test_activity_title), true);

    allFields.add(testNameET);

    imageViewElements = new ArrayList<ImageView>() {{
      add(addPic1);
      add(addPic2);
      add(addPic3);
      add(addPic4);
      add(addPic5);
    }};

    deleteButtonElements = new ArrayList<ImageView>() {{
      add(deletePic1);
      add(deletePic2);
      add(deletePic3);
      add(deletePic4);
      add(deletePic5);
    }};

    /**
     * Figure out which objects are available, from that figure out if this is for editing or creating
     */
    if (getIntent().getExtras() != null) {
      int day = 0, month = 0, year = 0;

      final LocalDate dt = new LocalDate();

      testicularMetrics =
          getIntent().getExtras().getParcelableArrayList(Constants.TESTICULAR_METRICS_ARRAY);

      existingMedicaltest =
          getIntent().getExtras().getParcelable(Constants.EXTRAS_TEST_OBJECT_DETAILS);

      try {
        String dateTakenForDisplay = null;

        if (testicularMetrics != null) {
          /**
           * Metrics from Server were available, we're creating here.
           */
          day = dt.getDayOfMonth();
          month = dt.getMonthOfYear();
          year = dt.getYear();

          dateTakenForDisplay = new SimpleDateFormat("MMMM dd, yyyy").format(
              new SimpleDateFormat("yyyyMMdd").parse(
                  dt.getYear() + "" + dt.getMonthOfYear() + "" + dt.getDayOfMonth()));

          dateTakenForRequest = year + "-" + month + "-" + day;
        } else if (existingMedicaltest != null) {
          /**
           * Existing medical test was passed, we're editing here.
           */
          testNameET.setText(existingMedicaltest.getName());
          testNameET.setSelection(existingMedicaltest.getName().length());

          for (int i = 0; i < existingMedicaltest.getImages().size(); i++) {
            if (existingMedicaltest.getImages().get(i) != null
                && imageViewElements.get(i) != null) {
              Picasso.with(this)
                  .load(existingMedicaltest.getImages().get(i).getImage())
                  .into(imageViewElements.get(i));
              deleteButtonElements.get(i).setVisibility(View.VISIBLE);
            }
          }

          try {
            DateTime entryDate = new DateTime(existingMedicaltest.getDateTaken());

            dateTakenForDisplay = entryDate.monthOfYear().getAsText()
                + " "
                + entryDate.getDayOfMonth()
                + ", "
                + entryDate.getYear();

            day = entryDate.getDayOfMonth();
            month = entryDate.getMonthOfYear();
            year = entryDate.getYear();

            dateTakenForRequest = year + "-" + month + "-" + day;
          } catch (IllegalFieldValueException e) {
            Timber.d("Date taken for this test was returned as 0000-00-00");
          }
        }

        dateTakenTV.setText(dateTakenForDisplay);
      } catch (ParseException e) {
        e.printStackTrace();
      }

      final int finalYear = year;
      final int finalMonth = month;
      final int finalDay = day;

      dateTakenTV.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          CalendarDatePickerDialogFragment cdp =
              new CalendarDatePickerDialogFragment().setOnDateSetListener(
                  AddNewHealthTest_Part2_Activity.this)
                  .setFirstDayOfWeek(Calendar.MONDAY)
                  .setDoneText(getString(R.string.accept_ok))
                  .setCancelText(getString(R.string.decline_cancel))
                  .setPreselectedDate(finalYear, finalMonth - 1, finalDay)
                  .setThemeLight();
          cdp.show(getSupportFragmentManager(), ACTIVITY_TAG_TIME_PICKER);
        }
      });

      for (int i = 0; i < deleteButtonElements.size(); i++) {
        final int finalI = i;
        deleteButtonElements.get(i).setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View view) {
            imageViewElements.get(finalI).setImageResource(R.drawable.ic_insert_photo_black_24dp);

            deleteButtonElements.get(finalI).setVisibility(View.GONE);

            picturePaths.set(finalI, null);

            if (existingMedicaltest != null
                && existingMedicaltest.getImages().size() > 0
                && existingMedicaltest.getImages().get(finalI) != null) {
              deletedImages.add(existingMedicaltest.getImages().get(finalI).getId());
            }

            for (int i = 0; i < deletedImages.size(); i++) {
              Timber.d("Deleted images: " + deletedImages.get(i));
            }
          }
        });
      }
    }
  }

  @Override
  public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear,
      int dayOfMonth) {
    dateTakenTV.setText(
        new DateFormatSymbols().getMonths()[monthOfYear] + " " + dayOfMonth + ", " + year);

    try {
      dateTakenForRequest = new SimpleDateFormat("yyyy-MM-dd").format(
          new SimpleDateFormat("MMMM dd, yyyy").parse(dateTakenTV.getText().toString()));
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.add_new_health_test_part_2, menu);

    return super.onCreateOptionsMenu(menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.doneBTN:
        if (Helpers.validateFields(allFields)) {
          HashMap<String, RequestBody> metrics, imageParts;
          String deletedImages;

          waitingDialog = new ProgressDialog(this);
          waitingDialog.setMessage(getString(R.string.please_wait_dialog_message));

          alertDialog = new AlertDialog.Builder(this).create();
          alertDialog.setTitle(R.string.fetching_test_data_dialog_title);
          alertDialog.setButton(android.app.AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
              new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();

                  if (waitingDialog.isShowing()) waitingDialog.dismiss();
                }
              });

          waitingDialog.setTitle(R.string.creating_new_test_dialog_title);

          if (existingMedicaltest == null) {
            metrics = constructNewMetricsForRequest();

            if (metrics.isEmpty()) {
              Toast.makeText(this, "Please fill in the needed fields to proceed",
                  Toast.LENGTH_SHORT).show();
            } else {
              waitingDialog.show();

              imageParts = constructSelectedImagesForRequest();

              createNewHealthTestRequest(metrics, imageParts);
            }
          } else {
            metrics = constructEditableMetricsForRequest();

            deletedImages = constructDeletedImagesForRequest();

            imageParts = constructSelectedImagesForRequest();

            createEditedHealthRequest(metrics, imageParts, toRequestBody(deletedImages));
          }
        }
        break;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    switch (requestCode) {
      case CAPTURE_NEW_PICTURE_REQUEST_CODE:
        if (photoFile.getTotalSpace() > 0) {
          addTestPicture(photoFile.getAbsolutePath());
        } else {
          Timber.d("No picture was taken, photoFile size : %s", photoFile.getTotalSpace());
        }

        break;
      case REQUEST_PICK_IMAGE_GALLERY:
        if (data != null) {
          Uri selectedImageUri = data.getData();
          String selectedImagePath = getPhotoPathFromGallery(selectedImageUri);

          addTestPicture(selectedImagePath);
        }
    }
  }

  private void addTestPicture(String finalImagePath) {
    ImageView finalViewForPicture = null;

    switch (viewIdForTestPicture) {
      case R.id.addPic1:
        finalViewForPicture = (ImageView) findViewById(R.id.addPic1);
        picturePaths.set(0, finalImagePath);
        deleteButtonElements.get(0).setVisibility(View.VISIBLE);
        break;
      case R.id.addPic2:
        finalViewForPicture = (ImageView) findViewById(R.id.addPic2);
        picturePaths.set(1, finalImagePath);
        deleteButtonElements.get(1).setVisibility(View.VISIBLE);
        break;
      case R.id.addPic3:
        finalViewForPicture = (ImageView) findViewById(R.id.addPic3);
        picturePaths.set(2, finalImagePath);
        deleteButtonElements.get(2).setVisibility(View.VISIBLE);
        break;
      case R.id.addPic4:
        finalViewForPicture = (ImageView) findViewById(R.id.addPic4);
        picturePaths.set(3, finalImagePath);
        deleteButtonElements.get(3).setVisibility(View.VISIBLE);
        break;
      case R.id.addPic5:
        finalViewForPicture = (ImageView) findViewById(R.id.addPic5);
        picturePaths.set(4, finalImagePath);
        deleteButtonElements.get(4).setVisibility(View.VISIBLE);
        break;
    }

    Picasso.with(this)
        .load(new File(finalImagePath))
        .resize(500, 500)
        .centerInside()
        .into(finalViewForPicture);
  }

  @RequiresApi(api = Build.VERSION_CODES.M) public void triggerAddPicture(View view) {
    String[] neededPermissions = new String[] {
        Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    boolean hasCameraPermission =
        (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED);

    boolean hasWriteStoragePermission =
        (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED);

    viewIdForTestPicture = view.getId();

    if (!hasCameraPermission || !hasWriteStoragePermission) {
      requestPermissions(neededPermissions, ASK_CAMERA_AND_STORAGE_PERMISSION);
    } else {
      showImagePickerDialog();
    }
  }

  @Override public void onRequestPermissionsResult(int requestCode, String[] permissions,
      int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    switch (requestCode) {
      case ASK_CAMERA_AND_STORAGE_PERMISSION:
        if (grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
            && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
          showImagePickerDialog();
        } else {
          Toast.makeText(this,
              "The app was not allowed to write to your storage or take use the device's Camera. Hence, it cannot function properly."
                  + "Please consider granting it these permissions", Toast.LENGTH_LONG).show();
        }
        break;
    }
  }

  private void showImagePickerDialog() {
    AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
    builderSingle.setTitle("Choose a picture");

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
            /**
             * Can't case by String ID because it has to be constant, bleh
             */
            case "Choose from gallery":
              Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                  android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
              startActivityForResult(galleryIntent, REQUEST_PICK_IMAGE_GALLERY);
              break;
            case "Take a new picture":
              openTakePictureIntent();
              break;
          }
        }
      }
    });
    builderSingle.show();
  }

  public void openTakePictureIntent() {
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
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
  }

  private File createImageFile(String imagePath) throws IOException {
    return new File(imagePath);
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

  public String getPhotoPathFromGallery(Uri uri) {
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

  private HashMap<String, RequestBody> constructNewMetricsForRequest() {
    HashMap<String, RequestBody> metrics = new HashMap<>();

    for (int i = 0; i < testicularMetrics.size(); i++) {
      if (testicularMetrics.get(i).getValue() != null) {

        metrics.put("metrics[" + i + "][id]",
            toRequestBody(String.valueOf(testicularMetrics.get(i).getId())));

        metrics.put("metrics[" + i + "][value]",
            toRequestBody(testicularMetrics.get(i).getValue()));

        for (int j = 0; j < testicularMetrics.get(i).getUnits().size(); j++) {
          if (testicularMetrics.get(j).getUnits().get(j).isSelected()) {
            metrics.put("metrics[" + i + "][unit_id]",
                toRequestBody(String.valueOf(testicularMetrics.get(j).getUnits().get(j).getId())));
          }
        }
      }
    }

    return metrics;
  }

  private HashMap<String, RequestBody> constructEditableMetricsForRequest() {
    HashMap<String, RequestBody> metrics = new HashMap<>();

    for (int i = 0; i < existingMedicaltest.getMetrics().size(); i++) {
      if (Integer.parseInt(existingMedicaltest.getMetrics().get(i).getValue()) != -1) {

        metrics.put("metrics[" + i + "][id]",
            toRequestBody(String.valueOf(existingMedicaltest.getMetrics().get(i).getId())));

        metrics.put("metrics[" + i + "][value]",
            toRequestBody(existingMedicaltest.getMetrics().get(i).getValue()));

        for (int j = 0; j < existingMedicaltest.getMetrics().get(i).getUnits().size(); j++) {
          if (existingMedicaltest.getMetrics().get(j).getUnits().get(j).getSelected()) {
            metrics.put("metrics[" + i + "][unit_id]", toRequestBody(
                String.valueOf(existingMedicaltest.getMetrics().get(j).getUnits().get(j).getId())));
          }
        }
      }
    }

    return metrics;
  }

  private void createNewHealthTestRequest(HashMap<String, RequestBody> metrics,
      HashMap<String, RequestBody> imageParts) {
    dataAccessHandler.storeNewHealthTest(
        prefs.getString(Constants.PREF_USER_ACCESS_TOKEN, Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS),
        toRequestBody(testNameET.getText().toString()), toRequestBody(dateTakenForRequest), metrics,
        imageParts, new Callback<DefaultGetResponse>() {
          @Override public void onResponse(Call<DefaultGetResponse> call,
              Response<DefaultGetResponse> response) {
            switch (response.code()) {
              case 200:

                Log.d("TAG", "onResponse: Succeeded creating new test");

                waitingDialog.dismiss();

                hideKeyboard();

                EventBus_Singleton.getInstance()
                    .post(new EventBus_Poster(Constants.EXTRAS_TEST_EDIT_OR_CREATE_DONE));

                finish();

                break;
            }
          }

          @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            alertDialog.setMessage(
                getResources().getString(R.string.error_response_from_server_incorrect));
            alertDialog.show();
          }
        });
  }

  private void createEditedHealthRequest(HashMap<String, RequestBody> metrics,
      HashMap<String, RequestBody> imageParts, RequestBody deletedImagesForRequest) {
    dataAccessHandler.editExistingHealthTest(
        prefs.getString(Constants.PREF_USER_ACCESS_TOKEN, Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS),
        toRequestBody(String.valueOf(existingMedicaltest.getInstanceId())),
        toRequestBody(existingMedicaltest.getName()), toRequestBody(dateTakenForRequest), metrics,
        imageParts, deletedImagesForRequest, new Callback<DefaultGetResponse>() {
          @Override public void onResponse(Call<DefaultGetResponse> call,
              Response<DefaultGetResponse> response) {
            switch (response.code()) {
              case 200:

                Log.d("TAG", "onResponse: Succeeded creating new test");

                waitingDialog.dismiss();

                hideKeyboard();

                EventBus_Singleton.getInstance()
                    .post(new EventBus_Poster(Constants.EXTRAS_TEST_EDIT_OR_CREATE_DONE));

                finish();

                break;
            }
          }

          @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
            Timber.d("Call failed with error : %s", t.getMessage());
            alertDialog.setMessage(
                getResources().getString(R.string.error_response_from_server_incorrect));
            alertDialog.show();
          }
        });
  }

  private HashMap<String, RequestBody> constructSelectedImagesForRequest() {
    HashMap<String, RequestBody> imageParts = new HashMap<>();

    RequestBody file;
    File imageFile;

    for (int i = 0; i < imageViewElements.size(); i++) {
      if (picturePaths.get(i) != null) {
        imageFile = new File(picturePaths.get(i));
        file = RequestBody.create(MediaType.parse("image/jpeg"), imageFile);
        imageParts.put("images[" + 0 + "]\"; filename=\"" + picturePaths.get(i), file);
      }
    }

    return imageParts;
  }

  private String constructDeletedImagesForRequest() {
    String deletedImagesString = "";

    for (int i = 0; i < deletedImages.size(); i++) {
      deletedImagesString += deletedImages.get(i) + ",";
    }

    return deletedImagesString;
  }

  private void hideKeyboard() {
    View view = getCurrentFocus();
    if (view != null) {
      InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
  }
}
