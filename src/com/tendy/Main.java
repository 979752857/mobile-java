package com.tendy;

import java.text.SimpleDateFormat;
import java.util.*;

public class Main {

    private static Map<String, Integer> busMap = new HashMap<>();
    private static Map<String, Integer> cityMap = new HashMap<>();
    static{
        busMap.put("200", 3);
        cityMap.put("guangzhou", 200);
        busMap.put("769", 2);
        cityMap.put("dongguan", 769);
        busMap.put("377", 1);
        cityMap.put("nanyang", 377);
    }

    public static void main(String[] arg) throws Exception {
        List<String> citys = new ArrayList<>();
//        citys.add("nanyang");
//        citys.add("dongguan");
        citys.add("guangzhou");
        runPhone(citys);
    }

    public static void runPhone(List<String> citys) throws Exception {
        while(true){
            String format = "HH:mm:ss";
            Date nowTime = new SimpleDateFormat(format).parse(TimeUtil.formatDate(new Date(), format));
            Date startTime = new SimpleDateFormat(format).parse("06:30:00");
            Date endTime = new SimpleDateFormat(format).parse("23:00:00");
            if(!TimeUtil.isEffectiveDate(nowTime, startTime, endTime)){
                System.out.println(TimeUtil.formatDate(new Date(), format)+"********不在时间范围内");
                Thread.sleep(1000*60*5);
                continue;
            }
            if(citys.contains("nanyang")){
                Integer cityId = cityMap.get("nanyang");
                Phone nanyang = new HenanPhone(20, 1, 1, cityId, busMap.get(String.valueOf(cityId)));
                phoneProcess(nanyang);
                Thread.sleep(1000*60*3);
            }
            if(citys.contains("dongguan")){
                Integer cityId = cityMap.get("dongguan");
                Phone dongguan = new GuangDongPhone(10, 1, 10, cityId, busMap.get(String.valueOf(cityId)));
                phoneProcess(dongguan);
                Thread.sleep(1000*60*10);
            }
            if(citys.contains("guangzhou")){
                Integer cityId = cityMap.get("guangzhou");
                Phone guangzhou = new GuangDongPhone(10, 1, 10, cityId, busMap.get(String.valueOf(cityId)));
                phoneProcess(guangzhou);
                Thread.sleep(1000*60*10);
            }
        }
    }

    public static void phoneProcess(Phone phone) throws Exception {
        for(int i = phone.getPageStart(); i <= phone.getPageEnd(); i++){
            System.out.println(phone.getCityId()+"*********当前获取第"+i+"页数据,每页大小"+phone.getPageSize()+"***********");
            phone.execute(i);
            System.out.println(phone.getCityId()+"*********第"+i+"页数据处理完毕,每页大小"+phone.getPageSize()+"   成功数："+phone.getSuccessNum()+"   更新数："+
                    phone.getUpdateNum()+"   失败数："+phone.getFailNum()+"   ***********");
            Random random = new Random();
            int sleep = random.nextInt(20)*1000 + 8000;
            Thread.sleep(sleep);
        }
    }
}
