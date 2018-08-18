package com.lrkj.bitcoinwallet.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.lrkj.bitcoinwallet.util.WalletUtils;

import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.wallet.Wallet;

import java.util.Date;

/**
 * 交易类的主要操作
 */
public class BtcTx {

    private static final String TAG = "BtcTx";

    @NonNull
    private final Wallet wallet;

    @NonNull
    private final Transaction transaction;

    public BtcTx(@NonNull Wallet wallet, @NonNull Transaction transaction) {
        Log.d(TAG, "BtcTx: 创建对象");
        this.wallet = wallet;
        this.transaction = transaction;
    }

    /**
     * 交易修改时间
     *
     * @return
     */
    @NonNull
    public Date getUpdateTime() {
        Log.d(TAG, "getUpdateTime");
        return transaction.getUpdateTime();
    }

    /**
     * 判断是不是发送交易
     *
     * @return
     */
    public boolean isSent() {
        Log.d(TAG, "isSent");
        return transaction.getValue(wallet).signum() < 0;
    }

    /**
     * 获取交易Hash
     *
     * @return
     */
    @NonNull
    public String getHash() {
        Log.d(TAG, "getHash");
        return transaction.getHashAsString();
    }

    /**
     * 得到进行交易地址
     *
     * @return
     */
    @NonNull
    public String getAddress() {
        Log.d(TAG, "getAddress");
        return isSent() ? WalletUtils.getToAddressOfSent(transaction, wallet)
                : WalletUtils.getWalletAddressOfReceived(transaction, wallet);
    }

    /**
     * 得到交易手续费
     *
     * @return
     */
    @Nullable
    public String getFriendlyStringFee() {
        Log.d(TAG, "getFriendlyStringFee");
        return transaction.getFee() != null ? transaction.getFee().toFriendlyString() : null;
    }

    /**
     * 得到钱包信息
     *
     * @return
     */
    @NonNull
    public String getFriendlyStringValue() {
        Log.d(TAG, "getFriendlyStringValue");
        return transaction.getValue(wallet).toFriendlyString();
    }

    /**
     * 得到已确认块
     *
     * @return
     */
    public int getConfirmation() {
        Log.d(TAG, "getConfirmation");
        return transaction.getConfidence().getDepthInBlocks();
    }

    /**
     * 得到交易状态
     *
     * @return
     */
    @NonNull
    public String getStatus() {
        Log.d(TAG, "getStatus");
        return convertType(transaction.getConfidence().getConfidenceType());
    }

    @NonNull
    private static String convertType(@NonNull TransactionConfidence.ConfidenceType confidenceType) {
        Log.d(TAG, "convertType");
        switch (confidenceType) {
            case BUILDING: {
                return "BUILDING";
            }
            case PENDING: {
                return "PENDING";
            }
            case IN_CONFLICT: {
                return "IN CONFLICT";
            }
            case DEAD: {
                return "DEAD";
            }
            default: {
                return "UNKNOWN";
            }
        }
    }
}
