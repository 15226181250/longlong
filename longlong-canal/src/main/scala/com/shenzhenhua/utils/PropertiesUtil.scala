package com.shenzhenhua.utils

import java.util.Properties

/**
 * @author longlong
 * @create 2020 07 04 6:05
 * @Describe: 该类主要提供通过传入名配置称来获取配置信息
 */

object PropertiesUtil {

  private val is = ClassLoader.getSystemResourceAsStream("conf.properties")
  private val properties = new Properties()
  properties.load(is)

  def getProperty(propertyName : String) = properties.getProperty(propertyName)

//  def main(args: Array[String]): Unit = {
//    val aaa = propertiesUtil.getProperty("bootstrap.servers")
//    println(aaa)
//  }

}
