package com.shenzhenhua

import java.net.InetSocketAddress
import java.util

import com.alibaba.otter.canal.client.CanalConnectors
import com.alibaba.otter.canal.protocol.CanalEntry
import com.alibaba.otter.canal.protocol.CanalEntry.{EntryType, RowChange}
import com.google.protobuf.ByteString
import com.shenzhenhua.utils.CanalUtil



/**
 * @author longlong
 * @create 2020 07 06 0:33
 * @Describe: 该类主要负责监控mysql指定数据变化，并拉取数据进行解析
 */

object CanalClient {
  def main(args: Array[String]): Unit = {

    val address = new InetSocketAddress("hd003", 11111)
    val conn = CanalConnectors.newSingleConnector(address, "example", "", "")
    conn.connect() // 连接canal

    // 订阅数据   longlong库下所有的表
    conn.subscribe("longlong.*")

    while(true){

      val message = conn.get(100) // 一次订阅100条sql数据引起的变化
      val entriesOption = if(message != null) Some(message.getEntries) else None // 做非空判断

      // TODO 返回的结果是java类型的，如果遍历需要导入隐式转换
      import scala.collection.JavaConversions._

      if (entriesOption.isDefined && entriesOption.get.nonEmpty) {
        val entries = entriesOption.get
        // 一个entry封装一条sql变化的结果
        for (entry <- entries){
          // entryType的类型应该是ROWDATA类型
          if (entry != null && entry.hasEntryType &&  entry.getEntryType == EntryType.ROWDATA){
            // 从每个entry里面获取一个storeValue
            val storeValue: ByteString = entry.getStoreValue
            // 把rowChange从storeValue里面解析出来
            val rowChange: RowChange = RowChange.parseFrom(storeValue)
            // 每个rowChange里面有多个rowData，一个rowData表示一行数据的变化
            val rowDatas: util.List[CanalEntry.RowData] = rowChange.getRowDatasList

            // TODO 解析rowDatas中每行每列数据的变化，并发送到kafka
            CanalUtil.handleData(entry.getHeader.getTableName, rowChange.getEventType, rowDatas)

          }
        }
      }else{
        println("没有抓取到数据...., 2s 之后重新抓取")
        Thread.sleep(2000)
      }
    }
  }
}
