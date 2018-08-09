package com.lrkj.bitcoinwallet.major.create;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.lrkj.bitcoinwallet.MyApplication;
import com.lrkj.bitcoinwallet.R;
import com.lrkj.bitcoinwallet.base.BaseView;
import com.lrkj.bitcoinwallet.databinding.FragmentCreateWalletFromSeedBinding;
import com.lrkj.bitcoinwallet.major.landing.LandingActivity;
import com.lrkj.bitcoinwallet.major.main.MainActivity;

public class CreateWalletFromSeedFragment extends BaseView<CreateWalletFromSeedViewModel, FragmentCreateWalletFromSeedBinding> {

    public CreateWalletFromSeedFragment() {}

    public static CreateWalletFromSeedFragment newInstance() {
        return new CreateWalletFromSeedFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_wallet_from_seed, container, false);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (viewModel != null) {
            viewModel.stop();
        }
    }
}
