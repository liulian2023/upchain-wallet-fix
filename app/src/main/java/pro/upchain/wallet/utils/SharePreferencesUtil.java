package pro.upchain.wallet.utils;


import android.content.Context;
import android.content.SharedPreferences;

import pro.upchain.wallet.MyApplication;
import pro.upchain.wallet.R;

public class SharePreferencesUtil {

    private static SharedPreferences sharedPreferences;

    /**写入Boolean变量至sharedPreferences中

     * @param key   存储节点名称
     * @param value 存储节点的值
     */
    public static void putBoolean( String key, Boolean value){
        //(存储节点文件名称，读写方式)
        if (sharedPreferences == null){
            sharedPreferences = MyApplication.getsInstance().getSharedPreferences(MyApplication.getsInstance().getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putBoolean(key,value).commit();

    }
    /**读取boolean标识从sharedPreferences中

     * @param key   存储节点名称
     * @param value 没有此节点的默认值
     * @return  默认值或者此节点读取到的结果
     */
    public static boolean getBoolean( String key, Boolean value){
        //(存储节点文件名称,读写方式)
        if (sharedPreferences == null){
            sharedPreferences = MyApplication.getsInstance().getSharedPreferences(MyApplication.getsInstance().getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        }
        return sharedPreferences.getBoolean(key,value);
    }

    /**写入String变量至sharedPreferences中
     
     * @param key	存储节点名称
     * @param value	存储节点的值String
     */
    public  static void putString(String key,String value){
        //存储节点文件的名称，读写方式
        if(sharedPreferences == null){
            sharedPreferences = MyApplication.getsInstance().getSharedPreferences(MyApplication.getsInstance().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putString(key, value).commit();
    }

    /**写入float变量至sharedPreferences中
     
     * @param key	存储节点名称
     * @param value	存储节点的值float
     */
    public  static void putFloat(String key,float value){
        //存储节点文件的名称，读写方式
        if(sharedPreferences == null){
            sharedPreferences = MyApplication.getsInstance().getSharedPreferences(MyApplication.getsInstance().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putFloat(key, value).commit();
    }

    /**读取String标识从sharedPreferences中
     
     * @param key	存储节点名称
     * @param defValue	没有此节点的默认值
     * @return	返回默认值或者此节点读取到的结果
     */
    public  static String getString(String key,String defValue){
        //存储节点文件的名称，读写方式
        if(sharedPreferences == null){
            sharedPreferences = MyApplication.getsInstance().getSharedPreferences(MyApplication.getsInstance().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        }
        return sharedPreferences.getString(key, defValue);
    }
    public  static String getString(Context context,String key,String defValue){
        //存储节点文件的名称，读写方式
        if(sharedPreferences == null){
            sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        }
        return sharedPreferences.getString(key, defValue);
    }
    /**读取float标识从sharedPreferences中
     
     * @param key	存储节点名称
     * @param defValue	没有此节点的默认值
     * @return	返回默认值或者此节点读取到的结果
     */
    public  static float getFloat(String key,float defValue){
        //存储节点文件的名称，读写方式
        if(sharedPreferences == null){
            sharedPreferences = MyApplication.getsInstance().getSharedPreferences(MyApplication.getsInstance().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        }
        return sharedPreferences.getFloat(key, defValue);
    }

    /**写入int变量至sharedPreferences中
     
     * @param key	存储节点名称
     * @param value	存储节点的值String
     */
    public  static void putInt(String key,int value){
        //存储节点文件的名称，读写方式
        if(sharedPreferences == null){
            sharedPreferences = MyApplication.getsInstance().getSharedPreferences(MyApplication.getsInstance().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putInt(key, value).commit();
    }

    /**读取int标识从sharedPreferences中
     
     * @param key	存储节点名称
     * @param defValue	没有此节点的默认值
     * @return	返回默认值或者此节点读取到的结果
     */
    public  static int getInt(String key,int defValue){
        //存储节点文件的名称，读写方式
        if(sharedPreferences == null){
            sharedPreferences = MyApplication.getsInstance().getSharedPreferences(MyApplication.getsInstance().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        }
        return sharedPreferences.getInt(key, defValue);
    }

    /**写入Long变量至sharedPreferences中
     
     * @param key	存储节点名称
     * @param value	存储节点的值String
     */
    public  static void putLong(String key,Long value){
        //存储节点文件的名称，读写方式
        if(sharedPreferences == null){
            sharedPreferences = MyApplication.getsInstance().getSharedPreferences(MyApplication.getsInstance().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putLong(key, value).commit();
    }
    /**读取Long标识从sharedPreferences中
     
     * @param key	存储节点名称
     * @param defValue	没有此节点的默认值
     * @return	返回默认值或者此节点读取到的结果
     */
    public  static Long getLong(String key,Long defValue){
        //存储节点文件的名称，读写方式
        if(sharedPreferences == null){
            sharedPreferences = MyApplication.getsInstance().getSharedPreferences(MyApplication.getsInstance().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        }
        return sharedPreferences.getLong(key, defValue);
    }
    /**
     * 从sharedPreferences中移除指定节点
     
     * @param key	需要移除节点的名称
     */
    public static void remove(String key){
        //存储节点文件的名称，读写方式
        if(sharedPreferences == null){
            sharedPreferences = MyApplication.getsInstance().getSharedPreferences(MyApplication.getsInstance().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().remove(key).commit();
    }
   public  static  void  clearAll(){
    if(sharedPreferences == null){
        sharedPreferences = MyApplication.getsInstance().getSharedPreferences(MyApplication.getsInstance().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }
    sharedPreferences.edit().clear().commit();
}
}
