package com.mcsaatchi.gmfit.fitness.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.health.models.SelectionItem;
import java.util.ArrayList;

public class ActivityLevelsChoiceRecyclerAdapter
    extends RecyclerView.Adapter<ActivityLevelsChoiceRecyclerAdapter.RecyclerViewHolder> {
  private ArrayList<SelectionItem> activityLevels;
  private int activityLevelID;
  private boolean editingPurpose = false;

  public ActivityLevelsChoiceRecyclerAdapter(ArrayList<SelectionItem> activityLevels,
      int activityLevelID) {
    this.activityLevels = activityLevels;
    this.activityLevelID = activityLevelID;

    if (activityLevelID != -1) {
      editingPurpose = true;
    }
  }

  @Override public int getItemCount() {
    return activityLevels.size();
  }

  public long getItemId(int position) {
    return 0;
  }

  @Override public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.list_item_activity_level_choice, parent, false);

    return new RecyclerViewHolder(itemView);
  }

  @Override public void onBindViewHolder(RecyclerViewHolder holder, int position) {
    if (activityLevels.get(position).getId() == activityLevelID) {
      activityLevels.get(position).setItemSelected(true);
    }

    if (activityLevels.get(position).isItemSelected()) {
      holder.activityCheckedLayout.setVisibility(View.VISIBLE);
    } else {
      holder.activityCheckedLayout.setVisibility(View.GONE);
    }

    holder.activityLevelTV.setText((activityLevels.get(position)).getSelectionName());
  }

  class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView activityLevelTV;
    LinearLayout activityCheckedLayout;

    RecyclerViewHolder(View itemView) {
      super(itemView);

      activityLevelTV = itemView.findViewById(R.id.activityLevelTV);
      activityCheckedLayout = (LinearLayout) itemView.findViewById(R.id.activityCheckedLayout);

      itemView.setOnClickListener(this);
    }

    @Override public void onClick(View view) {

      //Remove selection from pre-existing activity level
      activityLevelID = -1;

      if (activityLevels.get(getAdapterPosition()).isItemSelected()) {
        activityLevels.get(getAdapterPosition()).setItemSelected(false);
        activityCheckedLayout.setVisibility(View.GONE);
      } else {
        activityLevels.get(getAdapterPosition()).setItemSelected(true);
        activityCheckedLayout.setVisibility(View.VISIBLE);

        for (SelectionItem activityLevel : activityLevels) {
          activityLevel.setItemSelected(false);
        }

        activityLevels.get(getAdapterPosition()).setItemSelected(true);
      }

      notifyDataSetChanged();
    }
  }
}