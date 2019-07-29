package com.z3pipe.z3location.config;

import com.z3pipe.z3core.model.BaseModel;

/**
 * Created with IntelliJ IDEA.
 * Description: ${TODO}
 * Date: 2019-04-13
 * Time: 18:27
 * Copyright © 2018 ZiZhengzhuan. All rights reserved.
 * https://www.z3pipe.com
 *
 * @author zhengzhuanzi
 */
public class PositionCollectionConfig extends BaseModel {
    public static final String KEY_DEVICE = "com.z3pipe.gps.deviceId";
    public static final String KEY_URL = "com.z3pipe.gps.url";
    public static final String KEY_HOST = "com.z3pipe.gps.url.host";
    public static final String KEY_PORT = "com.z3pipe.gps.url.port";
    public static final String KEY_SSL_ENABLE = "com.z3pipe.gps.ssl.enable";
    public static final String KEY_INTERVAL = "com.z3pipe.gps.interval";
    public static final String KEY_DISTANCE = "com.z3pipe.gps.distance";
    public static final String KEY_ANGLE = "com.z3pipe.gps.angle";
    public static final String KEY_ACCURACY = "com.z3pipe.gps.accuracy";
    public static final String KEY_STATUS = "com.z3pipe.gps.status";
    public static final String KEY_USERID = "com.z3pipe.gps.userId";
    public static final String KEY_USERNAME = "com.z3pipe.gps.userName";
    public static final String KEY_LOCATION_PROVIDER = "com.z3pipe.gps.locationprovider";
    /**
     * 设备编号
     */
    private String deviceId;
    /**
     * 采集间隔，单位为秒
     */
    private long interval;
    /**
     * 采集距离，单位为米
     */
    private double distance;
    /**
     * 采集角度，单位为度
     */
    private double angle;

    /**
     * 是否启用SSL
     */
    private boolean useSSL;
    /**
     * 主机地址
     */
    private String host;
    /**
     * 端口
     */
    private int port;

    public PositionCollectionConfig() {
        this("", 15, 10, 10, false, "", 80);
    }

    public PositionCollectionConfig(String deviceId, long interval, double distance, double angle, boolean useSSL, String host, int port) {
        this.deviceId = deviceId;
        this.interval = interval;
        this.distance = distance;
        this.angle = angle;
        this.useSSL = useSSL;
        this.host = host;
        this.port = port;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public boolean isUseSSL() {
        return useSSL;
    }

    public void setUseSSL(boolean useSSL) {
        this.useSSL = useSSL;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
