package utils

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.kafka.KafkaUtils

/**
 * @author longlong
 * @create 2020 07 04 6:32
 * @Describe: 该类用于提供kafka的数据流
 */
object MyKafakUtil { //返回kafkaStream

  def getKafkaStream(ssc : StreamingContext, topic : String, otherTopics : String*) = {

    val servers = PropertiesUtil.getProperty("bootstrap.servers")
    val groupId = PropertiesUtil.getProperty("group.id")

    //只返回value部分
    KafkaUtils.createStream(
      ssc,
      servers,
      groupId,
      Map(topic -> 3)
    ).map(_._2)

  }
}
