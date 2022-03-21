package app

import bean.{AlertInfo, EventLog}
import com.alibaba.fastjson.JSON
import common.Constant
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Minutes, Seconds, StreamingContext}
import utils.MyKafakUtil
import java.util

import scala.util.control.Breaks._

/**
 * @author longlong
 * @create 2020 07 08 2:42
 */
object AlertApp {

  def main(args: Array[String]): Unit = {

    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("DauApp")
    val ssc = new StreamingContext(conf, Seconds(5))
    // 添加窗口, 调整数据结构
    val sourceStream = MyKafakUtil.
              getKafkaStream(ssc, Constant.TOPIC_EVENT).
              window(Seconds(60), Seconds(5))
    // TODO 窗口大小应为采集周期的整数倍，窗口滑动的步长也应该为采集周期的整数倍

    val eventLogStram = sourceStream.map(jsonStr => JSON.parseObject(jsonStr, classOf[EventLog]))

    // 按照 mid 分组
    val enventLogsGroup: DStream[(String, Iterable[EventLog])] = eventLogStram
                    .map(evenLogs => (evenLogs.mid, evenLogs)).groupByKey()

    //  预警的业务逻辑
    val checkCouponAlertDStream = enventLogsGroup.map{
      case (mid, evenLogs:Iterable[EventLog]) => {
        // TODO 必须要用java的集合，scala的集合写到es的时候取不出数据

        //存储所有登陆过的用户，统计当前设备(mid),在最近5分钟内登陆过的所有用户
        val uids = new util.HashSet[String]()
        //存储被领取的优惠券对应的商品ID
        val itemIds = new util.HashSet[String]()
        //存储5分钟内当前设备所有事件
        val eventIds = new util.ArrayList[String]()
        //用户是否点击了商品，默认是没有点击
        var isBrowserProduct = false // 是否浏览商品, 默认没有浏览

        breakable {
          evenLogs.foreach(log => {

            eventIds.add(log.eventId)
            log.eventId match {
              case "coupon" =>
                uids.add(log.uid)  // 存储领取优惠卷用户的ID
                itemIds.add(log.itemId)  //存储优惠卷对应商品的ID
              case "clickItem" =>
                //只要有一次浏览商品就不应该产生预警信息
                isBrowserProduct = true
                break
              case _ => //其它事件不处理
            }

          })
        }
        // 组合成元组  (是否预警, 预警信息)
// TODO 为了快速见效把窗口调为60，uids.sizea改为大于等于2，设备改为10台,把是否点击取消
        (uids.size()>=2, AlertInfo(mid,uids,itemIds,eventIds,System.currentTimeMillis()))
      }
    }
    // 过滤掉不需要报警的信息
    val filteredDStream: DStream[AlertInfo] = checkCouponAlertDStream.filter(_._1).map(_._2)
    // 把预警信息写入到 ES
    filteredDStream.print()
    //启动采集器
    ssc.start()
    //Driver等待采集器的执行
    ssc.awaitTermination()
  }

  //需求：同一设备，5分钟内三次及以上用不同账号登录并领取优惠劵，
  //并且在登录到领劵过程中没有浏览商品。同时达到以上要求则产生一条预警日志。
  //同一设备，每分钟只记录一次预警。

  //需求分析：
  // 1.同一设备按照id进行分组
  // 2.5分钟内 window的概念，窗口大小，滑动步长
  // 3. 三次以上不同用户登录  统计登录的用户(聚合)
  // 4.领取优惠卷(过滤)
  // 5.登陆后没有浏览商品
  // 6.同一设备，每分钟只记录一次预警  交给es实现，id每分钟变化一次
}
