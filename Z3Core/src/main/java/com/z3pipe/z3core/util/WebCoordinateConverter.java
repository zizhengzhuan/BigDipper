package com.z3pipe.z3core.util;


import com.z3pipe.z3core.model.LonLat;

/**
 * Created with IntelliJ IDEA.
 * Description: Web坐标 转换
 * @author zhengzhuanzi
 * Date: 2018-07-10
 * Time: 下午5:59
 * Copyright © 2018 ZiZhengzhuan. All rights reserved.
 * https://www.z3pipe.com
 */
public class WebCoordinateConverter {
    private static final double a = 6378245.0;
    private static final double ee = 0.00669342162296594323;
    private static final double x_PI = Math.PI * 3000.0 / 180.0;
    private static final double PI = 3.14159265358979324;
    private static final double MIN_LON = 72.004;
    private static final double MIN_LAT = 0.8293;
    private static final double MAX_LON = 137.8347;
    private static final double MAX_LAT = 55.8271;
    /**
     * 百度坐标系 (BD-09) 与 火星坐标系 (GCJ-02)的转换
     * 即 百度 转 谷歌、高德
     *
     * @returns {*[]}
     */
    public static LonLat bd09ToGCJ02(LonLat bd09Lonlat) {
        double bd_lon = bd09Lonlat.getLongitude();
        double bd_lat = bd09Lonlat.getLatitude();
        double x = bd_lon - 0.0065;
        double y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_PI);
        double gg_lng = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);

        return new LonLat(gg_lng, gg_lat,bd09Lonlat.getHeight());
    }

    /**
     * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换
     * 即谷歌、高德 转 百度
     *
     * @returns {*[]}
     */
    public static LonLat gcj02ToBD09(LonLat gcj02Lonlat) {
        double lat = gcj02Lonlat.getLatitude();
        double lng = gcj02Lonlat.getLongitude();
        double z = Math.sqrt(lng * lng + lat * lat) + 0.00002 * Math.sin(lat * x_PI);
        double theta = Math.atan2(lat, lng) + 0.000003 * Math.cos(lng * x_PI);
        double bd_lng = z * Math.cos(theta) + 0.0065;
        double bd_lat = z * Math.sin(theta) + 0.006;

        return new LonLat(bd_lng, bd_lat,gcj02Lonlat.getHeight());
    }

    /**
     * WGS84转GCj02
     *
     * @returns {*[]}
     */
    public static LonLat wgs84ToGCJ02(LonLat wgs84Lonlat) {
        double lat = wgs84Lonlat.getLatitude();
        double lng = wgs84Lonlat.getLongitude();
        if (outOfChina(lng, lat)) {
            return wgs84Lonlat;
        } else {
            double dlat = transformLat(lng - 105.0, lat - 35.0);
            double dlng = transformLon(lng - 105.0, lat - 35.0);
            double radlat = lat / 180.0 * PI;
            double magic = Math.sin(radlat);
            magic = 1 - ee * magic * magic;
            double sqrtmagic = Math.sqrt(magic);
            dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * PI);
            dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * PI);
            double mglat = lat + dlat;
            double mglng = lng + dlng;
            return new LonLat(mglng, mglat,wgs84Lonlat.getHeight());
        }
    }


    /**
     * GCJ02 转换为 WGS84
     *
     * @returns {*[]}
     */
    public static LonLat gcj02ToWGS84(LonLat gcj02Lonlat) {
        double[] result = gcj02ToWGS84(gcj02Lonlat.getLongitude(),gcj02Lonlat.getLatitude());
        return new LonLat(result[0], result[1],gcj02Lonlat.getHeight());
    }

    public static double[] gcj02ToWGS84(double lng,double lat) {
        double[] result = {0,0};
        if (outOfChina(lng, lat)) {
            return result;
        } else {
            double dlat = transformLat(lng - 105.0, lat - 35.0);
            double dlng = transformLon(lng - 105.0, lat - 35.0);
            double radlat = lat / 180.0 * PI;
            double magic = Math.sin(radlat);
            magic = 1 - ee * magic * magic;
            double sqrtmagic = Math.sqrt(magic);
            dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * PI);
            dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * PI);
            double mglat = lat + dlat;
            double mglng = lng + dlng;
            result[0] = lng * 2 - mglng;
            result[1] = lat * 2 - mglat;
            return result;
        }
    }
    /**
     * GPS 坐标转为 Google WGS84
     *
     * @param wgLon
     * @param wgLat
     * @return
     */
    public static double[] gps2GoogleWGS84(double wgLon, double wgLat) {
        double[] latlng = new double[2];
        if (outOfChina(wgLon,wgLat)) {
            latlng[0] = wgLon;
            latlng[1] = wgLat;
            return latlng;
        }
        double dLat = transformLat(wgLon - 105.0, wgLat - 35.0);
        double dLon = transformLon(wgLon - 105.0, wgLat - 35.0);
        double radLat = wgLat / 180.0 * PI;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * PI);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * PI);

        latlng[0] = wgLon + dLon;
        latlng[1] = wgLat + dLat;
        return latlng;
    }

    /**
     * google WGS84 坐标转gps
     *
     * @return
     * @throws Exception
     */
    public static double[] google2WGS(double lon, double lat) {
        //反转到偏移点
        double[] tempArr = gps2GoogleWGS84(lon, lat);
        double offsetX = tempArr[0] - lon;
        double offsetY = tempArr[1] - lat;

        double offsetX1 = lon - offsetX;
        double offsetY1 = lat - offsetY;

        //根据偏移点求偏移距离
        tempArr = gps2GoogleWGS84(offsetX1, offsetY1);
        offsetX = tempArr[0] - offsetX1;
        offsetY = tempArr[1] - offsetY1;
        double[] retPoint = new double[2];
        retPoint[0] = lon - offsetX;
        retPoint[1] = lat - offsetY;
        return retPoint;
    }

    private static double transformLat(double pLng, double pLat) {
        double lat = pLat;
        double lng = pLng;
        double ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lat * PI) + 40.0 * Math.sin(lat / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(lat / 12.0 * PI) + 320 * Math.sin(lat * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLon(double pLng, double pLat) {
        double lat = pLat;
        double lng = pLng;
        double ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lng * PI) + 40.0 * Math.sin(lng / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(lng / 12.0 * PI) + 300.0 * Math.sin(lng / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }

    /**
     * 经纬度坐标是否超出中国范围
     * @param lon
     * @param lat
     * @return
     */
    public static final boolean outOfChina(double lon, double lat) {
        if (lon < MIN_LON || lon > MAX_LON) {
            return true;
        }
        return lat < MIN_LAT || lat > MAX_LAT;
    }
}
