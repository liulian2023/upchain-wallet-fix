package pro.upchain.wallet.utils;

import static android.content.Context.CLIPBOARD_SERVICE;
import static android.content.Context.INPUT_METHOD_SERVICE;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.LocaleList;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pro.upchain.wallet.C;
import pro.upchain.wallet.MyApplication;
import pro.upchain.wallet.R;
import pro.upchain.wallet.RxHttp.net.api.HttpApiUtils;
import pro.upchain.wallet.repository.RepositoryFactory;


public class Utils {
    private static String emptyAddress = "0x0000000000000000000000000000000000000000";

    public static int dp2px(Context context, int dp) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics()
        );
    }
    /**
     * ????????????????????????????????????
     * **/
    public static boolean isContainsLetter(String input){
        if(!StringUtils.isEmpty(input)){
            Matcher matcher = Pattern.compile(".*[a-zA-Z]+.*").matcher(input);
            return matcher.matches();
        }else{
            return false;
        }
    }
    public static String formatUrl(String url) {
        if (URLUtil.isHttpsUrl(url) || URLUtil.isHttpUrl(url)) {
            return url;
        } else {
            if (isValidUrl(url)) {
                return C.HTTP_PREFIX + url;
            } else {
                return C.GOOGLE_SEARCH_PREFIX + url;
            }
        }
    }

    public static boolean isValidUrl(String url) {
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url.toLowerCase());
        return m.matches();
    }
    public static void copyStr(String label, String content){
        ClipboardManager clipboardManager= (ClipboardManager) MyApplication.getsInstance().getSystemService(CLIPBOARD_SERVICE);//?????????clipboardManager??????
        ClipData bankCardNumData=  ClipData.newPlainText(label, content);//??????????????????????????????  newPlainText
        clipboardManager.setPrimaryClip(bankCardNumData);
       ToastUtils.showToast(R.string.copy_finish);
    }
    /**
     * ??????????????????
     * @param activity activity??????
     * @param bgcolor ?????????(0f-1f)?????????,????????????
     */
    public static void darkenBackground(Activity activity, Float bgcolor) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgcolor;
        if(bgcolor==1f){
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }else {

            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        activity.getWindow().setAttributes(lp);
    }
    public static void hideSoftKeyBoard(Activity activity) {
        if(activity!=null&&!activity.isFinishing()){
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getContentView(activity).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            }
        }

    public static View getContentView(Activity context) {
        return ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);
    }

    //????????????
    public static void start2Share(Context context, String url) {
        Intent share_intent = new Intent();
        share_intent.setAction(Intent.ACTION_SEND);//??????????????????
        share_intent.setType("text/plain");//???????????????????????????
        //     share_intent.putExtra(Intent.EXTRA_SUBJECT, title);//????????????????????????
        share_intent.putExtra(Intent.EXTRA_TEXT, url);//????????????????????????
        //???????????????Dialog
        share_intent = Intent.createChooser(share_intent, context.getString(R.string.choose_share_app));
        context.startActivity(share_intent);
    }
    public static void openBrowser(Context context, String url) {
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url.trim()));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_browser)));
        }else {
            ToastUtils.showToast(context.getString(R.string.no_browser));
        }

    }
    /**
     * ???????????????????????????????????????N????????????
     *
     * @return ?????????????????????
     */
    public static String randomPsw(int n) {
        char arr[] = new char[n];
        int i = 0;
        while (i < n) {
            char ch = (char) (int) (Math.random() * 124);
            if (ch >= 'A' && ch <= 'Z' || ch >= 'a' && ch <= 'z' || ch >= '0' && ch <= '9') {
                arr[i++] = ch;
            }
        }
        //????????????????????????
        return new String(arr);
    }

    public static void setAppLanguage(Context context, Locale locale) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
//Android 7.0???????????????
        if (Build.VERSION.SDK_INT >= 24) {
            configuration.setLocale(locale);
            configuration.setLocales(new LocaleList(locale));
            context.createConfigurationContext(configuration);
            resources.updateConfiguration(configuration, metrics);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//Android 4.1 ????????????
            configuration.setLocale(locale);
            resources.updateConfiguration(configuration, metrics);
        } else {
            configuration.locale = locale;
            resources.updateConfiguration(configuration, metrics);
        }
    }
    public static String getCurrentChain(){
        return SharePreferencesUtil.getString(CommonStr.CHAIN_TYPE,"2");
    }
    public static String getETHOrBsc2USDTRate(){
        if(getCurrentChain().equals("2")){
            return SharePreferencesUtil.getString(CommonStr.ETH2USDTRate,"");
        }else {
            return SharePreferencesUtil.getString(CommonStr.BSC2USDTRate,"");
        }
    }
    public static String getCurrentSymbol(){
        String chainType = SharePreferencesUtil.getString(CommonStr.CHAIN_TYPE, "2");
        if(chainType.equals("2")){
            return C.ETH_SYMBOL;
        }else if(chainType.equals("3")){
            return C.BSC_SYMBOL;
        }
        return C.ETH_SYMBOL;
    }
}
