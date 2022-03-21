package bean

/**
 * @author longlong
 * @create 2020 07 08 13:20
 */
case class AlertInfo(mid: String,
                     uids: java.util.HashSet[String],
                     itemIds: java.util.HashSet[String],
                     events: java.util.List[String],
                     ts: Long)

