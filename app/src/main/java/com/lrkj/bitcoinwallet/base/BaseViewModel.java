package com.lrkj.bitcoinwallet.base;

import android.databinding.BaseObservable;
import android.databinding.Observable;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.lrkj.bitcoinwallet.R;

/**
 * ViewModel的父类
 */
public abstract class BaseViewModel extends BaseObservable {

    @NonNull
    private final ObservableField<Integer> snackBarMessageResId = new ObservableField<>(R.string.empty);

    public void addOnMessageChangedCallback(@NonNull Observable.OnPropertyChangedCallback callback) {
        snackBarMessageResId.addOnPropertyChangedCallback(callback);
    }

    public void removeOnMessageChangedCallback(@NonNull Observable.OnPropertyChangedCallback callback) {
        snackBarMessageResId.removeOnPropertyChangedCallback(callback);
    }

    @StringRes
    public int getSnackBarMessageResId() {
        return snackBarMessageResId.get();
    }

    /**
     * 底部提示框
     * @param resId
     */
    protected void showSnackBarMessage(@StringRes int resId) {
        snackBarMessageResId.set(resId);
    }
}
