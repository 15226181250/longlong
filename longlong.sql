#!/bin/bash

for police in $@
do
case $police in
"zookeeper")
echo "############## Start Zookeeper ##############"
for host in hd001 hd002 hd003
do
echo "${host}:starting..."
ssh $host "/home/longlong/cluster/zookeeper/zookeeper-3.4.5-cdh5.9.0/bin/zkServer.sh start"
done
for host in hd001 hd002 hd003
do
ssh $host "/home/longlong/cluster/zookeeper/zookeeper-3.4.5-cdh5.9.0/bin/zkServer.sh status"
done
;;
"hdfs")
echo "########### Start HDFS in hd001 ###########"
ssh hd001 "/home/longlong/cluster/hadoop/hadoop-2.6.0-cdh5.9.0/sbin/start-dfs.sh"
;;
"yarn")
echo "########### Start YARN in hd001 ###########"
ssh hd001 "/home/longlong/cluster/hadoop/hadoop-2.6.0-cdh5.9.0/sbin/start-yarn.sh"
ssh hd002 "/home/longlong/cluster/hadoop/hadoop-2.6.0-cdh5.9.0/sbin/yarn-daemon.sh start resourcemanager"
;;

"kafka")
echo "############ Start Kafka ##############"
done
;;
"hbase")
echo "############## Start HBase ###############"
ssh hd002 "/home/longlong/cluster/hbase/hbase-1.2.0-cdh5.9.0/bin/start-hbase.sh"
;;

"elasticsearch")
echo "############## Start Elasticsearch ###############"
ssh hd003 "/home/longlong/cluster/elasticsearch/bin/elasticsearch 2>&1 &"
ssh hd004 "/home/longlong/cluster/elasticsearch/bin/elasticsearch 2>&1 &"
nohup /home/longlong/cluster/kibana/bin/kibana 2>&1 &
;;




"elasticsearch")
echo "############## Stop Elasticsearch ###############"
ps -ef|grep /home/longlong/cluster/kibana |grep -v grep|awk /home/longlong/cluster/kibana |xargs kill
ssh hd004 "ps -ef|grep /home/longlong/cluster/elasticsearch |grep -v grep|awk '{print \$2}'|xargs kill" >/dev/null 2>&1
ssh hd003 "ps -ef|grep /home/longlong/cluster/elasticsearch |grep -v grep|awk '{print \$2}'|xargs kill" >/dev/null 2>&1
;;

