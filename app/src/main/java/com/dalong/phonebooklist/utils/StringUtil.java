package com.dalong.phonebooklist.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhouweilong on 16/4/13.
 */
public class StringUtil {

    /**
     * 验证是否是手机号码
     * @param phoneNumber
     * @return
     */
    public static boolean isPhoneNumber(String phoneNumber) {
        if(phoneNumber==null)return false;
        if(TextUtils.isEmpty(phoneNumber))return false;
        Pattern pattern = Pattern.compile("^(86|\\+86)?[1][3,4,5,8][0-9]{9}$");
        Matcher m = pattern.matcher(phoneNumber);
        return m.matches();
    }


    /**
     * 验证是否是固定电话号码
     * @param phoneNumber
     * @return
     */
    public static boolean checkPhone(String phoneNumber) {
        if(TextUtils.isEmpty(phoneNumber))return false;
        String regex = "(\\+\\d+)?(\\d{3,4}\\-?)?\\d{7,8}$";
        return Pattern.matches(regex, phoneNumber);
    }
}
