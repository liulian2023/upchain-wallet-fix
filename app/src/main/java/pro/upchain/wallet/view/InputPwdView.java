package pro.upchain.wallet.view;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.ArrayList;

import pro.upchain.wallet.R;
import pro.upchain.wallet.RxHttp.net.utils.StringMyUtil;
import pro.upchain.wallet.entity.ConfirmPinEntity;
import pro.upchain.wallet.ui.activity.ConfirmPinActivity;
import pro.upchain.wallet.ui.adapter.ConfirmPinAdapter;
import pro.upchain.wallet.utils.ETHWalletUtils;
import pro.upchain.wallet.utils.StringUtils;
import pro.upchain.wallet.utils.ToastUtils;
import pro.upchain.wallet.utils.WalletDaoUtils;


public class InputPwdView extends FrameLayout {
    RecyclerView recyclerView;
    ImageView one_psw_iv;
    ImageView two_psw_iv;
    ImageView three_psw_iv;
    ImageView four_psw_iv;
    ImageView five_psw_iv;
    TextView confirm_tip_tv;
    ImageView six_psw_iv;
    LinearLayout pin_code_linear;
    LinearLayout confirm_back_linear;
    ConfirmPinAdapter confirmPinAdapter;
    ArrayList<ConfirmPinEntity> confirmPinEntityArrayList = new ArrayList<>();
    String firstPsw = "";
    private boolean incorrect = false;
    private onConfirmSend onConfirmSender;


    public interface onConfirmSend {
        void sendTransaction(String pwd);
    }

    public InputPwdView(@NonNull Context context, onConfirmSend l) {
        super(context);
        onConfirmSender = l;

        LayoutInflater.from(getContext())
                .inflate(R.layout.layout_input_password, this, true);
        recyclerView = findViewById(R.id.recyclerView);
        one_psw_iv = findViewById(R.id.one_psw_iv);
        two_psw_iv = findViewById(R.id.two_psw_iv);
        three_psw_iv = findViewById(R.id.three_psw_iv);
        four_psw_iv = findViewById(R.id.four_psw_iv);
        five_psw_iv = findViewById(R.id.five_psw_iv);
        six_psw_iv = findViewById(R.id.six_psw_iv);
        pin_code_linear = findViewById(R.id.pin_code_linear);
        four_psw_iv = findViewById(R.id.four_psw_iv);
        confirm_tip_tv = findViewById(R.id.confirm_tip_tv);
        confirm_back_linear = findViewById(R.id.confirm_back_linear);
        confirm_back_linear.setVisibility(INVISIBLE);
        initRecycler();
/*        findViewById(R.id.next_btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = password_etv.getText().toString();
                if(StringUtils.isEmpty(value)){
                    ToastUtils.showToast(R.string.please_input_password);
                    return;
                }
                onConfirmSender.sendTransaction(getPassword_etv());
            }
        });*/


    }
    private void initRecycler() {
        confirmPinAdapter = new ConfirmPinAdapter(R.layout.confirm_pin_recycler,confirmPinEntityArrayList);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        recyclerView.setAdapter(confirmPinAdapter);
        for (int i = 0; i < 12; i++) {
            ConfirmPinEntity confirmPinEntity = new ConfirmPinEntity();
            if(i == 9){
                confirmPinEntity.setCode("");
            }else if(i == 10){
                confirmPinEntity.setCode("0");
            } else {
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
                if(position == 9){
                    return;
                }
                if(delete){
                    if(StringMyUtil.isEmptyString(firstPsw)){
                        return;
                    }
                    incorrect = false;
                    if(firstPsw.length()<=1){
                        firstPsw = "";
                    }else {
                        if(firstPsw.length()>=6){
                            return;
                        }
                        firstPsw= firstPsw.substring(0,firstPsw.length()-1);
                    }
                    System.out.println(" delete firstPsw = "+firstPsw  );
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
                }else {
                    if(position == 9){
                        return;
                    }else {
                        if(incorrect){
                            return;
                        }
                        firstPsw+=code;
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
                        }
                        if(firstPsw.length()>6){
                            return;
                        }
                        String password = WalletDaoUtils.getCurrent().getPassword();
                        if(!password.substring(firstPsw.length()-1,firstPsw.length()).equals(code)){
                            confirm_tip_tv.setText(getContext().getString(R.string.incorrect));
                            incorrect = true;
                            YoYo.with(Techniques.Shake)
                                    .duration(500)
                                    .playOn(pin_code_linear);
                            if(firstPsw.length()==1){
                                one_psw_iv.setImageResource(R.drawable.pin_check_incorrcet_shape);
                            }else if(firstPsw.length()==2){
                                two_psw_iv.setImageResource(R.drawable.pin_check_incorrcet_shape);
                            }else if(firstPsw.length()==3){
                                three_psw_iv.setImageResource(R.drawable.pin_check_incorrcet_shape);
                            }else if(firstPsw.length()==4){
                                four_psw_iv.setImageResource(R.drawable.pin_check_incorrcet_shape);
                            }else if(firstPsw.length()==5){
                                five_psw_iv.setImageResource(R.drawable.pin_check_incorrcet_shape);
                            }else if(firstPsw.length()==6){
                                six_psw_iv.setImageResource(R.drawable.pin_check_incorrcet_shape);
                            }
                            System.out.println(" pinTest firstPsw = "+firstPsw  );
                        }else {
                            incorrect = false;
                            confirm_tip_tv.setText(getContext().getString(R.string.input_code));
                            if(firstPsw.trim().equals(password.trim())){
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        onConfirmSender.sendTransaction(firstPsw);
                                    }
                                },50);

                            }
                        }
                    }
                }
            }
        });
    }

}
