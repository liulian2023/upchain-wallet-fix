package pro.upchain.wallet.view;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import pro.upchain.wallet.R;
import pro.upchain.wallet.utils.StringUtils;
import pro.upchain.wallet.utils.ToastUtils;


public class InputPwdView extends FrameLayout {
    private EditText password_etv;
    private onConfirmSend onConfirmSender;

    public interface onConfirmSend {
        void sendTransaction(String pwd);
    }

    public InputPwdView(@NonNull Context context, onConfirmSend l) {
        super(context);
        onConfirmSender = l;

        LayoutInflater.from(getContext())
                .inflate(R.layout.layout_input_password, this, true);
        password_etv = (EditText)findViewById(R.id.password_etv);

        findViewById(R.id.next_btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = password_etv.getText().toString();
                if(StringUtils.isEmpty(value)){
                    ToastUtils.showToast(R.string.please_input_password);
                    return;
                }
                onConfirmSender.sendTransaction(getPassword_etv());
            }
        });
    }



    public String getPassword_etv() {
        return password_etv.getText().toString();
    }

    public void showKeyBoard() {
        password_etv.requestFocus();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        showKeyBoard();
    }
}
