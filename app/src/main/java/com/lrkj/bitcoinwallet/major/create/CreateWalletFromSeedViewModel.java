package com.lrkj.bitcoinwallet.major.create;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.Bindable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.lrkj.bitcoinwallet.BR;
import com.lrkj.bitcoinwallet.MyApplication;
import com.lrkj.bitcoinwallet.R;
import com.lrkj.bitcoinwallet.base.BaseViewModel;
import com.lrkj.bitcoinwallet.core.BtcWalletManager;
import com.lrkj.bitcoinwallet.major.landing.LandingActivity;
import com.lrkj.bitcoinwallet.major.landing.LandingFragment;
import com.lrkj.bitcoinwallet.major.landing.LandingViewModel;
import com.lrkj.bitcoinwallet.major.main.MainActivity;
import com.lrkj.bitcoinwallet.util.ActivityUtils;
import com.lrkj.bitcoinwallet.util.Keyboard;
import com.lrkj.bitcoinwallet.util.MD5Utils;
import com.lrkj.bitcoinwallet.util.SharedPreferencesUtils;

import io.reactivex.disposables.Disposable;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class CreateWalletFromSeedViewModel extends BaseViewModel {

    private boolean confirmBtnEnabled = true;

    private int progressVisibility = GONE;

    @Nullable
    private Disposable createDisposable;

    @NonNull
    private final BtcWalletManager btcWalletManager;

    @NonNull
    private final String mnemonic;

    public CreateWalletFromSeedViewModel(@NonNull BtcWalletManager btcWalletManager) {
        this.btcWalletManager = btcWalletManager;
        mnemonic = BtcWalletManager.generateMnemonic();
    }

    /**
     * 关闭
     */
    void stop() {
        if (createDisposable != null && !createDisposable.isDisposed()) {
            createDisposable.dispose();
        }
    }

    /**
     * 创建钱包
     * @param activity
     */
    public void createWallet(@NonNull LandingActivity activity) {
        EditText newPass = activity.findViewById(R.id.new_btcwallet_pass);
        EditText newRePass = activity.findViewById(R.id.new_btcwallet_repass);
        boolean flag = checkPass(newPass.getText().toString(), newRePass.getText().toString());

        if (flag){
            /*//关闭键盘
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(INPUT_METHOD_SERVICE);
            if(imm != null) {
                imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(),0);
            }*/
            Keyboard.hideSoftKeyboard(activity);
            setConfirmBtnEnabled(false);
            setProgressVisibility(VISIBLE);
            createDisposable = btcWalletManager.create(mnemonic)
                    .subscribe(() -> {
                                setProgressVisibility(GONE);
                                MainActivity.startAndFinishCurrent(activity);
                            }, throwable -> {
                                showSnackBarMessage(R.string.create_wallet_error);
                                setProgressVisibility(GONE);
                                setConfirmBtnEnabled(true);
                            }
                    );
        }
    }

    /**
     * 返回生成钱包方式选择页面
     * @param context
     */
    public void backPage(LandingActivity context){
        final LandingFragment fragment = LandingFragment.newInstance();
        fragment.setViewModel(new LandingViewModel(btcWalletManager));
        ActivityUtils.replaceAndKeepOld(context.getSupportFragmentManager(), fragment,  R.id.contentFrame);
    }

    /**
     * 显示助记词
     * @return
     */
    @NonNull
    @Bindable
    public String getMnemonic() {
        return "这是你恢复钱包的唯一凭证，请牢记它们:\n"+ mnemonic;
    }

    /**
     * 是否允许提交
     * @return
     */
    @Bindable
    public boolean isConfirmBtnEnabled() {
        return confirmBtnEnabled;
    }

    /**
     * 设置是否能提交
     * @param confirmBtnEnabled
     */
    private void setConfirmBtnEnabled(boolean confirmBtnEnabled) {
        this.confirmBtnEnabled = confirmBtnEnabled;
        notifyPropertyChanged(BR.confirmBtnEnabled);
    }

    /**
     * 获取进度条显示状态
     * @return
     */
    @Bindable
    public int getProgressVisibility() {
        return progressVisibility;
    }

    /**
     * 设置进度条显示状态
     * @param progressVisibility
     */
    private void setProgressVisibility(int progressVisibility) {
        this.progressVisibility = progressVisibility;
        notifyPropertyChanged(BR.progressVisibility);
    }

    /**
     * 校验密码
     * @param pass
     * @param rePass
     * @return
     */
    private boolean checkPass(String pass, String rePass){
        if (pass.isEmpty() || rePass.isEmpty()){
            Toast.makeText(MyApplication.getContext(), "密码不为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (pass.length() < 6 || pass.length() > 16 || rePass.length() < 6 || rePass.length() > 16){
            Toast.makeText(MyApplication.getContext(), "密码必须在6-16位之间", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (pass.equals(rePass)){
            //保存钱包密码到本地
            SharedPreferencesUtils.setObject(MyApplication.getContext(), "btcWalletPass", MD5Utils.md5(pass));
            return true;
        }else {
            Toast.makeText(MyApplication.getContext(), "密码不一致", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
