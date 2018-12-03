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
public class HenanPhone extends Phone{

    private static String server = "http://wap.ha.10086.cn/pay/card-sale!toforward.action?url=card&mealid=&mastercard=&plancode=&iccid=";

    public HenanPhone(Integer pageSize, Integer pageStart, Integer pageEnd, Integer cityId, Integer businessId) {
        super(pageSize, pageStart, pageEnd, cityId, businessId);
    }

    public void getPhoneAndHref(int row) throws InterruptedException {
        Map<String, String> param = new HashMap<>();
        param.put("queryRegion", "R");
        param.put("sumpage", "0");
        //134 35 36 37 38 39 50 51 52 57 58 59 82 83 84 87
//        param.put("section", "138");
        String result = HttpConnectionUtil.requestMethod(HttpConnectionUtil.HTTP_POST, server, HttpConnectionUtil.convertStringParamter(param), null);
        result = result.trim();
        System.out.println(result);
        processHtmlAndHref(result, row);
    }

    public void processHtmlAndHref(String result, int row) throws InterruptedException {
        List<UserAccountPhone> list = new ArrayList<>();
        Connection connection = MySqlUtil.getConnect();
        while(true){
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
            ItemRule itemRule = MobileRule.checkPhone(phone);
            String tag = "";
            String remark = "";
            if(itemRule != null){
                tag = itemRule.getTag();
                Map<String, String> map = new HashMap<>();
                map.put("tag", itemRule.getRemark());
                remark = JsonMapper.toJson(map);
            }
            UserAccountPhone userAccountPhone = new UserAccountPhone();
            userAccountPhone.setPhone(phone);
            userAccountPhone.setBusinessId(getBusinessId());
            userAccountPhone.setUrl(plancode);
            userAccountPhone.setStatus("private");
            userAccountPhone.setCityId(getCityId());
            userAccountPhone.setTag(tag);
            userAccountPhone.setRemark(remark);
            list.add(userAccountPhone);
            System.out.println(phone + "   plancode:" + plancode + "    row:"+row+"   放入list");
        }
        if(list != null && list.size() > 0){
            int num = MySqlUtil.saveDataBatch(connection, list);
            if(num > 1){
                setUpdateNum(getUpdateNum()+1);
                System.out.println("list:"+list.size()+"   处理成功    num:"+num);
            }else{
                setFailNum(getFailNum()+1);
                System.out.println("list:"+list.size()+"   处理失败    num:"+num);
            }

            //发送通知
            List<String> phones = new ArrayList<>();
            for(UserAccountPhone phone : list){
                phones.add(phone.getPhone());
            }
            SendAlertUtil.checkAndSendAlert(getCityId(), phones);
        }
        MySqlUtil.closeConnection(connection);
    }

    @Override
    public void execute(Integer pageStart) throws Exception {
        getPhoneAndHref(0);
    }
}