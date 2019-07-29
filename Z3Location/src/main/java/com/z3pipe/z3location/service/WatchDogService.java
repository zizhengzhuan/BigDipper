package com.z3pipe.z3location.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;

import com.z3pipe.z3location.R;
import com.z3pipe.z3location.task.WatchDogThread;

import java.util.concurrent.TimeUnit;

/***
 * y用于延长App运行时间
 * @author zhengzhuanzi
 */
public class WatchDogService extends Service {
    private WatchDogThread worker;
    private MediaPlayer mediaPlayer;
    private long startTime;

    private Notification createNotification(Context context, int resIdIcon) {
        int duration = (int) (System.currentTimeMillis() - startTime);
        String durationStr = formatTimeCost(duration);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        /*
         * SKD中API Level高于11低于16
         */
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
            builder1.setContentText("已持续运行" + durationStr);
            Notification notification = builder1.getNotification();
            notification.flags |= Notification.FLAG_NO_CLEAR;
            return notification;
        }
        /*
         * SKD中API Level高于16
         */
        else if (sdk_int >= Build.VERSION_CODES.JELLY_BEAN && sdk_int < Build.VERSION_CODES.O) {
            // 新建状态栏通知
            Notification.Builder baseNF1 = new Notification.Builder(context);
            baseNF1.setSmallIcon(resIdIcon);
            baseNF1.setAutoCancel(true);
            baseNF1.setAutoCancel(false);
            baseNF1.setDefaults(Notification.FLAG_FOREGROUND_SERVICE);
            baseNF1.setWhen(System.currentTimeMillis());
            baseNF1.setContentTitle(getContentTitle());
            baseNF1.setContentText("已持续运行" + durationStr);
            Notification notification = baseNF1.build();
            notification.flags |= Notification.FLAG_NO_CLEAR;
            return notification;
        } else if (sdk_int >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("2", "Channel2", NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(false);
            channel.setLightColor(Color.GREEN);
            channel.setShowBadge(false);
            channel.setSound(null,null);
            channel.setVibrationPattern(null);
            channel.enableVibration(false);
            manager.createNotificationChannel(channel);
            Notification.Builder builderAndroidO = new Notification.Builder(context, "2");
            builderAndroidO.setSmallIcon(resIdIcon).setAutoCancel(false).setWhen(System.currentTimeMillis()).setSound(null).setVibrate(null).setOnlyAlertOnce(true);
            builderAndroidO.setDefaults(Notification.FLAG_FOREGROUND_SERVICE);
            builderAndroidO.setContentTitle(getContentTitle());
            builderAndroidO.setContentText("已持续运行" + durationStr);
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

    @Override
    public void onCreate() {
        super.onCreate();
        worker = new WatchDogThread() {
            @Override
            public void run() {
                startTime = System.currentTimeMillis();
                while (!isExit) {
                    try {
                        Notification notification = createNotification(getApplication(), getApplicationInfo().icon);
                        startForeground(1, notification);
                        TimeUnit.SECONDS.sleep(5);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        setupSilentMusicPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        worker.start();
        worker.setName(this.getClass().getName());
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSilentMusic();
        stopForeground(true);
    }

    private void setupSilentMusicPlayer() {
        //保活机制之一，播放无声音乐
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.sd_slient);
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setLooping(true);
    }

    private void stopSilentMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private String formatTimeCost(long timeCost) {
        if (timeCost < 0) {
            return "";
        }

        long remain = timeCost;
        long oneHour = 3600000;
        long oneMinute = 60000;

        long hourPart = remain / oneHour;
        remain = remain % oneHour;
        long minutePart = remain / oneMinute;
        remain = remain % oneMinute;
        long secondPart = remain / 1000;

        String str = "";
        if (hourPart > 0) {
            str = hourPart + "小时" + minutePart + "分";
        } else if (minutePart > 0) {
            str = str + minutePart + "分";
        }

        return str + secondPart + "秒";
    }
}
