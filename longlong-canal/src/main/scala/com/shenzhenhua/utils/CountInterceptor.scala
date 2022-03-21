package com.shenzhenhua.utils

import java.util

import org.apache.kafka.clients.producer.{ProducerInterceptor, ProducerRecord, RecordMetadata}

/**
 * @author longlong
 * @create 2020 07 06 11:31
 * @Describe: 该类为kafka的拦截器，用来统计成功发送消息的次数
 */

class CountInterceptor extends ProducerInterceptor[Nothing, Nothing]{

  private var errorCounter = 0
  private var successCounter = 0

  override def onSend(producerRecord: ProducerRecord[Nothing, Nothing]): ProducerRecord[Nothing, Nothing] = {
    producerRecord
  }

  override def onAcknowledgement(recordMetadata: RecordMetadata, e: Exception): Unit = {
    // 统计成功和失败的次数
    if (e == null) successCounter += 1
    else errorCounter += 1
  }

  override def close(): Unit = {
    // 保存结果
    println("Successful sent: " + successCounter)
    println("Failed sent: " + errorCounter)
  }

  override def configure(map: util.Map[String, _]): Unit = {}
}
