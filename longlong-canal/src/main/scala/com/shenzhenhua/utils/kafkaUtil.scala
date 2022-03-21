package com.shenzhenhua.utils

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}

/**
 * @author longlong
 * @create 2020 07 06 11:01
 * @Describe: 该类提供kafka的连接，和发送数据到kafka
 */

object kafkaUtil {

  val props = new Properties()
  props.put("bootstrap.servers", PropertiesUtil.getProperty("bootstrap.servers"))
  // 等待所有副本节点的应答
  props.put("acks", PropertiesUtil.getProperty("acks"))
  // 消息发送最大尝试次数
  props.put("retries", PropertiesUtil.getProperty("retries"))
  // 一批消息处理大小
  props.put("batch.size", PropertiesUtil.getProperty("batch.size"))
  // 增加服务端请求延时
  props.put("linger.ms", PropertiesUtil.getProperty("linger.ms"))
  // 发送缓存区内存大小
  props.put("buffer.memory", PropertiesUtil.getProperty("buffer.memory"))
  // key序列化
  props.put("key.serializer", PropertiesUtil.getProperty("key.serializer"))
  // value序列化
  props.put("value.serializer", PropertiesUtil.getProperty("value.serializer"))

  // 添加拦截器统计成功次数和失败次数
//  val interceptor = "com.shenzhenhua.utils.CountInterceptor"
//  props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, interceptor)

  def getKafkaProducer:KafkaProducer[String, String] = {
    val kafkaProducer = new KafkaProducer[String, String](props)
    kafkaProducer
  }

  def sendData(topic: String, content: String) = {
    val kafkaProducer = kafkaUtil.getKafkaProducer
    kafkaProducer.send(new ProducerRecord[String,String](topic, content))
  }
}
