package com.mcsaatchi.gmfit.health.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.andreabaccega.widget.FormEditText;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.otto.MedicalTestEditCreateEvent;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.TakenMedicalTestsResponseBody;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.common.classes.ImageHandler;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.io.File;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import org.joda.time.DateTime;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.LocalDate;
import timber.log.Timber;

public class AddNewHealthTestActivity extends BaseActivity
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
  @Bind(R.id.pictureHolder1) RelativeLayout pictureHolder1;
  @Bind(R.id.pictureHolder2) RelativeLayout pictureHolder2;
  @Bind(R.id.pictureHolder3) RelativeLayout pictureHolder3;
  @Bind(R.id.pictureHolder4) RelativeLayout pictureHolder4;
  @Bind(R.id.pictureHolder5) RelativeLayout pictureHolder5;
  @Bind(R.id.deletePic1) ImageView deletePic1;
  @Bind(R.id.deletePic2) ImageView deletePic2;
  @Bind(R.id.deletePic3) ImageView deletePic3;
  @Bind(R.id.deletePic4) ImageView deletePic4;
  @Bind(R.id.deletePic5) ImageView deletePic5;

  private ArrayList<RelativeLayout> imageViewElements;
  private ArrayList<ImageView> deleteButtonElements;
  private ArrayList<ImageView> addPictureElements;

  private ArrayList<Integer> deletedImages = new ArrayList<>();

  private ArrayList<String> picturePaths = new ArrayList<String>() {{
    add(null);
    add(null);
    add(null);
    add(null);
    add(null);
  }};

  private File photoFile;
  private Uri photoFileUri;
  private String dateTakenForRequest;
  private int viewIdForTestPicture;

  private ArrayList<FormEditText> allFields = new ArrayList<>();

  private TakenMedicalTestsResponseBody existingMedicaltest;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_add_new_test);

    ButterKnife.bind(this);

    EventBusSingleton.getInstance().register(this);

    setupToolbar(getClass().getSimpleName(), toolbar,
        getResources().getString(R.string.add_new_test_activity_title), true);

    allFields.add(testNameET);

    addPictureElements = new ArrayList<ImageView>() {{
      add(addPic1);
      add(addPic2);
      add(addPic3);
      add(addPic4);
      add(addPic5);
    }};

    imageViewElements = new ArrayList<RelativeLayout>() {{
      add(pictureHolder1);
      add(pictureHolder2);
      add(pictureHolder3);
      add(pictureHolder4);
      add(pictureHolder5);
    }};

    deleteButtonElements = new ArrayList<ImageView>() {{
      add(deletePic1);
      add(deletePic2);
      add(deletePic3);
      add(deletePic4);
      add(deletePic5);
    }};

    if (getIntent().getExtras() != null) {
      existingMedicaltest =
          getIntent().getExtras().getParcelable(Constants.EXTRAS_TEST_OBJECT_DETAILS);

      int day = 0, month = 0, year = 0;

      final LocalDate dt = new LocalDate();

      existingMedicaltest =
          getIntent().getExtras().getParcelable(Constants.EXTRAS_TEST_OBJECT_DETAILS);

      try {
        String dateTakenForDisplay = null;

        if (existingMedicaltest == null) {
          /**
           * Existing medical test doesn't exist, we're creating here.
           */
          day = dt.getDayOfMonth();
          month = dt.getMonthOfYear();
          year = dt.getYear();

          dateTakenForDisplay = new SimpleDateFormat("MMMM dd, yyyy").format(
              new SimpleDateFormat("yyyyMMdd").parse(
                  dt.getYear() + "" + dt.getMonthOfYear() + "" + dt.getDayOfMonth()));

          dateTakenForRequest = year + "-" + month + "-" + day;
        } else {
          /**
           * Existing medical test was passed, we're editing here.
           */
          testNameET.setText(existingMedicaltest.getName());
          testNameET.setSelection(existingMedicaltest.getName().length());

          for (int i = 0; i < existingMedicaltest.getImages().size(); i++) {
            if (existingMedicaltest.getImages().get(i) != null
                && imageViewElements.get(i) != null) {

              final int finalI = i;

              Picasso.with(AddNewHealthTestActivity.this)
                  .load(existingMedicaltest.getImages().get(i).getImage())
                  .resize(400, 400)
                  .centerInside()
                  .into(new Target() {

                    @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                      addPictureElements.get(finalI).setVisibility(View.INVISIBLE);

                      imageViewElements.get(finalI)
                          .setBackground(new BitmapDrawable(getResources(), bitmap));
                    }

                    @Override public void onBitmapFailed(final Drawable errorDrawable) {
                      Log.d("TAG", "FAILED");
                    }

                    @Override public void onPrepareLoad(final Drawable placeHolderDrawable) {
                      Log.d("TAG", "Prepare Load");
                    }
                  });
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

      dateTakenTV.setOnClickListener(view -> {
        CalendarDatePickerDialogFragment cdp =
            new CalendarDatePickerDialogFragment().setOnDateSetListener(
                AddNewHealthTestActivity.this)
                .setFirstDayOfWeek(Calendar.MONDAY)
                .setDoneText(getString(R.string.accept_ok))
                .setCancelText(getString(R.string.decline_cancel))
                .setPreselectedDate(finalYear, finalMonth - 1, finalDay)
                .setThemeLight();
        cdp.show(getSupportFragmentManager(), ACTIVITY_TAG_TIME_PICKER);
      });

      hookupDeletImageButtons();
    }

    dateTakenTV.setOnClickListener(view -> {
      CalendarDatePickerDialogFragment cdp =
          new CalendarDatePickerDialogFragment().setOnDateSetListener(AddNewHealthTestActivity.this)
              .setFirstDayOfWeek(Calendar.MONDAY)
              .setDoneText(getString(R.string.accept_ok))
              .setCancelText(getString(R.string.decline_cancel))
              .setPreselectedDate(Calendar.getInstance().get(Calendar.YEAR),
                  Calendar.getInstance().get(Calendar.MONTH),
                  Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
              .setThemeLight();
      cdp.show(getSupportFragmentManager(), ACTIVITY_TAG_TIME_PICKER);
    });
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.add_new_health_test, menu);

    return super.onCreateOptionsMenu(menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.nextBTN:
        if (Helpers.validateFields(allFields)) {

          Intent intent =
              new Intent(AddNewHealthTestActivity.this, AddNewHealthTestPart2Activity.class);
          intent.putStringArrayListExtra(Constants.HEALTH_TEST_PICTURE_PATHS, picturePaths);
          intent.putExtra(Constants.HEALTH_TEST_NAME, testNameET.getText().toString());
          intent.putExtra(Constants.HEALTH_TEST_DATE_TAKEN, dateTakenForRequest);

          if (existingMedicaltest != null) {
            intent.putExtra(Constants.EXTRAS_TEST_OBJECT_DETAILS, existingMedicaltest);
          }

          if (deletedImages.size() != 0) {
            intent.putIntegerArrayListExtra(Constants.HEALTH_TEST_DELETED_IMAGE_IDS, deletedImages);
          }
          startActivity(intent);
        }
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
          String selectedImagePath = ImageHandler.getPhotoPathFromGallery(this, selectedImageUri);

          addTestPicture(selectedImagePath);
        }
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

  @Subscribe public void reflectMedicalTestEditCreate(MedicalTestEditCreateEvent event) {
    finish();
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

  private void addTestPicture(String finalImagePath) {
    RelativeLayout viewForPicture = null;

    switch (viewIdForTestPicture) {
      case R.id.addPic1:
        viewForPicture = (RelativeLayout) findViewById(R.id.pictureHolder1);
        picturePaths.set(0, finalImagePath);
        deleteButtonElements.get(0).setVisibility(View.VISIBLE);
        addPic1.setVisibility(View.GONE);
        break;
      case R.id.addPic2:
        viewForPicture = (RelativeLayout) findViewById(R.id.pictureHolder2);
        picturePaths.set(1, finalImagePath);
        deleteButtonElements.get(1).setVisibility(View.VISIBLE);
        addPic2.setVisibility(View.GONE);
        break;
      case R.id.addPic3:
        viewForPicture = (RelativeLayout) findViewById(R.id.pictureHolder3);
        picturePaths.set(2, finalImagePath);
        deleteButtonElements.get(2).setVisibility(View.VISIBLE);
        addPic3.setVisibility(View.GONE);
        break;
      case R.id.addPic4:
        viewForPicture = (RelativeLayout) findViewById(R.id.pictureHolder4);
        picturePaths.set(3, finalImagePath);
        deleteButtonElements.get(3).setVisibility(View.VISIBLE);
        addPic4.setVisibility(View.GONE);
        break;
      case R.id.addPic5:
        viewForPicture = (RelativeLayout) findViewById(R.id.pictureHolder5);
        picturePaths.set(4, finalImagePath);
        deleteButtonElements.get(4).setVisibility(View.VISIBLE);
        addPic5.setVisibility(View.GONE);
        break;
    }

    hookupDeletImageButtons();

    final RelativeLayout finalViewForPicture = viewForPicture;

    Picasso.with(AddNewHealthTestActivity.this)
        .load(new File(finalImagePath))
        .resize(400, 400)
        .centerInside()
        .into(new Target() {

          @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

            finalViewForPicture.setBackground(new BitmapDrawable(getResources(), bitmap));
          }

          @Override public void onBitmapFailed(final Drawable errorDrawable) {
            Log.d("TAG", "FAILED");
          }

          @Override public void onPrepareLoad(final Drawable placeHolderDrawable) {
            Log.d("TAG", "Prepare Load");
          }
        });
  }

  private void showImagePickerDialog() {
    AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
    builderSingle.setTitle("Choose a picture");

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
            openTakePictureIntent();
            break;
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
        photoFile = ImageHandler.createImageFile(ImageHandler.constructImageFilename());
        photoFileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", photoFile);
      } catch (IOException ex) {
        ex.printStackTrace();
      }

      if (photoFile != null) {
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFileUri);
        startActivityForResult(takePictureIntent, CAPTURE_NEW_PICTURE_REQUEST_CODE);
      }
    }
  }

  public void hookupDeletImageButtons() {
    for (int i = 0; i < deleteButtonElements.size(); i++) {
      final int finalI = i;
      deleteButtonElements.get(i).setOnClickListener(view -> {
        imageViewElements.get(finalI).setBackgroundResource(0);

        Timber.d("Delete button " + finalI + " clicked");

        addPictureElements.get(finalI).setVisibility(View.VISIBLE);

        deleteButtonElements.get(finalI).setVisibility(View.GONE);

        picturePaths.set(finalI, null);

        if (existingMedicaltest != null && !existingMedicaltest.getImages().isEmpty()) {
          try {
            deletedImages.add(existingMedicaltest.getImages().get(finalI).getId());
          } catch (IndexOutOfBoundsException e) {

          }
        }
      });
    }
  }
}
