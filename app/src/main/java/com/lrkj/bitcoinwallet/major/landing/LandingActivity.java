package com.lrkj.bitcoinwallet.major.landing;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

import com.lrkj.bitcoinwallet.MyApplication;
import com.lrkj.bitcoinwallet.R;
import com.lrkj.bitcoinwallet.base.BaseActivity;
import com.lrkj.bitcoinwallet.core.BtcWalletManager;
import com.lrkj.bitcoinwallet.major.main.MainActivity;
import com.lrkj.bitcoinwallet.util.ActivityUtils;

public class LandingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        //如果当前设备有钱包就直接启动，否则转到钱包生成页面
        if (BtcWalletManager.getInstance(this).hasWallet()) {
            MainActivity.startAndFinishCurrent(this);
        } else {
            showLandingPage();
        }
    }

    /**
     * 显示钱包生成页面
     */
    private void showLandingPage() {
        final LandingFragment fragment = LandingFragment.newInstance();
        fragment.setViewModel(new LandingViewModel(BtcWalletManager.getInstance(this)));
        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), fragment, R.id.contentFrame);
    }
}
