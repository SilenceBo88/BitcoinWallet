package com.lrkj.bitcoinwallet.major.main.tx;

import android.content.Context;
import android.content.Intent;
import android.databinding.Bindable;
import android.net.Uri;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;

import com.lrkj.bitcoinwallet.BuildConfig;
import com.lrkj.bitcoinwallet.Constants;
import com.lrkj.bitcoinwallet.R;
import com.lrkj.bitcoinwallet.BR;
import com.lrkj.bitcoinwallet.base.BaseViewModel;
import com.lrkj.bitcoinwallet.core.BtcTx;

import org.bitcoinj.params.TestNet3Params;

import java.text.SimpleDateFormat;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class TxItemViewModel extends BaseViewModel {

    private boolean isSent;

    @NonNull
    private String time = "";

    @ColorRes
    private int valueTextColor = android.R.color.holo_red_light;

    private int currentConfirmation = 0;

    @NonNull
    private String txHash = "";

    @NonNull
    private String value = "";

    @NonNull
    private String address = "";

    @NonNull
    private String fee = "";

    private int fromVisibility = GONE;

    private int toVisibility = GONE;

    private int feeVisibility = GONE;

    @NonNull
    private String txStatus = "";

    /**
     * 交易详细信息，外部链接
     *
     * @param context
     */
    public void launchExplore(@NonNull Context context) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.withAppendedPath(Uri.parse(getHost(context)), "tx/" + txHash)));
    }

    /**
     * 得到网口地址
     *
     * @param context
     * @return
     */
    @NonNull
    private String getHost(@NonNull Context context) {
        return context.getString(Constants.NETWORK_PARAMETERS.equals(TestNet3Params.get()) ? R.string.explore_testnet_url : R.string.explore_mainnet_url);
    }

    /**
     * 设置每一笔交易的值
     *
     * @param context
     * @param tx
     */
    public void setItem(@NonNull Context context, @NonNull BtcTx tx) {
        checkIsSendOrNot(tx);
        checkToFromViewVisibility();
        setTime(context, tx);
        setValueTextColor();
        setCurrentConfirmation(tx);
        setTxHash(tx);
        setAddress(tx);
        setTxStatus(tx);
        setValue(tx);
        setFee(tx);
    }

    /**
     * 是不是发送交易
     *
     * @param tx
     */
    private void checkIsSendOrNot(@NonNull BtcTx tx) {
        this.isSent = tx.isSent();
    }

    @NonNull
    @Bindable
    public String getTime() {
        return time;
    }

    private void setTime(@NonNull Context context, @NonNull BtcTx tx) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.time = simpleDateFormat.format(tx.getUpdateTime().getTime());
        notifyPropertyChanged(BR.time);
    }

    @ColorRes
    @Bindable
    public int getValueTextColor() {
        return valueTextColor;
    }

    private void setValueTextColor() {
        if (isSent) {
            this.valueTextColor = android.R.color.holo_red_light;
        } else {
            this.valueTextColor = android.R.color.holo_green_light;
        }
        notifyPropertyChanged(BR.valueTextColor);
    }

    @NonNull
    @Bindable
    public String getTxHash() {
        return txHash;
    }

    private void setTxHash(@NonNull BtcTx tx) {
        this.txHash = tx.getHash();
        notifyPropertyChanged(BR.txHash);
    }

    @NonNull
    @Bindable
    public String getAddress() {
        return address;
    }

    private void setAddress(@NonNull BtcTx tx) {
        address = tx.getAddress();
        notifyPropertyChanged(BR.address);
    }

    private void checkToFromViewVisibility() {
        setFromVisibility(isSent ? GONE : VISIBLE);
        setToVisibility(isSent ? VISIBLE : GONE);
    }

    @Bindable
    public int getToVisibility() {
        return toVisibility;
    }

    private void setToVisibility(int toVisibility) {
        this.toVisibility = toVisibility;
        notifyPropertyChanged(BR.toVisibility);
    }

    @Bindable
    public int getFromVisibility() {
        return fromVisibility;
    }

    private void setFromVisibility(int fromVisibility) {
        this.fromVisibility = fromVisibility;
        notifyPropertyChanged(BR.fromVisibility);
    }

    @NonNull
    @Bindable
    public String getValue() {
        return value;
    }

    private void setValue(@NonNull BtcTx tx) {
        this.value = tx.getFriendlyStringValue();
        notifyPropertyChanged(BR.value);
    }

    @NonNull
    @Bindable
    public String getFee() {
        return fee;
    }

    private void setFee(@NonNull BtcTx tx) {
        if (tx.getFriendlyStringFee() != null) {
            setFeeVisibility(VISIBLE);
            fee = tx.getFriendlyStringFee();
        } else {
            setFeeVisibility(GONE);
            fee = "";
        }
        notifyPropertyChanged(BR.fee);
    }

    @Bindable
    public int getFeeVisibility() {
        return feeVisibility;
    }

    private void setFeeVisibility(int feeVisibility) {
        this.feeVisibility = feeVisibility;
        notifyPropertyChanged(BR.feeVisibility);
    }

    @NonNull
    @Bindable
    public String getTxStatus() {
        return txStatus;
    }

    private void setTxStatus(@NonNull BtcTx tx) {
        this.txStatus = tx.getStatus();
        notifyPropertyChanged(BR.txStatus);
    }

    @Bindable
    public int getCurrentConfirmation() {
        return currentConfirmation;
    }

    private void setCurrentConfirmation(@NonNull BtcTx tx) {
        this.currentConfirmation = tx.getConfirmation();
    }
}
