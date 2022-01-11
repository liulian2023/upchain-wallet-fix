package pro.upchain.wallet.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;
import java.util.Locale;
public class LocalManageUtil {
    /**
     * 获取选择的语言设置
     * @param context
     * @return
     */
    private static Locale getSetLanguageLocale(Context context) {
        switch (SharePreferencesUtil.getString(CommonStr.LOCAL_LANGUAGE,"en")) {
            case "en"://英语
                return Locale.ENGLISH;
            case "zh"://汉语
                return Locale.CHINESE;
            default://默认 汉语
                return getSystemLocale();
        }
    }

    /**
     * 设置 本地语言
     *
     * @param context
     * @param language
     */
    public static void saveSelectLanguage(Context context, String language) {
        SharePreferencesUtil.putString(CommonStr.LOCAL_LANGUAGE,language);
        setApplicationLanguage(context);
    }


    /**
     * 初始化语言 方法
     *
     * @param context
     */
    public static Context setLocal(Context context) {
        return setApplicationLanguage(context);
    }

    /**
     * 设置语言类型
     */
    public static Context setApplicationLanguage(Context context) {

        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        Locale locale = getSetLanguageLocale(context);//获取sp里面保存的语言

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList localeList = new LocaleList(locale);
            LocaleList.setDefault(localeList);
            config.setLocales(localeList);
            Locale.setDefault(locale);
            return context.createConfigurationContext(config);
        } else {
            config.locale = locale;
        }
        resources.updateConfiguration(config, dm);
        return context;
    }

    /**
     * 获取App的locale
     *
     * @return Locale对象
     */
    public static Locale getAppLocale() {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }
        return locale;
    }

    /**
     * 获取系统local
     *
     * @return
     */
    public static Locale getSystemLocale() {
        Locale locale = Resources.getSystem().getConfiguration().locale;
        return locale;
    }

    /**
     * 获取本地保存的语言
     *
     * @param context
     * @return
     */
    public static String getLocalSaveLanguage(Context context) {
        Locale locale = getSetLanguageLocale(context);
        String language = locale.getLanguage();
        if (language.equals("zh")) {
            language = "zh-CN";
        } else if (language.equals("en")) {
            language = "en";
        } else if (language.equals("ja")) {
            language = "ja";
        }
        return language;
    }
}
