package com.longlong.longlongdisplay.service;


import com.longlong.longlongdisplay.dao.DisplayDao;
import com.longlong.longlongdisplay.dao.DisplayDaoImpl;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * @author longlong
 * @create 2020 07 05 3:33
 */
@Service
public class DisplayServiceImpl implements DisplayService {


    DisplayDao publishDao = new DisplayDaoImpl();
    @Override
    public Long getDaySum(String date) throws IOException {
        return publishDao.getDayTotal(date);
    }

    @Override
    public Map<String,Long> getHourSum(String date) throws IOException {
        Map<String,Long> tempMap = new TreeMap<>();
        String[] tempArr = new String[]{"00","01","02","03","04","05","06", "07",
                "08","09","10","11","12","13", "14","15",
                "16","17","18","19","20","21","22","23"};
        for (String str : tempArr) {
            Long tempTotle = publishDao.getHourTotal(date, str);
            if (tempTotle > 0L){ // 总日活人数绝对大于等于0
                tempMap.put(str, tempTotle);
            }
        }
        return tempMap;
    }

    @Override
    public Double getOrderSum(String date) throws IOException {
        return publishDao.getOrderTotal(date);
    }

    @Override
    public Map<String, Double> getHourOrderSum(String date) throws IOException {
        Map<String,Double> tempMap = new TreeMap<>();
        String[] tempArr = new String[]{"00","01","02","03","04","05","06", "07",
                "08","09","10","11","12","13", "14","15",
                "16","17","18","19","20","21","22","23"};
        for (String str : tempArr) {
            Double tempTotle = publishDao.getHourOrderTotal(date, str);
            if (tempTotle > 0.0){  // 交易额绝对大于等于0
                tempMap.put(str, tempTotle);
            }
        }
        return tempMap;
    }
}
