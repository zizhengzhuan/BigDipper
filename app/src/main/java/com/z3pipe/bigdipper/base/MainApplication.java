package com.z3pipe.bigdipper.base;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;

import com.ecity.android.log.LogUtil;
import com.xdandroid.hellodaemon.DaemonEnv;
import com.z3pipe.bigdipper.R;
import com.z3pipe.bigdipper.fragment.MainFragment;
import com.z3pipe.bigdipper.util.FileCleaner;
import com.z3pipe.z3location.service.PositionService;
import com.z3pipe.z3location.util.FileUtil;

/**
 * @author zhengzhuanzi
 * @link https://www.z3pipe.com
 * @date 2019-04-10
 */
public class MainApplication extends Application {
    /**
     * 文件路径所占最大空间
     */
    private static final int MAX_DIR_SIZE_MB = 100;
    /**
     * 单个文件存在的最长时间（天）
     */
    private static final int MAX_FILE_EXSIST_DAY = 20;
    /**
     * 单个文件最大空间
     */
    private static final int MAX_SINGLE_FILE_SIZE_MB = 2 * MAX_DIR_SIZE_MB / MAX_FILE_EXSIST_DAY;
    public static final String PRIMARY_CHANNEL = "default";

    private static MainApplication instance;
    public static MainApplication getInstance() {
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        System.setProperty("http.keepAliveDuration", String.valueOf(3 * 60 * 1000));
        DaemonEnv.initialize(this, PositionService.class, 1 * 60 * 1000);
        migrateLegacyPreferences(PreferenceManager.getDefaultSharedPreferences(this));
        initLogger();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerChannel();
        }
    }

    public void initLogger() {
        String path = FileUtil.getInstance(null).getConfPath();
        FileUtil.getInstance(null).hasFileDir(path);
        LogUtil.init(path, "Log", ".log", LogUtil.LOG_LEVEL_INFO);
        LogUtil.v(this, "Logger has been initialised.");
        FileCleaner cleaner = new FileCleaner(path, MAX_DIR_SIZE_MB, MAX_FILE_EXSIST_DAY, MAX_SINGLE_FILE_SIZE_MB);
        cleaner.work();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void registerChannel() {
        NotificationChannel channel = new NotificationChannel(
                PRIMARY_CHANNEL, getString(R.string.channel_default), NotificationManager.IMPORTANCE_MIN);
        channel.setLightColor(Color.GREEN);
        channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
    }

    private void migrateLegacyPreferences(SharedPreferences preferences) {
        String port = preferences.getString("port", "2437");
        if (port != null) {
            String host = preferences.getString("address", getString(R.string.settings_url_default_value));
            String scheme = preferences.getBoolean("secure", false) ? "https" : "http";

            Uri.Builder builder = new Uri.Builder();
            builder.scheme(scheme).encodedAuthority(host + ":" + port).build();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(MainFragment.KEY_URL, builder.toString());

            editor.remove("port");
            editor.remove("address");
            editor.remove("secure");
            editor.apply();
        }
    }
}
