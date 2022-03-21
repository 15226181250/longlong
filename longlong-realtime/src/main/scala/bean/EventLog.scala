package bean

import java.text.SimpleDateFormat
import java.util.Date

/**
 * @author longlong
 * @create 2020 07 08 2:59
 */
case class EventLog(mid: String,
                    uid: String,
                    appId: String,
                    area: String,
                    os: String,
                    logType: String,
                    eventId: String,
                    pageId: String,
                    nextPageId: String,
                    itemId: String,
                    ts: Long,
                    var logDate: String = "",
                    var logHour: String = ""){
  logDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date(ts))
  logHour = new SimpleDateFormat("HH").format(new Date(ts))
}
