package com.lrkj.bitcoinwallet.core;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.common.base.Joiner;
import com.lrkj.bitcoinwallet.Constants;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.wallet.SendRequest;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;

/**
 * 钱包的主要操作
 */
public class BtcWallet {

    private static final String TAG = "BtcWallet";

    @NonNull
    private final WalletAppKit walletAppKit;

    public BtcWallet(@NonNull WalletAppKit walletAppKit) {
        Log.d(TAG, "BtcWallet: 构造");
        this.walletAppKit = walletAppKit;
    }

    /**
     * 得到钱包的助记词
     * @return
     */
    @NonNull
    public String getMnemonic() {
        Log.d(TAG, "getMnemonic: ");
        try {
            final List<String> mnemonic = walletAppKit.wallet()
                    .getKeyChainSeed()
                    .getMnemonicCode();
            if (mnemonic == null) {
                throw new Resources.NotFoundException();
            } else {
                return Joiner.on(" ").join(mnemonic);
            }
        } catch (Exception e) {
            throw new Resources.NotFoundException(e.getMessage());
        }
    }

    /**
     * 得到钱包的余额
     * @return
     */
    @NonNull
    public String getBalance() {
        Log.d(TAG, "getBalance: ");
        return walletAppKit.wallet().getBalance().toFriendlyString().replace(" BTC", " ");
    }

    /**
     * 得到当前接收地址
     * @return
     */
    @NonNull
    public String getAddress() {
        Log.d(TAG, "getAddress: ");
        return walletAppKit.wallet()
                .currentReceiveAddress()
                .toBase58();
    }

    /**
     * 得到所有交易
     * @return
     */
    @NonNull
    public List<BtcTx> getTxs() {
        Log.d(TAG, "getTxs: ");
        final List<Transaction> transitions = walletAppKit.wallet()
                .getTransactionsByTime();
        final List<BtcTx> result = new ArrayList<>(transitions.size());
        for (Transaction transaction : transitions) {
            result.add(new BtcTx(walletAppKit.wallet(), transaction));
        }
        return result;
    }

    /**
     * 发送比特币
     * @param base58ToAddress
     * @param amountInSatoshis
     * @param feeInSatoshis
     * @return
     */
    @SuppressWarnings("JavaDoc")
    @NonNull
    public Single<BtcTx> send(@NonNull String base58ToAddress, @NonNull String amountInSatoshis, @NonNull String feeInSatoshis) {
        Log.d(TAG, "send: ");
        return Single.create(emitter -> {
            //输入校验
            if (isInvalidAddress(base58ToAddress)) {
                emitter.onError(new IllegalArgumentException("invalid address: " + base58ToAddress));
            } else if (isInvalidCoinValue(amountInSatoshis)) {
                emitter.onError(new IllegalArgumentException("invalid amount: " + amountInSatoshis));
            } else if (isInvalidCoinValue(feeInSatoshis)) {
                emitter.onError(new IllegalArgumentException("invalid fee: " + feeInSatoshis));
            } else if (isNotEnoughBalance(amountInSatoshis, feeInSatoshis)) {
                emitter.onError(new InsufficientMoneyException(
                        Coin.parseCoin(amountInSatoshis)
                                .add(Coin.parseCoin(feeInSatoshis))
                                .minus(walletAppKit.wallet().getBalance())
                ));
            } else {
                try {
                    final SendRequest request = SendRequest.to(
                            Address.fromBase58(Constants.NETWORK_PARAMETERS, base58ToAddress),
                            Coin.parseCoin(amountInSatoshis)
                    );
                    request.feePerKb = Coin.parseCoin(feeInSatoshis);
                    walletAppKit.wallet().completeTx(request);
                    walletAppKit.wallet().commitTx(request.tx);
                    walletAppKit.peerGroup()
                            .broadcastTransaction(request.tx)
                            .broadcast();
                    emitter.onSuccess(new BtcTx(walletAppKit.wallet(), request.tx));
                } catch (InsufficientMoneyException e) {
                    emitter.onError(e);
                }
            }
        });
    }

    /**
     * 校验地址
     * @param base58ToAddress
     * @return
     */
    private boolean isInvalidAddress(@NonNull String base58ToAddress) {
        Log.d(TAG, "isInvalidAddress: ");
        try {
            Address.fromBase58(Constants.NETWORK_PARAMETERS, base58ToAddress);
            return base58ToAddress.isEmpty();
        } catch (AddressFormatException e) {
            return true;
        }
    }

    /**
     * 校验比特币
     * @param valueInSatoshis
     * @return
     */
    private boolean isInvalidCoinValue(@NonNull String valueInSatoshis) {
        Log.d(TAG, "isInvalidCoinValue: ");
        try {
            final Coin value = Coin.parseCoin(valueInSatoshis);
            return valueInSatoshis.isEmpty() || value.isZero() || value.isNegative();
        } catch (IllegalArgumentException e) {
            return true;
        }
    }

    /**
     * 校验是否有足够的余额
     * @param amountInSatoshis
     * @param feeInSatoshis
     * @return
     */
    private boolean isNotEnoughBalance(@NonNull String amountInSatoshis, @NonNull String feeInSatoshis) {
        Log.d(TAG, "isNotEnoughBalance: ");
        final Coin value = Coin.parseCoin(amountInSatoshis);
        final Coin fee = Coin.parseCoin(feeInSatoshis);
        return walletAppKit.wallet()
                .getBalance()
                .isLessThan(value.add(fee));
    }

    /**
     * 接收比特币监听器
     */
    public void addReceivedTxListener(@NonNull ReceivedTxListener listener) {
        Log.d(TAG, "addReceivedTxListener: ");
        walletAppKit.wallet()
                .addCoinsReceivedEventListener((wallet, tx, prevBalance, newBalance) -> {
                    Log.d(TAG, "balance: " + getBalance());
                    listener.onReceivedTx(new BtcTx(wallet, tx));
                });
    }

    /**
     * 发送交易监听器
     */
    public void addSentTxListener(@NonNull SentTxListener listener) {
        Log.d(TAG, "addSentTxListener: ");
        walletAppKit.wallet().addCoinsSentEventListener((wallet, tx, prevBalance, newBalance) -> {
            Log.d(TAG, "balance: " + getBalance());
            listener.onSentTx(new BtcTx(wallet, tx));
        });
    }

    public interface ReceivedTxListener {
        void onReceivedTx(@NonNull BtcTx tx);
    }

    public interface SentTxListener {
        void onSentTx(@NonNull BtcTx tx);
    }
}
