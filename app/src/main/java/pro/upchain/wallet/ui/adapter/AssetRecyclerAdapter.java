package pro.upchain.wallet.ui.adapter;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import pro.upchain.wallet.C;
import pro.upchain.wallet.R;
import pro.upchain.wallet.entity.Token;
import pro.upchain.wallet.entity.TokenInfo;
import pro.upchain.wallet.utils.GlideLoadUtils;
import pro.upchain.wallet.utils.StringUtils;

public class AssetRecyclerAdapter extends BaseQuickAdapter<Token, BaseViewHolder> {

    public AssetRecyclerAdapter(int layoutResId, @Nullable List<Token> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, Token token) {
        ImageView asset_iv = baseViewHolder.getView(R.id.asset_iv);
        ImageView delete_iv = baseViewHolder.getView(R.id.delete_iv);
        TokenInfo tokenInfo = token.tokenInfo;
        baseViewHolder.setText(R.id.asset_address_tv, tokenInfo.address);
        baseViewHolder.setText(R.id.asset_name_tv, tokenInfo.symbol);
        String balance = token.balance;
        if(tokenInfo.symbol.equals(C.ETH_SYMBOL) || tokenInfo.symbol.equals(C.BSC_SYMBOL)){
            delete_iv.setVisibility(View.INVISIBLE);
        }else {
            delete_iv.setVisibility(View.VISIBLE);
        }
        if(StringUtils.isEmpty(balance)){
            baseViewHolder.setText(R.id.asset_amount_tv, "0");
        }else {
            baseViewHolder.setText(R.id.asset_amount_tv, balance);
        }

        GlideLoadUtils.loadTokenImage(getContext(),asset_iv,tokenInfo.imgUrl);
    }

}
