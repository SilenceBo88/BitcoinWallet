package com.lrkj.bitcoinwallet.major.create;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lrkj.bitcoinwallet.R;
import com.lrkj.bitcoinwallet.base.BaseView;
import com.lrkj.bitcoinwallet.databinding.FragmentCreateWalletFromSeedBinding;

public class CreateWalletFromSeedFragment extends BaseView<CreateWalletFromSeedViewModel, FragmentCreateWalletFromSeedBinding> {

    public CreateWalletFromSeedFragment() {
    }

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
