package com.lrkj.bitcoinwallet.major.send;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;


import com.lrkj.bitcoinwallet.R;
import com.lrkj.bitcoinwallet.base.BaseView;
import com.lrkj.bitcoinwallet.databinding.FragmentSendCoinBinding;
import com.lrkj.bitcoinwallet.util.ActivityUtils;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import static com.lrkj.bitcoinwallet.BR.viewModel;

public class SendCoinFragment extends BaseView<SendCoinViewModel, FragmentSendCoinBinding> {

    public SendCoinFragment() {
    }

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
        ImageButton imageButton = getActivity().findViewById(R.id.send_coin_scan);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CaptureActivity.class);
                startActivityForResult(intent, 2);
            }
        });
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    viewModel.setToAddress(result.split(":")[1]);
                    /*Toast.makeText(getActivity(), "解析结果:" + result, Toast.LENGTH_LONG).show();*/
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(getActivity(), "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (viewModel != null) {
            viewModel.stop();
        }
    }
}
