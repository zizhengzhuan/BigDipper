package com.z3pipe.bigdipper.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.z3pipe.bigdipper.R;


public class InputDialog extends Dialog implements OnClickListener {
    private TextView tvTitle;
    private EditText etvRemark;
    private IOkBtnCallback mCallback;

    public InputDialog(Context context, IOkBtnCallback callback) {
        this(context, 0, callback);
    }

    public InputDialog(Context context, int theme, IOkBtnCallback callback) {
        super(context, theme);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_input);
        this.mCallback = callback;
        initView();
    }

    public interface IOkBtnCallback {
        void onOk(String remark);
    }

    private void initView() {
        tvTitle = (TextView) findViewById(R.id.custom_dialog_title);
        etvRemark = (EditText) findViewById(R.id.et_remark);
        findViewById(R.id.custom_dialog_ok).setOnClickListener(this);
        findViewById(R.id.custom_dialog_cancel).setOnClickListener(this);
    }

    public void setTitle(int resId) {
        this.tvTitle.setText(resId);
    }

    public void setTitle(String res) {
        this.tvTitle.setText(res);
    }

    public void setEtvHint(int resId) {
        this.etvRemark.setHint(resId);
    }

    public void setEtvHint(String server) {
        this.etvRemark.setHint(server);
    }

    public void setEtv(int resId) {
        this.etvRemark.setText(resId);
    }

    public void setEtv(String server) {
        this.etvRemark.setText(server);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.custom_dialog_ok:
            postEvent();
            break;
        case R.id.custom_dialog_cancel:
            dismiss();
            break;
        }
    }

    private void postEvent() {
        String remark = etvRemark.getText().toString().trim();
        if (remark.isEmpty()) {
            Toast.makeText(getContext(), "remark is null", Toast.LENGTH_SHORT).show();
            return;
        }
        mCallback.onOk(remark);
        dismiss();
    }
}
