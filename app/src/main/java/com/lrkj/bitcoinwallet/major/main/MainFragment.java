package com.lrkj.bitcoinwallet.major.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lrkj.bitcoinwallet.MyApplication;
import com.lrkj.bitcoinwallet.R;
import com.lrkj.bitcoinwallet.base.BaseView;
import com.lrkj.bitcoinwallet.core.BtcWalletManager;
import com.lrkj.bitcoinwallet.databinding.FragmentMainBinding;
import com.lrkj.bitcoinwallet.util.ActivityUtils;

import java.util.ArrayList;

public class MainFragment extends BaseView<MainViewModel, FragmentMainBinding> {

    @NonNull
    private static final String EXPORT_DIALOG_TAG = "EXPORT_TO_MNEMONIC";

    /*private TxListAdapter txListAdapter;*/

    private DrawerLayout drawerLayout;

    private NavigationView navigationView;

    public MainFragment() {}

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
        navigationView = ((MainActivity)getActivity()).findViewById(R.id.nav_view);
        //菜单项点击事件
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_scan:
                        Toast.makeText(MyApplication.getContext(), "扫一扫", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_wallet:
                        Toast.makeText(MyApplication.getContext(), "创建", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_import:
                        Toast.makeText(MyApplication.getContext(), "导入", Toast.LENGTH_SHORT).show();
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
        /*setupListAdapter();
        txListAdapter.notifyDataSetChanged();
        final RecyclerView.Adapter adapter = binding.txsContainer.getAdapter();
        if (viewModel != null && adapter != null) {
            viewModel.start(adapter);
        }*/
        viewModel.start(null);
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
        if (actionBar !=  null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.btn_menu);
        }
    }

    //菜单按钮点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        drawerLayout = ((MainActivity)getActivity()).findViewById(R.id.drawer_layout);
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
        return true;
    }

    /*private void setupListAdapter() {
        final Context context = getContext();
        if (context != null) {
            binding.txsContainer.setLayoutManager(new LinearLayoutManager(context));
            txListAdapter = new TxListAdapter(context, new ArrayList<>(0));
            binding.txsContainer.setAdapter(txListAdapter);
        }

        ViewCompat.setNestedScrollingEnabled(binding.txsContainer, false);
    }
*/
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

    /*@Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }*/

    /*@Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionExport:
                showExportDialog();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }*/

    /**
     * Launch the create wallet page.
     */
    /*private void showExportDialog() {
        final FragmentActivity activity = getActivity();
        if (activity != null) {
            ExportToMnemonicDialogFragment.newInstance()
                    .show(activity.getSupportFragmentManager(), EXPORT_DIALOG_TAG);
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
