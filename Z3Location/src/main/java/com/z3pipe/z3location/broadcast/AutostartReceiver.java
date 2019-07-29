package com.z3pipe.z3location.broadcast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.z3pipe.z3location.config.PositionCollectionConfig;
import com.z3pipe.z3location.service.PositionService;


/**
 * @author zhengzhuanzi
 * @link https://www.z3pipe.com
 * @date 2019-04-10
 */
public class AutostartReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences.getBoolean(PositionCollectionConfig.KEY_STATUS, false)) {
            //startWakefulForegroundService(context, new Intent(context, PositionService.class));
        }
    }
}
