package com.lrkj.bitcoinwallet.base;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

/**
 * 抽象RecyclerView绑定适配器
 *
 * @param <M>
 * @param <B>
 */
public abstract class BaseBindingAdapter<M, B extends ViewDataBinding> extends RecyclerView.Adapter {

    @NonNull
    private final Context context;

    private List<M> items;

    public BaseBindingAdapter(@NonNull Context context, @NonNull List<M> items) {
        this.context = context;
        this.items = items;
    }

    /**
     * 数据替换
     *
     * @param items
     */
    public void replaceData(@NonNull List<M> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    /**
     * 获取数据总条数
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final B binding = DataBindingUtil.inflate(LayoutInflater.from(context), getLayoutResId(viewType), parent, false);
        return new BaseBindingViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setBackgroundColor(0xFFFFFFFF);
        final B binding = DataBindingUtil.getBinding(holder.itemView);
        onBindItem(binding, items.get(position));
    }


    @SuppressWarnings("SameReturnValue")
    @LayoutRes
    protected abstract int getLayoutResId(int viewType);

    protected abstract void onBindItem(@Nullable B binding, @NonNull M item);
}