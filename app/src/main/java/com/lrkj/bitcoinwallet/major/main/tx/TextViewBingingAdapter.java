package com.lrkj.bitcoinwallet.major.main.tx;

import android.databinding.BindingAdapter;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

/**
 * TextView的数据绑定适配器
 */
public class TextViewBingingAdapter {

  /**
   * Set the text color with color resource. This uses data binding,
   * you can use {@code app:textColorRes} attribute for {@link TextView} in your XML layout.
   *
   * @param colorRes is the resource id of color.
   */
  @BindingAdapter("textColorRes")
  public static void setTextColorResource(@NonNull TextView view, @ColorRes int colorRes) {
    view.setTextColor(ContextCompat.getColor(view.getContext(), colorRes));
  }
}
