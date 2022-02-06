package pro.upchain.wallet.ui.activity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.Bindable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.ArrayList;

import butterknife.BindView;
import pro.upchain.wallet.R;
import pro.upchain.wallet.RxHttp.net.api.HttpApiUtils;
import pro.upchain.wallet.RxHttp.net.utils.StringMyUtil;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.entity.ConfirmPinEntity;
import pro.upchain.wallet.interact.CreateWalletInteract;
import pro.upchain.wallet.ui.adapter.ConfirmPinAdapter;
import pro.upchain.wallet.utils.ETHWalletUtils;
import pro.upchain.wallet.utils.LogUtils;
import pro.upchain.wallet.utils.ToastUtils;

public class ConfirmPinActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.confirm_tip_tv)
    TextView confirm_tip_tv;
    @BindView(R.id.one_psw_iv)
    ImageView one_psw_iv;
    @BindView(R.id.two_psw_iv)
    ImageView two_psw_iv;
    @BindView(R.id.three_psw_iv)
    ImageView three_psw_iv;
    @BindView(R.id.four_psw_iv)
    ImageView four_psw_iv;
    @BindView(R.id.five_psw_iv)
    ImageView five_psw_iv;
    @BindView(R.id.six_psw_iv)
    ImageView six_psw_iv;
    @BindView(R.id.pin_code_linear)
    LinearLayout pin_code_linear;
    ConfirmPinAdapter confirmPinAdapter;
    ArrayList<ConfirmPinEntity>confirmPinEntityArrayList = new ArrayList<>();
    String firstPsw = "";
    String confirmPsw = "";
    boolean firstPswSuccess = false;
    boolean confirmPswSuccess = false;
    private boolean incorrect = false;
    private CreateWalletInteract createWalletInteract;
    public static int TO_IMPORT_WALLET = 111;
    public static void startAty (Context context, String walletName,boolean isCreate){
        Intent intent = new Intent(context, ConfirmPinActivity.class);
        intent.putExtra("walletName",walletName) ;
        intent.putExtra("isCreate",isCreate) ;
        context.startActivity(intent);
    }

    public static void startAty (Activity context, String mnemonic){
        Intent intent = new Intent(context, ConfirmPinActivity.class);
        intent.putExtra("mnemonic",mnemonic) ;
        context.startActivityForResult(intent,TO_IMPORT_WALLET);
    }
    @Override
    public int getLayoutId() {
        return R.layout.activity_confirm_pin;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {
        createWalletInteract = new CreateWalletInteract();
    initRecycler();
    }

    private void initRecycler() {
        confirmPinAdapter = new ConfirmPinAdapter(R.layout.confirm_pin_recycler,confirmPinEntityArrayList);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        recyclerView.setAdapter(confirmPinAdapter);
        for (int i = 0; i < 12; i++) {
            ConfirmPinEntity confirmPinEntity = new ConfirmPinEntity();
            if(i == 9){
                confirmPinEntity.setCode("");
            }else {
                confirmPinEntity.setCode((i+1)+"");
                if(i == 11){
                    confirmPinEntity.setDelete(true);
                }
            }
            confirmPinEntityArrayList.add(confirmPinEntity);

        }
        confirmPinAdapter.notifyDataSetChanged();
        confirmPinAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                ConfirmPinEntity confirmPinEntity = confirmPinEntityArrayList.get(position);
                String code = confirmPinEntity.getCode();
                boolean delete = confirmPinEntity.isDelete();
                if(delete){
                    if(StringMyUtil.isEmptyString(firstPsw)){
                        return;
                    } else if(firstPswSuccess&&StringMyUtil.isNotEmpty(confirmPsw)){
                        if(confirmPsw.length()<=1){
                            confirmPsw = "";
                        }else {
                            confirmPsw= confirmPsw.substring(0,confirmPsw.length()-1);
                        }
                        incorrect = false;
                        if(StringMyUtil.isEmptyString(confirmPsw)){
                            one_psw_iv.setImageResource(R.drawable.pin_un_check);
                            two_psw_iv.setImageResource(R.drawable.pin_un_check);
                            three_psw_iv.setImageResource(R.drawable.pin_un_check);
                            four_psw_iv.setImageResource(R.drawable.pin_un_check);
                            five_psw_iv.setImageResource(R.drawable.pin_un_check);
                            six_psw_iv.setImageResource(R.drawable.pin_un_check);
                        }else if(confirmPsw.length()==1){
                            one_psw_iv.setImageResource(R.drawable.pin_check);
                            two_psw_iv.setImageResource(R.drawable.pin_un_check);
                            three_psw_iv.setImageResource(R.drawable.pin_un_check);
                            four_psw_iv.setImageResource(R.drawable.pin_un_check);
                            five_psw_iv.setImageResource(R.drawable.pin_un_check);
                            six_psw_iv.setImageResource(R.drawable.pin_un_check);
                        }else if(confirmPsw.length() == 2){
                            one_psw_iv.setImageResource(R.drawable.pin_check);
                            two_psw_iv.setImageResource(R.drawable.pin_check);
                            three_psw_iv.setImageResource(R.drawable.pin_un_check);
                            four_psw_iv.setImageResource(R.drawable.pin_un_check);
                            five_psw_iv.setImageResource(R.drawable.pin_un_check);
                            six_psw_iv.setImageResource(R.drawable.pin_un_check);
                        }else if(confirmPsw.length() == 3){
                            one_psw_iv.setImageResource(R.drawable.pin_check);
                            two_psw_iv.setImageResource(R.drawable.pin_check);
                            three_psw_iv.setImageResource(R.drawable.pin_check);
                            four_psw_iv.setImageResource(R.drawable.pin_un_check);
                            five_psw_iv.setImageResource(R.drawable.pin_un_check);
                            six_psw_iv.setImageResource(R.drawable.pin_un_check);
                        }else if(confirmPsw.length() == 4){
                            one_psw_iv.setImageResource(R.drawable.pin_check);
                            two_psw_iv.setImageResource(R.drawable.pin_check);
                            three_psw_iv.setImageResource(R.drawable.pin_check);
                            four_psw_iv.setImageResource(R.drawable.pin_check);
                            five_psw_iv.setImageResource(R.drawable.pin_un_check);
                            six_psw_iv.setImageResource(R.drawable.pin_un_check);
                        }else if(confirmPsw.length() == 5){
                            one_psw_iv.setImageResource(R.drawable.pin_check);
                            two_psw_iv.setImageResource(R.drawable.pin_check);
                            three_psw_iv.setImageResource(R.drawable.pin_check);
                            four_psw_iv.setImageResource(R.drawable.pin_check);
                            five_psw_iv.setImageResource(R.drawable.pin_check);
                            six_psw_iv.setImageResource(R.drawable.pin_un_check);
                        }else if(confirmPsw.length() == 6){
                            one_psw_iv.setImageResource(R.drawable.pin_check);
                            two_psw_iv.setImageResource(R.drawable.pin_check);
                            three_psw_iv.setImageResource(R.drawable.pin_check);
                            four_psw_iv.setImageResource(R.drawable.pin_check);
                            five_psw_iv.setImageResource(R.drawable.pin_check);
                            six_psw_iv.setImageResource(R.drawable.pin_check);
                        }
                        System.out.println(" delete firstPsw = "+firstPsw +"///// confirmPsw= "+confirmPsw );
                        confirm_tip_tv.setText(getString(R.string.retype_your_code));
                    }else {
                        if(firstPsw.length()<=1){
                            firstPsw = "";
                        }else {
                            if(firstPsw.length()>=6){
                                return;
                            }
                            firstPsw= firstPsw.substring(0,firstPsw.length()-1);
                        }
                        if(firstPsw.length()<6){
                            firstPswSuccess = false;
                        }
                        if(StringMyUtil.isEmptyString(firstPsw)){
                            one_psw_iv.setImageResource(R.drawable.pin_un_check);
                            two_psw_iv.setImageResource(R.drawable.pin_un_check);
                            three_psw_iv.setImageResource(R.drawable.pin_un_check);
                            four_psw_iv.setImageResource(R.drawable.pin_un_check);
                            five_psw_iv.setImageResource(R.drawable.pin_un_check);
                            six_psw_iv.setImageResource(R.drawable.pin_un_check);
                        }else if(firstPsw.length()==1){
                            one_psw_iv.setImageResource(R.drawable.pin_check);
                            two_psw_iv.setImageResource(R.drawable.pin_un_check);
                            three_psw_iv.setImageResource(R.drawable.pin_un_check);
                            four_psw_iv.setImageResource(R.drawable.pin_un_check);
                            five_psw_iv.setImageResource(R.drawable.pin_un_check);
                            six_psw_iv.setImageResource(R.drawable.pin_un_check);
                        }else if(firstPsw.length() == 2){
                            one_psw_iv.setImageResource(R.drawable.pin_check);
                            two_psw_iv.setImageResource(R.drawable.pin_check);
                            three_psw_iv.setImageResource(R.drawable.pin_un_check);
                            four_psw_iv.setImageResource(R.drawable.pin_un_check);
                            five_psw_iv.setImageResource(R.drawable.pin_un_check);
                            six_psw_iv.setImageResource(R.drawable.pin_un_check);
                        }else if(firstPsw.length() == 3){
                            one_psw_iv.setImageResource(R.drawable.pin_check);
                            two_psw_iv.setImageResource(R.drawable.pin_check);
                            three_psw_iv.setImageResource(R.drawable.pin_check);
                            four_psw_iv.setImageResource(R.drawable.pin_un_check);
                            five_psw_iv.setImageResource(R.drawable.pin_un_check);
                            six_psw_iv.setImageResource(R.drawable.pin_un_check);
                        }else if(firstPsw.length() == 4){
                            one_psw_iv.setImageResource(R.drawable.pin_check);
                            two_psw_iv.setImageResource(R.drawable.pin_check);
                            three_psw_iv.setImageResource(R.drawable.pin_check);
                            four_psw_iv.setImageResource(R.drawable.pin_check);
                            five_psw_iv.setImageResource(R.drawable.pin_un_check);
                            six_psw_iv.setImageResource(R.drawable.pin_un_check);
                        }else if(firstPsw.length() == 5){
                            one_psw_iv.setImageResource(R.drawable.pin_check);
                            two_psw_iv.setImageResource(R.drawable.pin_check);
                            three_psw_iv.setImageResource(R.drawable.pin_check);
                            four_psw_iv.setImageResource(R.drawable.pin_check);
                            five_psw_iv.setImageResource(R.drawable.pin_check);
                            six_psw_iv.setImageResource(R.drawable.pin_un_check);
                        }else if(firstPsw.length() == 6){
                            one_psw_iv.setImageResource(R.drawable.pin_check);
                            two_psw_iv.setImageResource(R.drawable.pin_check);
                            three_psw_iv.setImageResource(R.drawable.pin_check);
                            four_psw_iv.setImageResource(R.drawable.pin_check);
                            five_psw_iv.setImageResource(R.drawable.pin_check);
                            six_psw_iv.setImageResource(R.drawable.pin_check);
                        }
                        System.out.println(" delete firstPsw = "+firstPsw +"///// confirmPsw= "+confirmPsw );
                    }
                }else {
                    if(position == 9){
                        return;
                    }else {
                        if(!firstPswSuccess){
                            firstPsw+= code;
                            if(firstPsw.length()==1){
                                one_psw_iv.setImageResource(R.drawable.pin_check);
                            }else if(firstPsw.length()==2){
                                two_psw_iv.setImageResource(R.drawable.pin_check);
                            }else if(firstPsw.length()==3){
                                three_psw_iv.setImageResource(R.drawable.pin_check);
                            }else if(firstPsw.length()==4){
                                four_psw_iv.setImageResource(R.drawable.pin_check);
                            }else if(firstPsw.length()==5){
                                five_psw_iv.setImageResource(R.drawable.pin_check);
                            }else if(firstPsw.length()==6){
                                six_psw_iv.setImageResource(R.drawable.pin_check);

                                one_psw_iv.setImageResource(R.drawable.pin_un_check);
                                two_psw_iv.setImageResource(R.drawable.pin_un_check);
                                three_psw_iv.setImageResource(R.drawable.pin_un_check);
                                four_psw_iv.setImageResource(R.drawable.pin_un_check);
                                five_psw_iv.setImageResource(R.drawable.pin_un_check);
                                six_psw_iv.setImageResource(R.drawable.pin_un_check);
                                confirm_tip_tv.setText(getString(R.string.retype_your_code));

                            }
                            System.out.println(" pinTest firstPsw = "+firstPsw +"///// confirmPsw= "+confirmPsw );
                            if(firstPsw.length()==6){
                                firstPswSuccess = true;
                            }
                        }else {
                            if(incorrect){
                                return;
                            }
                            confirmPsw+=code;
                            if(confirmPsw.length()==1){
                                one_psw_iv.setImageResource(R.drawable.pin_check);
                            }else if(confirmPsw.length()==2){
                                two_psw_iv.setImageResource(R.drawable.pin_check);
                            }else if(confirmPsw.length()==3){
                                three_psw_iv.setImageResource(R.drawable.pin_check);
                            }else if(confirmPsw.length()==4){
                                four_psw_iv.setImageResource(R.drawable.pin_check);
                            }else if(confirmPsw.length()==5){
                                five_psw_iv.setImageResource(R.drawable.pin_check);
                            }else if(confirmPsw.length()==6){
                                six_psw_iv.setImageResource(R.drawable.pin_check);
                            }
                            if(confirmPsw.length()>6){
                                return;
                            }
                            System.out.println(" pinTest firstPsw = "+firstPsw +"///// confirmPsw= "+confirmPsw );
                                if(!firstPsw.substring(confirmPsw.length()-1,confirmPsw.length()).equals(code)){
                                    confirm_tip_tv.setText(getString(R.string.incorrect));
                                     incorrect = true;
                                    YoYo.with(Techniques.Shake)
                                            .duration(500)
                                            .playOn(pin_code_linear);
                                    if(confirmPsw.length()==1){
                                        one_psw_iv.setImageResource(R.drawable.pin_check_incorrcet_shape);
                                    }else if(confirmPsw.length()==2){
                                        two_psw_iv.setImageResource(R.drawable.pin_check_incorrcet_shape);
                                    }else if(confirmPsw.length()==3){
                                        three_psw_iv.setImageResource(R.drawable.pin_check_incorrcet_shape);
                                    }else if(confirmPsw.length()==4){
                                        four_psw_iv.setImageResource(R.drawable.pin_check_incorrcet_shape);
                                    }else if(confirmPsw.length()==5){
                                        five_psw_iv.setImageResource(R.drawable.pin_check_incorrcet_shape);
                                    }else if(confirmPsw.length()==6){
                                        six_psw_iv.setImageResource(R.drawable.pin_check_incorrcet_shape);
                                    }
                                    System.out.println(" pinTest firstPsw = "+firstPsw +"///// confirmPsw= "+confirmPsw );
                                }else {
                                    incorrect = false;
                                    confirm_tip_tv.setText(getString(R.string.retype_your_code));
                                    if(confirmPsw.trim().equals(firstPsw.trim())){
                                            if(getIntent().getBooleanExtra("isCreate",false)){
                                                showDialog(getString(R.string.creating_wallet_tip));
                                                createWalletInteract.create(getIntent().getStringExtra("walletName"), firstPsw, confirmPsw, "")
                                                        .subscribe(ConfirmPinActivity.this::jumpToWalletBackUp, ConfirmPinActivity.this::showError);

                                            }else {
                                                showDialog(getString(R.string.loading_wallet_tip));
                                                createWalletInteract.loadWalletByMnemonic( ETHWalletUtils.ETH_JAXX_TYPE, getIntent().getStringExtra("mnemonic"), confirmPsw.trim()).subscribe(ConfirmPinActivity.this::loadSuccess, ConfirmPinActivity.this::onError);
                                            }




                                    }
                                }

                        }
                    }
                }

            }
        });
    }
    public void loadSuccess(ETHWallet wallet) {
        dismissDialog();
        wallet.setIsBackup(true);
        ToastUtils.showToast(R.string.Import_wallet_successfully);
        setResult(RESULT_OK);
        finish();
    }

    public void onError(Throwable e) {
        ToastUtils.showToast(R.string.Failed_to_import_wallet);
        dismissDialog();
    }

    public void showError(Throwable errorInfo) {
        dismissDialog();
        LogUtils.e("CreateWalletActivity", errorInfo);
        ToastUtils.showToast(errorInfo.toString());
    }
    public void jumpToWalletBackUp(ETHWallet wallet) {
        ToastUtils.showToast("创建钱包成功");
        dismissDialog();

        boolean firstAccount = getIntent().getBooleanExtra("first_account", false);

        HttpApiUtils.addAddress(this,null,wallet);

//        setResult(CREATE_WALLET_RESULT, new Intent());
        Intent intent = new Intent(this, WalletBackupActivity.class);
        intent.putExtra("walletId", wallet.getId());
        intent.putExtra("walletPwd", wallet.getPassword());
        intent.putExtra("walletAddress", wallet.getAddress());
        intent.putExtra("walletName", wallet.getName());
        intent.putExtra("walletMnemonic", wallet.getMnemonic());
        intent.putExtra("first_account", true);
        startActivity(intent);

    }

    @Override
    public void configViews() {

    }
}