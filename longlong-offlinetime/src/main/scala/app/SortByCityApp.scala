package app

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}
import utils.mysqlUtil

/**
 * @author longlong
 * @create 2020 07 08 19:01
 */
object SortByCityApp {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("jdbcRDD").setMaster("local")
    val spark: SparkSession = SparkSession.builder().config(conf).getOrCreate()

    val frame: DataFrame = spark.read.json("E:/students.json")

    //将DataFrame转换成一张表
    frame.createOrReplaceTempView("student")

    //采用sql的语法访问数据
    //spark.sql("select * from student where area = 'beijing'").show()
    val dataFrame = spark.sql("select * from student")
    val rdd: RDD[Row] = dataFrame.rdd //转rdd
    val resultRdd = rdd.map(rows => (rows.get(0).toString, rows.get(1).toString, rows.get(2).toString))
    resultRdd.collect.foreach(row => {

      val conn = mysqlUtil.getMysqlConn
      val ps = conn.prepareStatement("insert into `tb_contacts` values (?,?,?)")
        ps.setString(2, row._2)
        ps.setString(3, row._3)
        ps.setInt(1, row._1.toInt)
        ps.executeUpdate()

    })

    //展示数据
    //frame.show()

    //rdd.foreachPartition(insertData)

    //释放资源
    spark.stop

  }
  /*def insertData(iterator: Iterator[Row]): Unit = {

    val conn = mysqlUtil.getMysqlConn
    val ps = conn.prepareStatement("insert into `tb_contacts` values (?,?,?)")
    iterator.foreach(data => {
      ps.setString(2, data)
      ps.setString(3, data._3)
      ps.setInt(1, data._1)
      ps.executeUpdate()
    })
  }*/
}
