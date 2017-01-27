package com.mcsaatchi.gmfit.health.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.health.adapters.DaysChoiceRecyclerAdapter;
import com.mcsaatchi.gmfit.health.models.DayChoice;
import java.util.ArrayList;

import static com.mcsaatchi.gmfit.health.activities.AddExistingMedicationActivity.REMINDER_DAYS_CHOSEN;

public class DaysChoiceListActivity extends BaseActivity {
  @Bind(R.id.daysRecyclerView) RecyclerView daysRecyclerView;
  @Bind(R.id.toolbar) Toolbar toolbar;

  private ArrayList<DayChoice> days = new ArrayList<>();

  private DaysChoiceRecyclerAdapter daysChoiceRecyclerAdapter;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_days_choice);

    ButterKnife.bind(this);

    setupToolbar(toolbar, getString(R.string.days_choice_activity_title), true);

    if (getIntent().getExtras() != null) {
      days = getIntent().getExtras().getParcelableArrayList("REMINDER_DAYS");
    } else {
      days = setupDays();
    }

    setupDaysChoiceList(days);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.pill_reminder_days_choice, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.selectAllitem:
        for (DayChoice day : days) {
          day.setDaySelected(true);
        }

        daysChoiceRecyclerAdapter.notifyDataSetChanged();

        break;
    }
    return super.onOptionsItemSelected(item);
  }

  private ArrayList<DayChoice> setupDays() {
    ArrayList<DayChoice> daysToReturn = new ArrayList<>();
    daysToReturn.add(new DayChoice("Monday", false));
    daysToReturn.add(new DayChoice("Tuesday", false));
    daysToReturn.add(new DayChoice("Wednesday", false));
    daysToReturn.add(new DayChoice("Thursday", false));
    daysToReturn.add(new DayChoice("Friday", false));
    daysToReturn.add(new DayChoice("Saturday", false));
    daysToReturn.add(new DayChoice("Sunday", false));

    return daysToReturn;
  }

  private void setupDaysChoiceList(ArrayList<DayChoice> daysToSetup) {
    daysChoiceRecyclerAdapter = new DaysChoiceRecyclerAdapter(this, daysToSetup);
    daysRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    daysRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
    daysRecyclerView.setAdapter(daysChoiceRecyclerAdapter);
  }

  @Override public void finish() {
    Intent intent = new Intent();
    intent.putParcelableArrayListExtra("REMINDER_DAYS", days);
    setResult(REMINDER_DAYS_CHOSEN, intent);

    super.finish();
  }
}
