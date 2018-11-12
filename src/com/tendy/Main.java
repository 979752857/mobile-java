package com.tendy;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class Main {

    //http://wap.gd.10086.cn/nwap/card/cardDetail/doCardDetail.jsps?cardPackSid=CARD_PACK-20180901062611846-55202441

    private static String server = "http://wap.gd.10086.cn";
    private static String url = "http://wap.gd.10086.cn/nwap/card/cardSearch/cardlist.jsps";
    private static String pageSize = "20";
    private static Integer pageStart = 1;
    private static Integer pageEnd = 15;
    private static Integer successNum = 0;
    private static Integer updateNum = 0;
    private static Integer failNum = 0;
    private static Integer cityId = 769;

    public static void main(String[] arg) throws InterruptedException {
        if(arg != null){
            for(int i = 0; i<arg.length; i++){
                switch (i){
                    case 0:
                        cityId = Integer.valueOf(arg[i]);
                        break;
                    case 1:
                        pageStart = Integer.valueOf(arg[i]);
                        break;
                    case 2:
                        pageEnd = Integer.valueOf(arg[i]);
                        break;
                }
            }
        }
        while(true){
            for(int i = pageStart; i <= pageEnd; i++){
                System.out.println("*********当前获取第"+i+"页数据,每页大小"+pageSize+"***********");
                getPhoneAndHref(i, (i-1)*Integer.valueOf(pageSize));
                System.out.println("*********第"+i+"页数据处理完毕,每页大小"+pageSize+"   成功数："+successNum+"   更新数："+updateNum+"   失败数："+failNum+"   ***********");
                if(i != pageEnd){
                    Thread.sleep(10000);
                }
            }
            Thread.sleep(1000*60*60);
        }
    }

    public static void getPhoneAndHref(Integer pageNo, int row){

        Map<String, String> param = new HashMap<>();
        param.put("city", String.valueOf(cityId));
        param.put("pageNo", String.valueOf(pageNo));
        param.put("pageSize", pageSize);
        param.put("offLine", "1");
        param.put("hzhbjr", "SQD021782");
        param.put("recoempltel", "S121122007");
        param.put("newflow", "1");
        String result = HttpConnectionUtil.requestMethod(HttpConnectionUtil.HTTP_POST, url, HttpConnectionUtil.convertStringParamter(param));
        result = result.trim();
        processHtmlAndHref(result, row);
    }

    public static void processHtmlAndHref(String result, int row){
        int preLength = "<td>134<i></i>2841<i></i>".length();
        int poxLength = "<span class=\"text_red\">7280</span></td>".length();
        int indexHrefBeforeLength = "<td><a href=".length()+1;
        int indexHrefAfterLength = "class=\"numberpaybtn\"".length();

        Connection connection = MySqlUtil.getConnect();
        while(true){
            int index = result.indexOf("<span class=\"text_red\">");
            int indexHrefBefore = result.indexOf("<td><a href=");
            int indexHrefAfter = result.indexOf("class=\"numberpaybtn\"");
            if(index < 0){
                break;
            }
            String item = result.substring(index-preLength, index+poxLength);
            String href = server + result.substring(indexHrefBefore+indexHrefBeforeLength, indexHrefAfter-2);
            item = item.trim();
            String phone = "";
            if(item != null && !"".equals(item)) {
                for (int i = 0; i < item.length(); i++) {
                    if (item.charAt(i) >= 48 && item.charAt(i) <= 57) {
                        phone += item.charAt(i);
                    }
                }
            }
            result = result.substring(indexHrefAfter+indexHrefAfterLength);
            int num = MySqlUtil.saveData(connection, phone, href, cityId, "new");
            if(num > 1){
                updateNum++;
                System.out.println(phone + "   href:" + href.substring(22) + "    row:"+row+"   更新成功");
            }else if(num > 0){
                successNum++;
                System.out.println(phone + "   href:" + href.substring(22) + "    row:"+row+"   处理成功");
            }else{
                failNum++;
                System.out.println(phone + "   href:" + href.substring(22) + "    row:"+row+"   处理失败");
            }
            row++;
        }
        MySqlUtil.closeConnection(connection);
    }
}
