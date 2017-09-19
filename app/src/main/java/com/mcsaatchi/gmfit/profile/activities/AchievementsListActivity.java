package com.mcsaatchi.gmfit.profile.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.AchievementsResponseBody;
import com.mcsaatchi.gmfit.common.activities.BaseActivity;
import com.mcsaatchi.gmfit.common.classes.SimpleDividerItemDecoration;
import com.mcsaatchi.gmfit.profile.adapters.AchievementDetailsRecyclerAdapter;
import java.util.List;

public class AchievementsListActivity extends BaseActivity {
  @Bind(R.id.achievementsRecycler) RecyclerView achievementsRecycler;
  @Bind(R.id.achievementsCompletionHintTV) TextView achievementsCompletionHintTV;
  @Bind(R.id.toolbar) Toolbar toolbar;

  private List<AchievementsResponseBody> achievementsResponseBodyList;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_achievement_details);

    ButterKnife.bind(this);

    if (getIntent().getExtras() != null) {
      achievementsResponseBodyList = getIntent().getExtras().getParcelableArrayList("ACHIEVEMENTS");

      setupToolbar(getClass().getSimpleName(), toolbar,
          getString(R.string.achievement_details_activity_title), true);

      showAchievements(achievementsResponseBodyList);

      calculateRemainingAchievements(achievementsResponseBodyList);
    }
  }

  private void showAchievements(List<AchievementsResponseBody> achievementsResponseBodyList) {
    AchievementDetailsRecyclerAdapter userActivitiesListRecyclerAdapter =
        new AchievementDetailsRecyclerAdapter(this, achievementsResponseBodyList);
    achievementsRecycler.setLayoutManager(new LinearLayoutManager(this));
    achievementsRecycler.addItemDecoration(new SimpleDividerItemDecoration(this));
    achievementsRecycler.setAdapter(userActivitiesListRecyclerAdapter);
  }

  private void calculateRemainingAchievements(
      List<AchievementsResponseBody> achievementsResponseBodyList) {

    int achievementsDone = 0;

    for (int i = 0; i < achievementsResponseBodyList.size(); i++) {
      if (achievementsResponseBodyList.get(i).getIsDone()) {
        achievementsDone++;
      }
    }

    achievementsCompletionHintTV.setText(getString(R.string.achievement_completed_sentence_part_1)
        + achievementsDone
        + getString(R.string.achievement_completed_sentence_part_2)
        + achievementsResponseBodyList.size()
        + getString(R.string.achievement_completed_sentence_part_3));
  }
}
