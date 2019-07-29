package com.z3pipe.z3location.controller;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.z3pipe.z3core.base.BaseSdFileReadWriter;
import com.z3pipe.z3location.config.PositionCollectionConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created with IntelliJ IDEA.
 * Description: ${TODO}
 * Date: 2019-04-15
 * Time: 12:08
 * Copyright © 2018 ZiZhengzhuan. All rights reserved.
 * https://www.z3pipe.com
 *
 * @author zhengzhuanzi
 */
public class TrackingConfigController extends BaseSdFileReadWriter {
    /**
     * 获得GPS定位配置信息
     *
     * @param context
     * @return
     */
    public static PositionCollectionConfig getPositionCollectionConfig(Context context) {
        ObjectInputStream inputStream = null;
        PositionCollectionConfig positionCollectionConfig = null;
        try {
            File file = new File(getConfigCachePath(context));
            inputStream = new ObjectInputStream(new FileInputStream(file));
            Object object = inputStream.readObject();
            positionCollectionConfig = (PositionCollectionConfig) object;
            return positionCollectionConfig;
        } catch (Exception e) {
            positionCollectionConfig = new PositionCollectionConfig();
        } finally {
            try {
                inputStream.close();
            } catch (Exception e2) {
            }
        }

        return positionCollectionConfig;
    }

    /**
     * 保存GPS定位配置
     *
     * @param context
     * @param positionCollectionConfig
     * @return
     */
    public static void savePositionCollectionConfig(Context context, PositionCollectionConfig positionCollectionConfig) {
        ObjectOutputStream objectOutputStream = null;
        try {
            File file = new File(getConfigCachePath(context));
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
            objectOutputStream.writeObject(positionCollectionConfig);
        } catch (IOException e) {
        } finally {
            try {
                objectOutputStream.close();
            } catch (Exception e2) {

            }
        }
    }

    /**
     * 获得配置信息缓存路径
     *
     * @param context
     * @return
     */
    private static String getConfigCachePath(Context context) {
        String rootPath = "//Z3Pipe//tracking";
        boolean hasSdCard = Environment.getExternalStorageState().equals("mounted");
        String sdCardPath = Environment.getExternalStorageDirectory().getPath();
        if (!hasSdCard) {
            sdCardPath = Environment.getRootDirectory().getPath();
        }

        String dir = sdCardPath + rootPath + "//media//";
        hasFileDir(dir);
        return dir + "user.config.bin";
    }

    private static boolean hasFileDir(String var1) {
        File file;
        if (!(file = new File(var1)).exists()) {
            file.mkdirs();
        }

        return file.exists();
    }
}
