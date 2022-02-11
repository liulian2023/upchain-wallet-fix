package pro.upchain.wallet.ui.adapter;

import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import pro.upchain.wallet.R;
import pro.upchain.wallet.RxHttp.net.utils.StringMyUtil;
import pro.upchain.wallet.entity.Token;
import pro.upchain.wallet.utils.GlideLoadUtils;


import java.util.ArrayList;
import java.util.List;

public class TokensAdapter extends BaseQuickAdapter<Token, BaseViewHolder> {

    private final List<Token> items = new ArrayList<>();


    public TokensAdapter(int layoutResId, @Nullable List<Token> data) {
        super(layoutResId, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, Token token) {
        helper.setText(R.id.symbol, token.tokenInfo.symbol);
        if(StringMyUtil.isNotEmpty(token.balance)){
            helper.setText(R.id.balance, token.balance);
        }
        helper.setText(R.id.name_tv, token.tokenInfo.name);
        if(StringMyUtil.isNotEmpty(token.value)){
            token.value = token.value.equals("0")?"0.00":token.value;
            helper.setText(R.id.tv_property_cny, "US$"+token.value+"");
        }
        ImageView logo = helper.getView(R.id.logo);
        String imgUrl = token.tokenInfo.imgUrl;
        if(TextUtils.isEmpty(imgUrl)){
            logo.setImageResource(R.drawable.wallet_logo_demo);
        }else {
            GlideLoadUtils.loadNetImage( getContext(),logo,imgUrl);
        }
    }

    public void setTokens(List<Token> tokens) {
        setNewData(tokens);
    }


}
