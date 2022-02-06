package pro.upchain.wallet.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseFragment;
import pro.upchain.wallet.entity.BookmarkEntity;
import pro.upchain.wallet.ui.adapter.BookMarkAdapter;


public class BookMaskFragment extends BaseFragment {
    @BindView(R.id.bookmark_recycler)
    RecyclerView bookmark_recycler;
    @BindView(R.id.bookmark_manage_tv)
    TextView bookmark_manage_tv;
    BookMarkAdapter bookMarkAdapter;
    ArrayList<BookmarkEntity> bookmarkEntityArrayList = new ArrayList<>();
    private DappBrowserFragment dappBrowserFragment;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_book_mask;
    }



    @Override
    public void attachView() {

    }

    @Override
    public void initDatas() {
        Fragment parentFragment = getParentFragment();
         dappBrowserFragment = (DappBrowserFragment) parentFragment;

        bookMarkAdapter = new BookMarkAdapter(R.layout.bookmask_item_layout,bookmarkEntityArrayList);
        bookmark_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        bookmark_recycler.setAdapter(bookMarkAdapter);
        bookMarkAdapter.addChildClickViewIds(R.id.delete_iv);
        bookMarkAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                BookmarkEntity bookmarkEntity = bookmarkEntityArrayList.get(position);
                if(dappBrowserFragment!=null){
                    dappBrowserFragment.loadUrl(bookmarkEntity.getAddress());
                }

            }
        });
        bookMarkAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                bookmarkEntityArrayList.remove(position);
                dappBrowserFragment.removeBookmark();
                bookMarkAdapter.notifyItemRemoved(position);
                if(bookmarkEntityArrayList.size() == 0){
                    dappBrowserFragment.hideBookmarks();
                }
            }
        });
        List<BookmarkEntity> bookmarks = dappBrowserFragment.viewModel.getBookmarks();
        bookmarkEntityArrayList.addAll(bookmarks);
        bookMarkAdapter.notifyDataSetChanged();

    }
    @OnClick({R.id.bookmark_manage_tv})
    public  void  onCLick(View view){
        switch (view.getId()){
            case R.id.bookmark_manage_tv:
                if(bookmarkEntityArrayList!=null && bookmarkEntityArrayList.size()!=0){
                    boolean showDelete = bookmarkEntityArrayList.get(0).isShowDelete();
                    for (int i = 0; i < bookmarkEntityArrayList.size(); i++) {
                        bookmarkEntityArrayList.get(i).setShowDelete(!showDelete);
                    }
                    bookMarkAdapter.notifyDataSetChanged();
                }

                break;
            default:
                break;
        }
    }
    @Override
    public void configViews() {

    }
}