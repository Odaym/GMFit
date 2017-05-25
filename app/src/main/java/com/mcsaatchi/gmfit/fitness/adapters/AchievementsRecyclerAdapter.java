package com.mcsaatchi.gmfit.fitness.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.retrofit.responses.AchievementsResponseBody;
import com.squareup.picasso.Picasso;
import java.util.List;

public class AchievementsRecyclerAdapter extends RecyclerView.Adapter {
  private Context context;
  private List<AchievementsResponseBody> achievementsResponseBodies;

  public AchievementsRecyclerAdapter(Context context,
      List<AchievementsResponseBody> achievementsResponseBodies) {
    this.context = context;
    this.achievementsResponseBodies = achievementsResponseBodies;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater =
        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View view = inflater.inflate(R.layout.list_item_achievements_list, parent, false);

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

    public ViewHolder(View itemView) {
      super(itemView);

      achievementImageIV = (ImageView) itemView.findViewById(R.id.achievementImageIV);
      achievementCompletedCheckmarkIV =
          (ImageView) itemView.findViewById(R.id.achievementCompletedCheckmarkIV);

      //itemView.setOnClickListener(view -> {
      //  Intent intent = new Intent(context, AchievementsListActivity.class);
      //  intent.putExtra("ACHIEVEMENT_ITEM", achievementsResponseBodies.get(getAdapterPosition()));
      //  context.startActivity(intent);
      //});
    }
  }
}
