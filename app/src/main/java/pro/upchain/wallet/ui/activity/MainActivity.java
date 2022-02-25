package pro.upchain.wallet.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.azhon.appupdate.config.UpdateConfiguration;
import com.azhon.appupdate.manager.DownloadManager;

import pro.upchain.wallet.BuildConfig;
import pro.upchain.wallet.R;
import pro.upchain.wallet.RxHttp.net.api.HttpApiUtils;
import pro.upchain.wallet.RxHttp.net.api.RequestUtils;
import pro.upchain.wallet.RxHttp.net.utils.StringMyUtil;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.entity.AppUpdateEntity;
import pro.upchain.wallet.ui.adapter.HomePagerAdapter;
import pro.upchain.wallet.ui.fragment.DappBrowserFragment;
import pro.upchain.wallet.ui.fragment.MineFragment;
import pro.upchain.wallet.ui.fragment.PropertyFragment;
import pro.upchain.wallet.utils.ToastUtils;
import pro.upchain.wallet.view.NoScrollViewPager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import butterknife.BindView;


/**
 * Created by Tiny 熊 @ Upchain.pro
 * 微信: xlbxiong
 */

public class MainActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.vp_home)
    NoScrollViewPager vpHome;
    @BindView(R.id.iv_mall)
    ImageView ivMall;
    @BindView(R.id.tv_mall)
    TextView tvMall;
    @BindView(R.id.lly_mall)
    LinearLayout llyMall;

    @BindView(R.id.iv_dapp)
    ImageView ivDapp;
    @BindView(R.id.tv_dapp)
    TextView tvDapp;
    @BindView(R.id.lly_dapp)
    LinearLayout llyDapp;


    @BindView(R.id.iv_mine)
    ImageView ivMine;
    @BindView(R.id.tv_mine)
    TextView tvMine;
    @BindView(R.id.lly_mine)
    LinearLayout llyMine;

    private HomePagerAdapter homePagerAdapter;

    public static final int DAPP_BARCODE_READER_REQUEST_CODE = 1;

    DappBrowserFragment dappBrowserFragment;

    public   boolean isDappShow  = false;
    private DownloadManager manager;
    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initToolBar() {

    }


    @Override
    public void initDatas() {
        requestAppUpdate();
    }


    private void requestAppUpdate() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("type",1);
        HttpApiUtils.wwwNormalRequest(this, null, RequestUtils.VERSION_UPDATE, data, new HttpApiUtils.OnRequestLintener() {
            @Override
            public void onSuccess(String result) {
                AppUpdateEntity appUpdateEntity = JSONObject.parseObject(result, AppUpdateEntity.class);
                String downloadUrl = appUpdateEntity.getDownloadUrl();
                String internetDownloadUrl = appUpdateEntity.getInternetDownloadUrl();
                String url;
                if(StringMyUtil.isNotEmpty(internetDownloadUrl)){
                    url = internetDownloadUrl;
                }else {
                    url = downloadUrl;
                }
                String versionName = appUpdateEntity.getVersionName();
                int versionCode = appUpdateEntity.getVersionCode();
                if(versionCode>BuildConfig.VERSION_CODE){
                    String mustUpdate = appUpdateEntity.getMustUpdate();
                    if(mustUpdate.equals("1")){
                        startUpdate(false,url,versionName, versionCode,appUpdateEntity.getVersionContent());
                    }else {
                        startUpdate(true,url,versionName, versionCode,appUpdateEntity.getVersionContent());
                    }
                }
            }

            @Override
            public void onFail(String msg) {

            }
        });
    }
    @Override
    public void configViews() {
        ivMall.setSelected(true);
        tvMall.setSelected(true);
        llyMall.setOnClickListener(this);
        llyDapp.setOnClickListener(this);
        llyMine.setOnClickListener(this);

        vpHome.setOffscreenPageLimit(3);

        List<Fragment> fragmentList = new ArrayList<>();
        PropertyFragment propertyFragment = new PropertyFragment();
        fragmentList.add(propertyFragment);
        dappBrowserFragment = new DappBrowserFragment();
        fragmentList.add(dappBrowserFragment);
        fragmentList.add(new MineFragment());
        homePagerAdapter = new HomePagerAdapter(getSupportFragmentManager(), fragmentList);
        vpHome.setAdapter(homePagerAdapter);
        vpHome.setCurrentItem(0, false);

    }

    // 退出时间++
    private long currentBackPressedTime = 0;
    // 退出间隔
    private static final int BACK_PRESSED_INTERVAL = 2000;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - currentBackPressedTime > BACK_PRESSED_INTERVAL) {
                currentBackPressedTime = System.currentTimeMillis();
                ToastUtils.showToast(getString(R.string.exit_tips));
                return true;
            } else {
                finish(); // 退出
            }
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


    @Override
    public void onClick(View v) {
        setAllUnselected();
        switch (v.getId()) {
            case R.id.lly_mall:
                ivMall.setSelected(true);
                tvMall.setSelected(true);
                vpHome.setCurrentItem(0, false);
                isDappShow = false;
                break;
            case R.id.lly_dapp:
                ivDapp.setSelected(true);
                tvDapp.setSelected(true);
                vpHome.setCurrentItem(1, false);
//                setupToolbarForDapp();
                isDappShow = true;
                break;
            case R.id.lly_mine:// 我的
                ivMine.setSelected(true);
                tvMine.setSelected(true);
                vpHome.setCurrentItem(2, false);
                isDappShow = false;
                break;
        }
    }

    private void setupToolbarForDapp() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.empty);

        enableDisplayHomeAsHome(true);
        invalidateOptionsMenu();

    }

    protected void enableDisplayHomeAsHome(boolean active) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(active);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_browser_home);
        }
    }

    private void setAllUnselected() {
        ivMall.setSelected(false);
        tvMall.setSelected(false);
        ivDapp.setSelected(false);
        tvDapp.setSelected(false);
        ivMine.setSelected(false);
        tvMine.setSelected(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        if (dappBrowserFragment.getUrlIsBookmark()) {
            getMenuInflater().inflate(R.menu.menu_added, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_add_bookmark, menu);
        }
        getMenuInflater().inflate(R.menu.menu_bookmarks, menu);
        setIconsVisible(menu,true);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                dappBrowserFragment.homePressed();
                return true;
            }
            case R.id.action_add_bookmark: {
                dappBrowserFragment.addBookmark();
                invalidateOptionsMenu();
                return true;
            }
            case R.id.action_bookmarks: {
                dappBrowserFragment.viewBookmarks();
                return true;
            }
            case R.id.action_added: {
                dappBrowserFragment.removeBookmark();
                invalidateOptionsMenu();
                return true;
            }
            case R.id.action_reload: {
                dappBrowserFragment.reloadPage();
                return true;
            }
            case R.id.action_share: {
                dappBrowserFragment.share();
                return true;
            }
            case R.id.action_scan: {
                Intent intent = new Intent(this, QRCodeScannerActivity.class);
                startActivityForResult(intent, DAPP_BARCODE_READER_REQUEST_CODE);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 解决不显示menu icon的问题
     * @param menu
     * @param flag
     */
    private void setIconsVisible(Menu menu, boolean flag) {
        //判断menu是否为空
        if(menu != null) {
            try {
                //如果不为空,就反射拿到menu的setOptionalIconsVisible方法
                Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                //暴力访问该方法
                method.setAccessible(true);
                //调用该方法显示icon
                method.invoke(menu, flag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void startUpdate(boolean isForcedUpgrade,String url,String versionName,int versionCode,String content) {
        /*
         * 整个库允许配置的内容
         * 非必选
         */
        UpdateConfiguration configuration = new UpdateConfiguration()
                //输出错误日志
                .setEnableLog(true)
                //设置自定义的下载
                //.setHttpManager()
                //下载完成自动跳动安装页面
                .setJumpInstallPage(true)
                //设置对话框背景图片 (图片规范参照demo中的示例图)
                //.setDialogImage(R.drawable.ic_dialog)
                //设置按钮的颜色
                //.setDialogButtonColor(Color.parseColor("#E743DA"))
                //设置对话框强制更新时进度条和文字的颜色
                //.setDialogProgressBarColor(Color.parseColor("#E743DA"))
                //设置按钮的文字颜色
                .setDialogButtonTextColor(Color.WHITE)
                //设置是否显示通知栏进度
                .setShowNotification(true)
                //设置是否提示后台下载toast
                .setShowBgdToast(false)
                //设置强制更新
                .setForcedUpgrade(isForcedUpgrade);
        //设置对话框按钮的点击监听
//                .setButtonClickListener(this);
        //设置下载过程的监听
//                .setOnDownloadListener(listenerAdapter);

        manager = DownloadManager.getInstance(this);
        manager.setApkName("coincide"+versionCode+".apk")
                .setApkUrl(url)
                .setSmallIcon(R.mipmap.logo)
//                .setShowNewerToast(true)
                .setConfiguration(configuration)
                .setApkVersionCode(versionCode)
                .setApkVersionName(versionName)
//                .setApkSize("20.4")
                .setApkDescription(content)
//                .setApkMD5("DC501F04BBAA458C9DC33008EFED5E7F")
                .download();
    }
}
