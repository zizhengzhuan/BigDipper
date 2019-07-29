package com.z3pipe.z3location.model;

/**
 * Created with IntelliJ IDEA.
 * Description: ${TODO}
 * Date: 2019-04-15
 * Time: 09:40
 * Copyright © 2018 ZiZhengzhuan. All rights reserved.
 * https://www.z3pipe.com
 *
 * @author zhengzhuanzi
 */
public enum ELocationQuality {
    /**
     * GPS GPS状态正常
     */
    GPS_STATUS_OK(0, "GPS状态正常"),
    /**
     * GPS NOGPSPROVIDER
     */
    GPS_STATUS_NOGPSPROVIDER(1, "手机中没有GPS Provider，无法进行GPS定位"),
    /**
     * GPS OFF
     */
    GPS_STATUS_OFF(2, "GPS关闭，建议开启GPS，提高定位质量"),
    /**
     * GPS SAVING
     */
    GPS_STATUS_MODE_SAVING(3, "选择的定位模式中不包含GPS定位，建议选择包含GPS定位的模式，提高定位质量"),
    /**
     * GPS NOGPSPERMISSION
     */
    GPS_STATUS_NOGPSPERMISSION(4, "没有GPS定位权限，建议开启gps定位权限");

    private final int value;
    private final String name;

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    ELocationQuality(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static ELocationQuality getTypeByValue(int type) {
        for (ELocationQuality e : values()) {
            if (e.getValue() == type) {
                return e;
            }
        }
        return null;
    }
}
