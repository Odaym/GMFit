package com.mcsaatchi.gmfit.fitness.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.UserActivitiesResponseBody;
import com.mcsaatchi.gmfit.fitness.activities.AddActivityDetailsActivity;
import java.util.List;

public class UserActivitiesListRecyclerAdapter extends RecyclerView.Adapter {
  private Context context;
  private List<UserActivitiesResponseBody> userActivitiesResponseBodies;

  public UserActivitiesListRecyclerAdapter(Context context,
      List<UserActivitiesResponseBody> userActivitiesResponseBodies) {
    this.context = context;
    this.userActivitiesResponseBodies = userActivitiesResponseBodies;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View view = inflater.inflate(R.layout.list_item_user_activities_list, parent, false);

    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
    final ViewHolder holder = (ViewHolder) h;

    holder.activityNameTV.setText(userActivitiesResponseBodies.get(position).getActivityName());
    holder.durationAndLevelTV.setText(userActivitiesResponseBodies.get(position).getDuration()
        + " - "
        + userActivitiesResponseBodies.get(position).getName());
    holder.caloriesBurnedTV.setText(String.format("%.2f",
        userActivitiesResponseBodies.get(position).getCalories()) + " kcal");
  }

  @Override public int getItemCount() {
    return userActivitiesResponseBodies.size();
  }

  public UserActivitiesResponseBody getItem(int position) {
    return userActivitiesResponseBodies.get(position);
  }

  private class ViewHolder extends RecyclerView.ViewHolder {
    private TextView activityNameTV, durationAndLevelTV, caloriesBurnedTV;

    public ViewHolder(View itemView) {
      super(itemView);

      activityNameTV = (TextView) itemView.findViewById(R.id.activityNameTV);
      durationAndLevelTV = (TextView) itemView.findViewById(R.id.durationAndLevelTV);
      caloriesBurnedTV = (TextView) itemView.findViewById(R.id.caloriesBurnedTV);

      itemView.setOnClickListener(view -> {
        Intent intent = new Intent(context, AddActivityDetailsActivity.class);
        intent.putExtra("ACTIVITY_ITEM", userActivitiesResponseBodies.get(getAdapterPosition()));
        intent.putExtra("CALL_PURPOSE_EDIT", true);
        context.startActivity(intent);
      });
    }
  }
}
