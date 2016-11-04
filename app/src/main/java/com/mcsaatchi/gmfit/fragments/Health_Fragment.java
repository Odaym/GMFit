package com.mcsaatchi.gmfit.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.activities.AddNewHealthTest_Activity;
import com.mcsaatchi.gmfit.adapters.HealthWidgets_GridAdapter;
import com.mcsaatchi.gmfit.adapters.UserTestsRecycler_Adapter;
import com.mcsaatchi.gmfit.classes.Constants;
import com.mcsaatchi.gmfit.classes.EventBus_Poster;
import com.mcsaatchi.gmfit.classes.EventBus_Singleton;
import com.mcsaatchi.gmfit.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.models.HealthWidget;
import com.mcsaatchi.gmfit.rest.WidgetsResponse;
import com.mcsaatchi.gmfit.rest.WidgetsResponseDatum;
import com.mcsaatchi.gmfit.rest.TakenMedicalTestsResponse;
import com.mcsaatchi.gmfit.rest.TakenMedicalTestsResponseBody;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Health_Fragment extends Fragment {

    private static final String TAG = "Health_Fragment";
    @Bind(R.id.metricCounterTV)
    TextView metricCounterTV;
    @Bind(R.id.widgetsGridView)
    GridView widgetsGridView;
    @Bind(R.id.addEntryBTN_MEDICAL_TESTS)
    TextView addEntryBTN_MEDICAL_TESTS;
    @Bind(R.id.userTestsListView)
    RecyclerView userTestsListView;
    @Bind(R.id.loadingWidgetsProgressBar)
    ProgressBar loadingWidgetsProgressBar;
    @Bind(R.id.loadingTestsProgressBar)
    ProgressBar loadingTestsProgressBar;

    private ArrayList<HealthWidget> healthWidgetsMap = new ArrayList<>();
    private SharedPreferences prefs;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        prefs = getActivity().getSharedPreferences(Constants.SHARED_PREFS_TITLE, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_health, container, false);

        ButterKnife.bind(this, fragmentView);

        EventBus_Singleton.getInstance().register(this);

        setHasOptionsMenu(true);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.health_tab_title);

        getWidgets();

        getTakenMedicalTests();

        metricCounterTV.setText(String.valueOf((int) prefs.getFloat(Constants.EXTRAS_USER_PROFILE_WEIGHT, 0)));

        addEntryBTN_MEDICAL_TESTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddNewHealthTest_Activity.class);
                startActivity(intent);
            }
        });

        return fragmentView;
    }

    private void getWidgets() {
        DataAccessHandler.getInstance().getWidgets(prefs.getString(Constants.PREF_USER_ACCESS_TOKEN, Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS), "medical", new Callback<WidgetsResponse>() {
            @Override
            public void onResponse(Call<WidgetsResponse> call, Response<WidgetsResponse> response) {
                switch (response.code()) {
                    case 200:
                        List<WidgetsResponseDatum> widgetsFromResponse = response.body().getData().getBody().getData();

                        for (int i = 0; i < widgetsFromResponse.size(); i++) {
                            HealthWidget widget = new HealthWidget();

                            widget.setId(widgetsFromResponse.get(i).getWidgetId());
                            widget.setMeasurementUnit(widgetsFromResponse.get(i).getUnit());
                            widget.setPosition(Integer.parseInt(widgetsFromResponse.get(i).getPosition()));
                            widget.setValue(widgetsFromResponse.get(i).getTotal());
                            widget.setTitle(widgetsFromResponse.get(i).getName());
                            widget.setSlug(widgetsFromResponse.get(i).getSlug());

                            healthWidgetsMap.add(widget);
                        }

                        if (!healthWidgetsMap.isEmpty() && healthWidgetsMap.size() > 4) {
                            healthWidgetsMap = new ArrayList<>(healthWidgetsMap.subList(0, 4));

                            HealthWidgets_GridAdapter healthWidgetsGridAdapter = new HealthWidgets_GridAdapter(getActivity(), healthWidgetsMap, R.layout.grid_item_health_widgets);

                            widgetsGridView.setAdapter(healthWidgetsGridAdapter);

                            loadingWidgetsProgressBar.setVisibility(View.GONE);
                        }

                        break;
                }
            }

            @Override
            public void onFailure(Call<WidgetsResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: Request failed");
            }
        });
    }

    private void getTakenMedicalTests() {
        DataAccessHandler.getInstance().getTakenMedicalTests(prefs.getString(Constants.PREF_USER_ACCESS_TOKEN, Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS), new Callback<TakenMedicalTestsResponse>() {
            @Override
            public void onResponse(Call<TakenMedicalTestsResponse> call, Response<TakenMedicalTestsResponse> response) {
                switch (response.code()) {
                    case 200:
                        List<TakenMedicalTestsResponseBody> takenMedicalTests = response.body().getData().getBody();

                        loadingTestsProgressBar.setVisibility(View.GONE);

                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                        UserTestsRecycler_Adapter userTestsRecyclerAdapter = new UserTestsRecycler_Adapter(getActivity(), takenMedicalTests);

                        userTestsListView.setLayoutManager(mLayoutManager);
                        userTestsListView.setNestedScrollingEnabled(false);
                        userTestsListView.setAdapter(userTestsRecyclerAdapter);
                        userTestsListView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));

                        break;
                }
            }

            @Override
            public void onFailure(Call<TakenMedicalTestsResponse> call, Throwable t) {

            }
        });
    }

    @Subscribe
    public void handle_BusEvents(EventBus_Poster ebp) {
        String ebpMessage = ebp.getMessage();

        switch (ebpMessage) {
            case Constants.EXTRAS_TEST_EDIT_OR_CREATE_DONE:
                getTakenMedicalTests();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus_Singleton.getInstance().unregister(this);
    }
}