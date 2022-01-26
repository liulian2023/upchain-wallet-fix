package pro.upchain.wallet.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pro.upchain.wallet.R;

public class CommonToolBarUtils {
    public static void initToolbar(Context context, int resId){
        Activity activity = (Activity) context;
        TextView titleTv = getContentView(activity).findViewById(R.id.tv_title);
        if(titleTv!=null){
            titleTv.setText(context.getString(resId));
        }

    }
    public static void initToolbar(Context context, String title){
        Activity activity = (Activity) context;
        TextView titleTv = getContentView(activity).findViewById(R.id.tv_title);
        if(titleTv!=null){
            titleTv.setText(title);
        }

    }
    public static View getContentView(Activity context) {
        return ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);
    }
}
