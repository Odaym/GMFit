package com.mcsaatchi.gmfit.insurance.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.insurance.models.FilterChoice;
import java.util.ArrayList;

public class FilterChoiceRecyclerAdapter
    extends RecyclerView.Adapter<FilterChoiceRecyclerAdapter.RecyclerViewHolder> {
  private ArrayList<FilterChoice> choices;

  public FilterChoiceRecyclerAdapter(ArrayList<FilterChoice> choices) {
    this.choices = choices;
  }

  @Override public int getItemCount() {
    return choices.size();
  }

  public long getItemId(int position) {
    return 0;
  }

  @Override public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.list_item_filter_choice, parent, false);

    return new RecyclerViewHolder(itemView);
  }

  @Override public void onBindViewHolder(RecyclerViewHolder holder, int position) {
    if (choices.get(position).isSelected()) {
      holder.choiceCheckedLayout.setVisibility(View.VISIBLE);
    } else {
      holder.choiceCheckedLayout.setVisibility(View.GONE);
    }

    holder.choiceNameTV.setText((choices.get(position)).getName());
  }

  class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView choiceNameTV;
    LinearLayout choiceCheckedLayout;

    RecyclerViewHolder(View itemView) {
      super(itemView);

      choiceNameTV = (TextView) itemView.findViewById(R.id.choiceNameTV);
      choiceCheckedLayout = (LinearLayout) itemView.findViewById(R.id.choiceCheckedLayout);

      itemView.setOnClickListener(this);
    }

    @Override public void onClick(View view) {
      if (choices.get(getAdapterPosition()).isSelected()) {
        choices.get(getAdapterPosition()).setSelected(false);
        choiceCheckedLayout.setVisibility(View.GONE);
      } else {
        choices.get(getAdapterPosition()).setSelected(true);
        choiceCheckedLayout.setVisibility(View.VISIBLE);
      }
    }
  }
}