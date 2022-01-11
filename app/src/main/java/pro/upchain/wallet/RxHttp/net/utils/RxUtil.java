/*
 * Copyright (c) 2019.  ganzhe
 */

package pro.upchain.wallet.RxHttp.net.utils;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pro.upchain.wallet.RxHttp.net.common.ProgressUtils;

;

public class RxUtil {

    /**
     * @param activity    Activity
     * @param showLoading 是否显示Loading
     * @return 转换后的ObservableTransformer
     */
    public static <T> ObservableTransformer<T, T> rxSchedulerHelper(final AppCompatActivity activity, final boolean showLoading) {
        if (activity == null) return rxSchedulerHelper();

        return observable -> {
            Observable<T> compose =observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(AndroidLifecycle.createLifecycleProvider(activity).bindUntilEvent(Lifecycle.Event.ON_DESTROY));
            if (showLoading) {
                return compose.compose(ProgressUtils.applyProgressBar(activity));
            } else {
                return compose;
            }
        };
    }


    /**
     * @param fragment    fragment
     * @param showLoading 是否显示Loading
     * @return 转换后的ObservableTransformer
     */
    public static <T> ObservableTransformer<T, T> rxSchedulerHelper(final Fragment fragment, boolean showLoading) {
        if (fragment == null || fragment.getActivity() == null) return rxSchedulerHelper();
        return observable -> {
            Observable<T> compose = observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(AndroidLifecycle.createLifecycleProvider(fragment.getActivity()).bindUntilEvent(Lifecycle.Event.ON_PAUSE));
            if (showLoading) {
                return compose.compose(ProgressUtils.applyProgressBar(fragment.getActivity()));
            } else {
                return compose;
            }
        };
    }


    /**
     * 统一线程处理
     * @return 转换后的ObservableTransformer
     */
    public static <T> ObservableTransformer<T, T> rxSchedulerHelper() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
