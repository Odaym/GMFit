package com.mcsaatchi.gmfit.health.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.otto.MedicalTestEditCreateEvent;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.MedicalTestMetricsResponseBody;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.TakenMedicalTestsResponseBody;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.TakenMedicalTestsResponseMetricsDatum;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.health.adapters.EditableTestMetricsRecyclerAdapter;
import com.mcsaatchi.gmfit.health.adapters.TesticularMetricsRecyclerAdapter;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class AddNewHealthTestPart2Activity extends BaseActivity
    implements AddNewHealthTestPart2ActivityPresenter.AddNewHealthTestPart2ActivityView {
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.availableTestMetricsListview) RecyclerView availableTestMetricsListview;
  @Bind(R.id.searchTestsAutoCompleTV) EditText searchTestsAutoCompleTV;
  @Bind(R.id.searchIconIV) ImageView searchIconIV;

  private ArrayList<String> picturePaths = new ArrayList<>();
  private String testName, testDateTaken;

  private AddNewHealthTestPart2ActivityPresenter presenter;

  private TakenMedicalTestsResponseBody existingMedicaltest;

  private ArrayList<MedicalTestMetricsResponseBody> testicularMetrics = null;

  private ArrayList<Integer> deletedImages = new ArrayList<>();

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_add_new_test_part_2);

    ButterKnife.bind(this);

    EventBusSingleton.getInstance().register(this);

    setupToolbar(getClass().getSimpleName(), toolbar,
        getResources().getString(R.string.add_new_test_activity_title), true);

    presenter = new AddNewHealthTestPart2ActivityPresenter(this, dataAccessHandler);

    if (getIntent().getExtras() != null) {
      picturePaths =
          getIntent().getExtras().getStringArrayList(Constants.HEALTH_TEST_PICTURE_PATHS);
      testName = getIntent().getExtras().getString(Constants.HEALTH_TEST_NAME);
      testDateTaken = getIntent().getExtras().getString(Constants.HEALTH_TEST_DATE_TAKEN);

      existingMedicaltest = (TakenMedicalTestsResponseBody) getIntent().getExtras()
          .get(Constants.EXTRAS_TEST_OBJECT_DETAILS);

      deletedImages =
          getIntent().getExtras().getIntegerArrayList(Constants.HEALTH_TEST_DELETED_IMAGE_IDS);

      if (existingMedicaltest != null) {
        setupEditableTestMetrics(existingMedicaltest.getMetrics());
        hookupSearchBar(null, existingMedicaltest.getMetrics());
      } else {
        presenter.getTesticularMetrics();
      }
    }
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.add_new_health_test_part_2, menu);

    return super.onCreateOptionsMenu(menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.doneBTN:
        LinkedHashMap<String, RequestBody> imageParts;
        HashMap<String, RequestBody> metrics;

        String deletedImagesString = "";

        if (existingMedicaltest == null) {
          metrics = constructNewMetricsForRequest();

          if (metrics.isEmpty()) {
            Toast.makeText(this, "Please fill in the needed fields to proceed", Toast.LENGTH_SHORT)
                .show();
          } else {
            imageParts = constructSelectedImagesForRequest();

            presenter.storeNewHealthTest(testName, testDateTaken, metrics, imageParts);
          }
        } else {
          metrics = constructEditableMetricsForRequest();

          if (deletedImages != null) deletedImagesString = constructDeletedImagesForRequest();

          imageParts = constructSelectedImagesForRequest();

          presenter.storeEditedHealthTest(existingMedicaltest.getInstanceId(), testName,
              testDateTaken, metrics, imageParts, Helpers.toRequestBody(deletedImagesString));
        }

        break;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    EventBusSingleton.getInstance().unregister(this);
  }

  @Override public void handleAvailableTestMetrics(
      ArrayList<MedicalTestMetricsResponseBody> metricsFromResponse) {
    setupAvailableTestMetrics(metricsFromResponse);

    hookupSearchBar(metricsFromResponse, null);
  }

  @Override public void handleHealthTestCreated() {
    Helpers.hideKeyboard(getCurrentFocus(), AddNewHealthTestPart2Activity.this);
    EventBusSingleton.getInstance().post(new MedicalTestEditCreateEvent());
    finish();
  }

  @Override public void handleEditedHealthTestCreated() {
    Helpers.hideKeyboard(getCurrentFocus(), AddNewHealthTestPart2Activity.this);
    EventBusSingleton.getInstance().post(new MedicalTestEditCreateEvent());
    finish();
  }

  private void hookupSearchBar(final ArrayList<MedicalTestMetricsResponseBody> metricsFromResponse,
      final List<TakenMedicalTestsResponseMetricsDatum> existingMetrics) {
    searchTestsAutoCompleTV.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override public void onTextChanged(CharSequence charSequence, int i1, int i2, int i3) {
        ArrayList<MedicalTestMetricsResponseBody> searchResults = new ArrayList<>();
        ArrayList<TakenMedicalTestsResponseMetricsDatum> editableSearchResults = new ArrayList<>();

        if (charSequence.toString().isEmpty()) {
          searchIconIV.setImageResource(R.drawable.ic_search_white_24dp);
          searchIconIV.setOnClickListener(null);

          if (metricsFromResponse != null) {
            setupAvailableTestMetrics(metricsFromResponse);
          } else {
            setupEditableTestMetrics(existingMetrics);
          }
        } else {
          searchIconIV.setImageResource(R.drawable.ic_clear_search);
          searchIconIV.setOnClickListener(view -> {
            searchTestsAutoCompleTV.setText("");

            //Hide keyboard
            InputMethodManager imm =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
          });

          if (metricsFromResponse != null) {
            for (int i = 0; i < metricsFromResponse.size(); i++) {
              if (metricsFromResponse.get(i)
                  .getName()
                  .toLowerCase()
                  .contains(charSequence.toString().toLowerCase())) {
                searchResults.add(metricsFromResponse.get(i));
              }
            }

            setupAvailableTestMetrics(searchResults);
          } else {
            for (int i = 0; i < existingMetrics.size(); i++) {
              if (existingMetrics.get(i)
                  .getName()
                  .toLowerCase()
                  .contains(charSequence.toString().toLowerCase())) {

                editableSearchResults.add(existingMetrics.get(i));
              }
            }

            setupEditableTestMetrics(editableSearchResults);
          }
        }
      }

      @Override public void afterTextChanged(Editable editable) {

      }
    });
  }

  private void setupAvailableTestMetrics(
      ArrayList<MedicalTestMetricsResponseBody> testsFromResponse) {

    testicularMetrics = testsFromResponse;

    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
    TesticularMetricsRecyclerAdapter editableTestMetricsRecyclerAdapter =
        new TesticularMetricsRecyclerAdapter(this, testsFromResponse);

    availableTestMetricsListview.setItemViewCacheSize(testsFromResponse.size());

    availableTestMetricsListview.setLayoutManager(mLayoutManager);
    availableTestMetricsListview.setAdapter(editableTestMetricsRecyclerAdapter);
    availableTestMetricsListview.setNestedScrollingEnabled(false);
    availableTestMetricsListview.addItemDecoration(new SimpleDividerItemDecoration(this));
  }

  private void setupEditableTestMetrics(
      List<TakenMedicalTestsResponseMetricsDatum> existingTestMetrics) {
    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
    EditableTestMetricsRecyclerAdapter editableTestMetricsRecyclerAdapter =
        new EditableTestMetricsRecyclerAdapter(this, existingTestMetrics);

    availableTestMetricsListview.setItemViewCacheSize(existingTestMetrics.size());

    availableTestMetricsListview.setLayoutManager(mLayoutManager);
    availableTestMetricsListview.setAdapter(editableTestMetricsRecyclerAdapter);
    availableTestMetricsListview.setNestedScrollingEnabled(false);
    availableTestMetricsListview.addItemDecoration(new SimpleDividerItemDecoration(this));
  }

  private HashMap<String, RequestBody> constructNewMetricsForRequest() {
    HashMap<String, RequestBody> metrics = new HashMap<>();

    for (int i = 0; i < testicularMetrics.size(); i++) {
      if (testicularMetrics.get(i).getValue() != null) {

        metrics.put("metrics[" + i + "][id]",
            Helpers.toRequestBody(String.valueOf(testicularMetrics.get(i).getId())));

        metrics.put("metrics[" + i + "][value]",
            Helpers.toRequestBody(testicularMetrics.get(i).getValue()));

        for (int j = 0; j < testicularMetrics.get(i).getUnits().size(); j++) {
          if (testicularMetrics.get(j).getUnits().get(j).isSelected()) {
            metrics.put("metrics[" + i + "][unit_id]", Helpers.toRequestBody(
                String.valueOf(testicularMetrics.get(j).getUnits().get(j).getId())));
          }
        }
      }
    }

    return metrics;
  }

  private LinkedHashMap<String, RequestBody> constructSelectedImagesForRequest() {
    LinkedHashMap<String, RequestBody> imageParts = new LinkedHashMap<>();

    RequestBody file;
    File imageFile;

    for (int i = 0; i < picturePaths.size(); i++) {
      if (picturePaths.get(i) != null) {
        imageFile = new File(picturePaths.get(i));
        file = RequestBody.create(MediaType.parse("image/jpeg"), imageFile);
        imageParts.put("images[" + 0 + "]\"; filename=\"" + picturePaths.get(i), file);
      }
    }

    return imageParts;
  }

  private LinkedHashMap<String, RequestBody> constructEditableMetricsForRequest() {
    LinkedHashMap<String, RequestBody> metrics = new LinkedHashMap<>();

    for (int i = 0; i < existingMedicaltest.getMetrics().size(); i++) {
      if (Integer.parseInt(existingMedicaltest.getMetrics().get(i).getValue()) != -1) {

        metrics.put("metrics[" + i + "][id]",
            Helpers.toRequestBody(String.valueOf(existingMedicaltest.getMetrics().get(i).getId())));

        metrics.put("metrics[" + i + "][value]",
            Helpers.toRequestBody(existingMedicaltest.getMetrics().get(i).getValue()));

        for (int j = 0; j < existingMedicaltest.getMetrics().get(i).getUnits().size(); j++) {
          if (existingMedicaltest.getMetrics().get(j).getUnits().get(j).getSelected()) {
            metrics.put("metrics[" + i + "][unit_id]", Helpers.toRequestBody(
                String.valueOf(existingMedicaltest.getMetrics().get(j).getUnits().get(j).getId())));
          }
        }
      }
    }

    return metrics;
  }

  private String constructDeletedImagesForRequest() {
    String deletedImagesString = "";

    for (int i = 0; i < deletedImages.size(); i++) {
      deletedImagesString += deletedImages.get(i) + ",";
    }

    return deletedImagesString;
  }
}
