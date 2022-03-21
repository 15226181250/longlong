package com.longlong.longlongdisplay.utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author longlong
 * @create 2020 07 09 8:37
 */
public class MysqlUtil {
/*
    val driver = PropertiesUtil.getProperty("mysql.driver")
    val url = PropertiesUtil.getProperty("mysql.url")
    val userName = PropertiesUtil.getProperty("mysql.userName")
    val passWd = PropertiesUtil.getProperty("mysql.passWd")

    def getMysqlConn:Connection = {
        Class.forName (driver).newInstance()
        java.sql.DriverManager.getConnection(url, userName, passWd)
    }
*/

    public static Connection getMysqlConn(){
        Connection conn = null;
        try {
            String driver = PropertiesUtil.getProperty("mysql.driver");
            String url = PropertiesUtil.getProperty("mysql.url");
            String userName = PropertiesUtil.getProperty("mysql.userName");
            String passWd = PropertiesUtil.getProperty("mysql.passWd");
            //初始化驱动类com.mysql.jdbc.Driver
            Class.forName(driver);
            conn = DriverManager.getConnection(url, userName, passWd);
            //该类就在 mysql-connector-java-5.0.8-bin.jar中,如果忘记了第一个步骤的导包，就会抛出ClassNotFoundException
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return conn;
    }

}
