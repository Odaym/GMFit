package com.mcsaatchi.gmfit.health.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.health.activities.AddNewHealthTest_Activity;
import com.mcsaatchi.gmfit.health.adapters.HealthWidgets_GridAdapter;
import com.mcsaatchi.gmfit.health.adapters.UserTestsRecycler_Adapter;
import com.mcsaatchi.gmfit.common.Constants;
import com.mcsaatchi.gmfit.architecture.otto.EventBusPoster;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.GMFit_Application;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.architecture.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.health.models.HealthWidget;
import com.mcsaatchi.gmfit.architecture.rest.TakenMedicalTestsResponse;
import com.mcsaatchi.gmfit.architecture.rest.TakenMedicalTestsResponseBody;
import com.mcsaatchi.gmfit.architecture.rest.WidgetsResponse;
import com.mcsaatchi.gmfit.architecture.rest.WidgetsResponseDatum;
import com.squareup.otto.Subscribe;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class Health_Fragment extends Fragment {

  @Inject DataAccessHandler dataAccessHandler;
  @Inject SharedPreferences prefs;

  @Bind(R.id.metricCounterTV) TextView metricCounterTV;
  @Bind(R.id.widgetsGridView) GridView widgetsGridView;
  @Bind(R.id.addEntryBTN_MEDICAL_TESTS) TextView addEntryBTN_MEDICAL_TESTS;
  @Bind(R.id.userTestsListView) RecyclerView userTestsListView;
  @Bind(R.id.loadingWidgetsProgressBar) ProgressBar loadingWidgetsProgressBar;
  @Bind(R.id.loadingTestsProgressBar) ProgressBar loadingTestsProgressBar;

  private ArrayList<HealthWidget> healthWidgetsMap = new ArrayList<>();

  @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    View fragmentView = inflater.inflate(R.layout.fragment_health, container, false);

    ButterKnife.bind(this, fragmentView);

    EventBusSingleton.getInstance().register(this);

    setHasOptionsMenu(true);

    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.health_tab_title);
    ((GMFit_Application) getActivity().getApplication()).getAppComponent().inject(this);

    getWidgets();

    getTakenMedicalTests();

    metricCounterTV.setText(
        String.valueOf((int) prefs.getFloat(Constants.EXTRAS_USER_PROFILE_WEIGHT, 0)));

    addEntryBTN_MEDICAL_TESTS.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Intent intent = new Intent(getActivity(), AddNewHealthTest_Activity.class);
        startActivity(intent);
      }
    });

    return fragmentView;
  }

  private void getWidgets() {
    dataAccessHandler.getWidgets(
        prefs.getString(Constants.PREF_USER_ACCESS_TOKEN, Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS),
        "medical", new Callback<WidgetsResponse>() {
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
                  widget.setPosition(Integer.parseInt(widgetsFromResponse.get(i).getPosition()));
                  widget.setValue(Float.parseFloat(widgetsFromResponse.get(i).getTotal()));
                  widget.setTitle(widgetsFromResponse.get(i).getName());
                  widget.setSlug(widgetsFromResponse.get(i).getSlug());

                  healthWidgetsMap.add(widget);
                }

                if (!healthWidgetsMap.isEmpty() && healthWidgetsMap.size() > 4) {
                  healthWidgetsMap = new ArrayList<>(healthWidgetsMap.subList(0, 4));

                  HealthWidgets_GridAdapter healthWidgetsGridAdapter =
                      new HealthWidgets_GridAdapter(getActivity(), healthWidgetsMap,
                          R.layout.grid_item_health_widgets);

                  widgetsGridView.setAdapter(healthWidgetsGridAdapter);

                  loadingWidgetsProgressBar.setVisibility(View.GONE);
                }

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
    dataAccessHandler.getTakenMedicalTests(
        prefs.getString(Constants.PREF_USER_ACCESS_TOKEN, Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS),
        new Callback<TakenMedicalTestsResponse>() {
          @Override public void onResponse(Call<TakenMedicalTestsResponse> call,
              Response<TakenMedicalTestsResponse> response) {
            switch (response.code()) {
              case 200:
                List<TakenMedicalTestsResponseBody> takenMedicalTests =
                    response.body().getData().getBody();

                loadingTestsProgressBar.setVisibility(View.GONE);

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                UserTestsRecycler_Adapter userTestsRecyclerAdapter =
                    new UserTestsRecycler_Adapter(getActivity(), takenMedicalTests);

                userTestsListView.setLayoutManager(mLayoutManager);
                userTestsListView.setNestedScrollingEnabled(false);
                userTestsListView.setAdapter(userTestsRecyclerAdapter);
                userTestsListView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));

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

  @Subscribe public void handle_BusEvents(EventBusPoster ebp) {
    String ebpMessage = ebp.getMessage();

    switch (ebpMessage) {
      case Constants.EXTRAS_TEST_EDIT_OR_CREATE_DONE:
        getTakenMedicalTests();
        break;
    }
  }

  @Override public void onDestroy() {
    super.onDestroy();
    EventBusSingleton.getInstance().unregister(this);
  }
}