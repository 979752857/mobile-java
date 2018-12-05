package com.tendy;

import com.tendy.model.ItemRule;
import com.tendy.model.SendIM;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendAlertUtil {

    private static Map<Integer, List<Map<String, String>>> cityMap = new HashMap<>();

    public static void init(Connection connection){
        cityMap = MySqlUtil.getAlertConfig(connection);
        if(cityMap == null){
            cityMap = new HashMap<>();
        }
    }

    public static void checkAndSendAlert(Integer cityId, List<String> phones) {
        List<Map<String, String>> list = cityMap.get(cityId);
        if(list == null || list.size() == 0){
            return;
        }
        Map<String, List<SendIM>> sendIMListMap = new HashMap<>();
        for(String phone : phones){
            for(Map<String, String> item : list){
                String tag = item.get("tag");
                String key = item.get("key");
                String send = item.get("send");
                SendIM sendIM = checkTag(tag, phone, send);
                if(sendIM == null){
                    sendIM = checkKey(key, phone, send);
                }
                if(sendIM != null){
                    List<SendIM> imList = sendIMListMap.get(send);
                    if(imList == null){
                        imList = new ArrayList<>();
                    }
                    imList.add(sendIM);
                    sendIMListMap.put(send, imList);
                }
            }
        }
        for(Map.Entry<String, List<SendIM>> entry : sendIMListMap.entrySet()){
            if(entry.getValue() == null || entry.getValue().size() == 0){
                continue;
            }
            try {
                sendIm(entry.getKey(), entry.getValue());
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static SendIM checkTag(String tag, String phone, String send){
        if(tag == null || "".equals(tag) || phone == null || "".equals(phone) || send == null || "".equals(send)){
            return null;
        }
        String tags = ","+tag+",";
        ItemRule itemRule = MobileRule.checkPhone(phone);
        if(itemRule != null){
            if(tags.contains(","+itemRule.getTag()+",")){
                return new SendIM(phone, itemRule.getTag(), null);
            }
        }
        return null;
    }

    public static SendIM checkKey(String key, String phone, String send){
        if(key == null || "".equals(key) || phone == null || "".equals(phone) || send == null || "".equals(send)){
            return null;
        }
        String[] keys = key.split(",");
        for(String item : keys){
            if(MobileRule.isMatch(phone, item)){
                return new SendIM(phone, null, item);
            }
        }
        return null;
    }

    public static void sendIm(String send, List<SendIM> list) throws ParseException {
        if(send == null || "".equals(send) || list == null || list.size() == 0){
            return;
        }
        String format = "HH:mm:ss";
        Date nowTime = new SimpleDateFormat(format).parse(TimeUtil.formatDate(new Date(), format));
        Date startTime = new SimpleDateFormat(format).parse("08:00:00");
        Date endTime = new SimpleDateFormat(format).parse("22:00:00");
        if(!TimeUtil.isEffectiveDate(nowTime, startTime, endTime)){
            return;
        }
        StringBuilder body = new StringBuilder("{\"msgtype\":\"text\",\"text\":{\"content\":\"");
        for(SendIM item : list){
            if(item.getTag() != null){
                body.append("手机号："+item.getPhone()+"  符合标签："+item.getTag()+"\n");
            }
            if(item.getKey() != null){
                String key = item.getKey().replace("%", "");
                body.append("手机号："+item.getPhone()+"  符合关键词："+key+"\n");
            }
        }
        body.append("请及时在系统中查询！");
        body.append("\"},\"at\":{\"isAtAll\":false}}");
        Map<String,String> head = new HashMap<>();
        head.put("Content-Type", "application/json;charset=utf-8");
        String result = HttpConnectionUtil.requestMethod(HttpConnectionUtil.HTTP_POST, send, body.toString(), head);
        System.out.println(body.toString()+"\n"+result);
    }

}