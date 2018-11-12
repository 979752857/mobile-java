package com.tendy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
            sql = "INSERT INTO `user_account_phone_test` (`phone`, `url`, `business_id`, `city_id`, `tag`, `status`, `create_time`, `update_time`, `remark`) " +
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
}