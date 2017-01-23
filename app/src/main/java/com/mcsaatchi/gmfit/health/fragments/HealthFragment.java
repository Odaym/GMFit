package com.mcsaatchi.gmfit.health.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.otto.HealthWidgetsOrderChangedEvent;
import com.mcsaatchi.gmfit.architecture.otto.MedicalTestEditCreateEvent;
import com.mcsaatchi.gmfit.architecture.otto.MedicationItemCreatedEvent;
import com.mcsaatchi.gmfit.architecture.rest.DefaultGetResponse;
import com.mcsaatchi.gmfit.architecture.rest.TakenMedicalTestsResponse;
import com.mcsaatchi.gmfit.architecture.rest.TakenMedicalTestsResponseBody;
import com.mcsaatchi.gmfit.architecture.rest.UserProfileResponse;
import com.mcsaatchi.gmfit.architecture.rest.UserProfileResponseDatum;
import com.mcsaatchi.gmfit.architecture.rest.WeightHistoryResponse;
import com.mcsaatchi.gmfit.architecture.rest.WeightHistoryResponseDatum;
import com.mcsaatchi.gmfit.architecture.rest.WidgetsResponse;
import com.mcsaatchi.gmfit.architecture.rest.WidgetsResponseDatum;
import com.mcsaatchi.gmfit.architecture.touch_helpers.SimpleSwipeItemTouchHelperCallback;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.activities.CustomizeWidgetsAndChartsActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.common.components.CustomLineChart;
import com.mcsaatchi.gmfit.health.activities.AddNewHealthTestActivity;
import com.mcsaatchi.gmfit.health.activities.SearchMedicationsActivity;
import com.mcsaatchi.gmfit.health.adapters.HealthWidgetsRecyclerAdapter;
import com.mcsaatchi.gmfit.health.adapters.MedicationsRecyclerAdapter;
import com.mcsaatchi.gmfit.health.adapters.UserTestsRecyclerAdapter;
import com.mcsaatchi.gmfit.health.models.HealthWidget;
import com.mcsaatchi.gmfit.health.models.Medication;
import com.squareup.otto.Subscribe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import org.joda.time.LocalDateTime;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class HealthFragment extends Fragment {

  @Inject DataAccessHandler dataAccessHandler;
  @Inject SharedPreferences prefs;

  @Bind(R.id.metricCounterTV) TextView metricCounterTV;
  @Bind(R.id.widgetsGridView) RecyclerView widgetsGridView;
  @Bind(R.id.addEntryBTN_MEDICAL_TESTS) TextView addEntryBTN_MEDICAL_TESTS;
  @Bind(R.id.addEntryBTN_MEDICATIONS) TextView addEntryBTN_MEDICATIONS;
  @Bind(R.id.userTestsListView) RecyclerView userTestsListView;
  @Bind(R.id.loadingWidgetsProgressBar) ProgressBar loadingWidgetsProgressBar;
  @Bind(R.id.loadingTestsProgressBar) ProgressBar loadingTestsProgressBar;
  @Bind(R.id.medicationRemindersRecyclerView) RecyclerView medicationRemindersRecyclerView;
  @Bind(R.id.medicationsEmptyLayout) LinearLayout medicationsEmptyLayout;
  @Bind(R.id.medicalTestsEmptyLayout) LinearLayout medicalTestsEmptyLayout;
  @Bind(R.id.lineChartContainer) LinearLayout lineChartContainer;

  private CustomLineChart customLineChart;

  private RuntimeExceptionDao<Medication, Integer> medicationDAO;

  private ArrayList<HealthWidget> healthWidgetsMap = new ArrayList<>();

  @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    View fragmentView = inflater.inflate(R.layout.fragment_health, container, false);

    ButterKnife.bind(this, fragmentView);

    EventBusSingleton.getInstance().register(this);

    setHasOptionsMenu(true);

    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.health_tab_title);
    ((GMFitApplication) getActivity().getApplication()).getAppComponent().inject(this);

    medicationDAO = ((BaseActivity) getActivity()).dbHelper.getMedicationDAO();

    setupMedicationRemindersList(medicationDAO.queryForAll());

    getWidgets();

    getTakenMedicalTests();

    getUserProfile();

    setupWeightChart();

    addEntryBTN_MEDICATIONS.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Intent intent = new Intent(getActivity(), SearchMedicationsActivity.class);
        startActivity(intent);
      }
    });

    addEntryBTN_MEDICAL_TESTS.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Intent intent = new Intent(getActivity(), AddNewHealthTestActivity.class);
        startActivity(intent);
      }
    });

    return fragmentView;
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);

    inflater.inflate(R.menu.main, menu);

    MenuItem calendarTodayItem = menu.findItem(R.id.calendarToday);

    if (calendarTodayItem != null) {
      calendarTodayItem.setVisible(false);
    }
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.settings:
        Intent intent = new Intent(getActivity(), CustomizeWidgetsAndChartsActivity.class);
        intent.putExtra(Constants.EXTRAS_FRAGMENT_TYPE, Constants.EXTRAS_HEALTH_FRAGMENT);
        intent.putExtra(Constants.BUNDLE_HEALTH_WIDGETS_MAP, healthWidgetsMap);
        startActivity(intent);
        break;
    }

    return super.onOptionsItemSelected(item);
  }

  private void setupWeightChart() {
    dataAccessHandler.getUserWeightHistory(new Callback<WeightHistoryResponse>() {
      @Override public void onResponse(Call<WeightHistoryResponse> call,
          Response<WeightHistoryResponse> response) {
        switch (response.code()) {
          case 200:
            if (getActivity() != null) customLineChart = new CustomLineChart(getActivity());

            List<WeightHistoryResponseDatum> weightHistoryList =
                response.body().getData().getBody().getData();

            if (weightHistoryList != null) {
              customLineChart.setLineChartData(lineChartContainer, weightHistoryList);

              final TextView updateUserWeightTV =
                  (TextView) customLineChart.getView().findViewById(R.id.updateWeightTV);

              updateUserWeightTV.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                  final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                  dialogBuilder.setTitle(R.string.profile_edit_weight_dialog_title);

                  View dialogView = LayoutInflater.from(getActivity())
                      .inflate(R.layout.profile_edit_weight_dialog, null);
                  final EditText editWeightET =
                      (EditText) dialogView.findViewById(R.id.dialogWeightET);

                  editWeightET.setText(
                      String.valueOf(prefs.getFloat(Constants.EXTRAS_USER_PROFILE_WEIGHT, 0)));
                  editWeightET.setSelection(editWeightET.getText().toString().length());

                  dialogBuilder.setView(dialogView);
                  dialogBuilder.setPositiveButton(R.string.ok,
                      new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialogInterface, int i) {
                          double userWeight = Double.parseDouble(editWeightET.getText().toString());

                          updateUserWeight(editWeightET, userWeight,
                              Helpers.prepareDateWithTimeForAPIRequest(new LocalDateTime()));
                        }
                      });
                  dialogBuilder.setNegativeButton(R.string.decline_cancel,
                      new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialogInterface, int i) {
                          dialogInterface.dismiss();
                        }
                      });

                  AlertDialog alertDialog = dialogBuilder.create();
                  alertDialog.show();
                }
              });
            }
            break;
        }
      }

      @Override public void onFailure(Call<WeightHistoryResponse> call, Throwable t) {
        Timber.d("Call failed with error : %s", t.getMessage());
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
        alertDialog.show();
      }
    });
  }

  private void updateUserWeight(final EditText editWeightET, double weight, String created_at) {
    final ProgressDialog waitingDialog = new ProgressDialog(getActivity());
    waitingDialog.setTitle(getString(R.string.updating_user_profile_dialog_title));
    waitingDialog.setMessage(getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
    alertDialog.setTitle(R.string.updating_user_profile_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();

            if (waitingDialog.isShowing()) waitingDialog.dismiss();
          }
        });

    dataAccessHandler.updateUserWeight(weight, created_at, new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        switch (response.code()) {
          case 200:

            waitingDialog.dismiss();

            lineChartContainer.removeAllViews();
            lineChartContainer.invalidate();

            setupWeightChart();

            getUserProfile();

            InputMethodManager imm =
                (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editWeightET.getWindowToken(), 0);

            break;
        }
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
        Timber.d("Call failed with error : %s", t.getMessage());
        alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
        alertDialog.show();
      }
    });
  }

  private void setupMedicationRemindersList(List<Medication> medicationsList) {
    if (medicationsList.isEmpty()) {
      medicationRemindersRecyclerView.setVisibility(View.GONE);
      medicationsEmptyLayout.setVisibility(View.VISIBLE);
    } else {
      medicationRemindersRecyclerView.setVisibility(View.VISIBLE);
      medicationsEmptyLayout.setVisibility(View.GONE);

      Collections.reverse(medicationsList);

      medicationRemindersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
      MedicationsRecyclerAdapter medicationsRecyclerAdapter =
          new MedicationsRecyclerAdapter(getActivity(), medicationsList, medicationDAO);
      medicationRemindersRecyclerView.addItemDecoration(
          new SimpleDividerItemDecoration(getActivity()));
      medicationRemindersRecyclerView.setAdapter(medicationsRecyclerAdapter);
    }
  }

  private void getUserProfile() {
    dataAccessHandler.getUserProfile(new Callback<UserProfileResponse>() {
      @Override public void onResponse(Call<UserProfileResponse> call,
          Response<UserProfileResponse> response) {
        switch (response.code()) {
          case 200:
            UserProfileResponseDatum userProfileData =
                response.body().getData().getBody().getData();

            SharedPreferences.Editor prefsEditor = prefs.edit();

            if (userProfileData != null) {

              /**
               * Set the weight
               */
              if (userProfileData.getWeight() != null && !userProfileData.getWeight().isEmpty()) {
                prefsEditor.putFloat(Constants.EXTRAS_USER_PROFILE_WEIGHT,
                    Float.parseFloat(userProfileData.getWeight()));
                metricCounterTV.setText(String.valueOf(String.format(Locale.getDefault(), "%.1f",
                    Float.parseFloat(userProfileData.getWeight()))));
              }

              prefsEditor.apply();

              break;
            }
        }
      }

      @Override public void onFailure(Call<UserProfileResponse> call, Throwable t) {
        Timber.d("Call failed with error : %s", t.getMessage());
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
        alertDialog.show();
      }
    });
  }

  private void getWidgets() {
    dataAccessHandler.getWidgets("medical", new Callback<WidgetsResponse>() {
      @Override
      public void onResponse(Call<WidgetsResponse> call, Response<WidgetsResponse> response) {
        switch (response.code()) {
          case 200:
            List<WidgetsResponseDatum> widgetsFromResponse =
                response.body().getData().getBody().getData();

            for (int i = 0; i < widgetsFromResponse.size(); i++) {
              HealthWidget widget = new HealthWidget();

              widget.setId(widgetsFromResponse.get(i).getWidgetId());
              widget.setMeasurementUnit(widgetsFromResponse.get(i).getUnit());
              widget.setPosition(i);
              widget.setValue(Float.parseFloat(widgetsFromResponse.get(i).getTotal()));
              widget.setTitle(widgetsFromResponse.get(i).getName());
              widget.setSlug(widgetsFromResponse.get(i).getSlug());

              healthWidgetsMap.add(widget);
            }

            setupWidgetViews(healthWidgetsMap);

            break;
        }
      }

      @Override public void onFailure(Call<WidgetsResponse> call, Throwable t) {
        Timber.d("Call failed with error : %s", t.getMessage());
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
        alertDialog.show();
      }
    });
  }

  private void getTakenMedicalTests() {
    dataAccessHandler.getTakenMedicalTests(new Callback<TakenMedicalTestsResponse>() {
      @Override public void onResponse(Call<TakenMedicalTestsResponse> call,
          Response<TakenMedicalTestsResponse> response) {
        switch (response.code()) {
          case 200:
            List<TakenMedicalTestsResponseBody> takenMedicalTests =
                response.body().getData().getBody();

            loadingTestsProgressBar.setVisibility(View.GONE);

            if (takenMedicalTests.isEmpty()) {
              userTestsListView.setVisibility(View.GONE);
              medicalTestsEmptyLayout.setVisibility(View.VISIBLE);
            } else {
              userTestsListView.setVisibility(View.VISIBLE);
              medicalTestsEmptyLayout.setVisibility(View.GONE);

              RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
              UserTestsRecyclerAdapter userTestsRecyclerAdapter =
                  new UserTestsRecyclerAdapter(getActivity().getApplication(), takenMedicalTests);
              ItemTouchHelper.Callback callback =
                  new SimpleSwipeItemTouchHelperCallback(userTestsRecyclerAdapter);
              ItemTouchHelper touchHelper = new ItemTouchHelper(callback);

              userTestsListView.setLayoutManager(mLayoutManager);
              userTestsListView.setNestedScrollingEnabled(false);
              userTestsListView.setAdapter(userTestsRecyclerAdapter);
              userTestsListView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
              touchHelper.attachToRecyclerView(userTestsListView);
            }

            break;
        }
      }

      @Override public void onFailure(Call<TakenMedicalTestsResponse> call, Throwable t) {
        Timber.d("Call failed with error : %s", t.getMessage());
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setMessage(getString(R.string.error_response_from_server_incorrect));
        alertDialog.show();
      }
    });
  }

  @Subscribe public void updateWidgetsOrder(HealthWidgetsOrderChangedEvent event) {
    healthWidgetsMap = event.getWidgetsMapHealth();
    setupWidgetViews(healthWidgetsMap);
  }

  @Subscribe public void reflectMedicalTestEditCreate(MedicalTestEditCreateEvent event) {
    getWidgets();
    getTakenMedicalTests();
  }

  @Subscribe public void updateMedicationRemindersList(MedicationItemCreatedEvent event) {
    setupMedicationRemindersList(medicationDAO.queryForAll());
  }

  @Override public void onDestroy() {
    super.onDestroy();
    EventBusSingleton.getInstance().unregister(this);
  }

  private void setupWidgetViews(ArrayList<HealthWidget> healthWidgetsMap) {
    if (!healthWidgetsMap.isEmpty() && healthWidgetsMap.size() > 4) {
      healthWidgetsMap = new ArrayList<>(healthWidgetsMap.subList(0, 4));

      HealthWidgetsRecyclerAdapter healthWidgetsGridAdapter =
          new HealthWidgetsRecyclerAdapter(getActivity(), healthWidgetsMap,
              R.layout.grid_item_health_widgets);
      widgetsGridView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
      widgetsGridView.setAdapter(healthWidgetsGridAdapter);

      loadingWidgetsProgressBar.setVisibility(View.GONE);
    }
  }
}