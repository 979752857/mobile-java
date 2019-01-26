package com.tendy;

import com.tendy.model.ItemRule;

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

    public HenanPhone(Integer pageSize, Integer pageStart, Integer pageEnd, Integer cityId, Integer businessId, String url, String urlParam) {
        super(pageSize, pageStart, pageEnd, cityId, businessId, url, urlParam);
    }

    public void getPhoneAndHref(int row) throws InterruptedException {
        Map<String, String> param = new HashMap<>();
        if(getUrlParam() != null && getUrlParam() != ""){
            Map<String, Object> urlMap = JsonMapper.json2Map(getUrlParam());
            for(Map.Entry<String, Object> entry : urlMap.entrySet()){
                param.put(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        String result = HttpConnectionUtil.requestMethod(HttpConnectionUtil.HTTP_POST, getUrl(), HttpConnectionUtil.convertStringParamter(param), null);
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
            System.out.println(phone + "   plancode:" + plancode + "    row:"+row+"   put the list");
        }
        if(list != null && list.size() > 0){
            int num = MySqlUtil.saveDataBatch(connection, list);
            if(num > 1){
                setUpdateNum(getUpdateNum()+1);
                System.out.println("list:"+list.size()+"   process success    num:"+num);
            }else{
                setFailNum(getFailNum()+1);
                System.out.println("list:"+list.size()+"   process fail    num:"+num);
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