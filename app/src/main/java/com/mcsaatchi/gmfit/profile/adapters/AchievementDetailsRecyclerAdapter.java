package com.mcsaatchi.gmfit.profile.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    if (achievementsResponseBodies.get(position).getIsDone()) {
      holder.achievementCompletionProgressLayout.setVisibility(View.VISIBLE);

      holder.achievementCompletedPercentageTV.setText(
          achievementsResponseBodies.get(position).getProgress() + "%");
      holder.completionProgressBar.setProgress(
          achievementsResponseBodies.get(position).getProgress());
    } else {
      holder.achievementCompletedDetailsTV.setText(
          achievementsResponseBodies.get(position).getFinishes()
              + " times - Last completed: "
              + achievementsResponseBodies.get(position).getLastFinish());
    }
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
    private TextView achievementCompletedDetailsTV;
    private TextView achievementCompletedPercentageTV;
    private ProgressBar completionProgressBar;
    private LinearLayout achievementCompletionProgressLayout;

    public ViewHolder(View itemView) {
      super(itemView);

      achievementCompletionProgressLayout =
          (LinearLayout) itemView.findViewById(R.id.achievementCompletionProgressLayout);
      achievementImageIV = (ImageView) itemView.findViewById(R.id.achievementImageIV);
      achievementCompletedCheckmarkIV =
          (ImageView) itemView.findViewById(R.id.achievementCompletedCheckmarkIV);
      achievementNameTV = (TextView) itemView.findViewById(R.id.achievementNameTV);
      achievementDetailsTV = (TextView) itemView.findViewById(R.id.achievementDetailsTV);
      achievementCompletedDetailsTV =
          (TextView) itemView.findViewById(R.id.achievementCompletedDetailsTV);
      achievementCompletedPercentageTV =
          (TextView) itemView.findViewById(R.id.achievementCompletedPercentageTV);
      completionProgressBar = (ProgressBar) itemView.findViewById(R.id.completionProgressBar);
    }
  }
}
