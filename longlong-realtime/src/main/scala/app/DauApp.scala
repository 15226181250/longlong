package app

import java.text.SimpleDateFormat
import java.util
import java.util.Date

import bean.StartupLog
import com.alibaba.fastjson.JSON
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import common.Constant
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.streaming.dstream.DStream
import utils.{HbaseUtil, MyKafakUtil, RedisUtil}

/**
 * @author longlong
 * @create 2020 07 04 6:46
 * @Describe: 该类负责从kafka中读数据，用spark streaming转换数据结构，
 *           然后通过redis去重，再写到hbase中
 */

object DauApp {

  def main(args: Array[String]): Unit = {

    // 1.从kafka消费数据

    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("DauApp")
    val ssc = new StreamingContext(conf, Seconds(3))
    val sourceStream = MyKafakUtil.getKafkaStream(ssc, Constant.TOPIC_STARTUP)
          // 将json数据解析成对象，方便操作
    val startLogStram = sourceStream.map(jsonStr => JSON.parseObject(jsonStr, classOf[StartupLog]))

    // 2.过滤去重得到日活数据

    // 需要借助第三方工具redis去重，distinct只能去重3秒内的数据
    //Dstram与外界交互两种方式：1.transform(转换)，2.foreachRDD(行动)

    val firstStartupLogStream: DStream[StartupLog] = startLogStram.transform(rdd => {
      //从redis中读取已启动的设备
      //用set去重，格式key: TOPIC_STARTUP+":"+2020-07-04    value: mid1,mid2.......
      val redis = RedisUtil.getRedis
      val key = Constant.TOPIC_STARTUP+":"+new SimpleDateFormat("yyyy-MM-dd").format(new Date())
      val mids = redis.smembers(key)  //根据当天日期查出所有设备
      redis.close() //没有用连接池必须关掉

      //将已启动的设备去除
      val midsBD = ssc.sparkContext.broadcast(mids) //由于mids可能很大，这里使用广播变量

      // 考虑到某个mid可能在一个批次启动了多次(而且这个mid是第一次启动)，会出现重复情况，对过滤进行改良
      rdd
        .filter(log => !midsBD.value.contains(log.mid))
        .map(log =>(log.mid,log))
        .groupByKey()
        .map{
          case(_, it) => it.toList.sortBy(_.ts).head //按时间排序只要第一个
        }
    })


    // 将过滤后的第一次启动的设备添加到redis中

    firstStartupLogStream.foreachRDD(rdd => {

      rdd.foreachPartition(objs =>{  // 使用foreachPartition减少连接数据库次数，一个分区连一次
          val redis = RedisUtil.getRedis
          objs.foreach(obj => {
            //每次存入一个mid
            redis.sadd(Constant.TOPIC_STARTUP+":"+obj.logDate, obj.mid)
          })
          redis.close()
        }
      )
    })

    firstStartupLogStream.print()

    // 3.将数据写到hbase中

    firstStartupLogStream.foreachRDD(rdd =>{
      rdd.foreachPartition(partition => {   // 使用foreachPartition减少连接数据库次数，一个分区连一次

        val conn = HbaseUtil.getConnection()
        val tableNmae = TableName.valueOf(Constant.TABLE_NAME)
        val table = conn.getTable(tableNmae)
        val puts = new util.ArrayList[Put]()

        try {
          partition.foreach(arr => {

            val rowkey = arr.mid + "_" + arr.logDate
            val put = new Put(Bytes.toBytes(rowkey))

            put.addImmutable(Bytes.toBytes("startup"), Bytes.toBytes("mid"), Bytes.toBytes(arr.mid))
            put.addImmutable(Bytes.toBytes("startup"), Bytes.toBytes("uid"), Bytes.toBytes(arr.uid))
            put.addImmutable(Bytes.toBytes("startup"), Bytes.toBytes("appId"), Bytes.toBytes(arr.appId))
            put.addImmutable(Bytes.toBytes("startup"), Bytes.toBytes("area"), Bytes.toBytes(arr.area))
            put.addImmutable(Bytes.toBytes("startup"), Bytes.toBytes("os"), Bytes.toBytes(arr.os))
            put.addImmutable(Bytes.toBytes("startup"), Bytes.toBytes("channel"), Bytes.toBytes(arr.channel))
            put.addImmutable(Bytes.toBytes("startup"), Bytes.toBytes("logType"), Bytes.toBytes(arr.logType))
            put.addImmutable(Bytes.toBytes("startup"), Bytes.toBytes("version"), Bytes.toBytes(arr.version))
            put.addImmutable(Bytes.toBytes("startup"), Bytes.toBytes("ts"), Bytes.toBytes(arr.ts))
            put.addImmutable(Bytes.toBytes("startup"), Bytes.toBytes("logDate"), Bytes.toBytes(arr.logDate))
            put.addImmutable(Bytes.toBytes("startup"), Bytes.toBytes("logHour"), Bytes.toBytes(arr.logHour))

            puts.add(put)

          })
        }catch {
          case e: Exception => e.printStackTrace
        }finally {
          table.put(puts)
          table.close()
          conn.close()
        }
      })
    })

    //启动采集器
    ssc.start()
    //Driver等待采集器的执行
    ssc.awaitTermination()
  }

}
