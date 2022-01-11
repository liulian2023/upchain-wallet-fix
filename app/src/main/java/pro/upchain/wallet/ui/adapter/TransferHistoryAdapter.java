package pro.upchain.wallet.ui.adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseDelegateMultiAdapter;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.math.BigDecimal;
import java.util.List;

import pro.upchain.wallet.R;
import pro.upchain.wallet.entity.TransferHistoryEntity;

public class TransferHistoryAdapter extends BaseMultiItemQuickAdapter<TransferHistoryEntity.ListBean, BaseViewHolder> {
    String symbol;
    String usdtRate;

    public TransferHistoryAdapter(@Nullable List<TransferHistoryEntity.ListBean> data, String symbol, String usdtRate) {
        super(data);
        this.symbol = symbol;
        this.usdtRate = usdtRate;
        addItemType(0,R.layout.transfer_history_title_layout);
        addItemType(1,R.layout.transfer_history_recycler_item);
    }
    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, TransferHistoryEntity.ListBean listBean) {
        int itemViewType = baseViewHolder.getItemViewType();
        switch (itemViewType){
            case 0:
                baseViewHolder.setText(R.id.transfer_title_name,listBean.getTitleName());
                break;
            case 1:
                TextView address_tv = baseViewHolder.getView(R.id.address_tv);
                TextView date_tv = baseViewHolder.getView(R.id.date_tv);
                TextView us_amount_tv = baseViewHolder.getView(R.id.us_amount_tv);
                TextView transfer_amount_tv = baseViewHolder.getView(R.id.transfer_amount_tv);
                String money = listBean.getMoney();
                transfer_amount_tv.setText(money+symbol);
                date_tv.setText(listBean.getCreateTime());
                String toUid = listBean.getToUid();
                BigDecimal rateAmount = new BigDecimal(money).multiply(new BigDecimal(usdtRate)).setScale(2, BigDecimal.ROUND_HALF_UP);
                if(toUid.equals("0")){
                    //转出
                    address_tv.setText(listBean.getToAddress());
                    us_amount_tv.setText("-US"+rateAmount);
                    us_amount_tv.setTextColor(Color.BLACK);
                }else {
                    //转入
                    address_tv.setText(listBean.getFromAddress());
                    us_amount_tv.setText("+US"+rateAmount);
                    us_amount_tv.setTextColor(ContextCompat.getColor(getContext(),R.color.in_amount_color));
                }
                break;
            default:
                break;
        }
    }
}
