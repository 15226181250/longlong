package utils

import java.sql.Connection


/**
 * @author longlong
 * @create 2020 07 08 18:50
 * @Describe: 该类用于提供mysql的连接
 */
object mysqlUtil {

  val driver = PropertiesUtil.getProperty("mysql.driver")
  val url = PropertiesUtil.getProperty("mysql.url")
  val userName = PropertiesUtil.getProperty("mysql.userName")
  val passWd = PropertiesUtil.getProperty("mysql.passWd")

  def getMysqlConn:Connection = {
    Class.forName (driver).newInstance()
    java.sql.DriverManager.getConnection(url, userName, passWd)
  }

}
