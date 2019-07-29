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

import com.xdandroid.hellodaemon.DaemonEnv;
import com.z3pipe.bigdipper.R;
import com.z3pipe.bigdipper.fragment.MainFragment;
import com.z3pipe.z3location.service.PositionService;

/**
 * @author zhengzhuanzi
 * @link https://www.z3pipe.com
 * @date 2019-04-10
 */
public class MainApplication extends Application {

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerChannel();
        }
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
