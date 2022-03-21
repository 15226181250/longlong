package com.longlong.longlongdisplay.dao;


import com.longlong.longlongdisplay.utils.HbaseUtil;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author longlong
 * @create 2020 07 05 1:32
 */
public class DisplayDaoImpl implements DisplayDao {

    @Override
    public Long getDayTotal(String date) throws IOException {
        Connection conn = HbaseUtil.getConnection();
        Table table = conn.getTable(TableName.valueOf("start_up"));
        // 扫描整张表需要创建扫描器scan
        Scan scan=new Scan();
        // 设置扫描器只扫描logDate这一列
        scan.addColumn(Bytes.toBytes("startup"), Bytes.toBytes("logDate"));
        // 按照filter过滤扫描结果
        Filter filter = new SingleColumnValueFilter(
        Bytes.toBytes("startup"), Bytes.toBytes("logDate"), CompareFilter.CompareOp.EQUAL, Bytes.toBytes(date));
        scan.setFilter(filter);

        ResultScanner rs = table.getScanner(scan);
        Long totleByDay = 0L;
        for (Result r : rs) {
            for (KeyValue kv : r.raw()) {
              //System.out.println(Bytes.toString(kv.getValue()));
            }
            totleByDay += 1;
        }
        table.close();
        conn.close(); // TODO 记得关连接，要zookeeper会断开链接

        return totleByDay;
    }

    @Override
    public Long getHourTotal(String date, String hour) throws IOException {

        Connection conn = HbaseUtil.getConnection();
        Table table = conn.getTable(TableName.valueOf("start_up"));
        // 扫描整张表需要创建扫描器scan
        Scan scan=new Scan();
        // 设置扫描器只扫描logDate,logHour这两列
        scan.addColumn(Bytes.toBytes("startup"), Bytes.toBytes("logDate"));
        scan.addColumn(Bytes.toBytes("startup"), Bytes.toBytes("logHour"));

        FilterList filterList = new FilterList();

        // 按照天过滤
        Filter filterDay = new SingleColumnValueFilter(
                Bytes.toBytes("startup"), Bytes.toBytes("logDate"), CompareFilter.CompareOp.EQUAL, Bytes.toBytes(date));
        // 按照小时过滤
        Filter filterHour = new SingleColumnValueFilter(
                Bytes.toBytes("startup"), Bytes.toBytes("logHour"), CompareFilter.CompareOp.EQUAL, Bytes.toBytes(hour));

        filterList.addFilter(filterDay);
        filterList.addFilter(filterHour);

        scan.setFilter(filterList); //多条件过滤

        ResultScanner rs = table.getScanner(scan);
        long totleByHour = 0L;
        for (Result r : rs) {
            for (KeyValue kv : r.raw()) {
                //System.out.println(Bytes.toString(kv.getValue()));
            }
            totleByHour += 1;
        }
        table.close();
        conn.close(); // TODO 记得关连接，要zookeeper会断开链接

        return totleByHour;

    }

    @Override
    public Double getOrderTotal(String date) throws IOException {
        Connection conn = HbaseUtil.getConnection();
        Table table = conn.getTable(TableName.valueOf("order"));
        // 扫描整张表需要创建扫描器scan
        Scan scan=new Scan();
        // 设置扫描器只扫描logDate这一列
        //scan.addColumn(Bytes.toBytes("order_info"), Bytes.toBytes("total_amount"));
        scan.addColumn(Bytes.toBytes("order_info"), Bytes.toBytes("create_date"));
        // 按照filter过滤扫描结果
        Filter filter = new SingleColumnValueFilter(
                Bytes.toBytes("order_info"), Bytes.toBytes("create_date"), CompareFilter.CompareOp.EQUAL, Bytes.toBytes(date));
        scan.setFilter(filter);

        ResultScanner rs = table.getScanner(scan);
        Double totleByOrder = 0.0;

        List<Get> list = new ArrayList<>();

        for (Result r : rs) {
            for (KeyValue kv : r.raw()) {
                // System.out.println(Bytes.toString(kv.getRow()));
                Get get = new Get(kv.getRow());
                get.addColumn(Bytes.toBytes("order_info"), Bytes.toBytes("total_amount"));
                list.add(get);
            }
        }
        Result[] results = table.get(list);
        for (Result result : results){
            for(Cell cell : result.rawCells()){
                //System.out.println("值:" + Bytes.toDouble(CellUtil.cloneValue(cell)));
                totleByOrder += Bytes.toDouble(CellUtil.cloneValue(cell));
            }
        }
        table.close();
        conn.close(); // TODO 记得关连接，要zookeeper会断开链接
        //System.out.println("#################################");
        return totleByOrder;
    }

    @Override
    public Double getHourOrderTotal(String date, String hour) throws IOException {
        Connection conn = HbaseUtil.getConnection();
        Table table = conn.getTable(TableName.valueOf("order"));
        // 扫描整张表需要创建扫描器scan
        Scan scan=new Scan();
        // 设置扫描器只扫描logDate这一列
        scan.addColumn(Bytes.toBytes("order_info"), Bytes.toBytes("create_date"));

        // 按照filter过滤扫描结果
        Filter filter = new SingleColumnValueFilter(
                Bytes.toBytes("order_info"), Bytes.toBytes("create_date"), CompareFilter.CompareOp.EQUAL, Bytes.toBytes(date));

        scan.setFilter(filter);

        ResultScanner rs = table.getScanner(scan);
        Double totleByOrder = 0.0;

        // list 将来用作取交易额 total_amount
        List<Get> list = new ArrayList<>();
        // list2 将来用作取小时 create_hour
        List<Get> list2 = new ArrayList<>();

        for (Result r : rs) {
            for (KeyValue kv : r.raw()) {
                // System.out.println(Bytes.toString(kv.getRow()));
                Get get = new Get(kv.getRow());
                Get get2 = new Get(kv.getRow());
                get.addColumn(Bytes.toBytes("order_info"), Bytes.toBytes("total_amount"));
                get2.addColumn(Bytes.toBytes("order_info"), Bytes.toBytes("create_hour"));
                list.add(get);
                list2.add(get2);
            }
        }
        // results 放的是所有交易额
        Result[] results = table.get(list);
        // results2 放的是所有小时
        Result[] results2 = table.get(list2);

        // 创建一个list用来存放符合小时要求的RowKey
        List<String> listTemp = new ArrayList();
        for (Result result : results2){
            for(Cell cell : result.rawCells()){
                //System.out.println("值:" + Bytes.toDouble(CellUtil.cloneValue(cell)));
                if( hour.equals(Bytes.toString(CellUtil.cloneValue(cell)))){
                    listTemp.add(Bytes.toString(result.getRow()));
                }
            }
        }
        //System.out.println(listTemp);

        for (Result result : results){
            for(Cell cell : result.rawCells()){
                //System.out.println("值:" + Bytes.toDouble(CellUtil.cloneValue(cell)));
                //System.out.println(Bytes.toString(result.getRow()));
                for (String rowKey : listTemp) {
                    // TODO 将符合RowKey要求的交易额相加
                    if (rowKey.equals(Bytes.toString(result.getRow()))){
                        //System.out.println("_________________________");
                        //System.out.println(Bytes.toDouble(CellUtil.cloneValue(cell)));
                        totleByOrder += Bytes.toDouble(CellUtil.cloneValue(cell));
                        break;
                    }
                }

            }
        }

        table.close();
        conn.close(); // TODO 记得关连接，要zookeeper会断开链接
        //System.out.println("#################################");
        return totleByOrder;
    }

  /*  public static void main(String[] args) throws IOException {
        DisplayDaoImpl a = new DisplayDaoImpl();
        Double s = a.getHourOrderTotal("2020-07-03", "17");
        System.out.println(s);

    }*/
}
