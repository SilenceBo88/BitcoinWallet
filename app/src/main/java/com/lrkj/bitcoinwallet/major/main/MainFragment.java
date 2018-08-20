package com.lrkj.bitcoinwallet.major.main;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.lrkj.bitcoinwallet.MyApplication;
import com.lrkj.bitcoinwallet.R;
import com.lrkj.bitcoinwallet.base.BaseView;
import com.lrkj.bitcoinwallet.core.BtcWalletManager;
import com.lrkj.bitcoinwallet.databinding.FragmentMainBinding;
import com.lrkj.bitcoinwallet.major.landing.LandingActivity;
import com.lrkj.bitcoinwallet.major.main.tx.TxListAdapter;
import com.lrkj.bitcoinwallet.major.send.SendCoinFragment;
import com.lrkj.bitcoinwallet.major.send.SendCoinViewModel;
import com.lrkj.bitcoinwallet.util.ActivityUtils;
import com.lrkj.bitcoinwallet.util.MD5Utils;
import com.lrkj.bitcoinwallet.util.SharedPreferencesUtils;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class MainFragment extends BaseView<MainViewModel, FragmentMainBinding> {

    @NonNull
    private static final String EXPORT_DIALOG_TAG = "EXPORT_TO_MNEMONIC";

    private TxListAdapter txListAdapter;

    private DrawerLayout drawerLayout;

    private NavigationView navigationView;

    public MainFragment() {
    }

    @NonNull
    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        if (getActivity() != null) {
            binding.setFragmentManager(getActivity().getSupportFragmentManager());
        }
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        drawerLayout = ((MainActivity) getActivity()).findViewById(R.id.drawer_layout);
        ZXingLibrary.initDisplayOpinion(getActivity());
        navigationView = ((MainActivity) getActivity()).findViewById(R.id.nav_view);
        //菜单项点击事件
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_scan:
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
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_new:
                        alert_new_wallet();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_save:
                        alert_export_seed();
                        drawerLayout.closeDrawers();
                        break;
                    default:
                        break;
                }
                drawerLayout.closeDrawers();
                return false;
            }
        });


        super.onActivityCreated(savedInstanceState);
        setUpActionBar();
        setupRefreshLayout();
        setupListAdapter();
        txListAdapter.notifyDataSetChanged();
        final RecyclerView.Adapter adapter = binding.txsContainer.getAdapter();
        if (viewModel != null && adapter != null) {
            viewModel.start(adapter);
        }
    }

    public static void addToActivity(@NonNull FragmentManager fragmentManager, @NonNull BtcWalletManager btcWalletManager) {
        final MainFragment view = newInstance();
        view.setViewModel(new MainViewModel(btcWalletManager));
        ActivityUtils.addFragmentToActivity(fragmentManager, view, R.id.contentFrame);
    }

    private void setUpActionBar() {
        final AppCompatActivity appCompatActivity = ((AppCompatActivity) getActivity());
        //Toolbar
        Toolbar toolbar = appCompatActivity.findViewById(R.id.toolbar);
        appCompatActivity.setSupportActionBar(toolbar);

        //设置菜单按钮
        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.btn_menu);
        }
    }

    //菜单按钮点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
        return true;
    }

    private void setupListAdapter() {
        final Context context = getContext();
        if (context != null) {
            binding.txsContainer.setLayoutManager(new LinearLayoutManager(context));
            txListAdapter = new TxListAdapter(context, new ArrayList<>(0));
            binding.txsContainer.setAdapter(txListAdapter);
        }

        ViewCompat.setNestedScrollingEnabled(binding.txsContainer, false);
    }

    private void setupRefreshLayout() {
        final SwipeRefreshLayout swipeRefreshLayout = binding.refreshLayout;
        final Activity activity = getActivity();
        if (activity != null) {
            swipeRefreshLayout.setColorSchemeColors(
                    ContextCompat.getColor(activity, R.color.colorPrimary),
                    ContextCompat.getColor(activity, R.color.colorAccent),
                    ContextCompat.getColor(activity, R.color.colorPrimaryDark)
            );
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

                    //去发送比特币页面
                    final SendCoinFragment view = SendCoinFragment.newInstance();
                    SendCoinViewModel sendCoinViewModel = new SendCoinViewModel(viewModel.getBtcWalletManager());
                    view.setViewModel(sendCoinViewModel);
                    ActivityUtils.replaceAndKeepOld(getActivity().getSupportFragmentManager(), view, R.id.contentFrame);
                    sendCoinViewModel.setToAddress(result.split(":")[1]);
                    /*Toast.makeText(getActivity(), "解析结果:" + result, Toast.LENGTH_LONG).show();*/
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(getActivity(), "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
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

    public void alert_new_wallet(){
        EditText et = new EditText((MainActivity) getActivity());
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        AlertDialog dialog = new AlertDialog.Builder((MainActivity) getActivity())
                .setView(et)
                .setTitle("请输入密码:")//设置对话框的标题
                //设置对话框的按钮
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String md5Pass = (String) SharedPreferencesUtils.getObject(MyApplication.getContext(), "btcWalletPass");
                        if (md5Pass.equals(MD5Utils.md5(et.getText().toString()))){
                            /*Toast.makeText(MyApplication.getContext(), "密码正确",Toast.LENGTH_LONG).show();*/
                            dialog.dismiss();

                            //消息提示
                            AlertDialog alertDialog = new AlertDialog.Builder((MainActivity) getActivity())
                                    .setTitle("提示:")//设置对话框的标题
                                    .setMessage("创建新的钱包会丢失现在的钱包，请确认已备份助记词。")//设置对话框的内容
                                    //设置对话框的按钮
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            BtcWalletManager.deleteWallet();
                                            Intent intent = new Intent(MyApplication.getContext(), LandingActivity.class);
                                            startActivity(intent);
                                        }
                                    }).create();
                            alertDialog.show();
                        }else {
                            Toast.makeText(MyApplication.getContext(), "密码错误",Toast.LENGTH_LONG).show();
                        }
                    }
                }).create();
        dialog.show();
    }

    public void alert_export_seed(){
        EditText et = new EditText((MainActivity) getActivity());
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        AlertDialog dialog = new AlertDialog.Builder((MainActivity) getActivity())
                .setView(et)
                .setTitle("请输入密码:")//设置对话框的标题
                //设置对话框的按钮
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String md5Pass = (String) SharedPreferencesUtils.getObject(MyApplication.getContext(), "btcWalletPass");
                        if (md5Pass.equals(MD5Utils.md5(et.getText().toString()))){
                            /*Toast.makeText(MyApplication.getContext(), "密码正确",Toast.LENGTH_LONG).show();*/
                            dialog.dismiss();

                            //助记词
                            AlertDialog alertDialog = new AlertDialog.Builder((MainActivity) getActivity())
                                    .setTitle("提示:这是你的助记词，请保存好")//设置对话框的标题
                                    .setMessage(viewModel.getBtcWalletManager().getCurrent().getMnemonic())//设置对话框的内容
                                    //设置对话框的按钮
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).create();
                            alertDialog.show();
                        }else {
                            Toast.makeText(MyApplication.getContext(), "密码错误",Toast.LENGTH_LONG).show();
                        }
                    }
                }).create();
        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (viewModel != null) {
            viewModel.stop();
        }
    }
}
