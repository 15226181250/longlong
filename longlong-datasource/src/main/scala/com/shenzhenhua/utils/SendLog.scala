package com.shenzhenhua.utils

/**
 * @author longlong
 * @create 2020 07 02 23:25
 * @Describe: 该类主要提供向固定端口发送日志信息
 */

import java.io.OutputStream
import java.net.{HttpURLConnection, URL}

object SendLog {
  /*发送日志*/
  def sendLog(log: String): Unit = {
    try {
      // 1. 日志服务器的地址      (这里发送的是nginx服务器)
      val logUrl = new URL("http://192.168.245.103:80/longlong/log")
      // 2. 得到一个 HttpURLConnection
      val conn: HttpURLConnection = logUrl.openConnection().asInstanceOf[HttpURLConnection]
      // 3. 设置请求方法(上传数据一般使用 post 请求)
      conn.setRequestMethod("POST")
      // 4. 用来供server进行时钟校对的
      conn.setRequestProperty("clientTime", System.currentTimeMillis + "")
      // 5. 允许上传数据
      conn.setDoOutput(true)
      // 6. 设置请求的头信息, post 请求必须这样设置
      conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
      // 7. 获取上传用的输出流
      val out: OutputStream = conn.getOutputStream
      // 8. 写出数据
      out.write(("one_log=" + log).getBytes())
      // 9. flush
      out.flush()
      // 10. 关闭资源
      out.close()
      // 11. 获取响应码.  (或者获取响应信息也行, 否则不会发送请求到服务器)
      val code: Int = conn.getResponseCode
      println(code)
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }
}
