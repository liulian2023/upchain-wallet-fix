package pro.upchain.wallet.ui.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import pro.upchain.wallet.R;
import pro.upchain.wallet.entity.RightMenuEntity;

public class RightMenuAdapter extends BaseQuickAdapter<RightMenuEntity, BaseViewHolder> {

    public RightMenuAdapter(int layoutResId, @Nullable List<RightMenuEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, RightMenuEntity rightMenuEntity) {
        baseViewHolder.setText(R.id.menu_tv,rightMenuEntity.getName());
    }
}
