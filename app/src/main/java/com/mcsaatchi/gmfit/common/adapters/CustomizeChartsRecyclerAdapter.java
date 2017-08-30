package com.mcsaatchi.gmfit.common.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.mcsaatchi.gmfit.R;
import com.mcsaatchi.gmfit.architecture.otto.DataChartsOrderChangedEvent;
import com.mcsaatchi.gmfit.architecture.otto.EventBusSingleton;
import com.mcsaatchi.gmfit.architecture.touch_helpers.DragItemTouchHelperAdapter;
import com.mcsaatchi.gmfit.common.models.DataChart;
import java.util.Collections;
import java.util.List;

public class CustomizeChartsRecyclerAdapter
    extends RecyclerView.Adapter<CustomizeChartsRecyclerAdapter.MyViewHolder>
    implements DragItemTouchHelperAdapter {

  private List<DataChart> dataChartsMap;
  private int drawableResId;

  public CustomizeChartsRecyclerAdapter(List<DataChart> dataChartsMap, int drawableResId) {
    this.dataChartsMap = dataChartsMap;
    this.drawableResId = drawableResId;
  }

  @Override public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.list_item_one_with_icon, parent, false);

    return new MyViewHolder(itemView);
  }

  @Override public void onBindViewHolder(MyViewHolder holder, int position) {
    holder.itemNameTV.setText(dataChartsMap.get(position).getName());
    holder.itemIconRightIMG.setImageResource(drawableResId);
  }

  @Override public int getItemCount() {
    return dataChartsMap.size();
  }

  @Override public void onItemMove(int fromPosition, int toPosition) {
    if (fromPosition < toPosition) {
      for (int i = fromPosition; i < toPosition; i++) {
        Collections.swap(dataChartsMap, i, i + 1);
      }
    } else {
      for (int i = fromPosition; i > toPosition; i--) {
        Collections.swap(dataChartsMap, i, i - 1);
      }
    }

    int tempNumber = dataChartsMap.get(fromPosition).getPosition();
    dataChartsMap.get(fromPosition).setPosition(dataChartsMap.get(toPosition).getPosition());
    dataChartsMap.get(toPosition).setPosition(tempNumber);

    notifyItemMoved(fromPosition, toPosition);
  }

  @Override public void onClearView() {
    EventBusSingleton.getInstance().post(new DataChartsOrderChangedEvent(dataChartsMap));
  }

  class MyViewHolder extends RecyclerView.ViewHolder {
    TextView itemNameTV;
    ImageView itemIconRightIMG;

    MyViewHolder(View view) {
      super(view);
      itemNameTV = view.findViewById(R.id.itemNameTV);
      itemIconRightIMG = view.findViewById(R.id.itemIconRightIMG);
    }
  }
}
