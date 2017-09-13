package com.mcsaatchi.gmfit.profile.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.AchievementsResponseBody;
import com.squareup.picasso.Picasso;
import java.util.List;

public class AchievementDetailsRecyclerAdapter extends RecyclerView.Adapter {
  private Context context;
  private List<AchievementsResponseBody> achievementsResponseBodies;

  public AchievementDetailsRecyclerAdapter(Context context,
      List<AchievementsResponseBody> achievementsResponseBodies) {
    this.context = context;
    this.achievementsResponseBodies = achievementsResponseBodies;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View view = inflater.inflate(R.layout.list_item_achievement_details_list, parent, false);

    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
    final ViewHolder holder = (ViewHolder) h;

    Picasso.with(context)
        .load(achievementsResponseBodies.get(position).getImage())
        .into(holder.achievementImageIV);

    if (achievementsResponseBodies.get(position).getIsDone()) {
      holder.achievementCompletedCheckmarkIV.setVisibility(View.VISIBLE);
    } else {
      holder.achievementCompletedCheckmarkIV.setVisibility(View.INVISIBLE);
    }

    holder.achievementNameTV.setText(achievementsResponseBodies.get(position).getName());
    holder.achievementDetailsTV.setText(achievementsResponseBodies.get(position).getDescription());

    String timeCounter;
    if (achievementsResponseBodies.get(position).getFinishes() == 1) {
      timeCounter = "time";
    } else {
      timeCounter = "times";
    }

    holder.achievementDateCompletedTV.setText(
        "Last completed: " + achievementsResponseBodies.get(position)
            .getLastFinish()
            .split(" ")[0]);

    holder.achievementTimesCompletedTV.setText(
        achievementsResponseBodies.get(position).getFinishes() + " " + timeCounter);

    holder.achievementCompletedPercentageTV.setText(
        achievementsResponseBodies.get(position).getProgress() + "%");
    holder.completionProgressBar.setProgress(
        achievementsResponseBodies.get(position).getProgress());
  }

  @Override public int getItemCount() {
    return achievementsResponseBodies.size();
  }

  public AchievementsResponseBody getItem(int position) {
    return achievementsResponseBodies.get(position);
  }

  private class ViewHolder extends RecyclerView.ViewHolder {
    private ImageView achievementImageIV;
    private ImageView achievementCompletedCheckmarkIV;
    private TextView achievementNameTV;
    private TextView achievementDetailsTV;
    private TextView achievementTimesCompletedTV;
    private TextView achievementDateCompletedTV;
    private TextView achievementCompletedPercentageTV;
    private ProgressBar completionProgressBar;

    public ViewHolder(View itemView) {
      super(itemView);

      achievementImageIV = itemView.findViewById(R.id.achievementImageIV);
      achievementCompletedCheckmarkIV = itemView.findViewById(R.id.achievementCompletedCheckmarkIV);
      achievementNameTV = itemView.findViewById(R.id.achievementNameTV);
      achievementDetailsTV = itemView.findViewById(R.id.achievementDetailsTV);
      achievementTimesCompletedTV = itemView.findViewById(R.id.achievementTimesCompletedTV);
      achievementDateCompletedTV = itemView.findViewById(R.id.achievementDateCompletedTV);
      achievementCompletedPercentageTV =
          itemView.findViewById(R.id.achievementCompletedPercentageTV);
      completionProgressBar = itemView.findViewById(R.id.completionProgressBar);
    }
  }
}
