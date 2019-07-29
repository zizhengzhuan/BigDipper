
package com.z3pipe.z3location.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.xdandroid.hellodaemon.AbsWorkService;
import com.z3pipe.z3location.broadcast.AutostartReceiver;
import com.z3pipe.z3location.controller.TrackingConfigController;
import com.z3pipe.z3location.controller.TrackingController;
import com.z3pipe.z3location.task.HeartBeatRunnable;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.observers.DisposableObserver;

/***
 * 坐标服务，用于启动GPS定位，GPS轨迹上报
 * 启动该Service之前 必须
 * @author ZiZhengzhuan
 *
 */
public class PositionService extends AbsWorkService {
    public static boolean shouldStopService;
    private static CompositeDisposable disposables;
    private ScheduledExecutorService backgroundService = null;
    public static boolean isStop = false;
    private static PowerManager pm;
    private static PowerManager.WakeLock wakeLock;
    private final IBinder mBinder = new LocalBinder();
    private final static int NOTIFICATION_ID_APP_IS_RUNNING = 110;
    private TrackingController trackingController;
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        PositionService getService() {
            return PositionService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForegroundService();

        if (intent != null) {
            AutostartReceiver.completeWakefulIntent(intent);
        }

        if (null == pm) {
            pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
            try {
                wakeLock.acquire();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        isStop = false;
        shouldStopService = false;
        trackingController = new TrackingController(this, TrackingConfigController.getPositionCollectionConfig(this));
        trackingController.start();

        if(null != backgroundService){
            backgroundService.shutdown();
        }

        backgroundService = new ScheduledThreadPoolExecutor(1);
        backgroundService.scheduleAtFixedRate(new HeartBeatRunnable("HeartBeat",trackingController), 0, 1000 * 10, TimeUnit.MILLISECONDS);

        return START_STICKY;
    }

    private void startForegroundService() {
        Notification notification = createNotification(getApplication(), getApplicationInfo().icon);
        if (null != notification) {
            startForeground(NOTIFICATION_ID_APP_IS_RUNNING, notification);
        }
    }

    private Notification createNotification(Context context, int resIdIcon) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int sdk_int = Build.VERSION.SDK_INT;
        if (sdk_int >= Build.VERSION_CODES.HONEYCOMB && sdk_int < Build.VERSION_CODES.JELLY_BEAN) {
            Notification.Builder builder1 = new Notification.Builder(context);
            // 设置图标
            builder1.setSmallIcon(resIdIcon);
            // 发送时间
            builder1.setWhen(System.currentTimeMillis());
            // 设置前台
            builder1.setDefaults(Notification.FLAG_FOREGROUND_SERVICE);
            // 打开程序后图标消失
            builder1.setAutoCancel(false);
            builder1.setContentTitle(getContentTitle());
            builder1.setContentText(getContentText());
            Notification notification = builder1.getNotification();
            notification.flags |= Notification.FLAG_NO_CLEAR;
            return notification;
        } else if (sdk_int >= Build.VERSION_CODES.JELLY_BEAN && sdk_int < Build.VERSION_CODES.O) {
            // 新建状态栏通知
            Notification.Builder baseNF1 = new Notification.Builder(context);
            baseNF1.setSmallIcon(resIdIcon);
            baseNF1.setAutoCancel(true);
            baseNF1.setAutoCancel(false);
            baseNF1.setDefaults(Notification.FLAG_FOREGROUND_SERVICE);
            baseNF1.setWhen(System.currentTimeMillis());
            baseNF1.setContentTitle(getContentTitle());
            baseNF1.setContentText(getContentText());
            Notification notification = baseNF1.build();
            notification.flags |= Notification.FLAG_NO_CLEAR;
            return notification;
        } else if (sdk_int >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("2", "Channel2", NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(false);
            channel.setLightColor(Color.GREEN);
            channel.setShowBadge(false);
            channel.setSound(null, null);
            channel.setVibrationPattern(null);
            channel.enableVibration(false);
            manager.createNotificationChannel(channel);
            Notification.Builder builderAndroidO = new Notification.Builder(context, "2");
            builderAndroidO.setSmallIcon(resIdIcon).setAutoCancel(false).setWhen(System.currentTimeMillis()).setSound(null).setVibrate(null).setOnlyAlertOnce(true);
            builderAndroidO.setDefaults(Notification.FLAG_FOREGROUND_SERVICE);
            builderAndroidO.setContentTitle(getContentTitle());
            builderAndroidO.setContentText(getContentText());
            Notification notification = builderAndroidO.build();
            notification.defaults = 0;
            notification.flags |= Notification.FLAG_NO_CLEAR;
            return notification;
        }

        return null;
    }

    private CharSequence getContentTitle() {
        return getApplicationInfo().name;
    }

    private CharSequence getContentText() {
        return "正在进行获取轨迹并上传服务器";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        if(null != wakeLock) {
            wakeLock.release();
            wakeLock = null;
        }
        isStop = true;
        if (backgroundService != null) {
            backgroundService.shutdown();
        }

        backgroundService = null;
        if (trackingController != null) {
            trackingController.stop();
        }
    }

    /**
     * 停止服务
     * @param context
     */
    public static void stopService(Context context) {
        //我们现在不再需要服务运行了, 将标志位置为 true
        shouldStopService = true;
        //取消对任务的订阅
        if (disposables != null) {
            disposables.dispose();
        }
        //取消 Job / Alarm / Subscription
        cancelJobAlarmSub();
        if (null != context) {
            Intent intent = new Intent(context, PositionService.class);
            context.stopService(intent);
        }
    }

    /**
     * 是否 任务完成, 不再需要服务运行?
     *
     * @return 应当停止服务, true; 应当启动服务, false; 无法判断, 什么也不做, null.
     */
    @Override
    public Boolean shouldStopService(Intent intent, int flags, int startId) {
        return shouldStopService;
    }

    @Override
    public void startWork(Intent intent, int flags, int startId) {
        disposables = new CompositeDisposable();
        disposables.add(sampleObservable().subscribeWith(new DisposableObserver<Long>() {
            @Override
            public void onComplete() {
                System.out.println("onComplete()");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("onError()" + e);
            }

            @Override
            public void onNext(Long count) {
                Log.d("zzz", "onNext");
            }
        }));
    }

    static Observable<Long> sampleObservable() {
        Observable observable = Observable.interval(60, TimeUnit.SECONDS).doOnDispose(new Action() {
            @Override
            public void run() {
                cancelJobAlarmSub();
            }
        });
        return observable;
    }

    @Override
    public void stopWork(Intent intent, int flags, int startId) {
        stopService(this);
    }

    /**
     * 任务是否正在运行?
     *
     * @return 任务正在运行, true; 任务当前不在运行, false; 无法判断, 什么也不做, null.
     */
    @Override
    public Boolean isWorkRunning(Intent intent, int flags, int startId) {
        //若还没有取消订阅, 就说明任务仍在运行.
        return disposables != null && !disposables.isDisposed();
    }

    @Override
    public IBinder onBind(Intent intent, Void v) {
        System.out.println("onBind");
        return null;
    }

    @Override
    public void onServiceKilled(Intent rootIntent) {
        System.out.println("onServiceKilled");
    }
}

