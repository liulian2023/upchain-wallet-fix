package pro.upchain.wallet.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import pro.upchain.wallet.R;

public class GlideLoadUtils {
    public static void loadNetImage(Context context, ImageView imageView,String url){
        Glide.with(context)
                .load(url)
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }


    public static void loadTokenImage(Context context, ImageView imageView, String url){
        Glide.with(context)
                .load(url)
                .skipMemoryCache(false)
                .circleCrop()
                .placeholder(R.drawable.wallet_logo_demo)
                .error(R.drawable.wallet_logo_demo)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }
}
