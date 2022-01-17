package pro.upchain.wallet.ui.adapter;

import android.view.View;
import android.widget.ImageView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import pro.upchain.wallet.R;
import pro.upchain.wallet.entity.TokenInfo;
import pro.upchain.wallet.ui.activity.AddTokenActivity;
import pro.upchain.wallet.utils.GlideLoadUtils;


public class AddTokenAdapter extends BaseQuickAdapter<AddTokenActivity.TokenItem,BaseViewHolder>{

    public AddTokenAdapter(int layoutResId, @Nullable List<AddTokenActivity.TokenItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, AddTokenActivity.TokenItem tokenItem) {
        ImageView add_token_iv = baseViewHolder.getView(R.id.add_token_iv);
        ImageView add_token_add_iv = baseViewHolder.getView(R.id.add_token_add_iv);
        TokenInfo tokenInfo = tokenItem.tokenInfo;
        GlideLoadUtils.loadCircleNetImage(getContext(),add_token_iv,tokenInfo.imgUrl);

        if(tokenItem.added){
            add_token_add_iv.setVisibility(View.GONE);
        }else {
            add_token_add_iv.setVisibility(View.VISIBLE);
        }
        baseViewHolder.setText(R.id.add_token_name_tv, tokenInfo.symbol);
        baseViewHolder.setText(R.id.add_token_adress_tv, tokenInfo.address);
    }
}
