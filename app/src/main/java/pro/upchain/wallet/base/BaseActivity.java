/**
 * Copyright 2016 JustWayward Team
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pro.upchain.wallet.base;

import static pro.upchain.wallet.ui.fragment.MineFragment.ACTION;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import pro.upchain.wallet.R;
import pro.upchain.wallet.utils.LanguageUtils;
import pro.upchain.wallet.utils.LocalManageUtil;
import pro.upchain.wallet.utils.SoftHideKeyBoardUtil;
import pro.upchain.wallet.utils.StatusBarUtil;
import pro.upchain.wallet.utils.Utils;
import pro.upchain.wallet.view.loadding.CustomDialog;


import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */

public abstract class BaseActivity extends MySupportActivity {

    public Toolbar mCommonToolbar;

    protected Context mContext;
    private CustomDialog dialog;//进度条
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        StatusBarUtil.setColor(this, Color.WHITE);
        StatusBarUtil.setDarkMode(this);
        mContext = this;
        unbinder = ButterKnife.bind(this);
        mCommonToolbar = findViewById( R.id.common_toolbar);
        if (mCommonToolbar != null) {

            setSupportActionBar(mCommonToolbar);
        }
        LinearLayout llyBack = (LinearLayout) findViewById(R.id.lly_back);
        if (llyBack != null) {
            llyBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftInputView();
                    finish();
                }
            });
        }
        initToolBar();
        initDatas();
        configViews();
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocalManageUtil.setLocal(base));
    }
    /**
     * 多语言androidx适配
     * @param overrideConfiguration
     */
    @Override
    public void applyOverrideConfiguration(Configuration overrideConfiguration) {
        if(overrideConfiguration!=null){
            int uiMode = overrideConfiguration.uiMode;
            overrideConfiguration.setTo(getBaseContext().getResources().getConfiguration());
            overrideConfiguration.uiMode  = uiMode;
        }
        super.applyOverrideConfiguration(overrideConfiguration);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        dismissDialog();
    }

    public abstract int getLayoutId();

    public abstract void initToolBar();

    public abstract void initDatas();

    /**
     * 对各种控件进行设置、适配、填充数据
     */
    public abstract void configViews();

    protected void gone(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.GONE);
                }
            }
        }
    }

    protected void visible(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                }
            }
        }

    }
    protected void hideSoftInputView() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            View currentFocus = getCurrentFocus();
            if (currentFocus != null) {
                manager.hideSoftInputFromWindow(currentFocus.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
    protected boolean isVisible(View view) {
        return view.getVisibility() == View.VISIBLE;
    }

    // dialog
    public CustomDialog getDialog() {
        if (dialog == null) {
            dialog = CustomDialog.instance(this);
            dialog.setCancelable(true);
        }
        return dialog;
    }

    public void hideDialog() {
        if (dialog != null)
            dialog.hide();
    }

    public void showDialog(String progressTip) {
        getDialog().show();
        if (progressTip != null) {
            getDialog().setTvProgress(progressTip);
        }
    }
    //请求列表数据失败时,重新加载
    public void errorRefresh(){

    }
    public void showDialog() {
        getDialog().show();
            getDialog().setTvProgress(getString(R.string.loading));
    }
    public void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
