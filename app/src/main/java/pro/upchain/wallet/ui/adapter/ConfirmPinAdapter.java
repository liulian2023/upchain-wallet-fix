package pro.upchain.wallet.ui.adapter;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import pro.upchain.wallet.R;
import pro.upchain.wallet.RxHttp.net.utils.StringMyUtil;
import pro.upchain.wallet.entity.ConfirmPinEntity;

public class ConfirmPinAdapter extends BaseQuickAdapter<ConfirmPinEntity, BaseViewHolder> {
    public ConfirmPinAdapter(int layoutResId, @Nullable List<ConfirmPinEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, ConfirmPinEntity confirmPinEntity) {
        int adapterPosition = baseViewHolder.getAdapterPosition();

        ImageView confirm_item_iv = baseViewHolder.getView(R.id.confirm_item_iv);
        String code = confirmPinEntity.getCode();
        if(StringMyUtil.isEmptyString(code)){
            confirm_item_iv.setVisibility(View.INVISIBLE);
        }else {
            confirm_item_iv.setVisibility(View.VISIBLE);
                if(adapterPosition == 0){
                    confirm_item_iv.setImageResource(R.drawable.one);
                }else if(adapterPosition == 1){
                    confirm_item_iv.setImageResource(R.drawable.two);
                }else if(adapterPosition == 2){
                    confirm_item_iv.setImageResource(R.drawable.three);
                }else if(adapterPosition == 3){
                    confirm_item_iv.setImageResource(R.drawable.four);
                }    else if(adapterPosition == 4){
                    confirm_item_iv.setImageResource(R.drawable.five);
                }else if(adapterPosition == 5){
                    confirm_item_iv.setImageResource(R.drawable.six);
                }else if(adapterPosition == 6){
                    confirm_item_iv.setImageResource(R.drawable.seven);
                }   else if(adapterPosition == 7){
                    confirm_item_iv.setImageResource(R.drawable.eight);
                }else if(adapterPosition == 8){
                    confirm_item_iv.setImageResource(R.drawable.nine);
                }else if(adapterPosition == 10){
                    confirm_item_iv.setImageResource(R.drawable.zero);
                }else {
                    confirm_item_iv.setImageResource(R.drawable.pin_delete);
                }
            }


    }
}
