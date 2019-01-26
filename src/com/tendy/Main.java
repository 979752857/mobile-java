package com.tendy;

import com.tendy.model.MobileSpiderConfig;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {

    public static void main(String[] arg) throws Exception {
        runPhone();
    }

    public static void runPhone() throws Exception {
        while(true){
            String format = "HH:mm:ss";
            Date nowTime = new SimpleDateFormat(format).parse(TimeUtil.formatDate(new Date(), format));
            Date startTime = new SimpleDateFormat(format).parse("06:30:00");
            Date endTime = new SimpleDateFormat(format).parse("23:00:00");
            if(!TimeUtil.isEffectiveDate(nowTime, startTime, endTime)){
                System.out.println(TimeUtil.formatDate(new Date(), format)+"******** not the time range");
                Thread.sleep(1000*60*5);
                continue;
            }
            Connection connection = MySqlUtil.getConnect();
            SendAlertUtil.init(connection);
            List<MobileSpiderConfig> list = MySqlUtil.getMobileSpiderConfig(connection);
            if(list == null){
                list = new ArrayList<>();
            }
            MySqlUtil.closeConnection(connection);
            for(MobileSpiderConfig config : list){
                Map<String, Object> paramMap = JsonMapper.json2Map(config.getMethodParam());
                Map<String, Object> configMap = JsonMapper.json2Map(config.getConfig());
                if("henan".equals(config.getProvince())){
                    Phone henan = new HenanPhone(Integer.valueOf(String.valueOf(paramMap.get("pageSize"))), Integer.valueOf(String.valueOf(paramMap.get("pageStart"))),
                            Integer.valueOf(String.valueOf(paramMap.get("pageEnd"))), Integer.valueOf(String.valueOf(paramMap.get("cityId"))),
                            Integer.valueOf(String.valueOf(paramMap.get("businessId"))), config.getUrl(), config.getUrlParam());
                    phoneProcess(henan);
                    if(configMap.get("sleep_second") != null){
                        Thread.sleep(1000*Integer.valueOf(String.valueOf(configMap.get("sleep_second"))));
                    }else{
                        Thread.sleep(1000*60*3);
                    }
                }else if("guangdong".equals(config.getProvince())){
                    Phone guangdong = new GuangDongPhone(Integer.valueOf(String.valueOf(paramMap.get("pageSize"))), Integer.valueOf(String.valueOf(paramMap.get("pageStart"))),
                            Integer.valueOf(String.valueOf(paramMap.get("pageEnd"))), Integer.valueOf(String.valueOf(paramMap.get("cityId"))),
                            Integer.valueOf(String.valueOf(paramMap.get("businessId"))), config.getUrl(), config.getUrlParam());
                    phoneProcess(guangdong);
                    if(configMap.get("sleep_second") != null){
                        Thread.sleep(1000*Integer.valueOf(String.valueOf(configMap.get("sleep_second"))));
                    }else{
                        Thread.sleep(1000*60*10);
                    }
                }
            }
        }
    }

    public static void phoneProcess(Phone phone) throws Exception {
        for(int i = phone.getPageStart(); i <= phone.getPageEnd(); i++){
            System.out.println(phone.getCityId()+"*********process the "+i+" page data, page size:"+phone.getPageSize()+"***********");
            phone.execute(i);
            System.out.println(TimeUtil.formatDate(new Date(), "HH:mm:ss") + "*****" + phone.getCityId()+"********* page data "+i+" was finished, page size:"+phone.getPageSize()+"   success："+phone.getSuccessNum()+"   update："+
                    phone.getUpdateNum()+"   fail："+phone.getFailNum()+"   ***********");
            Random random = new Random();
            int sleep = random.nextInt(20)*1000 + 8000;
            Thread.sleep(sleep);
        }
    }
}
