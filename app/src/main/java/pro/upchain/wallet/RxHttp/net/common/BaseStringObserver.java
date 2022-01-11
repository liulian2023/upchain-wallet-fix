package pro.upchain.wallet.RxHttp.net.common;

import static android.widget.Toast.LENGTH_SHORT;

import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;
import pro.upchain.wallet.RxHttp.net.model.SingleLoginEvent;
import pro.upchain.wallet.RxHttp.net.utils.AppContextUtils;
import pro.upchain.wallet.RxHttp.net.utils.RxExceptionUtils;
import pro.upchain.wallet.RxHttp.net.utils.StringMyUtil;
import retrofit2.Response;

/**
 * created  by ganzhe on 2019/11/16.
 */
public abstract class BaseStringObserver<T extends ResponseBody> implements Observer<Response<T>> {

    public static final String RESPONSE_RETURN_ERROR = "服务器错误";
    private static final String REQUEST_404 = "请求地址已失效";
    private static final String REQUEST_400 = "请求失败";
    private static final String REQUEST_502 = " 502 Bad Gateway";
    private static final String REQUEST_UN_KNOW = "未知异常";

    public  BaseStringObserver() {
    }

    @Override
    public void onSubscribe(Disposable d) {
        onRequestStart();
    }

    @Override
    public void onNext(@NotNull Response<T>tResponse) {
        String url = tResponse.raw().request().url().toString();
        if (tResponse.body()==null){
            ResponseBody errorBody = tResponse.errorBody();
            int code = tResponse.code();
            if(code!=404){
                if(code==500){
                    String result = null;
                    try {
                        result = errorBody.string();
                        if(isJson(result)){
                            JSONObject jsonObject = JSONObject.parseObject(result);
                            if(jsonObject!=null){
                                String msg = jsonObject.getString("message");
                                msg = StringMyUtil.isEmptyString(msg)?"":msg;
                                onFail(msg);
                                Toast toast = Toast.makeText(AppContextUtils.getContext(), null, LENGTH_SHORT);
                                toast.setText(msg);
                                toast.setGravity(Gravity.CENTER,0,0);
                                if(StringMyUtil.isNotEmpty(msg)){
                                    toast.show();
                                }
                            }
                        }else {
                            onFail(RESPONSE_RETURN_ERROR);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }else if(code==401){
                    EventBus.getDefault().post(new SingleLoginEvent(true,1));
                }else if (code==400){
                    onFail(REQUEST_400);
                }else if(code == 502){
                    onFail(REQUEST_502);
                }else {
                    try {
                        String result = errorBody.string();
                        if(isJson(result)){
                            JSONObject jsonObject = JSONObject.parseObject(result);
                            if(jsonObject!=null){
                                String msg = jsonObject.getString("msg");
                                msg = StringMyUtil.isEmptyString(msg)?"":msg;
                                onFail(msg);
                                Toast toast = Toast.makeText(AppContextUtils.getContext(), null, LENGTH_SHORT);
                                toast.setText(msg.equals("用户已失效")?"您已被拉黑":msg);
                                toast.setGravity(Gravity.CENTER,0,0);
                                if(msg.equalsIgnoreCase("timeout")){
                                    return;
                                }
                                if(StringMyUtil.isNotEmpty(msg)){
                                    toast.show();
                                }
                            }
                        }else {
                            onFail(REQUEST_UN_KNOW);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }else {
                onFail(REQUEST_404);
            }
        }else {
            try {
                String result = tResponse.body().string();
                okhttp3.Response response = tResponse.raw();
                if(!isJson(result)){
                    onFail("数据解析错误");
                    return;
                }
                JSONObject json = JSONObject.parseObject(result);
                if(json.containsKey("statusCode") || json.containsKey("message")){
                    String code = json.getString("statusCode");
                    String msg = json.getString("message");
                    if(code.equals("200")){
                        String data = json.getString("data");
                        onSuccess(data);//请求成功
                    }else {
                        if(code.equals("202")){
                            //token 无效
                            EventBus.getDefault().post(new SingleLoginEvent(true,1));
                        }else {
                            onFail(TextUtils.isEmpty(msg)?"":msg);
                        }
                    }
                }else {
                    onFail("数据格式不正确");
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    @Override
    public void onError(Throwable e) {
        String errorMsg = RxExceptionUtils.exceptionHandler(e);
        onFail(errorMsg);
        onRequestEnd();
    }

    @Override
    public void onComplete()      {
        onRequestEnd();
    }

    public abstract void onSuccess(String result);

    abstract public void onFail(String msg);

    /**
     * 网络请求开始
     */
    protected void onRequestStart() {
    }

    /**
     * 网络请求结束
     */
    protected void onRequestEnd() {
    }
    public static boolean isJson(String content) {
        if(TextUtils.isEmpty(content)){
            return false;
        }
        boolean isJsonObject = true;

        try {
            JSONObject.parseObject(content);
        } catch (Exception e) {
            isJsonObject = false;
        }

        if(!isJsonObject){ //不是json格式
            return false;
        }
        return true;
    }
}
