package com.tendy;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: tendy
 * @Description:
 * @Date: 2018/11/12
 */
public class GuangDongPhone extends Phone {

    private String server = "http://wap.gd.10086.cn";
    private String url = "http://wap.gd.10086.cn/nwap/card/cardSearch/cardlist.jsps";

    public GuangDongPhone(Integer pageSize, Integer pageStart, Integer pageEnd, Integer cityId, Integer businessId) {
        super(pageSize, pageStart, pageEnd, cityId, businessId);
    }

    @Override
    public void execute(Integer pageStart) {
        getPhoneAndHref(pageStart, pageStart*getPageSize());
    }

    public void getPhoneAndHref(Integer pageNo, int row){

        Map<String, String> param = new HashMap<>();
        param.put("city", String.valueOf(getCityId()));
        param.put("pageNo", String.valueOf(pageNo));
        param.put("pageSize", String.valueOf(getPageSize()));
        param.put("offLine", "1");
        param.put("hzhbjr", "SQD021782");
        param.put("recoempltel", "S121122007");
        param.put("newflow", "1");
        String result = HttpConnectionUtil.requestMethod(HttpConnectionUtil.HTTP_POST, url, HttpConnectionUtil.convertStringParamter(param));
        result = result.trim();
        processHtmlAndHref(result, row);
    }

    public void processHtmlAndHref(String result, int row){
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
            String href = this.server + result.substring(indexHrefBefore+indexHrefBeforeLength, indexHrefAfter-2);
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
            ItemRule itemRule = MobileRule.checkPhone(phone);
            String tag = "";
            String remark = "";
            if(itemRule != null){
                tag = itemRule.getTag();
                Map<String, String> map = new HashMap<>();
                map.put("tag", itemRule.getRemark());
                remark = JsonMapper.toJson(map);
            }
            int num = MySqlUtil.saveData(connection, phone, href, getCityId(), getBusinessId(), "private", tag, remark);
            if(num > 1){
                setUpdateNum(getUpdateNum()+1);
                System.out.println(phone + "   href:" + href.substring(22) + "    row:"+row+"   更新成功");
            }else if(num > 0){
                setSuccessNum(getSuccessNum()+1);
                System.out.println(phone + "   href:" + href.substring(22) + "    row:"+row+"   处理成功");
            }else{
                setFailNum(getFailNum()+1);
                System.out.println(phone + "   href:" + href.substring(22) + "    row:"+row+"   处理失败");
            }
            row++;
        }
        MySqlUtil.closeConnection(connection);
    }
}