package com.z3pipe.z3location.content;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.ecity.android.log.LogUtil;
import com.z3pipe.z3core.config.DateStyle;
import com.z3pipe.z3core.util.DateUtil;
import com.z3pipe.z3core.util.WebCoordinateConverter;
import com.z3pipe.z3location.R;
import com.z3pipe.z3location.config.PositionCollectionConfig;
import com.z3pipe.z3location.model.ELocationQuality;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Description: ${TODO}
 * Date: 2019-04-10
 * Time: 14:09
 * Copyright © 2018 ZiZhengzhuan. All rights reserved.
 * https://www.z3pipe.com
 *
 * @author zhengzhuanzi
 */
public class GaoDePositionProvider extends PositionProvider implements AMapLocationListener {
    private AMapLocationClient client;

    public GaoDePositionProvider(Context context, PositionCollectionConfig positionCollectionConfig, PositionListener listener) {
        super(context, positionCollectionConfig, listener);
    }

    @Override
    public void startUpdates() {
        stop();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        start(false);
    }

    @Override
    public void stopUpdates() {
        stop();
    }

    @Override
    public void requestSingleLocation() {
        stop();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        start(true);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation == null) {
            return;
        }

        if (aMapLocation.getErrorCode() != 0) {
            String errorCode = context.getResources().getString(R.string.str_position_gaode_error_code) + aMapLocation.getErrorCode();
            LogUtil.e("GaoDePositionProvider", errorCode);
            onLocationError(errorCode);
            return;
        }

        Location gpsLocation = new Location("");
        gpsLocation.setTime(aMapLocation.getTime());
        gpsLocation.setProvider(aMapLocation.getLocationType() == AMapLocation.LOCATION_TYPE_GPS ? "GD-GPS" : "GD-NET");
        double[] point = WebCoordinateConverter.google2WGS(aMapLocation.getLongitude(), aMapLocation.getLatitude());
        gpsLocation.setAccuracy(aMapLocation.getAccuracy());
        gpsLocation.setAltitude(aMapLocation.getAltitude());
        gpsLocation.setLatitude(point[1]);
        gpsLocation.setLongitude(point[0]);
//        gpsLocation.setLatitude(aMapLocation.getLatitude());
//        gpsLocation.setLongitude(aMapLocation.getLongitude());
        //获取当前速度 单位：米/秒 仅在AMapLocation.getProvider()是gps时有效
        gpsLocation.setSpeed(aMapLocation.getSpeed());
        processLocation(gpsLocation);
        onLocationQuality(ELocationQuality.getTypeByValue(aMapLocation.getLocationQualityReport().getGPSStatus()));
    }

    private void start(boolean onceLocation) {
        if (null == positionCollectionConfig) {
            positionCollectionConfig = new PositionCollectionConfig();
        }
        client = new AMapLocationClient(context);
        //初始化定位参数
        AMapLocationClientOption option = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        option.setNeedAddress(false);
        //设置是否只定位一次,默认为false
        option.setOnceLocation(onceLocation);
        //是否使用设备传感器
        option.setSensorEnable(true);
        //设置是否强制刷新WIFI，默认为强制刷新
        option.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        option.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        option.setInterval(10000);
        //设置是否优先返回GPS定位信息,默认值：false,只有在高精度定位模式下的单次定位有效
        option.setGpsFirst(true);
        //可选，设置是否使用缓存定位，默认为true
        option.setLocationCacheEnable(false);
        //设置优先返回卫星定位信息时等待卫星定位结果的超时时间
        option.setGpsFirstTimeout(20000);
        //给定位客户端对象设置定位参数
        client.setLocationOption(option);
        // 设置定位监听
        client.setLocationListener(this);
        //启动定位
        client.startLocation();
    }

    private void stop() {
        if (null != client) {
            client.unRegisterLocationListener(this);
            client.stopLocation();
            client.onDestroy();
            client = null;
        }
    }
}
