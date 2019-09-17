package com.speedata.uhf.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.speedata.libuhf.IUHFService;
import com.speedata.libuhf.UHFManager;
import com.speedata.uhf.MyApp;
import com.speedata.uhf.R;

/**
 * @author zzc
 */
public class DefaultSettingDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private IUHFService iuhfService;
    private TextView mTvXhMode, mTvDefaultMode, mTvS0Mode, mTvS1Mode;

    public DefaultSettingDialog(@NonNull Context context, IUHFService iuhfService) {
        super(context);
        mContext = context;
        this.iuhfService = iuhfService;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_default_setting);
        mTvXhMode = findViewById(R.id.tv_xh_setting);
        mTvXhMode.setOnClickListener(this);
        mTvDefaultMode = findViewById(R.id.tv_default_setting);
        mTvDefaultMode.setOnClickListener(this);
        mTvS0Mode = findViewById(R.id.tv_s0_setting);
        mTvS0Mode.setOnClickListener(this);
        mTvS1Mode = findViewById(R.id.tv_s1_setting);
        mTvS1Mode.setOnClickListener(this);
        initData();
    }

    private void initData() {
        if ("xinlian".equals(UHFManager.getUHFModel())) {
            if (MyApp.isFastMode) {
                mTvXhMode.setEnabled(false);
                mTvDefaultMode.setEnabled(false);
                mTvS0Mode.setText(mContext.getString(R.string.btn_stop_fast));
                mTvS1Mode.setText(mContext.getString(R.string.btn_stop_fast));
            } else {
                mTvXhMode.setEnabled(true);
                mTvDefaultMode.setEnabled(true);
                mTvS0Mode.setText(mContext.getString(R.string.tv_s0_mode));
                mTvS1Mode.setText(mContext.getString(R.string.tv_s1_mode));
            }
        } else {
            mTvS0Mode.setVisibility(View.GONE);
            mTvS1Mode.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_xh_setting:
                setXHMode();
                break;
            case R.id.tv_default_setting:
                setDefaultMode();
                break;
            case R.id.tv_s0_setting:
                setS0FastMode();
                break;
            case R.id.tv_s1_setting:
                setS1FastMode();
                break;
            default:
                break;
        }
        dismiss();
    }

    /**
     * 续航模式
     */
    private void setXHMode() {
        int i;
        i = iuhfService.setAntennaPower(27);
        Log.d("zzc:", "===DefaultSettingDialog===setAntennaPower:" + i);
        i = iuhfService.setQueryTagGroup(0, 0, 0);
        Log.d("zzc:", "===DefaultSettingDialog===setQueryTagGroup:" + i);
        SystemClock.sleep(100);
        i = iuhfService.setReadTime(200);
        Log.d("zzc:", "===DefaultSettingDialog===setReadTime:" + i);
        i = iuhfService.setSleep(300);
        Log.d("zzc:", "===DefaultSettingDialog===setSleep:" + i);
    }

    /**
     * 远距离模式
     */
    private void setDefaultMode() {
        int i;
        i = iuhfService.setAntennaPower(30);
        Log.d("zzc:", "===DefaultSettingDialog===setAntennaPower:" + i);
        i = iuhfService.setQueryTagGroup(0, 0, 0);
        Log.d("zzc:", "===DefaultSettingDialog===setQueryTagGroup:" + i);
        SystemClock.sleep(100);
        i = iuhfService.setReadTime(100);
        Log.d("zzc:", "===DefaultSettingDialog===setReadTime:" + i);
        i = iuhfService.setSleep(50);
        Log.d("zzc:", "===DefaultSettingDialog===setSleep:" + i);
    }

    /**
     * S0快速模式
     */
    private void setS0FastMode() {
        int i;
        if (MyApp.isFastMode) {
            i = iuhfService.stopFastMode();
            if (i == 0) {
                MyApp.isFastMode = false;
                mTvS0Mode.setText(mContext.getString(R.string.tv_s0_mode));
            }
        } else {
            i = iuhfService.setAntennaPower(30);
            Log.d("zzc:", "===DefaultSettingDialog===setAntennaPower:" + i);
            i = iuhfService.setQueryTagGroup(0, 0, 0);
            Log.d("zzc:", "===DefaultSettingDialog===setQueryTagGroup:" + i);
            SystemClock.sleep(100);
            i = iuhfService.startFastMode();
            if (i == 0) {
                MyApp.isFastMode = true;
                mTvS0Mode.setText(mContext.getString(R.string.btn_stop_fast));
            }
            Log.d("zzc:", "===DefaultSettingDialog===startFastMode:" + i);
        }
    }

    /**
     * S1快速模式
     */
    private void setS1FastMode() {
        int i;
        if (MyApp.isFastMode) {
            i = iuhfService.stopFastMode();
            if (i == 0) {
                MyApp.isFastMode = false;
                mTvS1Mode.setText(mContext.getString(R.string.tv_s1_mode));
            }
        } else {
            i = iuhfService.setAntennaPower(30);
            Log.d("zzc:", "===DefaultSettingDialog===setAntennaPower:" + i);
            i = iuhfService.setQueryTagGroup(0, 1, 0);
            Log.d("zzc:", "===DefaultSettingDialog===setQueryTagGroup:" + i);
            SystemClock.sleep(100);
            i = iuhfService.startFastMode();
            if (i == 0) {
                MyApp.isFastMode = true;
                mTvS1Mode.setText(mContext.getString(R.string.btn_stop_fast));
            }
            Log.d("zzc:", "===DefaultSettingDialog===startFastMode:" + i);
        }
    }
}
