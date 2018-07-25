package com.agmbat.meetyou.widget;

public interface OnItemChangedListener {

  boolean onItemMove(int fromPosition, int toPosition);

  void onItemDismiss(int position);
}
