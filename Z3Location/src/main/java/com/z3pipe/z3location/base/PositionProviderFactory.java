package com.z3pipe.z3location.base;

import android.content.Context;

import com.z3pipe.z3location.config.PositionCollectionConfig;
import com.z3pipe.z3location.content.GaoDePositionProvider;
import com.z3pipe.z3location.content.GpsPositionProvider;
import com.z3pipe.z3location.content.PositionProvider;

/**
 * Created with IntelliJ IDEA.
 * Description: ${TODO}
 * Date: 2019-04-13
 * Time: 19:16
 * Copyright © 2018 ZiZhengzhuan. All rights reserved.
 * https://www.z3pipe.com
 *
 * @author zhengzhuanzi
 */
public class PositionProviderFactory {

    public static PositionProvider creatPositionProvider(Context context, PositionCollectionConfig positionCollectionConfig, PositionProvider.PositionListener listener) {
        //TODO：支持不同类型的定位
        return new GaoDePositionProvider(context,positionCollectionConfig,listener);
    }

    public static PositionProvider creatGpsPositionProvider(Context context, PositionCollectionConfig positionCollectionConfig, PositionProvider.PositionListener listener) {
        //TODO：支持不同类型的定位
        return new GpsPositionProvider(context,positionCollectionConfig,listener);
    }
}
