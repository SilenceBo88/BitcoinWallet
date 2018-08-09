package com.lrkj.bitcoinwallet.major.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.lrkj.bitcoinwallet.R;
import com.lrkj.bitcoinwallet.base.BaseActivity;
import com.lrkj.bitcoinwallet.core.BtcWalletManager;
import com.lrkj.bitcoinwallet.major.landing.LandingActivity;
import com.lrkj.bitcoinwallet.util.AndroidBug5497Workaround;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AndroidBug5497Workaround.assistActivity(this);
        MainFragment.addToActivity(getSupportFragmentManager(), BtcWalletManager.getInstance(this));
    }

    /**
     * 启动新的活动,然后关闭上一个活动。
     * @param activity
     */
    public static void startAndFinishCurrent(@NonNull Activity activity) {
        activity.startActivity(new Intent(activity, MainActivity.class));
        activity.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BtcWalletManager.getInstance(this)
                .shutdown();
    }
}
