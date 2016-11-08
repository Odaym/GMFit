package com.mcsaatchi.gmfit.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.adapters.AvailableTestsRecycler_Adapter;
import com.mcsaatchi.gmfit.classes.Constants;
import com.mcsaatchi.gmfit.classes.EventBus_Poster;
import com.mcsaatchi.gmfit.classes.EventBus_Singleton;
import com.mcsaatchi.gmfit.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.rest.MedicalTestsResponse;
import com.mcsaatchi.gmfit.rest.MedicalTestsResponseBody;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNewHealthTest_Activity extends Base_Activity {
  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.availableTestsListview) RecyclerView availableTestsListview;

  private SharedPreferences prefs;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_add_new_test);

    ButterKnife.bind(this);

    EventBus_Singleton.getInstance().register(this);

    prefs = getSharedPreferences(Constants.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);

    setupToolbar(toolbar, R.string.add_new_test_activity_title, true);

    getMedicalTests();
  }

  private void getMedicalTests() {
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

    DataAccessHandler.getInstance()
        .getMedicalTests(prefs.getString(Constants.PREF_USER_ACCESS_TOKEN,
            Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS), new Callback<MedicalTestsResponse>() {
          @Override public void onResponse(Call<MedicalTestsResponse> call,
              Response<MedicalTestsResponse> response) {
            switch (response.code()) {
              case 200:

                waitingDialog.dismiss();

                ArrayList<MedicalTestsResponseBody> testsFromResponse =
                    (ArrayList<MedicalTestsResponseBody>) response.body().getData().getBody();

                setupAvailableTestsListView(testsFromResponse);

                break;
            }
          }

          @Override public void onFailure(Call<MedicalTestsResponse> call, Throwable t) {
            alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
            alertDialog.show();
          }
        });
  }

  private void setupAvailableTestsListView(ArrayList<MedicalTestsResponseBody> testsFromResponse) {
    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
    AvailableTestsRecycler_Adapter userTestsRecyclerAdapter =
        new AvailableTestsRecycler_Adapter(this, testsFromResponse);

    availableTestsListview.setLayoutManager(mLayoutManager);
    availableTestsListview.setAdapter(userTestsRecyclerAdapter);
    availableTestsListview.addItemDecoration(new SimpleDividerItemDecoration(this));
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
