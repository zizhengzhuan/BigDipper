package com.z3pipe.z3location.broadcast;

import android.location.Location;

/**
 * @ProjectName: app_prj
 * @Package: com.zzht.position.struct
 * @ClassName: GpsLocationListener
 * @Description: java类作用描述
 * @Author: gm
 * @CreateDate: 2019/8/9 17:48
 * @UpdateUser: gm
 * @UpdateDate: 2019/8/9 17:48
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public interface GpsLocationListener {

    void onLocationChanged(Location location);
}
