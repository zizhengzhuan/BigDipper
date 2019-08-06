package com.z3pipe.bigdipper.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;

import com.enn.sop.IUserInfoAidlInterface;
import com.z3pipe.z3location.config.PositionCollectionConfig;
import com.z3pipe.z3location.controller.TrackingConfigController;
import com.z3pipe.z3location.util.Constants;

import androidx.annotation.Nullable;

/**
 * Created by xiaobei on 2019/5/14.
 * 由智慧运营APP 获取到 用户信息 ：登录名 用户ID token 等信息
 */

public class SetUserInfoService extends Service {
    private static String TAG = "SetUserInfoService";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    private final IUserInfoAidlInterface.Stub mBinder = new IUserInfoAidlInterface.Stub() {

        @Override
        public void setUserInfos(String userName, String userId, String userToken, String deviceId) throws RemoteException {
            Log.d(TAG, "由智慧运营APP获取到的用户信息：用户名称: " + userName + " 用户ID: " + userId + " 用户token: " + userToken + " 用户deviceid: " + deviceId);
//            Constants.USERID = userId;
//            Constants.DEVICEID = deviceId;
//            Constants.USER_NAME = userName;
//            Constants.TRUE_NAME = userToken;
            initPositionCollectionConfig();
        }

        @Override
        public int writeBackLocationAppProcessId() throws RemoteException {
            return Process.myPid();
        }
    };

    private void initPositionCollectionConfig() {

    }

}
