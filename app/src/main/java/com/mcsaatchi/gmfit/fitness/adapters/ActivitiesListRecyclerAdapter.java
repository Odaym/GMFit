package com.mcsaatchi.gmfit.fitness.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.ActivitiesListResponseBody;
import java.util.List;

public class ActivitiesListRecyclerAdapter extends RecyclerView.Adapter {
  private Context context;
  private List<ActivitiesListResponseBody> activitiesListResponseBody;

  public ActivitiesListRecyclerAdapter(Context context,
      List<ActivitiesListResponseBody> activitiesListResponseBody) {
    this.context = context;
    this.activitiesListResponseBody = activitiesListResponseBody;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View view = inflater.inflate(R.layout.list_item_activities_list, parent, false);

    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
    final ViewHolder holder = (ViewHolder) h;

    holder.activityNameTV.setText(activitiesListResponseBody.get(position).getName());
  }

  @Override public int getItemCount() {
    return activitiesListResponseBody.size();
  }

  public ActivitiesListResponseBody getItem(int position) {
    return activitiesListResponseBody.get(position);
  }

  private class ViewHolder extends RecyclerView.ViewHolder {
    private TextView activityNameTV;

    public ViewHolder(View itemView) {
      super(itemView);

      activityNameTV = (TextView) itemView.findViewById(R.id.activityNameTV);

      //itemView.setOnClickListener(view -> {
      //  Intent intent = new Intent(context, OperationContactDetailsActivity.class);
      //  intent.putExtra("OPERATION_CONTACT",
      //      activitiesListResponseBody.get(getAdapterPosition()));
      //  context.startActivity(intent);
      //});
    }
  }
}
