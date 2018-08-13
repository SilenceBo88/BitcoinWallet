package com.lrkj.bitcoinwallet.major.send;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.lrkj.bitcoinwallet.R;
import com.lrkj.bitcoinwallet.base.BaseView;
import com.lrkj.bitcoinwallet.databinding.FragmentSendCoinBinding;

import static com.lrkj.bitcoinwallet.BR.viewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link #newInstance()} factory method to
 * create an instance of this fragment.
 */
public class SendCoinFragment extends BaseView<SendCoinViewModel, FragmentSendCoinBinding> {

  /**
   * Default and empty constructor.
   */
  public SendCoinFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   */
  @NonNull
  public static SendCoinFragment newInstance() {
    return new SendCoinFragment();
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_send_coin, container, false);
    if (getActivity() != null) {
      binding.setFragmentManager(getActivity().getSupportFragmentManager());
    }
    binding.setViewModel(viewModel);
    return binding.getRoot();
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    /*setUpToolBar();*/
  }

  /*private void setUpToolBar() {
    final AppCompatActivity activity = ((AppCompatActivity) getActivity());
    if (activity != null) {
      activity.setSupportActionBar(binding.toolbar);
      final ActionBar actionBar = activity.getSupportActionBar();
      if (actionBar != null) {
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.btn_pre);
      }
    }
  }*/

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (viewModel != null) {
      viewModel.stop();
    }
  }
}
