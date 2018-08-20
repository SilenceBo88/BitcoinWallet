package com.lrkj.bitcoinwallet.major.send;

import android.app.Activity;
import android.content.DialogInterface;
import android.databinding.Bindable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import com.lrkj.bitcoinwallet.MyApplication;
import com.lrkj.bitcoinwallet.R;
import com.lrkj.bitcoinwallet.BR;
import com.lrkj.bitcoinwallet.base.BaseViewModel;
import com.lrkj.bitcoinwallet.core.BtcWalletManager;
import com.lrkj.bitcoinwallet.major.main.MainActivity;
import com.lrkj.bitcoinwallet.util.Keyboard;
import com.lrkj.bitcoinwallet.util.MD5Utils;
import com.lrkj.bitcoinwallet.util.SharedPreferencesUtils;

import org.bitcoinj.core.InsufficientMoneyException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static java.util.concurrent.TimeUnit.SECONDS;

public class SendCoinViewModel extends BaseViewModel {

    @NonNull
    private String toAddress = "";

    @NonNull
    private String amount = "";

    @NonNull
    private String fee = "";

    private boolean confirmBtnEnabled = true;

    @Nullable
    private Disposable sendCoinDisposable;

    @NonNull
    private final BtcWalletManager btcWalletManager;

    public SendCoinViewModel(@NonNull BtcWalletManager btcWalletManager) {
        this.btcWalletManager = btcWalletManager;
    }

    void stop() {
        if (sendCoinDisposable != null && !sendCoinDisposable.isDisposed()) {
            sendCoinDisposable.dispose();
        }
    }

    public void back(@NonNull FragmentManager fragmentManager) {
        fragmentManager.popBackStack();
    }

    /**
     * Send coin then close current page.
     */
    public void send(@NonNull FragmentManager fragmentManager, @NonNull Activity activity) {
        EditText et = new EditText(activity);
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setView(et)
                .setTitle("请输入密码:")//设置对话框的标题
                //设置对话框的按钮
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String md5Pass = (String) SharedPreferencesUtils.getObject(MyApplication.getContext(), "btcWalletPass");
                        if (md5Pass.equals(MD5Utils.md5(et.getText().toString()))){
                            /*Toast.makeText(MyApplication.getContext(), "密码正确",Toast.LENGTH_LONG).show();*/
                            dialog.dismiss();

                            //发送交易
                            setConfirmBtnEnabled(false);
                            Keyboard.hideSoftKeyboard(activity);
                            sendCoinDisposable = btcWalletManager.getCurrent()
                                    .send(toAddress, amount, fee)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .doOnSuccess(btcTx -> showSnackBarMessage(R.string.send_coin_send_done))
                                    .observeOn(Schedulers.io())
                                    .doOnSuccess(btcTx -> SECONDS.sleep(1))
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(btcTx -> {
                                                back(fragmentManager);
                                            }, throwable -> {
                                                if (isInValidAddressException(throwable)) {
                                                    showSnackBarMessage(R.string.send_coin_invalid_to_address);
                                                } else if (isInValidAmountException(throwable)) {
                                                    showSnackBarMessage(R.string.send_coin_invalid_amount);
                                                } else if (isInValidFeeException(throwable)) {
                                                    showSnackBarMessage(R.string.send_coin_invalid_fee);
                                                } else if (isInsufficientMoneyException(throwable)) {
                                                    showSnackBarMessage(R.string.send_coin_not_enough_balance);
                                                } else {
                                                    showSnackBarMessage(R.string.send_coin_failed);
                                                }
                                                setConfirmBtnEnabled(true);
                                            }
                                    );

                        }else {
                            Toast.makeText(MyApplication.getContext(), "密码错误",Toast.LENGTH_LONG).show();
                        }
                    }
                }).create();
        dialog.show();
    }

    private boolean isInValidAddressException(@NonNull Throwable throwable) {
        return throwable instanceof IllegalArgumentException && throwable.getMessage().contains("invalid address");
    }

    private boolean isInValidAmountException(@NonNull Throwable throwable) {
        return throwable instanceof IllegalArgumentException && throwable.getMessage().contains("invalid amount");
    }

    private boolean isInValidFeeException(@NonNull Throwable throwable) {
        return throwable instanceof IllegalArgumentException && throwable.getMessage().contains("invalid fee");
    }

    private boolean isInsufficientMoneyException(@NonNull Throwable throwable) {
        return throwable instanceof InsufficientMoneyException;
    }

    @Bindable
    @NonNull
    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(@NonNull String toAddress) {
        this.toAddress = toAddress;
        notifyPropertyChanged(BR.toAddress);
    }

    @Bindable
    @NonNull
    public String getAmount() {
        return amount;
    }

    public void setAmount(@NonNull String amount) {
        this.amount = amount;
        notifyPropertyChanged(BR.amount);
    }

    @Bindable
    @NonNull
    public String getFee() {
        return fee;
    }

    public void setFee(@NonNull String fee) {
        this.fee = fee;
        notifyPropertyChanged(BR.fee);
    }

    @Bindable
    public boolean isConfirmBtnEnabled() {
        return confirmBtnEnabled;
    }

    private void setConfirmBtnEnabled(boolean confirmBtnEnabled) {
        this.confirmBtnEnabled = confirmBtnEnabled;
        notifyPropertyChanged(BR.confirmBtnEnabled);
    }
}
