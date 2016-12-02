package com.mcsaatchi.gmfit.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.mcsaatchi.gmfit.adapters.TesticularMetricsRecycler_Adapter;
import com.mcsaatchi.gmfit.classes.Constants;
import com.mcsaatchi.gmfit.classes.EventBus_Poster;
import com.mcsaatchi.gmfit.classes.EventBus_Singleton;
import com.mcsaatchi.gmfit.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.rest.MedicalTestMetricsResponse;
import com.mcsaatchi.gmfit.rest.MedicalTestMetricsResponseBody;
import com.squareup.otto.Subscribe;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class AddNewHealthTest_Part2_Activity extends Base_Activity {
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.availableTestMetricsListview) RecyclerView availableTestMetricsListview;
  @Bind(R.id.searchTestsAutoCompleTV) EditText searchTestsAutoCompleTV;
  @Bind(R.id.searchIconIV) ImageView searchIconIV;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_add_new_test);

    ButterKnife.bind(this);

    EventBus_Singleton.getInstance().register(this);

    setupToolbar(toolbar, getResources().getString(R.string.add_new_test_activity_title), true);

    getTesticularMetrics();
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.add_new_health_test, menu);

    return super.onCreateOptionsMenu(menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.doneBTN:

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

    dataAccessHandler.getTesticularMetrics(
        prefs.getString(Constants.PREF_USER_ACCESS_TOKEN, Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS),
        new Callback<MedicalTestMetricsResponse>() {
          @Override public void onResponse(Call<MedicalTestMetricsResponse> call,
              Response<MedicalTestMetricsResponse> response) {
            switch (response.code()) {
              case 200:

                waitingDialog.dismiss();

                Timber.d("There are : %s", response.body().getData().getBody().size());

                final ArrayList<MedicalTestMetricsResponseBody> metricsFromResponse =
                    (ArrayList<MedicalTestMetricsResponseBody>) response.body().getData().getBody();

                setupAvailableTestMetrics(metricsFromResponse);

                searchTestsAutoCompleTV.addTextChangedListener(new TextWatcher() {
                  @Override
                  public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                  }

                  @Override
                  public void onTextChanged(CharSequence charSequence, int i1, int i2, int i3) {
                    ArrayList<MedicalTestMetricsResponseBody> searchResults = new ArrayList<>();

                    if (charSequence.toString().isEmpty()) {
                      searchIconIV.setImageResource(R.drawable.ic_search_white_24dp);
                      searchIconIV.setOnClickListener(null);
                      setupAvailableTestMetrics(metricsFromResponse);
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

                      for (int i = 0; i < metricsFromResponse.size(); i++) {
                        if (metricsFromResponse.get(i)
                            .getName()
                            .toLowerCase()
                            .contains(charSequence.toString().toLowerCase())) {
                          searchResults.add(metricsFromResponse.get(i));
                        }
                      }

                      setupAvailableTestMetrics(searchResults);
                    }
                  }

                  @Override public void afterTextChanged(Editable editable) {

                  }
                });
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

  private void setupAvailableTestMetrics(
      ArrayList<MedicalTestMetricsResponseBody> testsFromResponse) {
    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
    TesticularMetricsRecycler_Adapter editableTestMetricsRecyclerAdapter =
        new TesticularMetricsRecycler_Adapter(this, testsFromResponse);

    availableTestMetricsListview.setItemViewCacheSize(testsFromResponse.size());

    availableTestMetricsListview.setLayoutManager(mLayoutManager);
    availableTestMetricsListview.setAdapter(editableTestMetricsRecyclerAdapter);
    availableTestMetricsListview.setNestedScrollingEnabled(false);
    availableTestMetricsListview.addItemDecoration(new SimpleDividerItemDecoration(this));
  }

  @Subscribe public void handle_BusEvents(EventBus_Poster ebp) {
    String ebpMessage = ebp.getMessage();

    switch (ebpMessage) {
      case Constants.EXTRAS_TEST_EDIT_OR_CREATE_DONE:
        finish();
        break;
    }
  }

  @Override public void onDestroy() {
    super.onDestroy();
    EventBus_Singleton.getInstance().unregister(this);
  }
}
