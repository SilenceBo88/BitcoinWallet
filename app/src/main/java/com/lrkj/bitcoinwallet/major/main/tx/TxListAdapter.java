package com.lrkj.bitcoinwallet.major.main.tx;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lrkj.bitcoinwallet.R;
import com.lrkj.bitcoinwallet.base.BaseBindingAdapter;
import com.lrkj.bitcoinwallet.core.BtcTx;
import com.lrkj.bitcoinwallet.databinding.TxItemBinding;

import java.util.List;

public class TxListAdapter extends BaseBindingAdapter<BtcTx, TxItemBinding> {

    @NonNull
    private final Context context;

    public TxListAdapter(@NonNull Context context, @NonNull List<BtcTx> transactions) {
        super(context, transactions);
        this.context = context;
    }

    @LayoutRes
    @Override
    protected int getLayoutResId(int viewType) {
        return R.layout.tx_item;
    }

    @Override
    protected void onBindItem(@Nullable TxItemBinding binding, @NonNull BtcTx transaction) {
        final TxItemViewModel viewModel = new TxItemViewModel();
        viewModel.setItem(context, transaction);
        if (binding != null) {
            binding.setViewModel(viewModel);
            binding.executePendingBindings();
        }
    }
}