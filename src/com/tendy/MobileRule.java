package com.tendy;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MobileRule {

    public static List<ItemRule> listRule = new LinkedList<>();
    static {
        listRule.add(new ItemRule("(\\d)\\1{5}", "6A精品靓号", "AAAAAA"));
        listRule.add(new ItemRule("(?:(?:0(?=1)|1(?=2)|2(?=3)|3(?=4)|4(?=5)|5(?=6)|6(?=7)|7(?=8)|8(?=9)){5}|(?:9(?=8)|8(?=7)|7(?=6)|6(?=5)|5(?=4)|4(?=3)|3(?=2)|2(?=1)|1(?=0)){5})\\d", "6顺精品靓号", "ABCDEF"));
        listRule.add(new ItemRule("(\\d)\\1{4}", "5A精品靓号", "AAAAA"));
        listRule.add(new ItemRule("(?:(?:0(?=1)|1(?=2)|2(?=3)|3(?=4)|4(?=5)|5(?=6)|6(?=7)|7(?=8)|8(?=9)){4}|(?:9(?=8)|8(?=7)|7(?=6)|6(?=5)|5(?=4)|4(?=3)|3(?=2)|2(?=1)|1(?=0)){4})\\d", "5顺精品靓号", "ABCDE"));
        listRule.add(new ItemRule("(\\d)\\1{2}(\\d)(\\d)\\3{2}", "AAABCCC靓号", "AAABCCC"));
        listRule.add(new ItemRule("(\\d\\d\\d)\\1{1}", "ABC重复靓号", "ABCABC"));
        listRule.add(new ItemRule("(\\d)\\1{1}(?!\\1)(\\d)\\2{1}(?!\\2)(\\d)\\3{1}", "ABC对子靓号", "AABBCC"));
        listRule.add(new ItemRule("(\\d)(\\d)(\\d)\\3\\2\\1", "3位回环靓号", "ABCCBA"));
        listRule.add(new ItemRule("(([\\d])\\1{0,}([\\d])\\2{0,})\\1{2,}", "ABABAB精品靓号", "ABABAB"));
        listRule.add(new ItemRule("([\\d])[0-9]\\1[0-9]\\1[0-9]", "ABACAD精品靓号", "ABACAD"));
        listRule.add(new ItemRule("(\\d)\\1{3}", "4A精品靓号", "AAAA"));
        listRule.add(new ItemRule("(?:(?:0(?=1)|1(?=2)|2(?=3)|3(?=4)|4(?=5)|5(?=6)|6(?=7)|7(?=8)|8(?=9)){3}|(?:9(?=8)|8(?=7)|7(?=6)|6(?=5)|5(?=4)|4(?=3)|3(?=2)|2(?=1)|1(?=0)){3})\\d", "4顺精品靓号", "ABCD"));
        listRule.add(new ItemRule("([\\d])\\1{1,}([\\d])\\2{1,}", "AABB精品靓号", "AABB"));
        listRule.add(new ItemRule("(([\\d])\\1{0,}([\\d])\\2{0,})\\1{1,}", "ABAB精品靓号", "ABAB"));
        listRule.add(new ItemRule("(\\d)\\1{2}", "3A精品靓号", "AAA"));
        listRule.add(new ItemRule("(?:(?:0(?=1)|1(?=2)|2(?=3)|3(?=4)|4(?=5)|5(?=6)|6(?=7)|7(?=8)|8(?=9)){2}|(?:9(?=8)|8(?=7)|7(?=6)|6(?=5)|5(?=4)|4(?=3)|3(?=2)|2(?=1)|1(?=0)){2})\\d", "3顺精品靓号", "ABC"));
        listRule.add(new ItemRule("([\\d])[0-9]\\1[0-9]", "ABAC精品靓号", "ABAC"));
    }

    public static ItemRule checkPhone(String phone){
        for(int i = 0; i<listRule.size(); i++){
            ItemRule item = listRule.get(i);
            //匹配6位顺增或顺降
            Matcher matcher = Pattern.compile(item.getPattern()).matcher(phone);
            if(matcher.find()){
                return new ItemRule(item.getPattern(), item.getRemark(), item.getTag(), phone, matcher.group());
            }
        }
        return null;
    }

    public static boolean isMatch(String str, String key){
        if(str == null || "".equals(str) || key == null || "".equals(key)){
            return false;
        }
        boolean positionHead = false;
        boolean positionTail = false;
        if(key.indexOf("%") == 0){
            positionHead = true;
            key = key.substring(1);
        }
        if(key.indexOf("%") > 0){
            positionTail = true;
        }
        key = key.replace("%", "");
        if(str.length() < key.length()){
            return false;
        }
        if(!positionHead && !positionTail){
            if(str.equals(key)){
                return true;
            }
        }
        if(!positionHead){
            if(str.indexOf(key) != 0){
                return false;
            }
        }
        if(!positionTail){
            if(!str.substring(str.length()-key.length(), str.length()).equals(key)){
                return false;
            }
        }
        if(str.contains(key)){
            return true;
        }else{
            return false;
        }
    }
}