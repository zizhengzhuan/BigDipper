package com.z3pipe.z3location.controller;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

import com.z3pipe.z3location.base.PositionProviderFactory;
import com.z3pipe.z3location.broadcast.NetworkBroadcastReceiver;
import com.z3pipe.z3location.config.PositionCollectionConfig;
import com.z3pipe.z3location.content.PositionProvider;
import com.z3pipe.z3location.db.DatabaseHelper;
import com.z3pipe.z3location.model.ELocationQuality;
import com.z3pipe.z3location.model.Position;
import com.z3pipe.z3location.task.PositionDataSendTask;
import com.z3pipe.z3location.util.Constants;

import androidx.core.content.ContextCompat;


/**
 * @link https://www.z3pipe.com
 * @author zhengzhuanzi
 * @date 2019-04-10
 */
public class TrackingController implements PositionProvider.PositionListener, NetworkBroadcastReceiver.NetworkHandler {
    private static final String TAG = TrackingController.class.getSimpleName();
    private static final int RETRY_DELAY = 30 * 1000;
    private static final int WAKE_LOCK_TIMEOUT = 120 * 1000;
    private boolean isOnline;
    private boolean isWaiting;
    private Context context;
    private Handler handler;
    private PositionProvider positionProvider;
    private DatabaseHelper databaseHelper;
    private NetworkBroadcastReceiver networkManager;
    private final PositionCollectionConfig positionCollectionConfig;
    private PowerManager.WakeLock wakeLock;
    private boolean isStop = false;
    private UploadTread uploadTread;
    public static boolean isNeedReconnect = false;

    private void lock() {
        wakeLock.acquire(WAKE_LOCK_TIMEOUT);
    }

    private void unlock() {
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    public TrackingController(Context context, PositionCollectionConfig positionCollectionConfig) {
        this.context = context;
        handler = new Handler();
        this.positionCollectionConfig = positionCollectionConfig;
        positionProvider = PositionProviderFactory.creatPositionProvider(context, positionCollectionConfig,this);
        databaseHelper = new DatabaseHelper(context);
        networkManager = new NetworkBroadcastReceiver(context, this);
        isOnline = networkManager.isOnline();
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        UploadHandler uploadHandler = new UploadHandler();
        uploadTread = new UploadTread(uploadHandler);
    }

    public void start() {
        isStop = false;
        PositionDataSendTask.getInstance(positionCollectionConfig);
        if (isOnline) {
            //read();
        }

        if (uploadTread!=null){
            uploadTread.cancelExecute(false);
            if (!uploadTread.checkStart()){
                uploadTread.start();
                uploadTread.setStart(true);
            }
        }

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                positionProvider.startUpdates();
            } catch (SecurityException e) {
                Log.w(TAG, e);
            }
        }

        networkManager.start();
    }

    public void stop() {
        isStop = true;
        uploadTread.cancelExecute(true);
        PositionDataSendTask.getInstance(positionCollectionConfig).clearSendSocketData();
        networkManager.stop();
        try {
            positionProvider.stopUpdates();
        } catch (SecurityException e) {
            Log.w(TAG, e);
        }

        handler.removeCallbacksAndMessages(null);
    }

    /**
     * 发送心跳
     */
    public void sendHeartBeat(){
        if (isStop) {
            return;
        }

        PositionDataSendTask.getInstance(positionCollectionConfig).sendHeartbeat();
    }

    @Override
    public void onPositionUpdate(Position position) {
        if (position != null) {
            if (position.getX()==0||position.getY()==0){
                Log.w(TAG, "x,y坐标为0");
                return;
            }
            if (isOnline){
                // 有网络时，直接上传
                //List<Position> positions = new ArrayList<>();
                //positions.add(position);
                //PositionDataSendTask.getInstance(positionCollectionConfig).sendOrder(JSONArray.toJSONString(positions));
                PositionDataSendTask.getInstance(positionCollectionConfig).sendOrder(position, databaseHelper);
                Log.d(TAG, "实时上报" + position.getLon());
            } else {
                // 没有网络时，存储到db
                write(position);
            }
        }
    }

    @Override
    public void onPositionError(Throwable error) {
    }

    @Override
    public void onLocationQuality(ELocationQuality quality) {

    }

    @Override
    public void onNetworkUpdate(boolean isOnline) {
        if (!this.isOnline && isOnline) {
            read();
        }
        if (!isOnline){
            isNeedReconnect = true;
        }
        this.isOnline = isOnline;
    }

    private void log(String action, Position position) {
        if (position != null) {
            action += " (" +
                    "id:" + position.getId() +
                    " time:" + position.getTime() / 1000 +
                    " lat:" + position.getLat() +
                    " lon:" + position.getLon() + ")";
        }
        Log.d(TAG, action);
    }

    private void write(Position position) {
        log("write", position);
        lock();
        databaseHelper.insertPositionAsync(position, new DatabaseHelper.DatabaseHandler<Void>() {
            @Override
            public void onComplete(boolean success, Void result) {
                if (success) {
//                    if (isOnline && isWaiting) {
//                        read();
//                        isWaiting = false;
//                    }
                }
                unlock();
            }
        });
    }

    public void read() {
        log("read", null);
        lock();
        databaseHelper.selectPositionAsync(new DatabaseHelper.DatabaseHandler<Position>() {
            @Override
            public void onComplete(boolean success, Position result) {
                if (success) {
                    if (result != null) {
                        if (result.getUserId().equals(Constants.USERID)) {
                            send(result);
                            Log.d(TAG, "数据库补报" + result.getId());
                            delete(result);
                        }
                    } else {
                        isWaiting = true;
                    }
                } else {
                    retry();
                }
                unlock();
            }
        });
    }

    private void delete(Position position) {
        log("delete", position);
        lock();
        databaseHelper.deletePositionAsync(position.getId(), new DatabaseHelper.DatabaseHandler<Void>() {
            @Override
            public void onComplete(boolean success, Void result) {
//                if (success) {
//                    read();
//                } else {
//                    retry();
//                }
                unlock();
            }
        });
    }

    private void send(final Position position) {
        log("send", position);
        if (position.getX()==0||position.getY()==0){
            Log.w(TAG, "x,y坐标为0");
            return;
        }
        lock();
        //List<Position> positions = new ArrayList<>();
        //positions.add(position);
        //PositionDataSendTask.getInstance(positionCollectionConfig).sendOrder(JSONArray.toJSONString(positions));
        PositionDataSendTask.getInstance(positionCollectionConfig).sendOrder(position,databaseHelper);
        unlock();
    }

    private void retry() {
//        log("retry", null);
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (isOnline) {
//                    read();
//                }
//            }
//        }, RETRY_DELAY);
    }

    private class UploadHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0){
                if (isOnline){
                    read();
                }
            }
        }
    }

    private static class UploadTread extends Thread{

        /**
         * 取消标记
         */
        private volatile boolean cancelFlag = false;

        private volatile boolean isStart = false;

        public void setStart(boolean start){
            isStart = start;
        }

        public boolean checkStart(){
            return isStart;
        }

        /**
         * 取消继续执行
         */
        public void cancelExecute(boolean cancelFlag) {
            this.cancelFlag = cancelFlag;
        }

        private UploadHandler uploadHandler;

        /**
         * 是否已取消
         *
         * @return
         */
        public boolean isCanceled() {
            return this.cancelFlag;
        }

        public UploadTread(UploadHandler uploadHandler){
            this.uploadHandler = uploadHandler;
        }

        @Override
        public void run() {
            super.run();
            while (!isCanceled()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                uploadHandler.sendEmptyMessage(0);
            }
        }
    }



}
