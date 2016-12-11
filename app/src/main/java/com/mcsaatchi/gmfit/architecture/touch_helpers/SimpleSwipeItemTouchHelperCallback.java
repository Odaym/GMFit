package com.mcsaatchi.gmfit.architecture.touch_helpers;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class SimpleSwipeItemTouchHelperCallback extends ItemTouchHelper.Callback {

  private final DragSwipeItemTouchHelperAdapter mAdapter;

  public SimpleSwipeItemTouchHelperCallback(DragSwipeItemTouchHelperAdapter adapter) {
    mAdapter = adapter;
  }

  @Override public boolean isItemViewSwipeEnabled() {
    return true;
  }

  @Override public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
    mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
  }

  @Override
  public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
    int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
    return makeMovementFlags(0, swipeFlags);
  }

  @Override public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
      RecyclerView.ViewHolder target) {
    return false;
  }
}