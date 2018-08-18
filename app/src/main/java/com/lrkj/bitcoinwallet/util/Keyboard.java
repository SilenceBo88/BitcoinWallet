package com.lrkj.bitcoinwallet.util;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * 键盘工具类
 */
public class Keyboard {

    /**
     * 打开键盘
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
     * 关闭键盘
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
