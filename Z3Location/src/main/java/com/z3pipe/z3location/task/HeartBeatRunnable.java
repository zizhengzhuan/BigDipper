package com.z3pipe.z3location.task;

import android.util.Log;

import com.z3pipe.z3location.controller.TrackingController;

import java.lang.ref.WeakReference;

/**
 * Created with IntelliJ IDEA.
 * Description: ${TODO}
 * Date: 2019-04-17
 * Time: 18:04
 * Copyright © 2018 ZiZhengzhuan. All rights reserved.
 * https://www.z3pipe.com
 *
 * @author zhengzhuanzi
 */
final public class HeartBeatRunnable implements Runnable {
    private WeakReference<TrackingController> trackingControllerOut;
    private String jobName;

    public HeartBeatRunnable(String jobName, TrackingController trackingController) {
        this.jobName = jobName;
        this.trackingControllerOut = new WeakReference<>(trackingController);
    }

    @Override
    public void run() {
        if (null == this.trackingControllerOut) {
            return;
        }

        TrackingController trackingController = trackingControllerOut.get();
        if (null == trackingController) {
            return;
        }

        try {
            trackingController.sendHeartBeat();
        } catch (Exception e) {
            Log.e("HeartBeatRunnable", e.getMessage());
        }
    }
}