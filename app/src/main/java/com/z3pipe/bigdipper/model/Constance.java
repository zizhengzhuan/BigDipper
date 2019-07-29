package com.z3pipe.bigdipper.model;

import android.Manifest;

/**
 * Created by ${xingen} on 2017/11/1.
 * blog:http://blog.csdn.net/hexingen
 *
 *  常量类
 *
 */

public final class Constance {
    /**
     * 写入权限的请求code,提示语，和权限码
     */
    public final static  int WRITE_PERMISSION_CODE=110;
    public final static String WRITE_PERMISSION_TIP ="为了正常使用，请允许读写权限!";
    public final  static String[] PERMS_WRITE ={Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_WIFI_STATE};
    /**
     * 相机，图库的请求code
     */
    public final static int PICTURE_CODE=10;
    public final static int GALLERY_CODE=11;
}
