package com.z3pipe.z3core.util;

import com.z3pipe.z3core.model.LonLat;

import java.text.DecimalFormat;

/**
 * @author zhengzhuanzi on 2018/8/17.
 */
public class GeomMathUtil {
    private static final double a = 6378245.0;
    private static final double ee = 0.00669342162296594323;
    private static final double X_PI = Math.PI * 3000.0 / 180.0;
    private static final double PI = Math.PI;
    private static final DecimalFormat DF = new DecimalFormat("#0.00");

    /**
     * 数字比较
     *
     * @param o1
     * @param o2
     * @return
     */
    public static int numCompare(int o1, int o2) {
        return numCompare((double) o1, (double) o2);
    }

    /**
     * 数字比较
     *
     * @param o1
     * @param o2
     * @return
     */
    public static int numCompare(double o1, double o2) {
        if (o1 == o2) {
            return 0;
        }

        if (o1 < o2) {
            return -1;
        }

        return 1;
    }

    /**
     * 计算长度
     *
     * @param startPoint
     * @param endPoint
     * @param forEllipse
     * @return
     */
    public static double calculateLength(LonLat startPoint, LonLat endPoint, boolean forEllipse) {
        return calculateLength(startPoint.getLongitude(), startPoint.getLatitude(), endPoint.getLongitude(), endPoint.getLatitude(), forEllipse, true);
    }

    /**
     * 计算两个点的球面距离
     *
     * @param x1_lon     起点x 或者经度
     * @param y1_lat     起点y 或者纬度
     * @param x2_lon     终点x 或者经度
     * @param y2_lat     终点y 或者纬度
     * @param forEllipse 是否为球面坐标计算
     * @param isLonLat   输入值是否是经纬度格式
     * @return
     */
    public static double calculateLength(double x1_lon, double y1_lat, double x2_lon, double y2_lat, boolean forEllipse, boolean isLonLat) {
        double len = 0.0;

        if (forEllipse) {
            if (!isLonLat) {
                double[] lonlat1 = mercator2lonLat(x1_lon, y1_lat);
                double[] lonlat2 = mercator2lonLat(x2_lon, y2_lat);
                //纬度，经度，纬度，经度
                len = calculateEllipseDistance(lonlat1[1], lonlat1[0], lonlat2[1], lonlat2[0]);
            } else {
                len = calculateEllipseDistance(y1_lat, x1_lon, y2_lat, x2_lon);
            }
        } else {
            if (isLonLat) {
                double[] xy1 = lonLat2Mercator(x1_lon, y1_lat);
                double[] xy2 = lonLat2Mercator(x2_lon, y2_lat);
                len = calculatePlaneDistance(xy1[1], xy1[0], xy2[1], xy2[0]);
            } else {
                len = calculatePlaneDistance(x1_lon, y1_lat, x2_lon, y2_lat);
            }
        }

        return len;
    }

    /**
     * 计算两个点的之间的距离
     *
     * @param x1         起点x
     * @param y1         起点y
     * @param x2         终点x
     * @param y2         终点y
     * @param forEllipse 是否为球面坐标
     */
    public static double distance(double x1, double y1, double x2, double y2, boolean forEllipse) {
        return calculateLength(x1, y1, x2, y2, forEllipse, false);
    }

    /**
     * 小数点保留两位
     *
     * @param length
     * @return
     */
    public static double formatDouble(double length) {
        String format = DF.format(length);
        return Double.parseDouble(format);
    }

    /**
     * 小数点保留两位
     *
     * @param length
     * @return
     */
    public static String formatDoubleString(double length) {
        String format = "" + length;
        try {
            format = DF.format(length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return format;
    }

    /**
     * @param mercatorX
     * @param mercatorY
     * @return
     */
    public static double[] mercator2lonLat(double mercatorX, double mercatorY) {
        double pi = 3.14159265358979324;
        double[] xy = new double[2];
        double x = mercatorX / 20037508.342789 * 180;
        double y = mercatorY / 20037508.342789 * 180;

        y = 180 / pi * (2 * Math.atan(Math.exp(y * pi / 180)) - pi / 2);

        xy[0] = x;
        xy[1] = y;
        return xy;
    }

    /****
     * 经纬度转墨卡托
     * @param lon
     * @param lat
     * @return
     * @throws Exception
     */
    public static double[] lonLat2Mercator(double lon, double lat) {
        double pi = 3.14159265358979324;
        double[] xy = new double[2];
        double x = lon * 20037508.342789 / 180;

        double y = Math.log(Math.tan((90 + lat) * pi / 360)) / (pi / 180);

        y = y * 20037508.34789 / 180;
        xy[0] = x;
        xy[1] = y;
        return xy;
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 球面距离
     *
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    public static double calculateEllipseDistance(double lat1, double lng1, double lat2, double lng2) {
        double theta = lng1 - lng2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        double miles = dist * 60 * 1.1515;
        //英里转成米
        miles = miles * 1.609344 * 1000;

        if (Double.isNaN(miles)) {
            miles = 0.0;
        }

        return miles;
    }

    /**
     * 将角度转换为弧度
     */
    public static double deg2rad(double degree) {
        return degree / 180 * Math.PI;
    }

    /**
     * 将弧度转换为角度
     */
    public static double rad2deg(double radian) {
        return radian * 180 / Math.PI;
    }

    /**
     * 高斯平面距离
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static double calculatePlaneDistance(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * 百度坐标系 (BD-09) 与 火星坐标系 (GCJ-02)的转换
     * 即 百度 转 谷歌、高德
     *
     * @returns {*[]}
     */
    public double[] bd09ToGCJ02(double[] bd09Lonlat) {
        double bd_lon = bd09Lonlat[0];
        double bd_lat = bd09Lonlat[1];
        double x = bd_lon - 0.0065;
        double y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * X_PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * X_PI);
        double gg_lng = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);

        return new double[]{gg_lng, gg_lat};
    }

    /**
     * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换
     * 即谷歌、高德 转 百度
     *
     * @returns {*[]}
     */
    public double[] gcj02ToBD09(double[] gcj02Lonlat) {
        double lat = gcj02Lonlat[1];
        double lng = gcj02Lonlat[0];
        double z = Math.sqrt(lng * lng + lat * lat) + 0.00002 * Math.sin(lat * X_PI);
        double theta = Math.atan2(lat, lng) + 0.000003 * Math.cos(lng * X_PI);
        double bd_lng = z * Math.cos(theta) + 0.0065;
        double bd_lat = z * Math.sin(theta) + 0.006;

        return new double[]{bd_lng, bd_lat};
    }

    /**
     * WGS84转GCj02
     *
     * @returns {*[]}
     */
    public double[] wgs84ToGCJ02(double[] wgs84Lonlat) {
        double lat = wgs84Lonlat[1];
        double lng = wgs84Lonlat[0];

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

        return new double[]{mglng, mglat};
    }


    /**
     * GCJ02 转换为 WGS84
     *
     * @returns {*[]}
     */
    public double[] gcj02ToWGS84(double[] gcj02Lonlat) {
        double lat = gcj02Lonlat[1];
        double lng = gcj02Lonlat[0];

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
        return new double[]{lng * 2 - mglng, lat * 2 - mglat};
    }


    private double transformLat(double plng, double plat) {
        double lat = plat;
        double lng = plng;
        double ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lat * PI) + 40.0 * Math.sin(lat / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(lat / 12.0 * PI) + 320 * Math.sin(lat * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private double transformLon(double plng, double plat) {
        double lat = plat;
        double lng = plng;
        double ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lng * PI) + 40.0 * Math.sin(lng / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(lng / 12.0 * PI) + 300.0 * Math.sin(lng / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }

    /**
     * 判断两个double值是否一样
     *
     * @param a
     * @param b
     * @return
     */
    public static boolean isSameDoubleValue(double a, double b) {
        double dis = 1e-15;
        return Math.abs(a - b) < dis;
    }
}
