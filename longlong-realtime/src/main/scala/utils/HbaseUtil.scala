package utils

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.{Connection, ConnectionFactory}

/**
 * @author longlong
 * @create 2020 07 04 19:32
 * @Describe: 该类主要提供hbase的连接
 */
object HbaseUtil {

  val servers = PropertiesUtil.getProperty("hbase.servers")
  val conf: Configuration = HBaseConfiguration.create()
  conf.set("hbase.zookeeper.quorum", servers)

  def getConnection(): Connection = {
    ConnectionFactory.createConnection(conf)
  }

}
