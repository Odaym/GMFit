package com.mcsaatchi.gmfit.health.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import butterknife.OnClick;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.classes.GMFitApplication;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.otto.HealthWidgetsOrderChangedEvent;
import com.mcsaatchi.gmfit.architecture.otto.MedicalTestEditCreateEvent;
import com.mcsaatchi.gmfit.architecture.otto.MedicationItemCreatedEvent;
import com.mcsaatchi.gmfit.architecture.retrofit.architecture.DataAccessHandlerImpl;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.DefaultGetResponse;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.TakenMedicalTestsResponseBody;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UserProfileResponseDatum;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.WeightHistoryResponseDatum;
import com.mcsaatchi.gmfit.architecture.touch_helpers.SimpleSwipeItemTouchHelperCallback;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.common.activities.CustomizeWidgetsAndChartsActivity;
import com.mcsaatchi.gmfit.common.classes.Helpers;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.common.components.CustomLineChart;
import com.mcsaatchi.gmfit.common.fragments.BaseFragment;
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

public class HealthFragment extends BaseFragment
    implements HealthFragmentPresenter.HealthFragmentView {

  @Bind(R.id.metricCounterTV) TextView metricCounterTV;
  @Bind(R.id.widgetsGridView) RecyclerView widgetsGridView;
  @Bind(R.id.userTestsListView) RecyclerView userTestsListView;
  @Bind(R.id.loadingWidgetsProgressBar) ProgressBar loadingWidgetsProgressBar;
  @Bind(R.id.loadingTestsProgressBar) ProgressBar loadingTestsProgressBar;
  @Bind(R.id.medicationRemindersRecyclerView) RecyclerView medicationRemindersRecyclerView;
  @Bind(R.id.medicationsEmptyLayout) LinearLayout medicationsEmptyLayout;
  @Bind(R.id.medicalTestsEmptyLayout) LinearLayout medicalTestsEmptyLayout;
  @Bind(R.id.lineChartContainer) LinearLayout lineChartContainer;
  @Inject DataAccessHandlerImpl dataAccessHandler;
  @Inject RuntimeExceptionDao<Medication, Integer> medicationDAO;
  @Inject SharedPreferences prefs;
  private CustomLineChart customLineChart;
  private ArrayList<HealthWidget> healthWidgetsMap = new ArrayList<>();
  private HealthFragmentPresenter presenter;

  @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    View fragmentView = inflater.inflate(R.layout.fragment_health, container, false);

    ButterKnife.bind(this, fragmentView);

    try {
      EventBusSingleton.getInstance().register(this);
    } catch (IllegalArgumentException ignored) {
    }

    setHasOptionsMenu(true);

    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.health_tab_title);

    ((GMFitApplication) getActivity().getApplication()).getAppComponent().inject(this);

    presenter = new HealthFragmentPresenter(this, medicationDAO, dataAccessHandler);

    setupMedicationRemindersList(presenter.getMedicationsFromDB());

    presenter.getWidgets();

    presenter.getTakenMedicalTests();

    presenter.getUserWeight();

    presenter.setupUserWeightChart();

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

  @Override public void onDestroy() {
    super.onDestroy();
    EventBusSingleton.getInstance().unregister(this);
  }

  @Override public void setupWidgetViews(ArrayList<HealthWidget> healthWidgetsMap) {
    this.healthWidgetsMap = healthWidgetsMap;

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

  @Override
  public void displayTakenMedicalTests(List<TakenMedicalTestsResponseBody> takenMedicalTests) {
    loadingTestsProgressBar.setVisibility(View.GONE);

    if (takenMedicalTests.isEmpty()) {
      userTestsListView.setVisibility(View.GONE);
      medicalTestsEmptyLayout.setVisibility(View.VISIBLE);
    } else {
      userTestsListView.setVisibility(View.VISIBLE);
      medicalTestsEmptyLayout.setVisibility(View.GONE);

      if (getActivity() != null) {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        UserTestsRecyclerAdapter userTestsRecyclerAdapter =
            new UserTestsRecyclerAdapter(getActivity().getApplication(), takenMedicalTests);
        ItemTouchHelper.Callback callback = new SimpleSwipeItemTouchHelperCallback(userTestsRecyclerAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);

        userTestsListView.setLayoutManager(mLayoutManager);
        userTestsListView.setNestedScrollingEnabled(false);
        userTestsListView.setAdapter(userTestsRecyclerAdapter);
        userTestsListView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        touchHelper.attachToRecyclerView(userTestsListView);
      }
    }
  }

  @Override public void saveAndDisplayUserWeight(UserProfileResponseDatum userProfileData) {
    if (userProfileData != null) {
      if (userProfileData.getWeight() != null && !userProfileData.getWeight().isEmpty()) {
        prefs.edit()
            .putFloat(Constants.EXTRAS_USER_PROFILE_WEIGHT,
                Float.parseFloat(userProfileData.getWeight()))
            .apply();
        metricCounterTV.setText(String.valueOf(String.format(Locale.getDefault(), "%.1f",
            Float.parseFloat(userProfileData.getWeight()))));
      }
    }
  }

  @Override public void displayUserWeightChart(List<WeightHistoryResponseDatum> weightHistoryList) {
    if (getActivity() != null) {

      if (weightHistoryList != null) {
        customLineChart = new CustomLineChart(getActivity());

        customLineChart.setLineChartData(lineChartContainer, weightHistoryList);

        final TextView updateUserWeightTV =
            (TextView) customLineChart.getView().findViewById(R.id.updateWeightTV);

        updateUserWeightTV.setOnClickListener(view -> {
          final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
          dialogBuilder.setTitle(R.string.profile_edit_weight_dialog_title);

          View dialogView =
              LayoutInflater.from(getActivity()).inflate(R.layout.profile_edit_weight_dialog, null);
          final EditText editWeightET = (EditText) dialogView.findViewById(R.id.dialogWeightET);

          editWeightET.setText(
              String.valueOf(prefs.getFloat(Constants.EXTRAS_USER_PROFILE_WEIGHT, 0)));
          editWeightET.setSelection(editWeightET.getText().toString().length());

          dialogBuilder.setView(dialogView);
          dialogBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> {
            double userWeight = Double.parseDouble(editWeightET.getText().toString());

            updateUserWeight(editWeightET, userWeight,
                Helpers.prepareDateWithTimeForAPIRequest(new LocalDateTime()));
          });
          dialogBuilder.setNegativeButton(R.string.decline_cancel,
              (dialogInterface, i) -> dialogInterface.dismiss());

          AlertDialog alertDialog = dialogBuilder.create();
          alertDialog.show();
        });
      }
    }
  }

  @Subscribe public void updateWidgetsOrder(HealthWidgetsOrderChangedEvent event) {
    healthWidgetsMap = event.getWidgetsMapHealth();
    setupWidgetViews(healthWidgetsMap);

    int[] widgets = new int[healthWidgetsMap.size()];
    int[] positions = new int[healthWidgetsMap.size()];

    for (int i = 0; i < healthWidgetsMap.size(); i++) {
      widgets[i] = healthWidgetsMap.get(i).getWidget_id();
      positions[i] = healthWidgetsMap.get(i).getPosition();
    }

    presenter.updateUserWidgets(widgets, positions);
  }

  @Subscribe public void reflectMedicalTestEditCreate(MedicalTestEditCreateEvent event) {
    presenter.getWidgets();
    presenter.getTakenMedicalTests();
  }

  @Subscribe public void updateMedicationRemindersList(MedicationItemCreatedEvent event) {
    setupMedicationRemindersList(medicationDAO.queryForAll());
  }

  @OnClick(R.id.addEntryBTN_MEDICATIONS) public void handleAddMedication() {
    Intent intent = new Intent(getActivity(), SearchMedicationsActivity.class);
    startActivity(intent);
  }

  @OnClick(R.id.addEntryBTN_MEDICAL_TESTS) public void handleAddMedicalTest() {
    Intent intent = new Intent(getActivity(), AddNewHealthTestActivity.class);
    startActivity(intent);
  }

  private void updateUserWeight(final EditText editWeightET, double weight, String created_at) {
    final ProgressDialog waitingDialog = new ProgressDialog(getActivity());
    waitingDialog.setTitle(getString(R.string.updating_user_profile_dialog_title));
    waitingDialog.setMessage(getString(R.string.please_wait_dialog_message));
    waitingDialog.show();

    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
    alertDialog.setTitle(R.string.updating_user_profile_dialog_title);
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) -> {
      dialog.dismiss();

      if (waitingDialog.isShowing()) waitingDialog.dismiss();
    });

    dataAccessHandler.updateUserWeight(weight, created_at, new Callback<DefaultGetResponse>() {
      @Override
      public void onResponse(Call<DefaultGetResponse> call, Response<DefaultGetResponse> response) {
        switch (response.code()) {
          case 200:

            waitingDialog.dismiss();

            lineChartContainer.removeAllViews();
            lineChartContainer.invalidate();

            presenter.setupUserWeightChart();

            presenter.getUserWeight();

            InputMethodManager imm =
                (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editWeightET.getWindowToken(), 0);

            break;
        }
      }

      @Override public void onFailure(Call<DefaultGetResponse> call, Throwable t) {
        Timber.d("Call failed with error : %s", t.getMessage());
        alertDialog.setMessage(getString(R.string.server_error_got_returned));
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
}