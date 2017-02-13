package com.mcsaatchi.gmfit.health.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.health.models.DayChoice;
import java.util.ArrayList;

public class DaysChoiceRecyclerAdapter
    extends RecyclerView.Adapter<DaysChoiceRecyclerAdapter.RecyclerViewHolder> {
  private ArrayList<DayChoice> days;

  public DaysChoiceRecyclerAdapter(ArrayList<DayChoice> days) {
    this.days = days;
  }

  @Override public int getItemCount() {
    return days.size();
  }

  public long getItemId(int position) {
    return 0;
  }

  @Override public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.list_item_days_choice, parent, false);

    return new RecyclerViewHolder(itemView);
  }

  @Override public void onBindViewHolder(RecyclerViewHolder holder, int position) {
    if (days.get(position).isDaySelected()) {
      holder.dayCheckedLayout.setVisibility(View.VISIBLE);
    } else {
      holder.dayCheckedLayout.setVisibility(View.GONE);
    }

    holder.dayNameTV.setText((days.get(position)).getDayName());
  }

  class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView dayNameTV;
    LinearLayout dayCheckedLayout;

    RecyclerViewHolder(View itemView) {
      super(itemView);

      dayNameTV = (TextView) itemView.findViewById(R.id.dayNameTV);
      dayCheckedLayout = (LinearLayout) itemView.findViewById(R.id.dayCheckedLayout);

      itemView.setOnClickListener(this);
    }

    @Override public void onClick(View view) {
      if (days.get(getAdapterPosition()).isDaySelected()) {
        days.get(getAdapterPosition()).setDaySelected(false);
        dayCheckedLayout.setVisibility(View.GONE);
      } else {
        days.get(getAdapterPosition()).setDaySelected(true);
        dayCheckedLayout.setVisibility(View.VISIBLE);
      }
    }
  }
}