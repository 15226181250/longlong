package utils

import redis.clients.jedis.Jedis

/**
 * @author longlong
 * @create 2020 07 04 16:58
 * @Describe: 该类用于提供操作redis的工具jedis
 */
object RedisUtil {

  val host = PropertiesUtil.getProperty("redis.host")
  val port = PropertiesUtil.getProperty("redis.port").toInt
  //Jedis是用来操作redis的API
  def getRedis = {
    val jedis = new Jedis(host, port, 60*1000)
    jedis.connect()
    jedis
  }
}
