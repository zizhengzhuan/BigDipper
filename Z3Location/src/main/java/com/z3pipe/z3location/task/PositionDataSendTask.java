package com.z3pipe.z3location.task;

import android.os.SystemClock;
import android.os.Trace;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.koushikdutta.async.AsyncNetworkSocket;
import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.AsyncSocket;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.Util;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.ConnectCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.callback.WritableCallback;
import com.z3pipe.z3location.config.PositionCollectionConfig;
import com.z3pipe.z3location.controller.TrackingController;
import com.z3pipe.z3location.db.DatabaseHelper;
import com.z3pipe.z3location.model.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * Description: ${TODO}
 * Date: 2019-04-17
 * Time: 09:53
 * Copyright © 2018 ZiZhengzhuan. All rights reserved.
 * https://www.z3pipe.com
 *
 * @author zhengzhuanzi
 */
public class PositionDataSendTask implements ConnectCallback {
    /**
     * 心跳超时时间
     */
    private final int HEARTBEAT_TIMEOUT = 1000 * 10 * 2;
    /**
     * 重连次数，连接成功后会设置为0
     */
    private int retryCount = 0;
    /**
     * 是否需要保持运行
     */
    private boolean keepRunning = true;
    /**
     * 是否正在连接
     */
    private boolean connecting = false;
    /**
     * 上次的心跳回应时间
     */
    private long lastHeartbeatResponseTime = 0;

    private AsyncSocket socket;
    private static PositionDataSendTask positionDataSendTask;
    private final PositionCollectionConfig positionCollectionConfig;
//    private double[] testX = {12990839.70354621,12991180.674849587,12991500.157741304,12991951.490859685,12991642.159760924,12991651.415807378,12991732.928732058,12992096.303198561,12991635.590953942,12992336.960404778};
//    private double[] testY = {4797635.648751838,4797862.618602804,4797752.651591223,4797802.5148089,4797679.7684828695,4797459.315139395,4797067.575369475,4797075.637087295,4796537.961135746,4796687.252206582};

    private double[] testX = {12990839.70354621,12991180.674849587,12991500.157741304,12991951.490859685,12991642.159760924};
    private double[] testY = {4797635.648751838,4797862.618602804,4797752.651591223,4797802.5148089,4797679.7684828695};


//    private double[] testLineX = {1.2992138022E7,1.2992137364E7,1.2992136875E7,1.2992128424E7,1.2992038709E7,1.2992032799E7,1.2992039109E7,
//            1.2992063244E7,1.2992056075E7,1.299206132E7,1.2992054908E7,1.2992202037E7,1.2991975682E7,1.2991975387E7,1.2992053556E7};
//    private double[] testLineY = {4799072.689,4799073.225,4799074.673,4799143.123,4799154.651,4799047.029,4799047.562,
//            4798731.979,4798731.32,4798771.597,4798865.641,4798854.339,4798854.587,4798857.717,4798865.582};

    private double[] testLineX = {1.2990477406E7,1.2990476878E7,1.2990475885E7,1.2990477677E7,1.2990306618E7,1.299030494E7,1.2990305941E7,1.2990290473E7,1.2990289081E7};
    private double[] testLineY = {4798909.94,4798916.549,4798928.99,4798929.428,4798898.761,4798898.631,4798888.802,4798887.329,4798886.552};

    private PositionDataSendTask(final PositionCollectionConfig positionCollectionConfig) {
        this.positionCollectionConfig = positionCollectionConfig;
        this.keepRunning = true;
        this.retryCount = 0;
    }

    public static PositionDataSendTask getInstance(PositionCollectionConfig positionCollectionConfig) {
        return SingletonHolder.instance(positionCollectionConfig);
    }

    @Override
    public void onConnectCompleted(Exception ex, AsyncSocket socket) {
        connecting = false;
        if (ex != null) {
            Log.d("Socket", "连接出错");
            return;
        }
        socket.setDataCallback(new DataCallback() {
            @Override
            public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                lastHeartbeatResponseTime = SystemClock.elapsedRealtime();
                Log.d("Socket", "接收到：" + new String(bb.getAllByteArray()));
            }
        });
        socket.setClosedCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                markConnectLost();
                if (ex != null) {
                    Log.d("Socket", "setClosedCallback出错");
                    return;
                }
                Log.d("Socket", "setClosedCallback");
            }
        });
        socket.setEndCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                markConnectLost();
                if (ex != null) {
                    Log.d("Socket", "setEndCallback出错");
                    return;
                }
                Log.d("Socket", "setEndCallback");
            }
        });
        socket.setWriteableCallback(new WritableCallback() {
            @Override
            public void onWriteable() {
                lastHeartbeatResponseTime = SystemClock.elapsedRealtime();
                Log.d("Socket", "onWriteable");
            }
        });

        this.socket = socket;
        lastHeartbeatResponseTime = SystemClock.elapsedRealtime();
        Log.d("Socket", "socket链接成功");
    }

    /**
     * 发送命令
     */
    public void sendOrder(String data) {
        checkConnect();
        if (socket != null) {
            final String order = "1#"+data;
            //final String order = data;
            Util.writeAll(socket, order.getBytes(), new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {
                    if (ex != null) {
                        Log.d("Socket", "writeAll出错");
                        return;
                    }
                    lastHeartbeatResponseTime = SystemClock.elapsedRealtime();
                    Log.d("Socket", "发送了：" + order);
                }
            });
        } else {
            Log.d("Socket", "socket==null");
        }
    }


    /**
     * 发送命令
     */
    public void sendOrder(final Position position, final DatabaseHelper databaseHelper) {
        if (TrackingController.isNeedReconnect){
            AsyncServer.getDefault().connectSocket(positionCollectionConfig.getHost(), positionCollectionConfig.getPort(), this);
        } else {
            checkConnect();
        }

        if (socket != null) {
            List<Position> positions = new ArrayList<>();
            // 模拟测试关键点到位
            int index = new Random().nextInt(testX.length);
            position.setX(testX[index]);
            position.setY(testY[index]);
            // 模拟测试覆盖线到位
//            int index = new Random().nextInt(testLineX.length);
//            position.setX(testLineX[index]);
//            position.setY(testLineY[index]);
            positions.add(position);
            String data = JSONArray.toJSONString(positions);
            final String order = "1#"+data;
            //final String order = data;
            Util.writeAll(socket, order.getBytes(), new CompletedCallback() {
                @Override
                public void onCompleted(Exception ex) {
                    TrackingController.isNeedReconnect = false;
                    if (ex != null) {
                        Log.d("Socket", "writeAll出错");
                        position.setState(0);
                        // 发送失败，存储到db
                        if (position.getId()<=0){
                            databaseHelper.insertPositionAsync(position,new DatabaseHelper.DatabaseHandler(){

                                @Override
                                public void onComplete(boolean success, Object result) {

                                }
                            });
                        }
                        return;
                    }
                    lastHeartbeatResponseTime = SystemClock.elapsedRealtime();
                    Log.d("Socket", "发送了：" + order);
//                    position.setState(1);
//                    // 发送成功更新数据
//                    if (position.getId()>0){
//                        databaseHelper.updatePositionAsync(position.getId(),new DatabaseHelper.DatabaseHandler(){
//
//                            @Override
//                            public void onComplete(boolean success, Object result) {
//
//                            }
//                        });
//                    }
                }
            });
        } else {
            // 发送失败，存储到db
            if (position.getId()<=0){
                databaseHelper.insertPositionAsync(position,new DatabaseHelper.DatabaseHandler(){

                    @Override
                    public void onComplete(boolean success, Object result) {

                    }
                });
            }
            Log.d("Socket", "socket==null");
        }
    }

    /**
     * 发送Socket登录命令
     */
    public void SendLoginOrder() {
        checkConnect();
        if (socket == null) {
            Log.d("Socket", "socket==null");
            return;
        }
        final String Data = "{\"header\":{\"cmd\":\"1000\"},\"body\":{\"name\":\" Constant.userName\",\"pass\":\"Constant.Pwd\",\"ver\":\"4.6936\",\"type\":\"json_common\",\"mode\":\"\"}}";
        Util.writeAll(socket, Data.getBytes(), new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) {
                    Log.d("Socket", "writeAll出错");
                    return;
                }
                Log.d("Socket", "发送了：" + Data);
            }
        });
    }

    /**
     * 发送Socket退出登录命令
     */
    public void SendLogoutOrder() {
        checkConnect();
        if (socket == null) {
            Log.d("Socket", "socket==null");
            return;
        }
        final String Data = "{\"header\":{\"cmd\":\"1001\"},\"body\":{\"data\":\"\"}}";
        Util.writeAll(socket, Data.getBytes(), new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) {
                    Log.d("SendSocket", "writeAll出错");
                    return;
                }
                Log.d("SendSocket", "发送了：" + Data);
            }
        });
    }

    /**
     * 发送Socket心跳命令
     */
    public void sendHeartbeat() {
        checkConnect();

        if (socket == null) {
            Log.d("Socket", "socket==null");
            return;
        }
        Util.writeAll(socket, "{\"header\":{\"cmd\":\"1002\"},\"body\":{\"result\":\"\"}}".getBytes(), new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                TrackingController.isNeedReconnect = false;
                if (ex != null) {
                    Log.d("SendSocket", "writeAll出错");
                    return;
                }
                lastHeartbeatResponseTime = SystemClock.elapsedRealtime();
                Log.d("SendSocket", "发送了：{\"header\":{\"cmd\":\"1002\"},\"body\":{\"result\":\"\"}}");
            }
        });
    }

    public void clearSendSocketData() {
        if (null != socket) {
            try {
                socket.close();
            } catch (Exception e) {
                Log.d("SendSocket", "socket close Exception" + e.getMessage());
            }
        }
        keepRunning = false;
        positionDataSendTask = null;
        connecting = false;
        retryCount = 0;
    }

    private static class SingletonHolder {
        public static PositionDataSendTask instance(final PositionCollectionConfig positionCollectionConfig) {
            if (positionDataSendTask == null) {
                positionDataSendTask = new PositionDataSendTask(positionCollectionConfig);
            }

            return positionDataSendTask;
        }
    }

    private void markConnectLost() {
        connecting = false;
    }

    private void checkConnect() {
        if (connecting || !keepRunning) {
            return;
        }

        long timeOffset = SystemClock.elapsedRealtime() - lastHeartbeatResponseTime;
        if (null == socket || !socket.isOpen() || timeOffset > HEARTBEAT_TIMEOUT) {
            connecting = true;
            retryCount++;
            Log.d("SendSocket", "重新连接次数：" + retryCount);
            AsyncServer.getDefault().connectSocket(positionCollectionConfig.getHost(), positionCollectionConfig.getPort(), this);
        }
    }
}
