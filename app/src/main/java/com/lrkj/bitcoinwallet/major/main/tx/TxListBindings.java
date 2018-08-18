package com.lrkj.bitcoinwallet.major.main.tx;

import android.databinding.BindingAdapter;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;


import com.lrkj.bitcoinwallet.core.BtcTx;

import java.util.List;

/**
 * items的数据绑定适配器
 */
public class TxListBindings {

    @BindingAdapter("items")
    public static void setItems(@NonNull RecyclerView view, @NonNull List<BtcTx> items) {
        TxListAdapter adapter = (TxListAdapter) view.getAdapter();
        if (adapter != null) {
            adapter.replaceData(items);
        }
    }
}