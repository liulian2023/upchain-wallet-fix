package pro.upchain.wallet.ui.adapter;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import pro.upchain.wallet.R;
import pro.upchain.wallet.base.CommonAdapter;
import pro.upchain.wallet.base.ViewHolder;
import pro.upchain.wallet.domain.ETHWallet;

import java.util.List;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */


public class WalletManagerAdapter extends BaseQuickAdapter<ETHWallet, BaseViewHolder> {


    public WalletManagerAdapter(int layoutResId, @Nullable List<ETHWallet> data) {
        super(layoutResId, data);
    }



    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, ETHWallet ethWallet) {
        int itemPosition = getItemPosition(ethWallet);
        View wallet_manage_wrap = baseViewHolder.getView(R.id.wallet_manage_wrap);
        if((itemPosition+1)%2 == 0){
            wallet_manage_wrap.setBackgroundResource(R.drawable.wallet_manage_double_shape);
        }else {
            wallet_manage_wrap.setBackgroundResource(R.drawable.wallet_manage_single_shape);
        }
        baseViewHolder.setText(R.id.wallet_name_tv,ethWallet.getName());
        baseViewHolder.setText(R.id.wallet_address_tv,ethWallet.getAddress());
    }
}
