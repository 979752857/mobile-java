package com.tendy;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: tendy
 * @Description:
 * @Date: 2018/10/31
 */
public class HenanPhone {

    private static String server = "http://wap.ha.10086.cn/pay/card-sale!toforward.action?url=card&mealid=&mastercard=&plancode=&iccid=";
    private static String html = "<li class=\"tc bgc-fafafa\" data-plancode=\"XWAPCARDNCS\" data-region=\"R\" data-card=\"13683914884\" data-page=\"9\" style=\"display:none;\" >\n" +
            "                            <a href=\"javascript:void(0);\">\n" +
            "                                <p>13683914884</p>\n" +
            "                            </a>\n" +
            "                        </li>";
    private static int updateNum = 0;
    private static int successNum = 0;
    private static int failNum = 0;

    public static void main(String[] args) {
        getPhoneAndHref(0);
    }

    public static void getPhoneAndHref(int row){
        Map<String, String> param = new HashMap<>();
        param.put("queryRegion", "R");
        param.put("sumpage", "0");
        //134 35 36 37 38 39 50 51 52 57 58 59 82 83 84 87
//        param.put("section", "138");
        String result = HttpConnectionUtil.requestMethod(HttpConnectionUtil.HTTP_POST, server, HttpConnectionUtil.convertStringParamter(param));
        result = result.trim();
        System.out.println(result);
        processHtmlAndHref(result, row);
    }

    public static void processHtmlAndHref(String result, int row){
        List<Map<String, String>> list = new ArrayList<>();
        while(true){
            Map<String, String> map = new HashMap<>();
            result = result.trim();
            int index = result.indexOf("class=\"tc bgc-fafafa\"");
            if(index < 0){
                break;
            }
            result = result.substring(index + "class=\"tc bgc-fafafa\"".length());
            int indexPhoneBefore = result.indexOf("<p>");
            int indexPhoneAfter = result.indexOf("</p>");
            int indexHrefBefore = result.indexOf("data-plancode=\"");
            int indexHrefAfter = result.indexOf("\" data-region");
            String item = result.substring(indexPhoneBefore, indexPhoneAfter);
            String plancode = result.substring(indexHrefBefore + "data-plancode=\"".length(), indexHrefAfter);
            item = item.trim();
            String phone = "";
            if(item != null && !"".equals(item)) {
                for (int i = 0; i < item.length(); i++) {
                    if (item.charAt(i) >= 48 && item.charAt(i) <= 57) {
                        phone += item.charAt(i);
                    }
                }
            }
            map.put("phone", phone);
            map.put("plancode", plancode);
            list.add(map);
            ItemRule itemRule = MobileRule.checkPhone(phone);
            if(itemRule != null){
                System.out.println("*************phone:"+phone+"*************");
            }
            successNum++;
            System.out.println("phone:"+phone+"   plancode:"+plancode+"   successNum:"+successNum);
        }

    }

}