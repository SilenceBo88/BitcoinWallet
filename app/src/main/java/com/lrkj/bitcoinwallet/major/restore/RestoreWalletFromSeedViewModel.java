package com.lrkj.bitcoinwallet.major.restore;

import android.databinding.Bindable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.lrkj.bitcoinwallet.MyApplication;
import com.lrkj.bitcoinwallet.R;
import com.lrkj.bitcoinwallet.base.BaseViewModel;
import com.lrkj.bitcoinwallet.core.BtcWalletManager;
import com.lrkj.bitcoinwallet.major.landing.LandingActivity;
import com.lrkj.bitcoinwallet.major.landing.LandingFragment;
import com.lrkj.bitcoinwallet.major.landing.LandingViewModel;
import com.lrkj.bitcoinwallet.major.main.MainActivity;

import io.reactivex.disposables.Disposable;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import com.lrkj.bitcoinwallet.BR;
import com.lrkj.bitcoinwallet.util.ActivityUtils;
import com.lrkj.bitcoinwallet.util.MD5Utils;
import com.lrkj.bitcoinwallet.util.SharedPreferencesUtils;

public class RestoreWalletFromSeedViewModel extends BaseViewModel {

  private static final long creationTimeSecond = 1526262375L;

  @NonNull
  private String mnemonic = "";

  private boolean confirmBtnEnabledImport = true;

  private int progressVisibilityImport = GONE;

  @Nullable
  private Disposable restoreDisposable;

  @NonNull
  private final BtcWalletManager btcWalletManager;

  public RestoreWalletFromSeedViewModel(@NonNull BtcWalletManager btcWalletManager) {
    this.btcWalletManager = btcWalletManager;
  }

  void stop() {
    if (restoreDisposable != null && !restoreDisposable.isDisposed()) {
      restoreDisposable.dispose();
    }
  }

  /**
   * 创建钱包成功就启动主页面
   * @param activity
   */
  public void restore(@NonNull LandingActivity activity) {
      EditText seed = activity.findViewById(R.id.import_btcwallet_seed);
      EditText newPass = activity.findViewById(R.id.import_btcwallet_pass);
      EditText newRePass = activity.findViewById(R.id.import_btcwallet_repass);
      boolean flag = checkPass(newPass.getText().toString(), newRePass.getText().toString());

      if (!BtcWalletManager.isValidMnemonic(seed.getText().toString())){
          Toast.makeText(MyApplication.getContext(), "助记词格式错误", Toast.LENGTH_SHORT).show();
      }else {
          mnemonic = seed.getText().toString();
      }

      if (flag) {
          //关闭键盘
          InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
          if (imm != null) {
              imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
          }

          setConfirmBtnEnabledImport(false);
          setProgressVisibilityImport(VISIBLE);
          restoreDisposable = btcWalletManager.create(mnemonic, creationTimeSecond)
              .subscribe(() -> {
                setProgressVisibilityImport(GONE);
                        MainActivity.startAndFinishCurrent(activity);
                    }, throwable -> {
                        showSnackBarMessage(R.string.restore_error);
                        setProgressVisibilityImport(GONE);
                        setConfirmBtnEnabledImport(true);
                    }
            );
        }
  }

  @NonNull
  @Bindable
  public String getMnemonic() {
    return mnemonic;
  }

  private void setMnemonic(@NonNull String mnemonic) {
    this.mnemonic = mnemonic;
    notifyPropertyChanged(BR.mnemonic);
  }

  @Bindable
  public boolean isConfirmBtnEnabledImport() {
    return confirmBtnEnabledImport;
  }

  private void setConfirmBtnEnabledImport(boolean confirmBtnEnabledImport) {
    this.confirmBtnEnabledImport = confirmBtnEnabledImport;
    notifyPropertyChanged(BR.confirmBtnEnabledImport);
  }

  @Bindable
  public int getProgressVisibilityImport() {
    return progressVisibilityImport;
  }

  private void setProgressVisibilityImport(int progressVisibilityImport) {
    this.progressVisibilityImport = progressVisibilityImport;
    notifyPropertyChanged(BR.progressVisibilityImport);
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
