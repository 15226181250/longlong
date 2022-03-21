package com.longlong.longlongdisplay.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.IOException;

/**
 * @author longlong
 * @create 2020 07 05 1:18
 * @Describe: 该类主要提供连接hbase
 */
public class HbaseUtil {

    private static Configuration conf = HBaseConfiguration.create();
    public static Connection getConnection() throws IOException {

        Connection conn = ConnectionFactory.createConnection(conf);
        return conn;
    }
}
