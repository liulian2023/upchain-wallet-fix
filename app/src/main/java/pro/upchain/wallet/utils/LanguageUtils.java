package pro.upchain.wallet.utils;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
public class LanguageUtils {
    private static final String TAG = "LanguageUtils";

    public static void initLanguage(Context mContext) {

        String languageName = readLanguageName(mContext);
        if (TextUtils.isEmpty(languageName)){
            return;
        }
        Resources resources = mContext.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = new Locale(languageName);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());//更新配置

    }


    private static String readLanguageName(Context mContext) {
        StringBuilder stringBuilder = new StringBuilder();

        AssetManager am=mContext.getAssets();
        try {
            InputStream is=am.open("language.txt");

            String code=getCode(is);
            is=am.open("language.txt");
            BufferedReader br=new BufferedReader(new InputStreamReader(is,code));

            String line=br.readLine();
            int i=0;
            while(line!=null){
                stringBuilder.append(line+"\n");
                line=br.readLine();
                i++;
                if(i==200){
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString().trim();
    }

    public static String getCode(InputStream is){
        try {
            BufferedInputStream bin = new BufferedInputStream(is);
            int p;

            p = (bin.read() << 8) + bin.read();

            String code = null;

            switch (p) {
                case 0xefbb:
                    code = "UTF-8";
                    break;
                case 0xfffe:
                    code = "Unicode";
                    break;
                case 0xfeff:
                    code = "UTF-16BE";
                    break;
                default:
                    code = "GBK";
            }
            is.close();
            return code;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前支持语言，不支持默认英文  TODO  新增华为
     * @return
     */
    public static String getCurrentLanguage() {
        try {
            /*Resources resources = context.getResources();
            Configuration configuration = resources.getConfiguration();*/
            Locale locale = getSysPreferredLocale();
            Log.e(TAG, "getCurrentLanguage:locale  ----  >  "+locale );
            String language = locale.getLanguage();
            Log.e(TAG, "getCurrentLanguage:  ----  >  "+locale.getCountry() );
            Log.e(TAG, "getCurrentLanguage:  获取到语言 "+language  );
            if(language.equals("en")){
                return "en_US";
            }else if(language.equals("zh")){
                if(locale.getCountry().equals("TW")||locale.getCountry().equals("HK")||locale.toString().contains("#Hant")){
                    return "zh_TW";
                }else{
                    return "zh_CN";
                }

            }else if(language.equals("ko")){
                return "ko_KR";

            }else{
                return "en_US";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "en_US";
        }


    }
    /**
     * 获取系统首选语言
     *
     * 注意：该方法获取的是用户实际设置的不经API调整的系统首选语言
     *
     * @return
     */
    public static Locale getSysPreferredLocale() {
        Locale locale;
        //7.0以下直接获取系统默认语言
        if (Build.VERSION.SDK_INT < 24) {
            // 等同于context.getResources().getConfiguration().locale;
            locale = Locale.getDefault();
            // 7.0以上获取系统首选语言
        } else {
            /*
             * 以下两种方法等价，都是获取经API调整过的系统语言列表（可能与用户实际设置的不同）
             * 1.context.getResources().getConfiguration().getLocales()
             * 2.LocaleList.getAdjustedDefault()
             */
            // 获取用户实际设置的语言列表
            locale = LocaleList.getDefault().get(0);
        }
        return locale;
    }

    public static void changeAppLanguage(Context context) {
        String sta = Store.getLanguageLocal(context);
        if(sta != null && !"".equals(sta)){
            // 本地语言设置
            Locale myLocale=null;
            if(sta.equals("zh_CN")){
                myLocale = new Locale(sta,Locale.CHINESE.getCountry());
            }else if(sta.equals("zh_TW")){
                myLocale = new Locale("TW",Locale.TRADITIONAL_CHINESE.getCountry());
            }else  if(sta.equals("en")||sta.equals("en_US")){
                myLocale = new Locale( "en",Locale.ENGLISH.getCountry());
            }
            Resources res = context.getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
        }
    }
}
