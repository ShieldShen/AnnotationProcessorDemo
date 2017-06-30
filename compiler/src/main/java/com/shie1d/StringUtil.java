package com.shie1d;

/**
 * Created by shenli on 2017/6/30.
 */
class StringUtil {

    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

    public static boolean equals(String str1, String str2) {
        return str1 == null && str2 == null || !(str1 == null || str2 == null) && str1.equals(str2);
    }
}
