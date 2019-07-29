package com.z3pipe.z3core.model;

/**
 * Created with IntelliJ IDEA.
 * Description: 经纬度坐标{longitude,latitude.height}
 * @author zhengzhuanzi
 * Date: 2018-07-10
 * Time: 下午2:01
 * Copyright © 2018 ZiZhengzhuan. All rights reserved.
 * https://www.z3pipe.com
 */
public class LonLat extends BaseModel {
    private double longitude;
    private double latitude;
    private double height;

    public LonLat(double longitude, double latitude) {
        this(longitude,latitude,0);
    }

    public LonLat(double longitude, double latitude, double height) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.height = height;
    }

    /**
     * 经度 单位：度或者弧度
     */
    public double getLongitude() {
        return longitude;
    }
    /**
     * 经度 单位：度或者弧度
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    /**
     * 纬度 单位：度或者弧度
     */
    public double getLatitude() {
        return latitude;
    }
    /**
     * 纬度 单位：度或者弧度
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    /**
     * 高度 单位：米
     */
    public double getHeight() {
        return height;
    }
    /**
     * 高度 单位：米
     */
    public void setHeight(double height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "{\"longitude\":"+ longitude +",\"latitude\":"+latitude+",\"height\":"+height+"}";
    }
}
