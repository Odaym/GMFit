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
import com.mcsaatchi.gmfit.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.classes.UserTest;
import com.mcsaatchi.gmfit.data_access.DataAccessHandler;
import com.mcsaatchi.gmfit.models.HealthWidget;
import com.mcsaatchi.gmfit.rest.HealthWidgetsResponse;
import com.mcsaatchi.gmfit.rest.HealthWidgetsResponseDatum;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Health_Fragment extends Fragment {

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

    private ArrayList<HealthWidget> healthWidgetsMap = new ArrayList<>();

    private ArrayList<UserTest> userTests = new ArrayList<>();

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

        setHasOptionsMenu(true);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.health_tab_title);

        metricCounterTV.setText(String.valueOf(prefs.getFloat(Constants.EXTRAS_USER_PROFILE_WEIGHT, 0.0f)));

        getWidgets();

        UserTest userTest1 = new UserTest();
        userTest1.setName("Blood Sugar Tests");
        userTest1.setDateTaken("Sat Apr 2, 2016");

        UserTest userTest2 = new UserTest();
        userTest2.setName("Cholesterol Tests");
        userTest2.setDateTaken("Fri Mar 18, 2016");

        UserTest userTest3 = new UserTest();
        userTest3.setName("Liver Function Test");
        userTest3.setDateTaken("Sat Nov 7, 2015");

        UserTest userTest4 = new UserTest();
        userTest4.setName("Ullamcorper Fringilla");
        userTest4.setDateTaken("Fri Oct 18, 2015");

        userTests.add(userTest1);
        userTests.add(userTest2);
        userTests.add(userTest3);
        userTests.add(userTest4);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        UserTestsRecycler_Adapter userTestsRecyclerAdapter = new UserTestsRecycler_Adapter(getActivity(), userTests);

        userTestsListView.setLayoutManager(mLayoutManager);
        userTestsListView.setAdapter(userTestsRecyclerAdapter);
        userTestsListView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));

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
        DataAccessHandler.getInstance().getHealthWidgets(prefs.getString(Constants.PREF_USER_ACCESS_TOKEN, Constants.NO_ACCESS_TOKEN_FOUND_IN_PREFS), "medical", new Callback<HealthWidgetsResponse>() {
            @Override
            public void onResponse(Call<HealthWidgetsResponse> call, Response<HealthWidgetsResponse> response) {
                switch (response.code()) {
                    case 200:
                        List<HealthWidgetsResponseDatum> widgetsFromResponse = response.body().getData().getBody().getData();

                        for (int i = 0; i < widgetsFromResponse.size(); i++){
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
            public void onFailure(Call<HealthWidgetsResponse> call, Throwable t) {

            }
        });
    }
}
