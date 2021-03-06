package com.z3pipe.bigdipper.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.z3pipe.bigdipper.R;
import com.z3pipe.bigdipper.util.ResponseUtil;
import com.z3pipe.z3location.util.SettingsManager;
import com.z3pipe.z3location.util.StringUtil;
import com.z3pipe.z3location.util.Constants;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class LoginDialog extends Dialog{
    private static final int LOGIN_SUCCESS = 1;
    private static final int LOGIN_FAILED = 2;

    private Context context;
    private EditText etUsername;
    private EditText etPassword;
    private ImageView imgClearInput;
    private String username;
    private String password;
    private Button btnLogin;

    private CheckBox cbRememberPassword;
    private TextView tvIPSetting;
    private boolean doesRememberPassword;
    private LoginHandler loginHandler;

    public LoginDialog(Context context) {
        this(context, 0);
        this.context = context;
    }

    public LoginDialog(Context context, int theme) {
        super(context, theme);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_login);
        initView();
        bindEvent();
    }

    private void initView() {
        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        loadLastUserInfo();
        imgClearInput = (ImageView) findViewById(R.id.img_clear_input);
        cbRememberPassword = (CheckBox) findViewById(R.id.cb_remember_password);
        cbRememberPassword.setOnCheckedChangeListener(rememberPasswordCheckedChangListener);
        cbRememberPassword.setChecked(SettingsManager.getInstance().getRememberPassword(getContext()));
        tvIPSetting = (TextView) findViewById(R.id.tv_setting);

        UserInfoTextWatcher textWatcher = new UserInfoTextWatcher();
        etUsername.addTextChangedListener(textWatcher);
        etPassword.addTextChangedListener(textWatcher);

        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setEnabled(txtUserNameEditTextFieldIsValid() && txtPasswordEditTextFieldIsValid());
        loginHandler = new LoginHandler();

    }

    private void bindEvent() {
        imgClearInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etUsername.setText("");
                etPassword.setText("");
                imgClearInput.setVisibility(View.GONE);
            }
        });

        tvIPSetting.setVisibility(View.GONE);
        tvIPSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIp();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginBtnClicked();
            }
        });


    }

    private void setIp() {
        InputDialog dialog = new InputDialog(context, new InputDialog.IOkBtnCallback() {
            @Override
            public void onOk(String remark) {
                SettingsManager.getInstance().setHbpBaseServer(context, remark);
                if(remark.contains("ibpsApi")) {
                    SettingsManager.getInstance().setHBPLogin(context, true);
                } else {
                    SettingsManager.getInstance().setHBPLogin(context, false);
                }
            }
        });
        dialog.setTitle("当前登录url");
        String baseUrl = SettingsManager.getInstance().getHbpBaseServer(context);
        if(StringUtil.isBlank(baseUrl)) {
            dialog.setEtv("http://svr02.sz-hkcw.com:8090/ServiceEngine/rest/userService/login");
        } else {
            dialog.setEtv(baseUrl);
        }
        dialog.show();
    }

    private CompoundButton.OnCheckedChangeListener rememberPasswordCheckedChangListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            doesRememberPassword = isChecked;
        }
    };


    private void startLogin(String username, String password) {
        showToast(getContext().getResources().getString(R.string.login_logging_in));
        new Thread(testConnectionTask).start();
    }

    Runnable testConnectionTask = new Runnable() {
        @Override
        public void run() {
            try {
                String url = SettingsManager.getInstance().getHbpBaseServer(getContext());
                boolean hbpLogin = SettingsManager.getInstance().getHBPLogin(getContext());
                if(StringUtil.isBlank(url)) {
//                    url = "http://svr02.sz-hkcw.com:8090/ServiceEngine/rest/userService/login";
                    url = "http://svr02.sz-hkcw.com:8090/ServiceEngine/rest/userService/login";
                }
                if(hbpLogin) {
                    url = url + "?account="+ username + "&pwd=" + password;
                } else {
                    url = url + "?sys=android&_type=json&username="+ username + "&password=" + password;
                }
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
                httpURLConnection.setConnectTimeout(30 * 1000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Accept", "application/json");
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
                    if(ResponseUtil.success(jsonObject)) {
                        message.what = LOGIN_SUCCESS;
                        message.obj = jsonObject;
                    } else {
                        message.what = LOGIN_FAILED;
                        message.obj = ResponseUtil.getErrorMsg(jsonObject);
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

    public void onLoginBtnClicked() {
        username = etUsername.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        if (isUserInfoEmpty()) {
            return;
        }

        startLogin(username, password);
    }

    private void loadLastUserInfo() {
        String lastUser = SettingsManager.getInstance().getLastUser(getContext());
        String lastPwd = SettingsManager.getInstance().getLastPWD(getContext());

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
        Toast.makeText(getContext(), string, Toast.LENGTH_SHORT).show();
    }

    private boolean isEmpty(String string) {
        return null == string || string.trim().length() == 0;
    }

    class LoginHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOGIN_SUCCESS:
                    showToast(getContext().getResources().getString(R.string.login_success));
                    SettingsManager.getInstance().setLastUser(getContext(), username, password, doesRememberPassword);
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    boolean hbpLogin = SettingsManager.getInstance().getHBPLogin(getContext());
                    if(hbpLogin) {
                        setHbpUserInfo(jsonObject);
                    } else {
                        setUserInfo(jsonObject);
                    }
                    dismiss();
                    break;
                case LOGIN_FAILED:
                    String error = (String) msg.obj;
                    if(StringUtil.isEmpty(error)) {
                        error = getContext().getResources().getString(R.string.login_fail_try_later);
                    }
                    showToast(error);
                    break;
                default:
                    break;
            }
        }
    }

    private void setHbpUserInfo(JSONObject jsonObject) {
        JSONObject userJson = jsonObject.optJSONObject("data");
        if(null == userJson) {
            return;
        }
        String userId = userJson.optString("id");
        String userName = userJson.optString("account");
        String trueName = userJson.optString("fullName");

        Constants.USERID = String.valueOf(userId);
        Constants.USER_NAME = userName;
        Constants.TRUE_NAME = trueName;
        SettingsManager.getInstance().setUserId(getContext(), Constants.USERID);
    }

    private void setUserInfo(JSONObject jsonObject) {
        JSONObject userJson = jsonObject.optJSONObject("user");
        if(null == userJson) {
            return;
        }
        int userId = userJson.optInt("id");
        String userName = userJson.optString("username");
        String trueName = userJson.optString("trueName");
        if(0 == userId) {
            return;
        }
        Constants.USERID = String.valueOf(userId);
        Constants.USER_NAME = userName;
        Constants.TRUE_NAME = trueName;
        SettingsManager.getInstance().setUserId(getContext(), Constants.USERID);
    }

}
