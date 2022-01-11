package pro.upchain.wallet.utils;

import java.math.BigDecimal;


public class StringUtils {
	
	/**
	 * 转String类型
	 * @param obj
	 * @return
	 */
	public static String getStringValue(Object obj){
		String ss="";
		if(obj!=null&&obj!="null"&&!"null".equals(obj)){
			ss = obj.toString();
		}
		return ss;
	}
	
	/**
	 * 转Int类型
	 * @param obj
	 * @return
	 */
	public static int getIntValue(Object obj){
		int ss = 0;
		if(obj!=null&&obj!="null"){
			ss = new BigDecimal(obj.toString()).intValue();
		}
		return ss;
	}
	
	/**
	 * 转BigDecimal类型
	 * @param obj
	 * @return
	 */
	public static BigDecimal getBigDecimalValue(Object obj){
		BigDecimal ss = BigDecimal.ZERO;
		if(obj!=null&&obj!="null"){
			ss = new BigDecimal(obj.toString());
		}
		return ss;
	}
	
	/**
	 * 转BigDecimal类型
	 * @param obj
	 * @return
	 */
	public static double getDoubleValue(Object obj){
		double ss = 0;
		if(obj!=null&&obj!="null"){
			ss = Double.valueOf(obj.toString());
		}
		return ss;
	}
	
	  /**
     * 判断字符串是否为空
     *
     * @param str
     * @return 空返回 true,非空返回false
     */
    public static boolean isEmpty(Object str) {
        if (str == null) {
            return true;
        } else if (str.toString().trim().length() == 0) {
            return true;
        } else if (str.toString().trim().equalsIgnoreCase("null")) {
            return true;
        } else if (str.toString().trim().equalsIgnoreCase("(null)")) {//ios传上来的字符串(null)
            return true;
        }else if (str.toString().trim().equalsIgnoreCase("undefined")) {
            return true;
        }
        return false;
    }
    public static boolean isNotEmpty(String email) {
        return !isEmpty(email);
    }
    public static boolean isNotEmpty(String... emails) {
        if(emails==null){
            return false;
        }
        for (String email:emails) {
            boolean emptyString = isEmpty(email);
            if(emptyString){
                //有空
                return false;
            }
        }
       return true;
    }
	public static String initEmptyStr(String content) {
		if(isEmpty(content)){
			return "";
		}
		return content;
	}
	public static String initEmptyStr(String emails,String defaultStr) {
		if(isEmpty(emails)){
			return defaultStr;
		}
		return emails;
	}
}
