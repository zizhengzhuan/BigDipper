package com.z3pipe.z3location.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
/**
 * @link https://www.z3pipe.com
 * @author zhengzhuanzi
 * @date 2019-04-10
 */
public class NetworkBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = NetworkBroadcastReceiver.class.getSimpleName();

    private Context context;
    private NetworkHandler handler;
    private ConnectivityManager connectivityManager;

    public NetworkBroadcastReceiver(Context context, NetworkHandler handler) {
        this.context = context;
        this.handler = handler;
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public interface NetworkHandler {
        /**
         * 网络状态更新
         * @param isOnline
         */
        void onNetworkUpdate(boolean isOnline);
    }

    public boolean isOnline() {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public void start() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(this, filter);
    }

    public void stop() {
        context.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION) && handler != null) {
            boolean isOnline = isOnline();
            Log.i(TAG, "network " + (isOnline ? "on" : "off"));
            handler.onNetworkUpdate(isOnline);
        }
    }

}
