package pro.upchain.wallet.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import pro.upchain.wallet.C;
import pro.upchain.wallet.MyApplication;
import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.base.BasePopupWindow;
import pro.upchain.wallet.pop.SetAmountPop;
import pro.upchain.wallet.repository.RepositoryFactory;
import pro.upchain.wallet.utils.CommonStr;
import pro.upchain.wallet.utils.CommonToolBarUtils;
import pro.upchain.wallet.utils.GlideImageLoader;
import pro.upchain.wallet.utils.SharePreferencesUtil;
import pro.upchain.wallet.utils.ToastUtils;


import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;
import io.reactivex.Single;
import pro.upchain.wallet.utils.Utils;
import pro.upchain.wallet.utils.Web3jUtils;

import static pro.upchain.wallet.C.EXTRA_ADDRESS;
import static pro.upchain.wallet.C.EXTRA_CONTRACT_ADDRESS;
import static pro.upchain.wallet.C.EXTRA_DECIMALS;
import static pro.upchain.wallet.C.EXTRA_SYMBOL;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.math.BigInteger;
import java.util.Timer;
import java.util.TimerTask;


public class GatheringQRCodeActivity extends BaseActivity {

    @BindView(R.id.iv_gathering_qrcode)
    ImageView ivGatheringQrcode;
    @BindView(R.id.tv_wallet_address)
    TextView tvWalletAddress;
    private String walletAddress;
    private String contractAddress;
    private int decimals;
    private String qRStr;
    private String symbol;

    @Override
    public int getLayoutId() {
        return R.layout.activity_gathering_qrcode;
    }

    @Override
    public void initToolBar() {


    }
    public static void startAty(Context context,String EXTRA_ADDRESS,String EXTRA_SYMBOL,
                                String EXTRA_CONTRACT_ADDRESS,int EXTRA_DECIMALS){

        Intent intent = new Intent(context, GatheringQRCodeActivity.class);
        intent.putExtra(C.EXTRA_ADDRESS,EXTRA_ADDRESS);
        intent.putExtra(C.EXTRA_SYMBOL,EXTRA_SYMBOL);
        intent.putExtra(C.EXTRA_CONTRACT_ADDRESS,EXTRA_CONTRACT_ADDRESS);
        intent.putExtra(C.EXTRA_DECIMALS,EXTRA_DECIMALS);
        context.startActivity(intent);
    }
    @Override
    public void initDatas() {
        View viewById = findViewById(R.id.common_toolbar);
        Intent intent = getIntent();
        walletAddress = intent.getStringExtra(EXTRA_ADDRESS);
        symbol = intent.getStringExtra(EXTRA_SYMBOL);
        contractAddress = intent.getStringExtra(EXTRA_CONTRACT_ADDRESS);
        decimals = intent.getIntExtra(EXTRA_DECIMALS, 18);
        if(symbol .equals(C.ETH_SYMBOL) ||symbol.equals(C.BSC_SYMBOL) ) {
            decimals = C.ETHER_DECIMALS;
        }
        if(decimals == 0){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    RepositoryFactory rf = MyApplication.repositoryFactory();
                    Web3j web3j = Web3j.build(new HttpService(rf.ethereumNetworkRepository.getDefaultNetwork().rpcServerUrl));
                    Web3jUtils.erc20Decimals(web3j, contractAddress, new Web3jUtils.Web3jSuccess() {
                        @Override
                        public void success(BigInteger decimals) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                   /*                 qRStr = "ethereum:" + walletAddress + "?decimal=" + decimals;
                                    if (!TextUtils.isEmpty(contractAddress)) {
                                        qRStr += "&contractAddress=" + contractAddress;
                                    }*/
                                    initAddressQRCode(decimals.intValue());
                                }
                            });

                        }
                    });
                }
            }).start();
        }else {
            decimals = C.ETHER_DECIMALS;
/*            qRStr = "ethereum:" + walletAddress + "?decimal=" + decimals;
            if (!TextUtils.isEmpty(contractAddress)) {
                qRStr += "&contractAddress=" + contractAddress;
            }*/
            initAddressQRCode(decimals);
        }
        TextView tv_title =   findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.Receive)+" "+symbol);
        tvWalletAddress.setText(walletAddress);

    }

    // 参考
    // ethereum:0x6B523CD4FCDF3332BcB3177050e22cF7272b4c3A?contractAddress=0xd03e0c90c088d92f05c0f493312860d9e524049c&decimal=1&value=100000
    private void initAddressQRCode(int decimals) {

        qRStr = "ethereum:" + walletAddress + "?decimal=" + decimals;
        if (!TextUtils.isEmpty(contractAddress)) {
            qRStr += "&contractAddress=" + contractAddress;
        }

        Single.fromCallable(
                () -> {
                    return QRCodeEncoder.syncEncodeQRCode(qRStr, BGAQRCodeUtil.dp2px(GatheringQRCodeActivity.this, 270), Color.parseColor("#000000"));
                }
        ).subscribe( bitmap ->  GlideImageLoader.loadBmpImage(ivGatheringQrcode, bitmap, -1) );

    }

    @Override
    public void configViews() {
    }

    @OnClick({R.id.lly_back, R.id.copy_btn,R.id.share_address_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lly_back:
                finish();
                break;
            case R.id.copy_btn:
                copyWalletAddress();
                break;
            case R.id.share_address_tv:
                Utils.start2Share(GatheringQRCodeActivity.this,contractAddress);
                break;

            default:
                break;

        }
    }

    private void initSetAmountPop() {
        SetAmountPop setAmountPop = new SetAmountPop(GatheringQRCodeActivity.this);
        setAmountPop.setOnPopClickListener(new BasePopupWindow.OnPopClickListener() {
            @Override
            public void onPopClick(View view) {
                if(setAmountPop!=null){
                    setAmountPop.dismiss();
                }

            }
        });
        setAmountPop.setmOnTextChangedListener(new BasePopupWindow.OnTextChangedListener() {
            @Override
            public void onTextChange(Editable editable) {
                Single.fromCallable(
                        () -> {
                            String value = setAmountPop.getSet_amount_etv().getText().toString();
                            if (TextUtils.isEmpty(value)) {
                                return QRCodeEncoder.syncEncodeQRCode(qRStr, BGAQRCodeUtil.dp2px(GatheringQRCodeActivity.this, 270), Color.parseColor("#000000"));
                            } else {
                                String weiValue = Convert.toWei(value, Convert.Unit.ETHER).toString();
                                return QRCodeEncoder.syncEncodeQRCode(qRStr + "&value=" + weiValue, BGAQRCodeUtil.dp2px(GatheringQRCodeActivity.this, 270), Color.parseColor("#000000"));
                            }
                        }
                ).subscribe( bitmap ->  GlideImageLoader.loadBmpImage(ivGatheringQrcode, bitmap, -1) );
            }
        });

        setAmountPop.showAtLocation(ivGatheringQrcode, Gravity.CENTER,0,0);
        Utils.darkenBackground(this,0.7f);
        setAmountPop.showSoftKeyBoard(setAmountPop.getSet_amount_etv());

    }

    private void copyWalletAddress() {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        if (cm != null) {
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", walletAddress);
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
        }
        ToastUtils.showToast(R.string.gathering_qrcode_copy_success);
    }
}
