package com.dalong.phonebooklist.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.dalong.phonebooklist.entity.Contact;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by zhouweilong on 16/4/13.
 */
public class PhoneBookUtls {

    static final int GB_SP_DIFF = 160;
    // 存放国标一级汉字不同读音的起始区位码
    static final int[] secPosValueList = {1601, 1637, 1833, 2078, 2274, 2302,
            2433, 2594, 2787, 3106, 3212, 3472, 3635, 3722, 3730, 3858, 4027,
            4086, 4390, 4558, 4684, 4925, 5249, 5600};
    // 存放国标一级汉字不同读音的起始区位码对应读音
    static final char[] firstLetter = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'w', 'x',
            'y', 'z'};

    /**
     * 获取所有通讯录数据
     */
    public static  List<Contact> getAllCallRecords(Context mContext) {
        List<Contact> list=new ArrayList<Contact>();//通讯录集合
        Cursor c = mContext.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME
                        + " COLLATE LOCALIZED ASC");
        try {
            if(c==null || c.getCount() == 0){
                return list;
            }

            if (c!=null&&c.moveToFirst()) {
                do {
                    // 获得联系人的ID号
                    String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                    // 获得联系人姓名
                    String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    // 查看该联系人有多少个电话号码。如果没有这返回值为0
                    int phoneCount = c.getInt(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    String number="";
                    if (phoneCount > 0) {
                        // 获得联系人的电话号码
                        Cursor phones = mContext.getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                        + " = " + contactId, null, null);
                        if (phones.moveToFirst()) {
                            do{
                                String mnumber = phones
                                        .getString(phones
                                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ","");
                                if(TextUtils.isEmpty(mnumber))
                                    continue;
                                if(StringUtil.isPhoneNumber(mnumber)||StringUtil.checkPhone(mnumber)){
                                    if(number.contains(mnumber))
                                        continue;
                                    number=mnumber+","+number;
                                }
                            } while (phones.moveToNext());
                        }
                        phones.close();
                    }
                    Contact contact=new Contact();
                    contact.setBookMarkName(name);
                    if(!TextUtils.isEmpty(number))
                        number=number.trim().substring(0, number.trim().length()-1);
                    else
                        continue;//如果没有号码就不上传这条数据
                    String[] num=number.split(",");
                    contact.setBookPhoneNo(num);
                    contact.setNameSortString(getSpells(contact.getBookMarkName()));
                    char aa = contact.getNameSortString().charAt(0);
                    contact.setNameSort(String.valueOf(Character.toUpperCase(aa)));
                    list.add(contact);
                } while (c.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        c.close();
        Collections.sort(list, new SortByName());
        return fenZu(list);
    }


    public static   List<Contact> fenZu(List<Contact> list){
        List<Contact> mContacts=new ArrayList<>();
        for (int i=0;i<list.size();i++){
            if(i==0){
                Contact mContact=new Contact();
                mContact.setNameSort(list.get(0).getNameSort());
                mContact.setTitle(true);
                mContacts.add(mContact);
            }else{
                Contact oneContact=list.get(i);
                Contact twoContact=list.get(i-1);
                if(oneContact.getNameSort().equals(twoContact.getNameSort())){
                    mContacts.add(list.get(i));
                }else{
                    Contact mContact=new Contact();
                    mContact.setNameSort(list.get(i).getNameSort());
                    mContact.setTitle(true);
                    mContacts.add(mContact);
                    mContacts.add(list.get(i));
                }
            }

        }
        return  mContacts;
    }










    static class SortByName implements Comparator {
        public int compare(Object o1, Object o2) {
            Contact s1 = (Contact) o1;
            Contact s2 = (Contact) o2;
            return s1.getNameSortString().compareTo(s2.getNameSortString());
        }
    }

    public static String getSpells(String characters) {

        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < characters.length(); i++) {

            char ch = characters.charAt(i);
            if ((ch >> 7) == 0) {
                // 判断是否为汉字，如果左移7为为0就不是汉字，否则是汉字
            } else {
                /*char spell = getFirstLetter(ch);
                buffer.append(String.valueOf(spell));*/
                String spell = getFirstSpell(characters.substring(i, i + 1));
                buffer.append(String.valueOf(spell));
            }
        }
        return buffer.toString();
    }

    public static String getFirstSpell(String chinese) {
        StringBuffer pybf = new StringBuffer();
        char[] arr = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (char curchar : arr) {
            if (curchar > 128) {
                try {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(curchar, defaultFormat);
                    if (temp != null) {
                        pybf.append(temp[0].charAt(0));
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pybf.append(curchar);
            }
        }
        return pybf.toString().replaceAll("\\W", "").trim();
    }

    /**
     * 获取一个汉字的拼音首字母。 GB码两个字节分别减去160，转换成10进制码组合就可以得到区位码
     * 例如汉字“你”的GB码是0xC4/0xE3，分别减去0xA0（160）就是0x24/0x43
     * 0x24转成10进制就是36，0x43是67，那么它的区位码就是3667，在对照表中读音为‘n’
     */
    static char convert(byte[] bytes) {
        char result = '-';
        int secPosValue = 0;
        int i;
        for (i = 0; i < bytes.length; i++) {
            bytes[i] -= GB_SP_DIFF;
        }
        secPosValue = bytes[0] * 100 + bytes[1];
        for (i = 0; i < 23; i++) {
            if (secPosValue >= secPosValueList[i]
                    && secPosValue < secPosValueList[i + 1]) {
                result = firstLetter[i];
                break;
            }
        }
        return result;
    }
}
