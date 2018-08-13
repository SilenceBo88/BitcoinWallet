package com.lrkj.bitcoinwallet.major.receive;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.widget.ImageView;

import com.lrkj.bitcoinwallet.MyApplication;
import com.lrkj.bitcoinwallet.R;
import com.lrkj.bitcoinwallet.base.BaseViewModel;
import com.lrkj.bitcoinwallet.core.BtcWalletManager;
import com.lrkj.bitcoinwallet.major.main.MainActivity;
import com.lrkj.bitcoinwallet.util.QRCodeUtils;

import io.reactivex.disposables.Disposable;

public class ReceiveCoinViewModel extends BaseViewModel {

    @Nullable
    private Disposable sendCoinDisposable;

    @NonNull
    private final BtcWalletManager btcWalletManager;

    private String address;

    public ReceiveCoinViewModel(@NonNull BtcWalletManager btcWalletManager, String address) {
        this.btcWalletManager = btcWalletManager;
        this.address = address;
    }

    void stop() {
        if (sendCoinDisposable != null && !sendCoinDisposable.isDisposed()) {
            sendCoinDisposable.dispose();
        }
    }

    public void back(@NonNull FragmentManager fragmentManager) {
        fragmentManager.popBackStack();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

   /* public void createQR(@NonNull Activity activity){
        ImageView qrImageView = activity.findViewById(R.id.QR);
        Bitmap mBitmap = QRCodeUtils.createQRCodeBitmap(address, 480, 480);
        qrImageView.setImageBitmap(mBitmap);
    }*/
}
