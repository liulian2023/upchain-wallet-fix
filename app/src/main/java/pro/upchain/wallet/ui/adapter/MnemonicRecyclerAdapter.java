package pro.upchain.wallet.ui.adapter;

import androidx.annotation.Nullable;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import pro.upchain.wallet.R;
import pro.upchain.wallet.entity.MnemonicEntity;

public class MnemonicRecyclerAdapter extends BaseQuickAdapter<MnemonicEntity, BaseViewHolder> {
    public MnemonicRecyclerAdapter(int layoutResId, @Nullable List<MnemonicEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, MnemonicEntity mnemonicEntity) {
        int num = baseViewHolder.getAdapterPosition() + 1;
        baseViewHolder.setText(R.id.mnemonic_name_tv,mnemonicEntity.getName());
        baseViewHolder.setText(R.id.mnemonic_num_tv,num+"");

    }
}
