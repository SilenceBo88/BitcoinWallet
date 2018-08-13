package com.lrkj.bitcoinwallet.util;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Keyboard {

  /**
   * open soft keyboard.
   */
  public static void showKeyBoard(@NonNull Context context, @NonNull View view) {
    try {
      final InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
      if (inputManager != null) {
        inputManager.showSoftInput(view, 0);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Closes opened soft keyboard.
   *
   * @param activity context
   */
  public static void hideSoftKeyboard(@NonNull Activity activity) {
    try {
      final View view = activity.getCurrentFocus();
      final InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
      if (view != null && inputManager != null) {
        view.clearFocus();
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
