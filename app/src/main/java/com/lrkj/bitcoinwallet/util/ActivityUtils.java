package com.lrkj.bitcoinwallet.util;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 活动工具类：帮助活动加载Fragment
 */
public final class ActivityUtils {

    private static final String TAG = "ActivityUtils";

    /**
     * 添加Fragment到活动
     *
     * @param fragmentManager
     * @param fragment
     * @param frameId
     */
    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, int frameId) {
        Log.d(TAG, "addFragmentToActivity");
        checkNotNull(fragmentManager);
        checkNotNull(fragment);
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameId, fragment);
        transaction.commitAllowingStateLoss();
    }

    /**
     * 替换Fragment
     *
     * @param fragmentManager
     * @param fragment
     * @param frameId
     */
    public static void replaceAndKeepOld(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, int frameId) {
        Log.d(TAG, "replaceAndKeepOld");
        checkNotNull(fragmentManager);
        checkNotNull(fragment);
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(frameId, fragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }
}
