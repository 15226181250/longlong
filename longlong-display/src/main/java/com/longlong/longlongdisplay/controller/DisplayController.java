package com.longlong.longlongdisplay.controller;

import com.alibaba.fastjson.JSON;
import com.longlong.longlongdisplay.service.DisplayService;
import com.longlong.longlongdisplay.service.DisplayServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author longlong
 * @create 2020 07 05 5:07
 */
@RestController
public class DisplayController {


    public DisplayService publishService = new DisplayServiceImpl();

    @GetMapping("/date")
    public String RealDateTotle(@RequestParam("date") String date) throws IOException {

        List<Map> totalList = new ArrayList<>();

        Map dauMap = new HashMap<>();
        dauMap.put("id", "dau");
        dauMap.put("name", "新增日活");
        dauMap.put("value", publishService.getDaySum(date));
        totalList.add(dauMap);

        Map midMap = new HashMap<>();
        midMap.put("id", "new_mid");
        midMap.put("name", "新增设备");
        midMap.put("value", "1000");
        totalList.add(midMap);

        Map orderMap = new HashMap<>();
        orderMap.put("id", "order_amount");
        orderMap.put("name", "新增交易额");
        orderMap.put("value", publishService.getOrderSum(date));
        totalList.add(orderMap);

        return JSON.toJSONString(totalList);

    }
    @GetMapping("/hour")
    public String RealHourTotle(@RequestParam("id") String id, @RequestParam("date") String date) throws IOException {

        if ("dua".equals(id)){

            Map<String,Long> today = publishService.getHourSum(date);
            Map<String,Long> yesterday = publishService.getHourSum(getYesterday(date));

            Map<String, Map<String, Long>> result = new HashMap<>();
            result.put("today", today);
            result.put("yesterday", yesterday);

            return JSON.toJSONString(result);
        }else if ("order_amount".equals(id)){

            Map<String,Double> today = publishService.getHourOrderSum(date);
            Map<String,Double> yesterday = publishService.getHourOrderSum(getYesterday(date));

            Map<String, Map<String, Double>> result = new HashMap<>();
            result.put("today", today);
            result.put("yesterday", yesterday);

            return JSON.toJSONString(result);
        }
        return null;
    }

    private String getYesterday(String today){
        return LocalDate.parse(today).plusDays(-1).toString();
        //return LocalDate.parse(today).minusDays(1).toString();
    }

}
