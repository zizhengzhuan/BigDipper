package com.z3pipe.z3location.model;

import android.location.Location;

import com.alibaba.fastjson.annotation.JSONField;
import com.z3pipe.z3core.config.DateStyle;
import com.z3pipe.z3core.model.BaseModel;
import com.z3pipe.z3core.util.DateUtil;
import com.z3pipe.z3core.util.WGS2Google;

import java.util.Date;

/**
 * @author zhengzhuanzi
 * @link https://www.z3pipe.com
 * @date 2019-04-10
 */
public class Position extends BaseModel {

    private long id;
    @JSONField(serialize=false)
    private double x;
    @JSONField(serialize=false)
    private double y;
    private double lon;
    private double lat;
    private double accuracy;
    private long time;
    private String gpsTime;
    private int state;
    private String userId;
    private double speed;
    private double course;
    private String battery;
    private String deviceId;
    private String userName;
    private String trueName;

    public Position() {
    }

    public Position(String userName, String trueName, String userId, Location location, double battery) {
        //this.deviceId = deviceId;
        this.userName = userName;
        this.trueName = trueName;
        this.userId = userId;
        this.battery = battery + "%";
        time = location.getTime();
        lat = location.getLatitude();
        lon = location.getLongitude();
        speed = location.getSpeed();
        accuracy = location.getAccuracy();
        double[] xy = WGS2Google.gpsLonLat2Mercator(lon, lat);
        if (xy != null && xy.length == 2) {
            x = xy[0];
            y = xy[1];
        }
        try {
            this.gpsTime = DateUtil.dateToString(new Date(time), DateStyle.YYYY_MM_DD_HH_MM_SS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getGpsTime() {
        return gpsTime;
    }

    public void setGpsTime(String gpsTime) {
        this.gpsTime = gpsTime;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
        try {
            this.gpsTime = DateUtil.dateToString(new Date(time), DateStyle.YYYY_MM_DD_HH_MM_SS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTrueName() {
        return trueName;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }

    public double getCourse() {
        return course;
    }

    public void setCourse(double course) {
        this.course = course;
    }

    @Override
    public String toString() {
        return "Position{" + "id=" + id + ", x=" + x + ", y=" + y + ", lon=" + lon + ", lat=" + lat + ", accuracy=" + accuracy + ", time=" + time + ", gpsTime='" + gpsTime + '\'' + ", state=" + state + ", userId='" + userId + '\'' + ", speed=" + speed + ", course=" + course + ", battery='" + battery + '\'' + ", deviceId='" + deviceId + '\'' + ", userName='" + userName + '\'' + ", trueName='" + trueName + '\'' + '}';
    }
}
