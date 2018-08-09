package com.lrkj.bitcoinwallet.major.landing;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import com.lrkj.bitcoinwallet.R;
import com.lrkj.bitcoinwallet.base.BaseViewModel;
import com.lrkj.bitcoinwallet.core.BtcWalletManager;
import com.lrkj.bitcoinwallet.major.create.CreateWalletFromSeedFragment;
import com.lrkj.bitcoinwallet.major.create.CreateWalletFromSeedViewModel;
import com.lrkj.bitcoinwallet.util.ActivityUtils;

public class LandingViewModel extends BaseViewModel {

    @NonNull
    private final BtcWalletManager btcWalletManager;

    public LandingViewModel(@NonNull BtcWalletManager btcWalletManager) {
        this.btcWalletManager = btcWalletManager;
    }

    /**
     * 启动创建钱包页面
     */
    public void goToCreateWalletPage(@NonNull FragmentManager fragmentManager) {
        final CreateWalletFromSeedFragment fragment = CreateWalletFromSeedFragment.newInstance();
        fragment.setViewModel(new CreateWalletFromSeedViewModel(btcWalletManager));
        ActivityUtils.replaceAndKeepOld(fragmentManager, fragment, R.id.contentFrame);
    }

    /**
     * 启动导入助记词页面
     */
    public void goToRestoreWalletPage(@NonNull FragmentManager fragmentManager) {
       /* final RestoreWalletFromSeedFragment fragment = RestoreWalletFromSeedFragment.newInstance();
        fragment.setViewModel(new RestoreWalletFromSeedViewModel(btcWalletManager));
        ActivityUtils.replaceAndKeepOld(fragmentManager, fragment, R.id.contentFrame);*/
    }
}
