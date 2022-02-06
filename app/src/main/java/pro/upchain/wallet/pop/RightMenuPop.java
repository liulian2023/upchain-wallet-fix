package pro.upchain.wallet.pop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;

import java.util.ArrayList;

import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BasePopupWindow;
import pro.upchain.wallet.entity.RightMenuEntity;
import pro.upchain.wallet.ui.adapter.RightMenuAdapter;
import pro.upchain.wallet.ui.fragment.DappBrowserFragment;
import pro.upchain.wallet.utils.TriangleDrawable;
import pro.upchain.wallet.viewmodel.DappBrowserViewModel;

public class RightMenuPop extends BasePopupWindow {
    View v_arrow;
    RecyclerView right_menu_pop_recycler;
    RightMenuAdapter rightMenuAdapter;
    public ArrayList<RightMenuEntity>rightMenuEntityArrayList = new ArrayList<>();
    Activity activity;
    DappBrowserViewModel viewModel;
    DappBrowserFragment dappBrowserFragment;
    public RightMenuPop(Context context, DappBrowserViewModel viewModel, DappBrowserFragment dappBrowserFragment) {
        super(context);
        activity= (Activity) context;
        this.viewModel= viewModel;
        this.dappBrowserFragment= dappBrowserFragment;
        setAnimationStyle(R.style.RightTop2PopAnim);
        initRecycler();
    }

    private void initRecycler() {
        rightMenuAdapter = new RightMenuAdapter(R.layout.right_menu_item_layout,rightMenuEntityArrayList);
        right_menu_pop_recycler.setLayoutManager(new LinearLayoutManager(mContext));
        right_menu_pop_recycler.setAdapter(rightMenuAdapter);
        rightMenuAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
               if(mOnPopItemClick!=null){
                   mOnPopItemClick.onPopItemClick(view,position);
               }
                RightMenuPop.this.dismiss();
            }
        });
        boolean bookmarkAdded = dappBrowserFragment.isBookmarkAdded();

        rightMenuEntityArrayList.add(new RightMenuEntity(mContext.getResources().getString(R.string.action_reload)));
        if(bookmarkAdded){
            rightMenuEntityArrayList.add(new RightMenuEntity(mContext.getResources().getString(R.string.action_view_bookmarks)));
        }else {
            rightMenuEntityArrayList.add(new RightMenuEntity(mContext.getResources().getString(R.string.action_add_bookmark)));
        }
        rightMenuEntityArrayList.add(new RightMenuEntity(mContext.getResources().getString(R.string.action_share)));
    }

    @Override
    public void initView() {
        super.initView();
        rootView = LayoutInflater.from(mContext).inflate(R.layout.layout_right_menu,null);
        right_menu_pop_recycler=rootView.findViewById(R.id.right_menu_pop_recycler);
        v_arrow=rootView.findViewById(R.id.v_arrow);
        v_arrow.setBackground(new TriangleDrawable(TriangleDrawable.TOP, Color.WHITE));
    }
}
