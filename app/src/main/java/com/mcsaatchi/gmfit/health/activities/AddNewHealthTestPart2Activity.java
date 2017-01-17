package com.mcsaatchi.gmfit.health.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.otto.MedicalTestEditCreateEvent;
import com.mcsaatchi.gmfit.architecture.rest.MedicalTestMetricsResponse;
import com.mcsaatchi.gmfit.architecture.rest.MedicalTestMetricsResponseBody;
import com.mcsaatchi.gmfit.architecture.rest.TakenMedicalTestsResponseBody;
import com.mcsaatchi.gmfit.architecture.rest.TakenMedicalTestsResponseMetricsDatum;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.health.adapters.EditableTestMetricsRecyclerAdapter;
import com.mcsaatchi.gmfit.health.adapters.TesticularMetricsRecyclerAdapter;
import com.squareup.otto.Subscribe;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class AddNewHealthTestPart2Activity extends BaseActivity {
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.availableTestMetricsListview) RecyclerView availableTestMetricsListview;
  @Bind(R.id.searchTestsAutoCompleTV) EditText searchTestsAutoCompleTV;
  @Bind(R.id.searchIconIV) ImageView searchIconIV;

  private TakenMedicalTestsResponseBody existingMedicaltest;

  private ArrayList<MedicalTestMetricsResponseBody> testicularMetrics = null;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_add_new_test);

    ButterKnife.bind(this);

    EventBusSingleton.getInstance().register(this);

    setupToolbar(toolbar, getResources().getString(R.string.add_new_test_activity_title), true);

    if (getIntent().getExtras() != null) {
      existingMedicaltest = (TakenMedicalTestsResponseBody) getIntent().getExtras()
          .get(Constants.EXTRAS_TEST_OBJECT_DETAILS);
    }

    if (existingMedicaltest != null) {
      setupEditableTestMetrics(existingMedicaltest.getMetrics());
      hookupSearchBar(null, existingMedicaltest.getMetrics());
    } else {
      getTesticularMetrics();
    }
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.add_new_health_test, menu);

    return super.onCreateOptionsMenu(menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.nextBTN:
        if (testicularMetrics != null) {
          Intent intent =
              new Intent(AddNewHealthTestPart2Activity.this, AddNewHealthTestActivity.class);
          intent.putParcelableArrayListExtra(Constants.TESTICULAR_METRICS_ARRAY, testicularMetrics);
          startActivity(intent);
        } else {
          Intent intent =
              new Intent(AddNewHealthTestPart2Activity.this, AddNewHealthTestActivity.class);
          intent.putExtra(Constants.EXTRAS_TEST_OBJECT_DETAILS, existingMedicaltest);
          startActivity(intent);
        }
        break;
    }

    return super.onOptionsItemSelected(item);
  }

  private void getTesticularMetrics() {
    final ProgressDialog waitingDialog = new ProgressDialog(this);
    waitingDialog.setTitle(getString(R.string.fetching_test_data_dialog_title));
    waitingDialog.setMessage(getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle(R.string.fetching_test_data_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();

            if (waitingDialog.isShowing()) waitingDialog.dismiss();
          }
        });

    dataAccessHandler.getTesticularMetrics(new Callback<MedicalTestMetricsResponse>() {
      @Override public void onResponse(Call<MedicalTestMetricsResponse> call,
          Response<MedicalTestMetricsResponse> response) {
        switch (response.code()) {
          case 200:

            waitingDialog.dismiss();

            final ArrayList<MedicalTestMetricsResponseBody> metricsFromResponse =
                (ArrayList<MedicalTestMetricsResponseBody>) response.body().getData().getBody();

            setupAvailableTestMetrics(metricsFromResponse);

            hookupSearchBar(metricsFromResponse, null);

            break;
        }
      }

      @Override public void onFailure(Call<MedicalTestMetricsResponse> call, Throwable t) {
        Timber.d("Call failed with error : %s", t.getMessage());
        alertDialog.setMessage(
            getResources().getString(R.string.error_response_from_server_incorrect));
        alertDialog.show();
      }
    });
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
          searchIconIV.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
              searchTestsAutoCompleTV.setText("");

              /**
               * Hide keyboard
               */
              InputMethodManager imm =
                  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
              imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
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

  @Subscribe public void reflectMedicalTestEditCreate(MedicalTestEditCreateEvent event) {
    finish();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    EventBusSingleton.getInstance().unregister(this);
  }
}
