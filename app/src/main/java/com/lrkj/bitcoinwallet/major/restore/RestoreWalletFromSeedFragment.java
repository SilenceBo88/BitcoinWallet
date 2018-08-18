package com.lrkj.bitcoinwallet.major.restore;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lrkj.bitcoinwallet.R;
import com.lrkj.bitcoinwallet.base.BaseView;
import com.lrkj.bitcoinwallet.databinding.FragmentRestoreWalletFromSeedBinding;


import static com.lrkj.bitcoinwallet.BR.viewModel;

public class RestoreWalletFromSeedFragment extends BaseView<RestoreWalletFromSeedViewModel, FragmentRestoreWalletFromSeedBinding> {

    public RestoreWalletFromSeedFragment() {
    }

    @NonNull
    public static RestoreWalletFromSeedFragment newInstance() {
        return new RestoreWalletFromSeedFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_restore_wallet_from_seed, container, false);
        if (getActivity() != null) {
            binding.setFragmentManager(getActivity().getSupportFragmentManager());
        }
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
