package pro.upchain.wallet.ui.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import pro.upchain.wallet.R;
import pro.upchain.wallet.entity.ContractEntity;
import pro.upchain.wallet.entity.TokenInfo;
import pro.upchain.wallet.utils.GlideLoadUtils;

public class AllCoinAdapter extends BaseMultiItemQuickAdapter<ContractEntity, BaseViewHolder> {

    public AllCoinAdapter(@Nullable List<ContractEntity> data) {
        super(data);
        addItemType(0,R.layout.all_coin_title_item);
        addItemType(1,R.layout.all_coin_recycler_item);
    }
    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder,ContractEntity contractEntity) {
        int itemViewType = baseViewHolder.getItemViewType();
        switch (itemViewType){
            case 0:
                baseViewHolder.setText(R.id.all_coin_title_tv,contractEntity.getTitleName());
                break;
            case 1:
                ImageView all_coin_iv = baseViewHolder.getView(R.id.all_coin_iv);
                GlideLoadUtils.loadTokenImage(getContext(),all_coin_iv,contractEntity.getIcon());
                baseViewHolder.setText(R.id.all_coin_symbol_tv, contractEntity.getTokenSymbol());
                baseViewHolder.setText(R.id.all_coin_name_tv, contractEntity.getTokenName());
                break;
            default:
                break;
        }
    }
}
