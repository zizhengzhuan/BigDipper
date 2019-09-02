package com.z3pipe.bigdipper.fragment;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.TwoStatePreference;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.z3pipe.bigdipper.R;
import com.z3pipe.bigdipper.activity.MainActivity;
import com.z3pipe.bigdipper.ui.dialog.LoginDialog;
import com.z3pipe.z3location.util.SettingsManager;
import com.z3pipe.bigdipper.util.ShareHelper;
import com.z3pipe.z3location.util.StringUtil;
import com.z3pipe.bigdipper.util.ZipUtil;
import com.z3pipe.z3core.util.DateUtil;
import com.z3pipe.z3location.broadcast.AutostartReceiver;
import com.z3pipe.z3location.service.PositionService;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.z3pipe.z3location.service.WatchDogService;
import com.z3pipe.z3location.util.Constants;
import com.z3pipe.z3location.util.FileUtil;
import com.z3pipe.z3location.util.Textwriter;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @link https://www.z3pipe.com
 * @author zhengzhuanzi
 * @date 2019-04-10
 */
public class MainFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

    private static final String TAG = MainFragment.class.getSimpleName();

    private static final int ALARM_MANAGER_INTERVAL = 15000;
    private static final int REPORT_SUCCESS = 1;
    private static final int REPORT_FAIL = 2;
    private static final int SHOW_LOGIN_DIALOG = 3;

    public static final String KEY_DEVICE = "id";
    public static final String KEY_URL = "url";
    public static final String KEY_INTERVAL = "interval";
    public static final String KEY_DISTANCE = "distance";
    public static final String KEY_ANGLE = "angle";
    public static final String KEY_ACCURACY = "accuracy";
    public static final String KEY_STATUS = "status";
    public static final String KEY_REPORT = "report";
    public static final String KEY_SHARE = "share";


    private static final int PERMISSIONS_REQUEST_LOCATION = 2;

    private SharedPreferences sharedPreferences;

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;
    private LoginDialog dialog;
    private PositionHandler handler;
    private ScheduledExecutorService scheduledExecutorService;
    private boolean isTimetaskStop;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //移除App的icon
        //removeLauncherIcon();

        setHasOptionsMenu(true);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        addPreferencesFromResource(R.xml.preferences);
        initPreferences();
        handler = new PositionHandler();

//        findPreference(KEY_DEVICE).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//            @Override
//            public boolean onPreferenceChange(Preference preference, Object newValue) {
//                return newValue != null && !newValue.equals("");
//            }
//        });
//        findPreference(KEY_URL).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//            @Override
//            public boolean onPreferenceChange(Preference preference, Object newValue) {
//                return (newValue != null) && validateServerURL(newValue.toString());
//            }
//        });
//
//        findPreference(KEY_INTERVAL).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//            @Override
//            public boolean onPreferenceChange(Preference preference, Object newValue) {
//                if (newValue != null) {
//                    try {
//                        int value = Integer.parseInt((String) newValue);
//                        return value > 0;
//                    } catch (NumberFormatException e) {
//                        Log.w(TAG, e);
//                    }
//                }
//                return false;
//            }
//        });
//
//        Preference.OnPreferenceChangeListener numberValidationListener = new Preference.OnPreferenceChangeListener() {
//            @Override
//            public boolean onPreferenceChange(Preference preference, Object newValue) {
//                if (newValue != null) {
//                    try {
//                        int value = Integer.parseInt((String) newValue);
//                        return value >= 0;
//                    } catch (NumberFormatException e) {
//                        Log.w(TAG, e);
//                    }
//                }
//                return false;
//            }
//        };
//        findPreference(KEY_DISTANCE).setOnPreferenceChangeListener(numberValidationListener);
//        findPreference(KEY_ANGLE).setOnPreferenceChangeListener(numberValidationListener);

        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent(getActivity(), AutostartReceiver.class), 0);

        if (sharedPreferences.getBoolean(KEY_STATUS, false)) {
            startTrackingService(true, false);
        }
        showLoginDialog();
        startTimerTask();
    }

    private void removeLauncherIcon() {
        String className = MainActivity.class.getCanonicalName().replace(".MainActivity", ".Launcher");
        ComponentName componentName = new ComponentName(getActivity().getPackageName(), className);
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.getComponentEnabledSetting(componentName) != PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
            packageManager.setComponentEnabledSetting(
                    componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setMessage(getString(R.string.hidden_alert));
            builder.setPositiveButton(android.R.string.ok, null);
            builder.show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        TwoStatePreference preference = (TwoStatePreference) findPreference(KEY_SHARE);
        preference.setChecked(false);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void showLoginDialog(){
        if(null == dialog) {
            dialog = new LoginDialog(getActivity());
        }

        dialog.getWindow().setFlags(0x80000000,0x80000000);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_HOME:
                        if(!isLogin()) {
                            return true;
                        }
                    case KeyEvent.KEYCODE_BACK:
                        if(!isLogin()) {
                            return true;
                        }
                }
                return false;
        }});
        dialog.show();
    }

    private boolean isLogin() {
        if(StringUtil.isBlank(Constants.USERID) || "0".equals(Constants.USERID)) {
            return false;
        }

        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    private void setPreferencesEnabled(boolean enabled) {
//        findPreference(KEY_DEVICE).setEnabled(enabled);
//        findPreference(KEY_URL).setEnabled(enabled);
//        findPreference(KEY_INTERVAL).setEnabled(enabled);
//        findPreference(KEY_DISTANCE).setEnabled(enabled);
//        findPreference(KEY_ANGLE).setEnabled(enabled);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_STATUS)) {
            if (sharedPreferences.getBoolean(KEY_STATUS, false)) {
                startTrackingService(true, false);
            } else {
                stopTrackingService();
            }
        } else if (key.equals(KEY_DEVICE)) {
            findPreference(KEY_DEVICE).setSummary(sharedPreferences.getString(KEY_DEVICE, null));
        } else if(key.equals(KEY_REPORT)) {
            if (sharedPreferences.getBoolean(KEY_REPORT, false)) {
                reportPosition();
            }
        } else if(key.equals(KEY_SHARE)) {
            if (sharedPreferences.getBoolean(KEY_SHARE, false)) {
                String srcPath = FileUtil.getInstance(null).getConfPath();
                String tagetPath = FileUtil.getInstance(null).getRootPath() + "/position.zip";
                try {
                    ZipUtil.zip(srcPath, tagetPath);
                    File file = new File(tagetPath);
                    if(file.exists()) {
                        ShareHelper.shareFile(getActivity(), file);
                    } else {
                        Toast.makeText(getActivity(), "未获取到日志文件",Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initPreferences() {
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
//        if (!sharedPreferences.contains(KEY_DEVICE)) {
//            String id = String.valueOf(new Random().nextInt(900000) + 100000);
//            sharedPreferences.edit().putString(KEY_DEVICE, id).apply();
//            ((EditTextPreference) findPreference(KEY_DEVICE)).setText(id);
//        }
//        findPreference(KEY_DEVICE).setSummary(sharedPreferences.getString(KEY_DEVICE, null));
    }

    private void startTrackingService(boolean checkPermission, boolean permission) {
        if (checkPermission) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                permission = true;
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
                }
                return;
            }
        }

        if (permission) {
            setPreferencesEnabled(false);
            ContextCompat.startForegroundService(getActivity(), new Intent(getActivity(), PositionService.class));
            ContextCompat.startForegroundService(getActivity(), new Intent(getActivity(), WatchDogService.class));
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    ALARM_MANAGER_INTERVAL, ALARM_MANAGER_INTERVAL, alarmIntent);
        } else {
            sharedPreferences.edit().putBoolean(KEY_STATUS, false).apply();
            TwoStatePreference preference = (TwoStatePreference) findPreference(KEY_STATUS);
            preference.setChecked(false);
        }
    }

    private void stopTrackingService() {
        alarmManager.cancel(alarmIntent);
        PositionService.stopService(getActivity());
        //getActivity().stopService(new Intent(getActivity(), PositionService.class));
        Intent serviceIntent = new Intent(getActivity(), WatchDogService.class);
        getActivity().stopService(serviceIntent);
        setPreferencesEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            boolean granted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                    break;
                }
            }
            startTrackingService(false, granted);
        }
    }

    private boolean validateServerURL(String userUrl) {
        int port = Uri.parse(userUrl).getPort();
        if (URLUtil.isValidUrl(userUrl) && (port == -1 || (port > 0 && port <= 65535))
                && (URLUtil.isHttpUrl(userUrl) || URLUtil.isHttpsUrl(userUrl))) {
            return true;
        }
        Toast.makeText(getActivity(), R.string.error_msg_invalid_url, Toast.LENGTH_LONG).show();
        return false;
    }

    private void startTimerTask() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new TimerIncreasedRunnable(), 0, 15, TimeUnit.MINUTES);
        isTimetaskStop = false;
    }

    private void stopTimerTask() {
        isTimetaskStop = true;
        if (null != scheduledExecutorService) {
            scheduledExecutorService.shutdownNow();
        }
    }

    public class TimerIncreasedRunnable implements Runnable {
        @Override
        public void run() {
            try {
                if (isTimetaskStop) {
                    return;
                }
                String userId = SettingsManager.getInstance().getUserId(getActivity());
                if(StringUtil.isBlank(Constants.USERID) && !"0".equals(userId)) {
                    if(StringUtil.isBlank(Constants.USERID)) {
                        Constants.USERID = userId;
                    }
                }

//                Message msg = Message.obtain();
//                msg.obj = SHOW_LOGIN_DIALOG;
//                handler.sendMessage(msg);
            } catch (Throwable t) {
                Log.e("LocationService", t.getMessage());
            }
        }
    }

    private void reportPosition(){
        String requestUrl = "http://www.z3pipe.com:2436/api/v1/z3iot/position/reportTrace";
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        final String requestBody = getPositionArr();
        Request request = new Request.Builder()
                .url(requestUrl)
                .post(RequestBody.create(mediaType, requestBody))
                .build();

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                boolean success = string.contains("SUCCESS");
                Message message = Message.obtain();
                if(success) {
                    message.what = REPORT_SUCCESS;
                } else {
                    message.what = REPORT_FAIL;
                }
                handler.sendMessage(message);
            }
        });
    }

    private class PositionHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REPORT_SUCCESS:
                    showNotice(true);
                    TwoStatePreference preference = (TwoStatePreference) findPreference(KEY_REPORT);
                    preference.setChecked(false);
                    break;
                case REPORT_FAIL:
                    showNotice(false);
                    break;
                case SHOW_LOGIN_DIALOG:
                    showLoginDialog();
                    break;
                default:
                    break;
            }
        }
    }

    private void showNotice(boolean success) {
        String notice = "";
        if(success) {
            notice = getResources().getString(R.string.status_send_success);
        } else {
            notice = getResources().getString(R.string.status_send_fail);
        }
        Toast.makeText(getActivity(), notice, Toast.LENGTH_SHORT).show();
    }

    private String getPositionArr() {
        String path = FileUtil.getInstance(null).getConfPath() + DateUtil.getDate(new Date()) + ".txt";
        String positions = Textwriter.readString(path).replace("\n", "");
        if(StringUtil.isBlank(positions)) {
            return null;
        }

        String[] array = positions.split("1#");
        if(null == array || array.length == 0) {
            return null;
        }
        JSONArray jsonArray = new JSONArray();
        for(int i=0; i< array.length; i++) {
            String json = array[i];
            if(StringUtil.isBlank(json)) {
                continue;
            }
            JSONObject object = JSON.parseObject(json);
            jsonArray.add(object);
        }

        return jsonArray.toString();
    }

}
