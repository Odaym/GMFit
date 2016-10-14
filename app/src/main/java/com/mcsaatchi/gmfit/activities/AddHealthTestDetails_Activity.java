package com.mcsaatchi.gmfit.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.adapters.EditableTestMetricsRecycler_Adapter;
import com.mcsaatchi.gmfit.adapters.TestMetricsRecycler_Adapter;
import com.mcsaatchi.gmfit.classes.Constants;
import com.mcsaatchi.gmfit.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.rest.MedicalTestsResponseDatum;
import com.mcsaatchi.gmfit.rest.TakenMedicalTestsResponseImagesDatum;
import com.mcsaatchi.gmfit.rest.TakenMedicalTestsResponseMetricsDatum;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddHealthTestDetails_Activity extends Base_Activity {

    private static final int SELECT_PICTURE = 1;
    private static final int RC_HANDLE_STORAGE_PERM = 2;
    private static final String TAG = "AddHealthTestDetails_Activity";

    @Bind(R.id.testPhotosLayout)
    LinearLayout testPhotosLayout;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.addNewTestPhotoBTN)
    TextView addNewTestPhotoBTN;
    @Bind(R.id.testMetricsListView)
    RecyclerView testMetricsListView;

    private String testName;
    private String test_slug;
    private String test_date_taken;
    private int test_instance_id;

    private SharedPreferences prefs;

    private boolean purposeIsEditing = false;

    private ArrayList<MedicalTestsResponseDatum> testMetrics = new ArrayList<>();

    private ArrayList<TakenMedicalTestsResponseMetricsDatum> editableTestMetrics = new ArrayList<>();
    private ArrayList<TakenMedicalTestsResponseImagesDatum> editableTestImages = new ArrayList<>();
    private EditableTestMetricsRecycler_Adapter editableTestMetricsRecyclerAdapter;

    private RecyclerView.LayoutManager mLayoutManager;
    private TestMetricsRecycler_Adapter testMetricsRecyclerAdapter;
    private ArrayList<Image> selectedImages = new ArrayList<>();

    private ArrayList<Integer> deletedImageIds = new ArrayList<>();

    public static RequestBody toRequestBody(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_health_test_details);

        ButterKnife.bind(this);

        prefs = getSharedPreferences(Constants.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

        addNewTestPhotoBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean hasPermission = (ContextCompat.checkSelfPermission(AddHealthTestDetails_Activity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);

                if (!hasPermission) {
                    ActivityCompat.requestPermissions(AddHealthTestDetails_Activity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            RC_HANDLE_STORAGE_PERM);
                } else {
                    openPictureChooser();
                }
            }
        });

        if (getIntent().getExtras() != null) {
            testName = getIntent().getExtras().getString(Constants.EXTRAS_TEST_TITLE);
            test_slug = getIntent().getExtras().getString(Constants.EXTRAS_TEST_SLUG);
            test_date_taken = getIntent().getExtras().getString(Constants.EXTRAS_TEST_DATE_TAKEN);
            test_instance_id = getIntent().getExtras().getInt(Constants.EXTRAS_TEST_INSTANCE_ID);
            purposeIsEditing = getIntent().getExtras().getBoolean(Constants.EXTRAS_TEST_ITEM_PURPOSE_EDITING, false);

            if (purposeIsEditing) {
                editableTestMetrics = getIntent().getExtras().getParcelableArrayList(Constants.EXTRAS_TEST_METRICS);
                editableTestImages = getIntent().getExtras().getParcelableArrayList(Constants.EXTRAS_TEST_IMAGES);
                setupTestImages(editableTestImages);
                setupEditableMetricsListView(editableTestMetrics);
            } else {
                testMetrics = getIntent().getExtras().getParcelableArrayList(Constants.EXTRAS_TEST_OBJECT_DETAILS);
                setupFreshMetricsListView(testMetrics);
            }
        }

        setupToolbar(toolbar, testName, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_new_health_test, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.doneBTN:

                HashMap<String, RequestBody> metrics;

                HashMap<String, RequestBody> imageParts;

                HashMap<String, RequestBody> deletedImages;

                if (purposeIsEditing) {
                    metrics = constructEditableMetricsForRequest(editableTestMetricsRecyclerAdapter);
                } else {
                    metrics = constructNewMetricsForRequest(testMetricsRecyclerAdapter);
                }

                if (metrics.isEmpty()) {
                    Toast.makeText(this, "Please fill in the needed fields to proceed", Toast.LENGTH_SHORT).show();
                } else {
                    imageParts = constructSelectedImagesForRequest();

                    deletedImages = constructDeletedImagesForRequest(deletedImageIds);

                    if (purposeIsEditing) {
                        editExistingHealthTestRequest(toRequestBody(String.valueOf(test_instance_id)), metrics, imageParts, deletedImages);
                    } else {
                        createNewHealthTestRequest(metrics, imageParts);
                    }
                }

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null) {
            selectedImages = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
            for (int i = 0; i < selectedImages.size(); i++) {
                testPhotosLayout.addView(createNewImageViewLayout(selectedImages.get(i).getPath(), null));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_STORAGE_PERM) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openPictureChooser();
            return;
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_test_photos_bar_title)
                .setMessage(R.string.no_storage_permission)
                .setPositiveButton(R.string.OK, listener)
                .show();
    }

    private HashMap<String, RequestBody> constructNewMetricsForRequest(TestMetricsRecycler_Adapter testMetricsRecyclerAdapter) {
        HashMap<String, RequestBody> metrics = new HashMap<>();

        for (int i = 0; i < testMetricsRecyclerAdapter.getItemCount(); i++) {
            if (mLayoutManager.findViewByPosition(i) != null) {
                Spinner metricUnitSpinner = ((Spinner) mLayoutManager.findViewByPosition(i).findViewById(R.id.metricUnitsSpinner));

                EditText metricValueTV = ((EditText) mLayoutManager.findViewByPosition(i).findViewById(R.id.metricValueET));

                if (!metricValueTV.getText().toString().isEmpty()) {
                    metrics.put("metrics[" + i + "][id]", toRequestBody(testMetricsRecyclerAdapter.getItem(i).getId()));

                    metrics.put("metrics[" + i + "][value]", toRequestBody(metricValueTV.getText().toString()));

                    if (metricUnitSpinner.getSelectedItem() != null) {
                        int finalUnitId = 0;

                        if (!testMetrics.get(i).getUnits().isEmpty()) {
                            for (int j = 0; j < testMetricsRecyclerAdapter.getItem(i).getUnits().size(); j++) {
                                if (testMetricsRecyclerAdapter.getItem(i).getUnits().get(j).getUnit().equals(metricUnitSpinner.getSelectedItem().toString())) {
                                    finalUnitId = testMetricsRecyclerAdapter.getItem(i).getUnits().get(j).getId();
                                }
                            }
                        }

                        metrics.put("metrics[" + i + "][unit_id]", toRequestBody(String.valueOf(finalUnitId)));
                    }
                }
            }
        }

        return metrics;
    }

    private HashMap<String, RequestBody> constructEditableMetricsForRequest(EditableTestMetricsRecycler_Adapter editableTestMetricsRecyclerAdapter) {
        HashMap<String, RequestBody> metrics = new HashMap<>();

        for (int i = 0; i < editableTestMetricsRecyclerAdapter.getItemCount(); i++) {
            if (mLayoutManager.findViewByPosition(i) != null) {
                Spinner metricUnitSpinner = ((Spinner) mLayoutManager.findViewByPosition(i).findViewById(R.id.metricUnitsSpinner));

                EditText metricValueTV = ((EditText) mLayoutManager.findViewByPosition(i).findViewById(R.id.metricValueET));

                if (!metricValueTV.getText().toString().isEmpty()) {
                    metrics.put("metrics[" + i + "][id]", toRequestBody(String.valueOf(editableTestMetricsRecyclerAdapter.getItem(i).getId())));

                    metrics.put("metrics[" + i + "][value]", toRequestBody(metricValueTV.getText().toString()));

                    if (metricUnitSpinner.getSelectedItem() != null) {
                        int finalUnitId = 0;

                        if (!editableTestMetrics.get(i).getUnits().isEmpty()) {
                            for (int j = 0; j < editableTestMetricsRecyclerAdapter.getItem(i).getUnits().size(); j++) {
                                if (editableTestMetricsRecyclerAdapter.getItem(i).getUnits().get(j).getUnit().equals(metricUnitSpinner.getSelectedItem().toString())) {
                                    finalUnitId = editableTestMetricsRecyclerAdapter.getItem(i).getUnits().get(j).getId();
                                }
                            }
                        }

                        metrics.put("metrics[" + i + "][unit_id]", toRequestBody(String.valueOf(finalUnitId)));
                    }
                }
            }
        }

        return metrics;
    }

    private HashMap<String, RequestBody> constructSelectedImagesForRequest() {
        HashMap<String, RequestBody> imageParts = new HashMap<>();

        RequestBody file;
        File imageFile;

        for (int i = 0; i < selectedImages.size(); i++) {
            try {
                imageFile = new File(selectedImages.get(i).getPath());

                file = RequestBody.create(MediaType.parse("image/jpeg"), imageFile);

                imageParts.put("images[" + i + "]\"; filename=\"" + selectedImages.get(i).getName(), file);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return imageParts;
    }

    private HashMap<String, RequestBody> constructDeletedImagesForRequest(ArrayList<Integer> deletedImageIds) {
        HashMap<String, RequestBody> deletedImages = new HashMap<>();

        String deletedImagesString = "";

        for (int i = 0; i < deletedImageIds.size(); i++) {
            try {

                deletedImagesString += deletedImageIds.get(i) + ",";


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        deletedImages.put("delete_image_ids", toRequestBody(deletedImagesString));

        return deletedImages;
    }

    private void createNewHealthTestRequest(HashMap<String, RequestBody> metrics, HashMap<String, RequestBody> imageParts) {
        DataAccessHandler.getInstance().storeNewHealthTest(prefs.getString(Constants.PREF_USER_ACCESS_TOKEN, Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS),
                toRequestBody(test_slug), toRequestBody(test_date_taken), metrics, imageParts, new Callback<DefaultGetResponse>() {
                    @Override
                    public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
                        switch (response.code()) {
                            case 200:

                                Log.d("TAG", "onResponse: Succeeded creating new test");

                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
                        Log.d("TAG", "onFailure: Failure");
                    }
                });
    }

    private void editExistingHealthTestRequest(RequestBody instance_id, HashMap<String, RequestBody> metrics, HashMap<String, RequestBody> imageParts, HashMap<String, RequestBody> deletedImages) {
        DataAccessHandler.getInstance().editExistingHealthTest(prefs.getString(Constants.PREF_USER_ACCESS_TOKEN, Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS),
                instance_id, metrics, imageParts, deletedImages, new Callback<DefaultGetResponse>() {
                    @Override
                    public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
                        switch (response.code()) {
                            case 200:

                                Log.d("TAG", "onResponse: Succeeded editing new test");

                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
                        Log.d("TAG", "onFailure: Failure");
                    }
                });
    }

    private void openPictureChooser() {
        selectedImages = new ArrayList<>();

        Intent intent = new Intent(this, ImagePickerActivity.class);

        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_FOLDER_MODE, true);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_MODE, ImagePickerActivity.MODE_MULTIPLE);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_LIMIT, 10);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_SHOW_CAMERA, true);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES, selectedImages);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_FOLDER_TITLE, "Album");
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_IMAGE_TITLE, "Tap to select images");
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_IMAGE_DIRECTORY, "Camera");

        startActivityForResult(intent, SELECT_PICTURE);
    }

    private void setupEditableMetricsListView(ArrayList<TakenMedicalTestsResponseMetricsDatum> editableTestMetrics) {
        mLayoutManager = new LinearLayoutManager(this);
        editableTestMetricsRecyclerAdapter = new EditableTestMetricsRecycler_Adapter(this, editableTestMetrics);

        testMetricsListView.setItemViewCacheSize(editableTestMetrics.size());

        testMetricsListView.setLayoutManager(mLayoutManager);
        testMetricsListView.setAdapter(editableTestMetricsRecyclerAdapter);
        testMetricsListView.addItemDecoration(new SimpleDividerItemDecoration(this));
    }

    private void setupFreshMetricsListView(ArrayList<MedicalTestsResponseDatum> testMetrics) {
        mLayoutManager = new LinearLayoutManager(this);
        testMetricsRecyclerAdapter = new TestMetricsRecycler_Adapter(this, testMetrics);

        testMetricsListView.setItemViewCacheSize(testMetrics.size());

        testMetricsListView.setLayoutManager(mLayoutManager);
        testMetricsListView.setAdapter(testMetricsRecyclerAdapter);
        testMetricsListView.addItemDecoration(new SimpleDividerItemDecoration(this));
    }

    private void setupTestImages(ArrayList<TakenMedicalTestsResponseImagesDatum> editableTestImages) {
        for (int i = 0; i < editableTestImages.size(); i++) {
            testPhotosLayout.addView(createNewImageViewLayout(editableTestImages.get(i).getImage(), editableTestImages.get(i)));
        }
    }

    public View createNewImageViewLayout(String imagePath, final TakenMedicalTestsResponseImagesDatum editableTestImage) {
        final View singlePhotoLayout = getLayoutInflater().inflate(R.layout.view_test_photo_item, null);

        ImageView testPhotoIMG = (ImageView) singlePhotoLayout.findViewById(R.id.testPhotoIMG);
        Button deleteTestPhotoBTN = (Button) singlePhotoLayout.findViewById(R.id.deleteTestPhotoBTN);

        if (imagePath.contains("http")) {
            imagePath = imagePath.replace("\\", "");
            Picasso.with(AddHealthTestDetails_Activity.this).load(imagePath).fit().into
                    (testPhotoIMG);
        } else {
            Picasso.with(AddHealthTestDetails_Activity.this).load(new File(imagePath)).fit().into
                    (testPhotoIMG);
        }

        final String finalImagePath = imagePath;

        deleteTestPhotoBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editableTestImage != null)
                    deletedImageIds.add(editableTestImage.getId());

                removeImageFromSelectedImages(finalImagePath);

                testPhotosLayout.removeView(singlePhotoLayout);
            }
        });

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.test_photos_width),
                getResources().getDimensionPixelSize(R.dimen.test_photos_height));
        singlePhotoLayout.setPadding(20, 0, 20, 0);
        singlePhotoLayout.setLayoutParams(layoutParams);

        return singlePhotoLayout;
    }

    public void removeImageFromSelectedImages(String imagePath) {
        for (int i = 0; i < selectedImages.size(); i++) {
            if (selectedImages.get(i).getPath().equals(imagePath)) {
                selectedImages.remove(selectedImages.get(i));
            }
        }
    }
}
