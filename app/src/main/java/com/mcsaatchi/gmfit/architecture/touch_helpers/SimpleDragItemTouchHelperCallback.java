package com.mcsaatchi.gmfit.architecture.touch_helpers;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class SimpleDragItemTouchHelperCallback extends ItemTouchHelper.Callback {

  private final Drag_ItemTouchHelperAdapter mAdapter;

  public SimpleDragItemTouchHelperCallback(Drag_ItemTouchHelperAdapter adapter) {
    mAdapter = adapter;
  }

  @Override public boolean isLongPressDragEnabled() {
    return true;
  }

  @Override public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

  }

  @Override
  public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
    int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
    return makeMovementFlags(dragFlags, 0);
  }

  @Override public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
      RecyclerView.ViewHolder target) {
    mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
    return true;
  }

  @Override public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
    mAdapter.onClearView();
  }
}