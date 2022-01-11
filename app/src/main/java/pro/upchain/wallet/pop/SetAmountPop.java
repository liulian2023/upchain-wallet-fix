package pro.upchain.wallet.pop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BasePopupWindow;

public class SetAmountPop extends BasePopupWindow {
    private TextView set_amount_sure_tv;
    private TextView set_amount_cancel_tv;
    public EditText set_amount_etv;
    public SetAmountPop(Context context) {
        super(context);
        bindView();

    }

    private void bindView() {
        set_amount_sure_tv = rootView.findViewById(R.id.set_amount_sure_tv);
        set_amount_cancel_tv = rootView.findViewById(R.id.set_amount_cancel_tv);
        set_amount_etv = rootView.findViewById(R.id.set_amount_etv);
        set_amount_cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetAmountPop.this.dismiss();
            }
        });
        set_amount_sure_tv.setOnClickListener(this);
        set_amount_etv.addTextChangedListener(this);
    }

    @Override
    public void initView() {
        super.initView();
        rootView = LayoutInflater.from(mContext).inflate(R.layout.set_amount_pop_layout,null);
    }

    public EditText getSet_amount_etv() {
        return set_amount_etv;
    }
}
