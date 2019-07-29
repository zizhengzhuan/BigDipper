package com.z3pipe.z3core.util;

/**
 * @author: hancuiyan  2015-6-2
 */
public class WGS2Google {
    private static final double pi = 3.14159265358979324;
    private static final double a = 6378245.0;
    private static final double ee = 0.00669342162296594323;

    /**
     * 将 GPS 坐标转为墨卡托坐标
     *
     * @param lon
     * @param lat
     * @return
     */
    public static double[] gpsLonLat2Mercator(double lon, double lat) {
        double[] lonLat = gps2GoogleWGS84(lon, lat);

        lonLat = googleLonLat2Mercator(lonLat[0], lonLat[1]);

        return lonLat;
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
        if (outOfChina(wgLat, wgLon)) {
            latlng[0] = wgLon;
            latlng[1] = wgLat;
            return latlng;
        }
        double dLat = transformLat(wgLon - 105.0, wgLat - 35.0);
        double dLon = transformLon(wgLon - 105.0, wgLat - 35.0);
        double radLat = wgLat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);

        latlng[0] = wgLon + dLon;
        latlng[1] = wgLat + dLat;
        return latlng;
    }


    /***
     * Google WGS84经纬度转WGS84 web mercator投影
     * @param lon
     * @param lat
     * @return
     */
    public static double[] googleLonLat2Mercator(double lon, double lat) {
        double pi = 3.14159265358979324;

        double x = lon * 20037508.342789 / 180;//lon * pi * 6378137

        double y = Math.log(Math.tan((90 + lat) * pi / 360)) / (pi / 180);

        y = y * 20037508.342789 / 180;
        return new double[]{x, y};
    }

    /***
     * web mercator 转为 Google wgs84 经纬度
     * @param mercatorX
     * @param mercatorY
     * @return
     */
    public static double[] mercator2LonLat(double mercatorX, double mercatorY) {
        double pi = 3.14159265358979324;
        double x = mercatorX / 20037508.342789 * 180;
        double y = mercatorY / 20037508.342789 * 180;

        y = 180 / pi * (2 * Math.atan(Math.exp(y * pi / 180)) - pi / 2);


        return new double[]{x, y};
    }

    private static boolean outOfChina(double lat, double lon) {
        if (lon < 72.004 || lon > 137.8347) {
            return true;
        }
        if (lat < 0.8293 || lat > 55.8271) {
            return true;
        }
        return false;
    }

    private static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0 * pi)) * 2.0 / 3.0;
        return ret;
    }
}
