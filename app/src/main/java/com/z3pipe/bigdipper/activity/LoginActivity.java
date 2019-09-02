package com.z3pipe.bigdipper.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.enn.sop.IUserInfoAidlInterface;
import com.z3pipe.bigdipper.R;
import com.z3pipe.bigdipper.content.DeviceIdentityProvider;
import com.z3pipe.bigdipper.model.Constance;
import com.z3pipe.bigdipper.ui.dialog.InputDialog;
import com.z3pipe.bigdipper.util.PermissionManager;
import com.z3pipe.z3location.util.SettingsManager;
import com.z3pipe.z3location.util.StringUtil;
import com.z3pipe.z3location.config.PositionCollectionConfig;
import com.z3pipe.z3location.controller.TrackingConfigController;
import com.z3pipe.z3location.service.WatchDogService;
import com.z3pipe.z3location.util.Constants;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author zhengzhuanzi
 * @link https://www.z3pipe.com
 * @date 2019-04-10
 */
public class LoginActivity extends Activity {
    private IUserInfoAidlInterface mService;
    private static final String BIND_ACTION = "com.young.server.START_AIDL_SERVICE";
    private static final int LOGIN_SUCCESS = 1;
    private static final int LOGIN_FAILED = 2;

    private LinearLayout layoutLoginSetting;
    private EditText etUsername;
    private EditText etPassword;
    private ImageView imgClearInput;
    private TextView tvVersion;
    private String username;
    private String password;
    private Button btnLogin;
    private ImageButton lineimg;

    private CheckBox cbAutoLogin;
    private CheckBox cbRememberPassword;
    private boolean doesAutoLogin;
    private boolean doesRememberPassword;
    private long mHints[];
    private LoginHandler loginHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initUI();
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
        //stopGuardService();
       // unbindService(mServiceConnection);
    }

    class LoginHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOGIN_SUCCESS:
                    showToast(getApplicationContext().getResources().getString(R.string.login_success));
                    SettingsManager.getInstance().setLastUser(LoginActivity.this, username, password, doesRememberPassword);
                    SettingsManager.getInstance().setAutoLogin(LoginActivity.this, doesAutoLogin);
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    setUserInfo(jsonObject);
                    goToMainActivity();
                    break;
                case LOGIN_FAILED:
                    showToast(getString(R.string.login_fail_try_later));
                    break;
                default:
                    break;
            }
        }
    }

    private void setUserInfo(JSONObject jsonObject) {
        JSONObject userJson = jsonObject.optJSONObject("user");
        int userId = userJson.optInt("id");
        String userName = userJson.optString("username");
        String trueName = userJson.optString("trueName");
        if(0 == userId) {
            return;
        }
        Constants.USERID = String.valueOf(userId);
        Constants.USER_NAME = userName;
        Constants.TRUE_NAME = trueName;
    }

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    private void initUI() {
        layoutLoginSetting = (LinearLayout) findViewById(R.id.layout_settings);
        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        loadLastUserInfo();
        tvVersion = (TextView) findViewById(R.id.tv_version);
        imgClearInput = (ImageView) findViewById(R.id.img_clear_input);
        cbRememberPassword = (CheckBox) findViewById(R.id.cb_remember_password);
        cbRememberPassword.setOnCheckedChangeListener(rememberPasswordCheckedChangListener);
        cbRememberPassword.setChecked(SettingsManager.getInstance().getRememberPassword(LoginActivity.this));
        cbAutoLogin = (CheckBox) findViewById(R.id.cb_auto_login);
        cbAutoLogin.setOnCheckedChangeListener(autoLoginCheckedChangListener);
        cbAutoLogin.setChecked(SettingsManager.getInstance().getAutoLogin(LoginActivity.this));

        tvVersion.setText("v0.0.1");
        tvVersion.setVisibility(View.VISIBLE);
        UserInfoTextWatcher textWatcher = new UserInfoTextWatcher();
        etUsername.addTextChangedListener(textWatcher);
        etPassword.addTextChangedListener(textWatcher);
        lineimg = (ImageButton) findViewById(R.id.lineimg);

        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setEnabled(txtUserNameEditTextFieldIsValid() && txtPasswordEditTextFieldIsValid());
        // click the version TextView five times;
        mHints = new long[5];
        loginHandler = new LoginHandler();
    }

    private CompoundButton.OnCheckedChangeListener rememberPasswordCheckedChangListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!isChecked) {
                cbAutoLogin.setChecked(false);
            }
            doesRememberPassword = isChecked;
        }
    };

    private CompoundButton.OnCheckedChangeListener autoLoginCheckedChangListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                cbRememberPassword.setChecked(true);
            }
            doesAutoLogin = isChecked;
        }
    };

    private void startLogin(String username, String password) {
        showToast(getResources().getString(R.string.login_logging_in));
        new Thread(testConnectionTask).start();
    }

    Runnable testConnectionTask = new Runnable() {
        @Override
        public void run() {
            try {
                String url = SettingsManager.getInstance().getHbpBaseServer(LoginActivity.this);
                if(StringUtil.isBlank(url)) {
                    url = "http://svr02.sz-hkcw.com:8090/ServiceEngine/rest/userService/login";
                }
                url = url + "?sys=android&_type=json&username="+ username + "&password=" + password;
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
                httpURLConnection.setConnectTimeout(10 * 1000);
                Message message = Message.obtain();
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream is = httpURLConnection.getInputStream();
                    //5. 解析is，获取responseText，这里用缓冲字符流
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while((line=reader.readLine()) != null){
                        sb.append(line);
                    }
                    //获取响应文本
                    String responseText = sb.toString();
                    JSONObject jsonObject = new JSONObject(responseText);
                    boolean success = jsonObject.optBoolean("isSuccess");
                    if (success) {
                        message.what = LOGIN_SUCCESS;
                        message.obj = jsonObject;
                    } else {
                        message.what = LOGIN_FAILED;
                        message.obj = httpURLConnection.getResponseMessage();
                    }
                } else {
                    message.what = LOGIN_FAILED;
                    message.obj = httpURLConnection.getResponseMessage();
                }
                loginHandler.sendMessage(message);
            } catch (Exception e) {
                Message message = Message.obtain();
                message.what = LOGIN_FAILED;
                loginHandler.sendMessage(message);
            }
        }
    };

    private class UserInfoTextWatcher implements TextWatcher {
        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            etUsername.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        etPassword.setText("");
                    }
                    return false;
                }
            });
            if (txtUserNameEditTextFieldIsValid()) {
                imgClearInput.setVisibility(View.VISIBLE);
            }

            btnLogin.setEnabled(txtUserNameEditTextFieldIsValid() && txtPasswordEditTextFieldIsValid());
        }

    }

    private boolean txtUserNameEditTextFieldIsValid() {
        return !TextUtils.isEmpty(etUsername.getText());
    }

    private boolean txtPasswordEditTextFieldIsValid() {
        return !TextUtils.isEmpty(etPassword.getText());
    }

    public void onClearInputClicked(View view) {
        etUsername.setText("");
        etPassword.setText("");
        imgClearInput.setVisibility(View.GONE);
    }

    public void onLoginSettingBtnClicked(View view) {
        String baseUrl = SettingsManager.getInstance().getHbpBaseServer(LoginActivity.this);
        InputDialog dialog = new InputDialog(LoginActivity.this, new InputDialog.IOkBtnCallback() {
            @Override
            public void onOk(String remark) {
                SettingsManager.getInstance().setHbpBaseServer(LoginActivity.this, remark);
            }
        });
        dialog.setTitle("当前登录url");
        if(StringUtil.isBlank(baseUrl)) {
            dialog.setEtv("http://svr02.sz-hkcw.com:8090/ServiceEngine/rest/userService/login");
        } else {
            dialog.setEtv(baseUrl);
        }
        dialog.show();
    }

    public void onLoginBtnClicked(View view) {
        username = etUsername.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        if (isUserInfoEmpty()) {
            return;
        }

        startLogin(username, password);
    }

    public void onDisplaySettingButton(View view) {
        System.arraycopy(mHints, 1, mHints, 0, mHints.length - 1);
        mHints[mHints.length - 1] = SystemClock.uptimeMillis();
        if (SystemClock.uptimeMillis() - mHints[0] <= 1000) {
            lineimg.setVisibility(View.VISIBLE);
            layoutLoginSetting.setVisibility(View.VISIBLE);
        }
    }

    private void loadLastUserInfo() {
        String lastUser = SettingsManager.getInstance().getLastUser(LoginActivity.this);
        String lastPwd = SettingsManager.getInstance().getLastPWD(LoginActivity.this);

        etUsername.setText(lastUser);
        etPassword.setText(lastPwd);
    }

    private boolean isUserInfoEmpty() {
        if (isEmpty(username)) {
            showToast("username is empty");
            return true;
        }

        if (isEmpty(password)) {
            showToast("password is empty");
            return true;
        }

        return false;
    }

    private void showToast(String string) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    private boolean isEmpty(String string) {
        return null == string || string.trim().length() == 0;
    }

    public void stopGuardService() {
        Intent serviceIntent = new Intent(getApplicationContext(), WatchDogService.class);
        getApplicationContext().stopService(serviceIntent);
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

        Intent serviceIntent = new Intent(getApplicationContext(), WatchDogService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getApplicationContext().startForegroundService(serviceIntent);
        } else {
            getApplicationContext().startService(serviceIntent);
        }
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
