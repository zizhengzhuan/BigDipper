package com.z3pipe.z3location.content;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.z3pipe.z3location.broadcast.GpsLocationListener;
import com.z3pipe.z3location.config.PositionCollectionConfig;

/**
 * @ProjectName: app_prj
 * @Package: com.zzht.position.provider
 * @ClassName: GpsPositionProvider
 * @Description: java类作用描述
 * @Author: gm
 * @CreateDate: 2019/8/9 17:27
 * @UpdateUser: gm
 * @UpdateDate: 2019/8/9 17:27
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class GpsPositionProvider extends PositionProvider implements GpsLocationListener {

    public GpsPositionProvider(Context context, PositionCollectionConfig positionCollectionConfig, PositionListener listener) {
        super(context, positionCollectionConfig, listener);
        GpsPosHelper.getInstance().init(context);
    }
    /**
     * 开始定位
     */
    @Override
    public void startUpdates() {

        GpsPosHelper.getInstance().startLocation(this);

    }

    /**
     * 停止定位
     */
    @Override
    public void stopUpdates() {
        GpsPosHelper.getInstance().stopLocation();
    }

    /**
     * 定位一次
     */
    @Override
    public void requestSingleLocation() {

    }

    @Override
    public void onLocationChanged(Location location) {
        processLocation(location);
    }
}
