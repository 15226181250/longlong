package com.longlong.longlongdisplay.dao;

import java.io.IOException;

/**
 * @author longlong
 * @create 2020 07 05 1:29
 */
public interface DisplayDao {
    // 按天查新增日活跃总数
    public Long getDayTotal(String date) throws IOException;
    // 按小时查活跃数
    public Long getHourTotal(String date, String hour) throws IOException;

    // 按天查新增交易额
    public Double getOrderTotal(String date) throws IOException;
    //按小时查新增交易额
    public Double getHourOrderTotal(String date, String hour) throws IOException;




}
