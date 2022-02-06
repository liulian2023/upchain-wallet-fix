package pro.upchain.wallet.ui.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import pro.upchain.wallet.R;
import pro.upchain.wallet.entity.BookmarkEntity;

public class BookMarkAdapter extends BaseQuickAdapter<BookmarkEntity, BaseViewHolder> {


    public BookMarkAdapter(int layoutResId, @Nullable List<BookmarkEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, BookmarkEntity bookmarkEntity) {
        baseViewHolder.setText(R.id.bookmark_title_tv,bookmarkEntity.getTitle());
        baseViewHolder.setText(R.id.bookmark_url_tv,bookmarkEntity.getAddress());
        View view = baseViewHolder.getView(R.id.delete_iv);
        if(bookmarkEntity.isShowDelete()){
            view.setVisibility(View.VISIBLE);
        }else {
            view.setVisibility(View.GONE);
        }
    }
}
