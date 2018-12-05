package com.tendy;

import com.tendy.model.MobileSpiderConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: tendy
 * @Description:
 * @Date: 2018/10/24
 */
public class MySqlUtil {

    public static Connection getConnect(){
        Connection conn = null;
        try {
            // 注册 JDBC 驱动
            Class.forName(DBConstants.JDBC_DRIVER);
            // 打开链接
            conn = DriverManager.getConnection(DBConstants.DB_URL, DBConstants.USER, DBConstants.PASS);
        }catch(Exception e){
            // 处理 Class.forName 错误
            e.printStackTrace();
            try{
                if(conn!=null){
                    conn.close();
                }
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
        return conn;
    }

    public static void closeConnection(Connection conn){
        try{
            if(conn!=null){
                conn.close();
            }
        }catch(SQLException se){
            se.printStackTrace();
        }
    }

    public static int saveData(Connection conn, String phone, String href, Integer cityId, Integer businessId, String status, String tag, String remark) {
        Statement stmt = null;
        int num = 0;
        try{
            // 执行查询
            stmt = conn.createStatement();
            String sql;
            sql = "INSERT INTO `user_account_phone` (`phone`, `url`, `business_id`, `city_id`, `tag`, `status`, `create_time`, `update_time`, `remark`) " +
                    "VALUES ('"+phone+"', '"+href+"', '"+businessId+"', '"+cityId+"', '"+tag+"', '"+status+"', now(), now(), "+"'"+remark+"') on DUPLICATE key update `url`='"+href+"'," +
                    "`update_time`=now();";
            num = stmt.executeUpdate(sql);
            stmt.close();
        }catch(SQLException se){
            // 处理 JDBC 错误
//            se.printStackTrace();
        }catch(Exception e){
            // 处理 Class.forName 错误
//            e.printStackTrace();
        }finally{
            // 关闭资源
            try{
                if(stmt!=null){
                    stmt.close();
                }
            }catch(SQLException se2){

            }
        }
        return num;
    }

    public static int saveDataBatch(Connection conn, List<UserAccountPhone> list) {
        Statement stmt = null;
        int num = 0;
        try{
            // 执行查询
            stmt = conn.createStatement();
            StringBuilder sql = new StringBuilder("INSERT INTO `user_account_phone` (`phone`, `url`, `business_id`, `city_id`, `tag`, `status`, `create_time`, `update_time`, `remark`) VALUES ");
            for(int i = 0; i < list.size(); i++){
                UserAccountPhone phone = list.get(i);
                sql.append("('"+phone.getPhone()+"', '"+phone.getUrl()+"', '"+phone.getBusinessId()+"', '"+phone.getCityId()+"', '"+phone.getTag()+"', '"+phone.getStatus()+"', now(), now(), "+"'"+phone.getRemark()+"')");
                if(i != list.size()-1){
                    sql.append(",");
                }
            }
            sql.append(" on DUPLICATE key update `update_time`=now();");
            num = stmt.executeUpdate(sql.toString());
            stmt.close();
        }catch(SQLException se){
            // 处理 JDBC 错误
//            se.printStackTrace();
        }catch(Exception e){
            // 处理 Class.forName 错误
//            e.printStackTrace();
        }finally{
            // 关闭资源
            try{
                if(stmt!=null){
                    stmt.close();
                }
            }catch(SQLException se2){

            }
        }
        return num;
    }

    public static Map<Integer, List<Map<String,String>>> getAlertConfig(Connection conn) {
        Map<Integer, List<Map<String,String>>> cityMap = new HashMap<>();
        Statement stmt = null;
        ResultSet rs = null;
        try{
            stmt = conn.createStatement();
            String sql = "select id,city_id,tag,contains_key,send_alert from phone_alert_config where status = 'online'";
            rs = stmt.executeQuery(sql);
            while(rs.next()){
                int id = rs.getInt("id");
                int city_id = rs.getInt("city_id");
                String tag = rs.getString("tag");
                String contains_key = rs.getString("contains_key");
                String send_alert = rs.getString("send_alert");
                Map<String, String> item = new HashMap<>();
                item.put("cityId", String.valueOf(city_id));
                item.put("id", String.valueOf(id));
                item.put("tag", tag);
                item.put("key", contains_key);
                item.put("send", send_alert);
                List<Map<String, String>> list = cityMap.get(city_id);
                if(list == null){
                    list = new ArrayList<>();
                }
                list.add(item);
                cityMap.put(city_id, list);
            }
            stmt.close();
        }catch(Exception e){

        }finally{
            try{
                if(stmt!=null){
                    stmt.close();
                }
            }catch(SQLException se2){

            }
        }
        return cityMap;
    }

    public static List<MobileSpiderConfig> getMobileSpiderConfig(Connection conn) {
        List<MobileSpiderConfig> list = new ArrayList<>();
        Statement stmt = null;
        ResultSet rs = null;
        try{
            stmt = conn.createStatement();
            String sql = "select province,url,method_param,type,url_param,config,remark from mobile_spider_config where status = 'online'";
            rs = stmt.executeQuery(sql);
            while(rs.next()){
                MobileSpiderConfig config = new MobileSpiderConfig();
                config.setProvince(rs.getString("province"));
                config.setUrl(rs.getString("url"));
                config.setMethodParam(rs.getString("method_param"));
                config.setType(rs.getString("type"));
                config.setUrlParam(rs.getString("url_param"));
                config.setConfig(rs.getString("config"));
                config.setRemark(rs.getString("remark"));
                list.add(config);
            }
            stmt.close();
        }catch(Exception e){

        }finally{
            try{
                if(stmt!=null){
                    stmt.close();
                }
            }catch(SQLException se2){

            }
        }
        return list;
    }
}