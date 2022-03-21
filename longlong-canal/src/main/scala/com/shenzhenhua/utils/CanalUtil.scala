package com.shenzhenhua.utils

import java.util

import com.alibaba.fastjson.JSONObject
import com.alibaba.otter.canal.protocol.CanalEntry
import com.alibaba.otter.canal.protocol.CanalEntry.{EventType, RowData}
import common.Constant



/**
 * @author longlong
 * @create 2020 07 06 8:02
 * @Describe: 该类负责解析rowDate数据，从而来得到mysql每行中每个属性值的变化
 */
object CanalUtil {
  /**
   * 处理从 canal 取来的数据
   *
   * @param tableName   表名
   * @param eventType   事件类型
   * @param rowDataList 数据类别
   */
  def handleData(tableName: String, eventType: EventType, rowDataList: util.List[RowData]) = {

    if ("order_info" == tableName && eventType == EventType.INSERT && rowDataList.size() > 0) {

      import scala.collection.JavaConversions._
      // TODO rowDataList值是java类型的，如果遍历需要导入隐式转换

      // 1. rowData 表示一行数据, 通过他得到每一列. 首先遍历每一行数据
      for (rowData <- rowDataList) {

        val result = new JSONObject()  // 把每行数据封装成一个JSON对象

        // 2. 得到每行中, 所有列组成的列表
        val columnList: util.List[CanalEntry.Column] = rowData.getAfterColumnsList
        for (column <- columnList) {
          // 3. 得到列名和列值
          val key = column.getName
          val value = column.getValue
          result.put(key, value)
        }

        // TODO 发送一行解析完的数据到kafka
        kafkaUtil.sendData(Constant.CANAL_DEMO, result.toJSONString)

      }
    }
  }
}
