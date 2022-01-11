

package pro.upchain.wallet.RxHttp.net.utils;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.base.BaseFragment;


public class ErrorUtil {
    public  static void  hideErrorLayout(View recyclerView, LinearLayout errorLinear){
        if(recyclerView!=null){
            if(recyclerView.getVisibility()!=View.VISIBLE){
                recyclerView.setVisibility(View.VISIBLE);
            }
        }if(errorLinear!=null){
            if(errorLinear.getVisibility()!=View.GONE){
                errorLinear.setVisibility(View.GONE);
            }
        }
    }

    public static void showErrorLayout(Activity activity,View recyclerView,LinearLayout errorLinear,TextView reloadTv){
        if(errorLinear!=null){
            if(errorLinear.getVisibility()!=View.VISIBLE){
                errorLinear.setVisibility(View.VISIBLE);
            }
        }
        if(recyclerView!=null){
            if(recyclerView.getVisibility()!=View.GONE){
                recyclerView.setVisibility(View.GONE);
            }
        }
        if(reloadTv!=null){
            reloadTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(activity instanceof BaseActivity){
                        ((BaseActivity) activity).errorRefresh();
                    }
                }
            });
        }
    }

    public static void showErrorLayout(Fragment fragment, View recyclerView, LinearLayout errorLinear, TextView reloadTv){
        if(errorLinear!=null){
            if(errorLinear.getVisibility()!=View.VISIBLE){

                errorLinear.setVisibility(View.VISIBLE);
            }
        }
        if(recyclerView!=null){
            if(recyclerView.getVisibility()!=View.GONE){

                recyclerView.setVisibility(View.GONE);
            }
        }
        if(reloadTv!=null){
            reloadTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(fragment!=null){
                        if(fragment instanceof BaseFragment){
                            ((BaseFragment) fragment).errorRefresh();
                        }
                    }
                }
            });
        }
    }

}



