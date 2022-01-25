package pro.upchain.wallet.view;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.spongycastle.util.encoders.Base64;
import org.web3j.utils.Numeric;

import java.nio.charset.Charset;

import pro.upchain.wallet.R;
import pro.upchain.wallet.web3.entity.Message;


public class SignMessageDialog extends Dialog {

    TextView message_content_tv;
    Button sure_btn;
    Button cancel_btb;
    private Context context;
    String signMessage;
    ImageView load_more_iv;
    NestedScrollView nestedScrollView;

    public SignMessageDialog(@NonNull Activity activity,String signMessage) {
        super(activity);
        this.context = activity;
        this.signMessage = signMessage;

        setContentView(R.layout.sign_message_dialog_layout);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCanceledOnTouchOutside(true);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.BOTTOM);

        nestedScrollView = findViewById(R.id.nestedScrollView);
        load_more_iv = findViewById(R.id.load_more_iv);
        message_content_tv = findViewById(R.id.message_content_tv);
        message_content_tv.setText(signMessage);
        sure_btn = findViewById(R.id.sure_btn);
        cancel_btb = findViewById(R.id.cancel_btb);

        cancel_btb.setOnClickListener(v -> dismiss());
        load_more_iv.setOnClickListener(v ->{
            Animation animation = null;

           if(nestedScrollView.getVisibility() == View.VISIBLE){
               animation=  AnimationUtils.loadAnimation(context, R.anim.rotate_anim);
               animation.setFillAfter(true);
               nestedScrollView.setVisibility(View.INVISIBLE);
           }else {
               animation= AnimationUtils.loadAnimation(context, R.anim.rotate_anim_end);
               animation.setFillAfter(true);
               nestedScrollView.setVisibility(View.VISIBLE);
           }
            load_more_iv.startAnimation(animation);
        });
    }




    public void setOnApproveListener(View.OnClickListener listener) {
        sure_btn.setOnClickListener(listener);
        //btnApprove.setOnClickListener(listener);
    }

    public void setOnRejectListener(View.OnClickListener listener) {
        cancel_btb.setOnClickListener(listener);
    }
}
