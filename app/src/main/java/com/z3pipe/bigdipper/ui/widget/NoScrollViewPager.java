package com.z3pipe.bigdipper.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

/**
 * @ProjectName: BigDipper
 * @Package: com.z3pipe.bigdipper.ui.widget
 * @ClassName:
 * @Description:
 * @Author: gxx
 * @CreateDate: 2019/8/12 14:35
 * @Version: 1.0
 */
public class NoScrollViewPager extends ViewPager {

    public NoScrollViewPager(@NonNull Context context) {
        super(context);
    }

    public NoScrollViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
}
