package com.z3pipe.z3location.content;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;


import com.z3pipe.z3location.broadcast.GpsLocationListener;

import java.util.List;

public class GpsPosHelper {

    private static GpsPosHelper m_instance = null;

    private LocationManager locationManager;

    /**
     * 是否正在使用网络定位
     */
    private boolean networkUsing = false;

    /**
     * 是否正在使用GPS
     */
    private boolean gpsUsing = false;

    private boolean init = false;


    private boolean getPosSuccess = false;



   private GpsLocationListener gpsLocationListener;


    /**
     * private访问，不允许外部new出实例
     */
    private GpsPosHelper() {
        // TODO Auto-generated constructor stub
    }

    /**
     * 获取定位实例
     *
     * @return
     */
    public static GpsPosHelper getInstance() {
        if (m_instance == null) {
            m_instance = new GpsPosHelper();
        }
        return m_instance;
    }

    //public void  setGetPosListener(GpsPosListener listener){
//        this.listener = listener;
//    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {

        if (init) {
            return;
        }


        this.getPosSuccess = false;
        initPositionSensor(context);
        init = true;
    }

    /**
     * 判断是否已经经过了初始化
     *
     * @return
     */
    public boolean isInited() {
        return init;
    }

//    public void pause(long timeminus) {
//        pausedTime = timeminus;
//        long currentTime = System.currentTimeMillis();
//        lastSendTime = currentTime;
//    }

    /**
     * 退出地图，注销定位监听器。
     */
    public void stopLocation() {

        gpsLocationListener = null;
        stopGpsLocation();
        stopNetworkLocation();

//        if (timer != null) {
//            try {
//                timer.cancel();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            timer = null;
//        }
        init = false;

    }

    /**
     * 开始监听位置。
     * 该时间之后依然未收到坐标，则认为失败。
     */
    public void startLocation(GpsLocationListener listener) {

        gpsLocationListener = listener;
        this.getPosSuccess = false;
        startGpsLocation();
        startNetworkLocation();

//        if (timeDelay > 0) {
//            if (timer != null) {
//                return;
//            }
//
//
//            timer = new Timer();
//            TimerTask timerTask = new TimerTask() {
//                @Override
//                public void run() {
//
//                    if (!getPosSuccess) {
//                        PositionManager.getInstance().positionError("获取坐标失败");
//                    }
//
//                }
//            };
//            timer.schedule(timerTask, timeDelay);
//        }

    }

    /**
     * 初始化定位
     *
     * @param context
     */
    private void initPositionSensor(Context context) {
        if (locationManager != null) {
            locationManager = null;
        }
        if (context != null) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

    }


    /**
     * 启动网络定位
     *
     * @return
     */
    private boolean startNetworkLocation() {
        if (getNetworkUseful()) {
            if (!networkUsing) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, networkListener);
            }
            networkUsing = true;
            return true;
        }
        return false;
    }

    /**
     * 启动GPS定位
     *
     * @return
     */
    private boolean startGpsLocation() {
        if (getGpsUseful()) {
            if (!gpsUsing) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, gpsListener);
            }
            gpsUsing = true;
            return true;
        }
        return false;
    }

    private void stopNetworkLocation() {
        if (networkUsing) {

            locationManager.removeUpdates(networkListener);
            networkUsing = false;
        }
    }

    private void stopGpsLocation() {
        if (gpsUsing) {
            locationManager.removeUpdates(gpsListener);
            gpsUsing = false;
        }
    }

    private LocationListener gpsListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {


        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub


        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
            if (location != null) {
                //ToastUtil.showShort("getLocationWithGPS:"+location.getLongitude()+","+location.getLatitude());
                processNewLocation(location);
            }
        }
    };


    private LocationListener networkListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {


        }

        @Override
        public void onProviderEnabled(String provider) {


        }

        @Override
        public void onProviderDisabled(String provider) {


        }

        @Override
        public void onLocationChanged(Location location) {

            if (location != null) {
                //ToastUtil.showShort("getLocationWithNetWork:" + location.getLongitude() + "," + location.getLatitude());
                processNewLocation(location);
            }
        }
    };

    public List<String> getUsefulLocationList() {
        if (locationManager == null) {
            return null;
        }
        //List<String> accessibleProviders = locationManager.getProviders(true);

        return locationManager.getProviders(true);
    }

    public boolean getGpsUseful() {
        List<String> accessibleProviders = getUsefulLocationList();
        if (accessibleProviders == null) {
            //没有
            return false;
        }
        if (accessibleProviders.contains(LocationManager.GPS_PROVIDER)) {
            return true;
        }

        return false;
    }

    public boolean getNetworkUseful() {
        List<String> accessibleProviders = getUsefulLocationList();
        if (accessibleProviders == null) {
            //没有
            return false;
        }
        if (accessibleProviders.contains(LocationManager.NETWORK_PROVIDER)) {
            return true;
        }

        return false;
    }


    /**
     * 处理定位的坐标
     *
     * @param location
     * @return
     */
    private void processNewLocation(Location location) {

        if (location == null) {
            return;
        }
        //pro
        getPosSuccess = true;

        if (gpsLocationListener != null){
            gpsLocationListener.onLocationChanged(location);
        }

//
//        long currentTime = System.currentTimeMillis();
//
//        if (lastSendTime == 0) {
//            lastSendTime = currentTime;
//        } else if (currentTime - lastSendTime > pausedTime) {
//
//            lastSendTime = currentTime;
//            pausedTime = 0;
//
//
//            if (location != null){
//
//                double accuracy = location.getAccuracy();
//
//                if (accuracy < 0) {
//                    accuracy = -accuracy;
//                }
//                double sqrtAccur = Math.sqrt(accuracy);
//
//                PositionManager.getInstance().notifyGpsPositionReady();
//
//                PositionManager.getInstance().newPositionArrive(new ECityPosition(location, ECityPosProducer.GPS),new PositionAccur(sqrtAccur, sqrtAccur, accuracy));
//            }

//            if (listener != null) {
//                listener.LocationReceived(new ECityPosition(location, ECityPosProducer.GPS));
//                double accuracy = location.getAccuracy();
//
//                if (accuracy < 0) {
//                    accuracy = -accuracy;
//                }
//
//                double sqrtAccur = Math.sqrt(accuracy);
//
//                listener.PositionAccurChanged(new PositionAccur(sqrtAccur, sqrtAccur, accuracy));
//            }
//        }
    }

//    private PositionChangeListener listener = new PositionChangeListener() {
//
//        @Override
//        public void LocationReceived(ECityPosition position) {
//
//            SimPosHelper.getInstance().stopPosReceive();
//            if (SessionManager.lastLocation == null) {
//                SessionManager.lastLocation = position;
//            }
//            BlueToothEvent event = new BlueToothEvent();
//            event.type = BlueToothEvent.BtEventType.POSITION;
//            event.data = position;
//            EventBusUtil.post(event);
//        }
//
//        @Override
//        public void PositionAccurChanged(PositionAccur accur) {
//            BlueToothEvent event = new BlueToothEvent();
//            event.type = BlueToothEvent.BtEventType.PRECISION;
//            event.data = accur;
//            EventBusUtil.post(event);
//        }
//
//        @Override
//        public void PositionErrord(String errorMsg) {
//
//            BlueToothEvent event = new BlueToothEvent();
//            event.type = BlueToothEvent.BtEventType.POS_ERROR;
//            event.data = errorMsg;
//            EventBusUtil.post(event);
//
//        }
//    };

}
