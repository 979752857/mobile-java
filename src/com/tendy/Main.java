package com.tendy;

import java.util.HashMap;
import java.util.Map;

public class Main {

    private static Map<String, Integer> busMap = new HashMap<>();
    static{
        busMap.put("769", 2);
        busMap.put("377", 1);
    }

    public static void main(String[] arg) throws InterruptedException {
        Phone dongguan = new GuangDongPhone(20, 0, 14, 769, busMap.get(String.valueOf(769)));
        while(true){
            for(int i = dongguan.getPageStart(); i <= dongguan.getPageStart(); i++){
                System.out.println("*********当前获取第"+i+"页数据,每页大小"+dongguan.getPageSize()+"***********");
                dongguan.execute(i);
                System.out.println("*********第"+i+"页数据处理完毕,每页大小"+dongguan.getPageSize()+"   成功数："+dongguan.getSuccessNum()+"   更新数："+
                        dongguan.getUpdateNum()+"   失败数："+dongguan.getFailNum()+"   ***********");
                if(i != dongguan.getPageStart()){
                    Thread.sleep(10000);
                }
            }
            Thread.sleep(1000*60*60);
        }
    }
}
