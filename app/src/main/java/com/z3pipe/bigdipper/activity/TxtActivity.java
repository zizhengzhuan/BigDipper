package com.z3pipe.bigdipper.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.z3pipe.bigdipper.R;
import com.z3pipe.z3core.util.DateUtil;
import com.z3pipe.z3location.util.FileUtil;
import com.z3pipe.z3location.util.Textwriter;

import java.util.Date;

/**
 * @ProjectName: BigDipper
 * @Package: com.z3pipe.bigdipper.activity
 * @ClassName:
 * @Description:
 * @Author: gxx
 * @CreateDate: 2019/7/29 18:41
 * @Version: 1.0
 */
public class TxtActivity extends Activity {
    private TextView tvPosition;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_txt);

        tvPosition = findViewById(R.id.tv_positions);
        String content = Textwriter.readString(getPath());

        if(null != content && content.trim().length() != 0) {
            tvPosition.setText(content);
        }
    }

    private String getPath() {
        return FileUtil.getInstance(this).getConfPath() + DateUtil.getDate(new Date()) + ".txt";
    }
}
