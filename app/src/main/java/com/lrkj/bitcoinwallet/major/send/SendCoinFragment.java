package com.lrkj.bitcoinwallet.major.send;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;


import com.lrkj.bitcoinwallet.MyApplication;
import com.lrkj.bitcoinwallet.R;
import com.lrkj.bitcoinwallet.base.BaseView;
import com.lrkj.bitcoinwallet.databinding.FragmentSendCoinBinding;
import com.lrkj.bitcoinwallet.major.main.MainActivity;
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
                //判断当前系统是否高于或等于6.0
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //当前系统大于等于6.0
                    if (ContextCompat.checkSelfPermission(MyApplication.getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        //具有拍照权限，直接调用相机
                        //具体调用代码
                        Intent intent = new Intent(getActivity(), CaptureActivity.class);
                        startActivityForResult(intent, 2);
                    } else {
                        //不具有拍照权限，需要进行权限申请
                        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CAMERA}, 1);
                    }
                } else {
                    //当前系统小于6.0，直接调用拍照
                    Intent intent = new Intent(getActivity(), CaptureActivity.class);
                    startActivityForResult(intent, 2);
                }
            }
        });
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length >= 1) {
                int cameraResult = grantResults[0];//相机权限
                boolean cameraGranted = cameraResult == PackageManager.PERMISSION_GRANTED;//拍照权限
                if (cameraGranted) {
                    //具有拍照权限，调用相机
                    Intent intent = new Intent(getActivity(), CaptureActivity.class);
                    startActivityForResult(intent, 2);
                } else {
                    //不具有相关权限，给予用户提醒，比如Toast或者对话框，让用户去系统设置-应用管理里把相关权限开启
                    //消息提示
                    AlertDialog alertDialog = new AlertDialog.Builder((MainActivity) getActivity())
                            .setTitle("提示:")//设置对话框的标题
                            .setMessage("不具有权限， 请去系统设置-应用管理开启。")//设置对话框的内容
                            //设置对话框的按钮
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create();
                    alertDialog.show();
                }
            }
        }
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
