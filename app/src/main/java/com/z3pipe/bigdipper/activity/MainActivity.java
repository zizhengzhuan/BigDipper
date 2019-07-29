package com.z3pipe.bigdipper.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.enn.sop.IUserInfoAidlInterface;
import com.xdandroid.hellodaemon.DaemonEnv;
import com.z3pipe.bigdipper.content.DeviceIdentityProvider;
import com.z3pipe.bigdipper.fragment.MainFragment;
import com.z3pipe.bigdipper.model.Constance;
import com.z3pipe.bigdipper.util.PermissionManager;
import com.z3pipe.z3location.config.PositionCollectionConfig;
import com.z3pipe.z3location.controller.TrackingConfigController;
import com.z3pipe.z3location.service.PositionService;
import com.z3pipe.z3location.service.WatchDogService;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author zhengzhuanzi
 * @link https://www.z3pipe.com
 * @date 2019-04-10
 */
public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private IUserInfoAidlInterface mService;
    private static final String BIND_ACTION = "com.young.server.START_AIDL_SERVICE";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().replace(android.R.id.content, new MainFragment()).commit();
        }
        checkWritePermission();
        //bindService();
    }

    @Override
    protected void onStart() {
        //startGuardService();
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopGuardService();
       // unbindService(mServiceConnection);
    }

    public void stopGuardService() {
//        Intent serviceIntent = new Intent(getApplicationContext(), WatchDogService.class);
//        getApplicationContext().stopService(serviceIntent);
    }

    public void startGuardService() {
        String deviceId = DeviceIdentityProvider.getInstance(getApplicationContext()).getDeviceId();
        //String deviceId = "abc";
        Log.e("SetUserInfoService", deviceId);

        PositionCollectionConfig positionCollectionConfig = TrackingConfigController.getPositionCollectionConfig(this);
        positionCollectionConfig.setDeviceId(deviceId);
        positionCollectionConfig.setHost("www.z3pipe.com");
        //positionCollectionConfig.setHost("10.39.0.36");
        //positionCollectionConfig.setPort(12345);
        //positionCollectionConfig.setHost("123.58.243.12");
        positionCollectionConfig.setPort(2437);
        TrackingConfigController.savePositionCollectionConfig(this, positionCollectionConfig);

//        Intent serviceIntent = new Intent(getApplicationContext(), WatchDogService.class);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            getApplicationContext().startForegroundService(serviceIntent);
//        } else {
//            getApplicationContext().startService(serviceIntent);
//        }
    }

    /**
     * 检查读写权限权限
     */
    private void checkWritePermission() {
        boolean result = PermissionManager.checkPermission(this, Constance.PERMS_WRITE);
        if (!result) {
            PermissionManager.requestPermission(this, Constance.WRITE_PERMISSION_TIP, Constance.WRITE_PERMISSION_CODE, Constance.PERMS_WRITE);
        }
    }

    /**
     * 重写onRequestPermissionsResult，用于接受请求结果
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //将请求结果传递EasyPermission库处理
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * 请求权限成功
     *
     * @param requestCode
     * @param perms
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        startGuardService();
        DaemonEnv.startServiceMayBind(PositionService.class);
    }

    /**
     * 请求权限失败
     *
     * @param requestCode
     * @param perms
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        /**
         * 若是在权限弹窗中，用户勾选了'NEVER ASK AGAIN.'或者'不在提示'，且拒绝权限。
         * 这时候，需要跳转到设置界面去，让用户手动开启。
         */
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                backToHome();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backToHome();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backToHome();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 返回到桌面
     */
    private void backToHome() {
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }

}
