package com.lrkj.bitcoinwallet.core;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.lrkj.bitcoinwallet.Constants;
import com.lrkj.bitcoinwallet.util.FileUtils;;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.KeyChainGroup;
import org.bitcoinj.wallet.Wallet;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.observables.ConnectableObservable;

public class BtcWalletManager {

  private static final String TAG = "BtcWalletManager";

  @NonNull
  private static final String WALLET_FILE_EXTENSION = "wallet";

  @Nullable
  private static BtcWalletManager manager;

  @NonNull
  private final NetworkParameters networkParameters;

  @NonNull
  private final AssetManager assetManager;

  @NonNull
  private final File directory;

  @Nullable
  private WalletAppKit walletAppKit;

  @Nullable
  private ObservableEmitter<Integer> accountObservable;

  @Nullable
  private Observable<Integer> observable;

  private BtcWalletManager(@NonNull NetworkParameters networkParameters, @NonNull AssetManager assetManager, @NonNull File directory) {
    Log.d(TAG, "BtcWalletManager: 构造方法");
    this.networkParameters = networkParameters;
    this.assetManager = assetManager;
    this.directory = directory;
  }

  /**
   * 单例，懒加载，得到一个BtcWalletManager
   */
  @NonNull
  public static BtcWalletManager getInstance(@NonNull Context context) {
    Log.d(TAG, "getInstance: 得到BtcWalletManager对象");
    if (manager == null) {
      manager = new BtcWalletManager(Constants.NETWORK_PARAMETERS, context.getAssets(), context.getFilesDir());
    }
    return manager;
  }

  /**
   * 是否有钱包
   * @return
   */
  public boolean hasWallet() {
    Log.d(TAG, "hasWallet: ");
    return FileUtils.isExist(directory, WALLET_FILE_EXTENSION);
  }

  public boolean isRunning() {
    return walletAppKit != null && walletAppKit.isRunning();
  }

  /**
   * 生成随机助记词
   * @return
   */
  @NonNull
  public static String generateMnemonic() {
    Log.d(TAG, "generateMnemonic: ");
    final DeterministicSeed seed = new KeyChainGroup(Constants.NETWORK_PARAMETERS)
        .getActiveKeyChain()
        .getSeed();
    if (seed != null && seed.getMnemonicCode() != null) {
      return Joiner.on(" ").join(seed.getMnemonicCode());
    } else {
      throw new RuntimeException("generate mnemonic failed, please try again");
    }
  }

  /**
   * 根据助记词生成钱包
   * @param mnemonic
   * @return
   */
  @NonNull
  public Completable create(@NonNull String mnemonic) {
    return Completable.create(emitter -> {
      if (isValidMnemonic(mnemonic)) {
        create(new DeterministicSeed(mnemonic, null, "", System.currentTimeMillis()), emitter);
      } else {
        emitter.onError(new IllegalArgumentException("invalid mnemonic: " + mnemonic));
      }
    });
  }

  /**
   * 根据助记词和第一次生成时间生成钱包
   * @param mnemonic
   * @param creationTimeSecond
   * @return
   */
  @NonNull
  public Completable create(@NonNull String mnemonic, long creationTimeSecond) {
    return Completable.create(emitter -> {
      if (isValidMnemonic(mnemonic)) {
        create(new DeterministicSeed(mnemonic, null, "", creationTimeSecond), emitter);
      } else {
        emitter.onError(new IllegalArgumentException("invalid mnemonic: " + mnemonic));
      }
    });
  }

  /**
   * 根据助记词生成钱包
   * @param seed
   * @param emitter
   */
  private void create(@NonNull DeterministicSeed seed, @NonNull CompletableEmitter emitter) {
    Log.d(TAG, "create wallet ");
    //生成钱包
    walletAppKit = new WalletAppKit(networkParameters, directory, "users_wallet") {
      @Override
      protected void onSetupCompleted() {
        int keyAmount = wallet().getImportedKeys().size();
        Log.i(TAG, "ImportedKeys: " + keyAmount);
        if (keyAmount < 1) {
          wallet().importKey(new ECKey());
        }
        emitter.onComplete();
      }

      @NonNull
      @Override
      protected Wallet createWallet() {
        return Wallet.fromSeed(networkParameters, seed);
      }
    };

    //下载区块信息监听器
    walletAppKit.setDownloadListener(new DownloadProgressTracker() {
      //下载进度
      @Override
      protected void progress(double percentage, int blocksSoFar, @NonNull Date date) {
        super.progress(percentage, blocksSoFar, date);
        Log.d(TAG, "下载进度: "+ percentage);
        if (accountObservable != null) {
          accountObservable.onNext((int) percentage);
        }
      }

      //下载完成
      @Override
      protected void doneDownload() {
        super.doneDownload();
        Log.d(TAG, "下载完成: ");
        if (accountObservable != null) {
          accountObservable.onComplete();
        }
      }
    });

    observable = initDownloadObservable();

    //下载并保存区块信息
    try {
      walletAppKit.setCheckpoints(assetManager.open("checkpoints-testnet.txt"));
    } catch (IOException e) {
      e.printStackTrace();
    }

    walletAppKit.setBlockingStartup(false);
    walletAppKit.startAsync();
    Log.d(TAG, "call WalletAppKit#startAsync()");
  }

  /**
   * 检查助记词是否正确
   * @param mnemonic
   * @return
   */
  @VisibleForTesting
  public static boolean isValidMnemonic(@NonNull String mnemonic) {
    Log.d(TAG, "isValidMnemonic: ");
    try {
      MnemonicCode.INSTANCE.check(Splitter.on(" ").splitToList(mnemonic));
      return true;
    } catch (MnemonicException e) {
      return false;
    }
  }

  /**
   * 启动存在的钱包
   * @return
   */
  @NonNull
  public Completable launch() {
    Log.d(TAG, "launch: ");
    return Completable.create(emitter -> {
      if (!isRunning()) {
        walletAppKit = new WalletAppKit(networkParameters, directory, "users_wallet") {
          @Override
          protected void onSetupCompleted() {
            emitter.onComplete();
          }
        };

        walletAppKit.setDownloadListener(new DownloadProgressTracker() {
          @Override
          protected void progress(double percentage, int blocksSoFar, @NonNull Date date) {
            super.progress(percentage, blocksSoFar, date);
            if (accountObservable != null) {
              accountObservable.onNext((int) percentage);
            }
          }

          @Override
          protected void doneDownload() {
            super.doneDownload();
            if (accountObservable != null) {
              accountObservable.onComplete();
            }
          }
        });

        observable = initDownloadObservable();

        try {
          //noinspection SpellCheckingInspection
          walletAppKit.setCheckpoints(assetManager.open("checkpoints-testnet.txt"));
          walletAppKit.setBlockingStartup(false);
        } catch (IOException e) {
          if (!emitter.isDisposed()) {
            emitter.onError(e);
          }
        }
        Log.d(TAG, "call WalletAppKit#startAsync()");
        walletAppKit.startAsync();
      } else if (!emitter.isDisposed()) {
        emitter.onError(new IllegalStateException("already launch this wallet"));
      }
    });
  }

  /**
   * 初始化
   * @return
   */
  @NonNull
  private Observable<Integer> initDownloadObservable() {
    Log.d(TAG, "initDownloadObservable: ");
    final ConnectableObservable<Integer> connectibleObservable = Observable
        .create((ObservableEmitter<Integer> emitter) -> accountObservable = emitter)
        .publish();
    connectibleObservable.connect();
    return connectibleObservable.share();
  }

  /**
   * 获取同步进度
   * @return
   */
  @NonNull
  public Observable<Integer> getDownloadObservable() {
    Log.d(TAG, "getDownloadObservable: ");
    if (observable != null) {
      return observable;
    } else {
      throw new IllegalStateException("you have to invoke #launch() first");
    }
  }

  /**
   * 得到当前钱包
   * @return
   */
  @NonNull
  public BtcWallet getCurrent() {
    Log.d(TAG, "getCurrent: ");
    if (walletAppKit != null) {
      return new BtcWallet(walletAppKit);
    } else {
      throw new IllegalStateException("you have to invoke #launch() first");
    }
  }

  /**
   * 关闭钱包
   */
  public void shutdown() {
    Log.d(TAG, "shutdown: ");
    if (walletAppKit != null) {
      walletAppKit.stopAsync()
          .awaitTerminated();
      walletAppKit = null;
    }
  }
}

