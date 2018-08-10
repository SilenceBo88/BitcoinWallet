package com.lrkj.bitcoinwallet.major.main;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * 滚动刷新效果
 */
public class ScrollChildSwipeRefreshLayout extends SwipeRefreshLayout {

  private static final int DIRECTION_UP = -1;

  @Nullable
  private View scrollUpChild;

  public ScrollChildSwipeRefreshLayout(@NonNull Context context) {
    super(context);
  }

  public ScrollChildSwipeRefreshLayout(@NonNull Context context, @NonNull AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public boolean canChildScrollUp() {
    return scrollUpChild != null ? scrollUpChild.canScrollVertically(DIRECTION_UP) : super.canChildScrollUp();
  }

  public void setScrollUpChild(@Nullable View scrollUpChild) {
    this.scrollUpChild = scrollUpChild;
  }

  /**
   * 重新加载数据
   * @param view
   * @param viewModel
   */
  @BindingAdapter("android:onRefresh")
  public static void setSwipeRefreshLayoutOnRefreshListener(@NonNull SwipeRefreshLayout view, @NonNull MainViewModel viewModel) {
    view.setOnRefreshListener(viewModel::loadTxs);
  }
}