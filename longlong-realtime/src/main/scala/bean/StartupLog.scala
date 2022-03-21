package bean

import java.text.SimpleDateFormat
import java.util.Date

/**
 * @author longlong
 * @create 2020 07 04 16:29
 * @Describe: 该类为startup(启动日志)样例类，用作json解析
 */
case class StartupLog(mid: String, //设备标识
                      uid: String,
                      appId: String,
                      area: String,
                      os: String,
                      channel: String,
                      logType: String,
                      version: String,
                      ts: Long,
                      var logDate: String = "",   //2020-07-04
                      var logHour: String = ""){  // 10
  logDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date(ts))
  logHour = new SimpleDateFormat("HH").format(new Date(ts))
}

