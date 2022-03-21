package com.shenzhenhua.longlonglogserver.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import common.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @author longlong
 * @create 2020 07 03 0:02
 * @Describe: 该类主要提供通日志的处理
 */

//@Controller
@RestController   // 等价于: @Controller + @ResponseBody
public class LogserverController {

    //    @ResponseBody  //表示返回值是一个 字符串, 而不是 页面名
    //get: http://localhost:8080/log?one_log=hello
    //post: http://localhost:8080/log   参数在请求体中

    @PostMapping("/log")// 等价于: @RequestMapping(value = "/log", method = RequestMethod.POST)
    public String logger(@RequestParam("log") String log) {
        /**
         * 业务:
         *
         * 1. 给日志添加时间戳 (客户端的时间有可能不准, 所以使用服务器端的时间)
         *
         * 2. 日志落盘 (给离线部分使用，比如flume去采集落盘的数据)
         *
         * 3. 日志发送 kafka
         */
        //1
        log = addTS(log);
        //2
        saveLog(log);
        //3
        sendToKafka(log);

        return "ok";
    }

    /**
     * 添加时间戳
     *
     * @param log
     * @return
     */
    public String addTS(String log) {
        //转成JSON对象，方便添加k,v类型的时间戳信息
        JSONObject tempLog = JSON.parseObject(log);
        tempLog.put("ts", System.currentTimeMillis());
        return tempLog.toJSONString();
    }

    private final Logger logger = LoggerFactory.getLogger(LogserverController.class);

    /**
     * 日志落盘
     * 使用 log4j
     *
     * @param log
     */
    public void saveLog(String log) {
        logger.info(log);
    }

    /**
     * 发送日志到 kafka
     *
     * @param logObj
     */
    // 使用注入的方式来实例化 KafkaTemplate. Spring boot 会自动完成
    @Autowired
    KafkaTemplate kafkaTemplate;

    private void sendToKafka(String log) {

        String topicName = Constant.TOPIC_STARTUP;

        if (log.contains("event")) {
            topicName = Constant.TOPIC_EVENT;
        }
        kafkaTemplate.send(topicName, log);
    }


}
