package com.z3pipe.z3core.util;

import android.os.SystemClock;

import com.z3pipe.z3core.config.DateStyle;

import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.TimeZone;

/**
 * 同步网络时间
 *
 * @author zhengzhuanzi
 */
public class WebTimeTask {

    private static WebTimeTask instance;
    /**
     * 时间起始 2019-01-01 00:00:00，当前时间出现小于这个值的时间直接无效
     */
    private final static long TIME_START_POINT = 1546272000;
    private long serverTimeOffset;
    private volatile boolean serverTimeInit = false;
    /**
     * 登录时的CPU时戳
     */
    private volatile long loginSystemClockTimestamp;
    /**
     * 登录服务器的时间偏移值
     */
    private volatile long loginServiceTimeOffset;
    /**
     * 服务器时间是否初始化
     */
    private volatile boolean loginServiceTimeInit = false;

    private Date dateTime;

    public static WebTimeTask getInstance() {
        if (null == instance) {
            instance = new WebTimeTask();
        }
        return instance;
    }

    /**
     * 服务器时间是否已经同步
     *
     * @return
     */
    public boolean isServerTimeInit() {
        return serverTimeInit;
    }

    private WebTimeTask() {
        requestTime();
    }

    public void requestTime() {
        if (isServerTimeInit()) {
            return;
        }
        NetWorkRunnable runnable = new NetWorkRunnable();
        Thread thread = new Thread(runnable);
        thread.start();
    }

    /**
     * 进行网络请求
     */
    private class NetWorkRunnable implements Runnable {
        public NetWorkRunnable() {
        }

        @Override
        public void run() {
            try {
                // 时区设置
                TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
                //取得资源对象
                URL url = new URL("http://www.baidu.com");
                //生成连接对象
                URLConnection uc = url.openConnection();
                long start = SystemClock.elapsedRealtime();
                //发出连接
                uc.connect();
                long offset = SystemClock.elapsedRealtime() - start;
                //取得网站日期时间（时间戳）
                long serverTimeStamp = uc.getDate();
                serverTimeStamp = serverTimeStamp + offset / 2;
                serverTimeOffset = serverTimeStamp - SystemClock.elapsedRealtime();
                if (serverTimeStamp < TIME_START_POINT) {
                    serverTimeInit = false;
                } else {
                    serverTimeInit = true;
                }
            } catch (Exception e) {
                serverTimeInit = false;
            }
        }
    }

    /**
     * 开始登录，在登录口调用
     */
    public void startLogin() {
        loginSystemClockTimestamp = SystemClock.elapsedRealtime();
        loginServiceTimeInit = false;
    }

    /**
     * 设置登录服务器返回的时间戳
     *
     * @param timestamp 登录服务器返回的时间戳
     */
    public void setLoginServiceTimestamp(long timestamp) {
        serverTimeInit = false;
        requestTime();
        if (timestamp < TIME_START_POINT) {
            return;
        }

        long localOffset = SystemClock.elapsedRealtime() - loginSystemClockTimestamp;
        timestamp = timestamp + localOffset / 2;
        this.loginServiceTimeOffset = timestamp - SystemClock.elapsedRealtime();
        loginServiceTimeInit = true;
    }

    /***
     * 获得网络时间
     */
    public long getWebTime() throws IllegalStateException {
        if (!isServerTimeInit()) {
            requestTime();
            if (!loginServiceTimeInit) {
                throw new IllegalStateException("Web time has not init");
            }

            return getLoginServiceTime();
        }

        return SystemClock.elapsedRealtime() + serverTimeOffset;
    }

    public Date getWebDate() {
        try {
            if (null == dateTime) {
                dateTime = new Date(getWebTime());
            } else {
                dateTime.setTime(getWebTime());
            }
        } catch (Exception e) {
            dateTime = new Date();
        }

        return dateTime;
    }

    private long getLoginServiceTime() {
        if (!loginServiceTimeInit) {
            return 0;
        }
        return SystemClock.elapsedRealtime() + loginServiceTimeOffset;
    }

    /**
     * 获取网络具体时间 字符串
     *
     * @return
     */
    public String getWebTimeString() {
        return DateUtil.dateToString(getWebDate(), DateStyle.YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 获取网络年月日 字符串
     *
     * @return
     */
    public String getWebDateString() {
        return DateUtil.dateToString(getWebDate(), DateStyle.YYYY_MM_DD);
    }
}
