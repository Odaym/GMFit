package com.mcsaatchi.gmfit.fitness.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.otto.FitnessActivityEvent;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ActivitiesListResponseBody;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.fitness.adapters.ActivitiesListRecyclerAdapter;
import com.squareup.otto.Subscribe;
import java.util.List;

public class ActivitiesListActivity extends BaseActivity
    implements ActivitiesListActivityPresenter.ActivitiesListActivityView {

  @Bind(R.id.toolbar) Toolbar toolbar;
  @Bind(R.id.activitiesRecycler) RecyclerView activitiesRecycler;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_activities_list);

    ButterKnife.bind(this);

    EventBusSingleton.getInstance().register(this);

    setupToolbar(getClass().getSimpleName(), toolbar,
        getResources().getString(R.string.add_activity_activity_title), true);

    ActivitiesListActivityPresenter presenter =
        new ActivitiesListActivityPresenter(this, dataAccessHandler);

    presenter.getAllActivities();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    EventBusSingleton.getInstance().unregister(this);
  }

  @Override
  public void displayAllActivities(List<ActivitiesListResponseBody> activitiesListResponseBodies) {
    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
    ActivitiesListRecyclerAdapter operationContactsRecyclerAdapter =
        new ActivitiesListRecyclerAdapter(this, activitiesListResponseBodies);

    activitiesRecycler.setLayoutManager(mLayoutManager);
    activitiesRecycler.setAdapter(operationContactsRecyclerAdapter);
    activitiesRecycler.setNestedScrollingEnabled(false);
    activitiesRecycler.addItemDecoration(new SimpleDividerItemDecoration(this));
  }

  @Subscribe public void handleFitnessActivityEvent(FitnessActivityEvent event){
    finish();
  }
}
