package com.longlong.longlongdisplay.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author longlong
 * @create 2020 07 05 3:32
 */
public interface DisplayService {
    public Long getDaySum(String date) throws IOException;
    public Map<String,Long> getHourSum(String date) throws IOException;
    public Double getOrderSum(String date) throws IOException;
    public Map<String,Double> getHourOrderSum(String date) throws IOException;
}
