package app

import java.util

import bean.OrderBean
import com.alibaba.fastjson.JSON
import common.Constant
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import utils.{HbaseUtil, MyKafakUtil}

/**
 * @author longlong
 * @create 2020 07 06 13:42
 * @Describe: 该类负责从kafka中读数据，用spark streaming转换数据结构，
 *           然后通过redis去重，再写到hbase中
 */

object OrderApp {

  def main(args: Array[String]): Unit = {

    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("OrderApp")
    val ssc = new StreamingContext(conf, Seconds(3))
    val sourceStream = MyKafakUtil.getKafkaStream(ssc, Constant.CANAL_DEMO)

    // 将json数据解析成对象，方便操作
    val orderStream = sourceStream.map(jsonStr => JSON.parseObject(jsonStr, classOf[OrderBean]))

    // 将数据插入到Hbase中

    orderStream.foreachRDD(rdd => {
      rdd.foreachPartition(partition => {

        val conn = HbaseUtil.getConnection()
        val tableNmae = TableName.valueOf(Constant.TABLE_NAME2)
        val table = conn.getTable(tableNmae)
        val puts = new util.ArrayList[Put]()

        try {
          partition.foreach(rdd => {

            val rowkey = rdd.id
            val put = new Put(Bytes.toBytes(rowkey))

            put.addImmutable(Bytes.toBytes("order_info"), Bytes.toBytes("province_id"), Bytes.toBytes(rdd.province_id))
            put.addImmutable(Bytes.toBytes("order_info"), Bytes.toBytes("consignee"), Bytes.toBytes(rdd.consignee))
            put.addImmutable(Bytes.toBytes("order_info"), Bytes.toBytes("order_comment"), Bytes.toBytes(rdd.order_comment))
            put.addImmutable(Bytes.toBytes("order_info"), Bytes.toBytes("consignee_tel"), Bytes.toBytes(rdd.consignee_tel))
            put.addImmutable(Bytes.toBytes("order_info"), Bytes.toBytes("order_status"), Bytes.toBytes(rdd.order_status))
            put.addImmutable(Bytes.toBytes("order_info"), Bytes.toBytes("payment_way"), Bytes.toBytes(rdd.payment_way))
            put.addImmutable(Bytes.toBytes("order_info"), Bytes.toBytes("user_id"), Bytes.toBytes(rdd.user_id))
            put.addImmutable(Bytes.toBytes("order_info"), Bytes.toBytes("img_url"), Bytes.toBytes(rdd.img_url))
            put.addImmutable(Bytes.toBytes("order_info"), Bytes.toBytes("total_amount"), Bytes.toBytes(rdd.total_amount))
            put.addImmutable(Bytes.toBytes("order_info"), Bytes.toBytes("expire_time"), Bytes.toBytes(rdd.expire_time))
            put.addImmutable(Bytes.toBytes("order_info"), Bytes.toBytes("delivery_address"), Bytes.toBytes(rdd.delivery_address))
            put.addImmutable(Bytes.toBytes("order_info"), Bytes.toBytes("create_time"), Bytes.toBytes(rdd.create_time))
            put.addImmutable(Bytes.toBytes("order_info"), Bytes.toBytes("operate_time"), Bytes.toBytes(rdd.operate_time))
            put.addImmutable(Bytes.toBytes("order_info"), Bytes.toBytes("tracking_no"), Bytes.toBytes(rdd.tracking_no))
            put.addImmutable(Bytes.toBytes("order_info"), Bytes.toBytes("parent_order_id"), Bytes.toBytes(rdd.parent_order_id))
            put.addImmutable(Bytes.toBytes("order_info"), Bytes.toBytes("out_trade_no"), Bytes.toBytes(rdd.out_trade_no))
            put.addImmutable(Bytes.toBytes("order_info"), Bytes.toBytes("trade_body"), Bytes.toBytes(rdd.trade_body))
            put.addImmutable(Bytes.toBytes("order_info"), Bytes.toBytes("create_date"), Bytes.toBytes(rdd.create_date))
            put.addImmutable(Bytes.toBytes("order_info"), Bytes.toBytes("create_hour"), Bytes.toBytes(rdd.create_hour))

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

    orderStream.print()

    //启动采集器
    ssc.start()
    //Driver等待采集器的执行
    ssc.awaitTermination()
  }
}
