package com.speedata.uhf.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.speedata.libuhf.IUHFService;
import com.speedata.libuhf.UHFManager;
import com.speedata.uhf.MyApp;
import com.speedata.uhf.R;
import com.speedata.uhf.libutils.ToastUtil;

/**
 * @author zzc
 */
public class DefaultSettingDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private IUHFService iuhfService;
    private TextView mTvXhMode, mTvDefaultMode, mTvS0Mode, mTvS1Mode;
    private boolean isSuccess = false;

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
        if (UHFManager.getUHFModel().contains(UHFManager.FACTORY_XINLIAN)) {
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
                normalTips(isSuccess);
                break;
            case R.id.tv_default_setting:
                setDefaultMode();
                normalTips(isSuccess);
                break;
            case R.id.tv_s0_setting:
                setS0FastMode();
                fastTips(isSuccess);
                break;
            case R.id.tv_s1_setting:
                setS1FastMode();
                fastTips(isSuccess);
                break;
            default:
                break;
        }


    }

    /**
     * 续航模式
     */
    private void setXHMode() {
        int i;
        i = iuhfService.setAntennaPower(27);
        if (i != 0) {
            isSuccess = false;
            return;
        }
        if (!UHFManager.FACTORY_YIXIN.equals(UHFManager.getUHFModel())) {
            i = iuhfService.setQueryTagGroup(0, 0, 0);
            if (i != 0) {
                isSuccess = false;
                return;
            }
            SystemClock.sleep(100);
        }
        if (UHFManager.getUHFModel().contains(UHFManager.FACTORY_XINLIAN)) {
            i = iuhfService.setLowpowerScheduler(200, 300);
            if (i != 0) {
                isSuccess = false;
                return;
            }
        }
        isSuccess = true;
    }

    /**
     * 远距离模式
     */
    private void setDefaultMode() {
        int i;
        i = iuhfService.setAntennaPower(33);
        if (i != 0) {
            i = iuhfService.setAntennaPower(30);
            if (i != 0) {
                isSuccess = false;
                return;
            }
        }
        if (!UHFManager.FACTORY_YIXIN.equals(UHFManager.getUHFModel())) {
            i = iuhfService.setQueryTagGroup(0, 0, 0);
            if (i != 0) {
                isSuccess = false;
                return;
            }
            SystemClock.sleep(100);
        }
        if (UHFManager.getUHFModel().contains(UHFManager.FACTORY_XINLIAN)) {
            i = iuhfService.setLowpowerScheduler(50, 0);
            if (i != 0) {
                isSuccess = false;
                return;
            }
        }
        isSuccess = true;
    }

    /**
     * S0快速模式
     */
    private void setS0FastMode() {
        int i;
        if (MyApp.isFastMode) {
            isSuccess = false;
            i = iuhfService.switchInvMode(2);
            if (i == 0) {
                MyApp.isFastMode = false;
                mTvS0Mode.setText(mContext.getString(R.string.tv_s0_mode));
            }
        } else {
            isSuccess = true;
            i = iuhfService.setAntennaPower(30);
            if (i != 0) {
                return;
            }
            i = iuhfService.setQueryTagGroup(0, 0, 0);
            if (i != 0) {
                return;
            }
            SystemClock.sleep(100);
            i = iuhfService.switchInvMode(1);
            if (i != 0) {
                return;
            }
            MyApp.isFastMode = true;
            mTvS0Mode.setText(mContext.getString(R.string.btn_stop_fast));
        }
    }

    /**
     * S1快速模式
     */
    private void setS1FastMode() {
        int i;
        if (MyApp.isFastMode) {
            isSuccess = false;
            i = iuhfService.switchInvMode(2);
            if (i == 0) {
                MyApp.isFastMode = false;
                mTvS1Mode.setText(mContext.getString(R.string.tv_s1_mode));
            }
        } else {
            isSuccess = true;
            i = iuhfService.setAntennaPower(30);
            if (i != 0) {
                return;
            }
            i = iuhfService.setQueryTagGroup(0, 1, 0);
            if (i != 0) {
                return;
            }
            SystemClock.sleep(100);
            i = iuhfService.switchInvMode(1);
            if (i != 0) {
                return;
            }
            MyApp.isFastMode = true;
            mTvS1Mode.setText(mContext.getString(R.string.btn_stop_fast));
        }
    }

    private void normalTips(boolean isSuccess) {
        if (isSuccess) {
            ToastUtil.customToastView(mContext, mContext.getResources().getString(R.string.set_success), Toast.LENGTH_SHORT
                    , (TextView) LayoutInflater.from(mContext).inflate(R.layout.layout_toast, null));
            dismiss();
        } else {
            ToastUtil.customToastView(mContext, mContext.getResources().getString(R.string.set_failed), Toast.LENGTH_SHORT
                    , (TextView) LayoutInflater.from(mContext).inflate(R.layout.layout_toast, null));
        }
    }

    private void fastTips(boolean isOpenFast) {
        if (isOpenFast) {
            if (MyApp.isFastMode) {
                ToastUtil.customToastView(mContext, mContext.getResources().getString(R.string.toast_start_fast_success), Toast.LENGTH_SHORT
                        , (TextView) LayoutInflater.from(mContext).inflate(R.layout.layout_toast, null));
                dismiss();
            } else {
                ToastUtil.customToastView(mContext, mContext.getResources().getString(R.string.toast_start_fast_failed), Toast.LENGTH_SHORT
                        , (TextView) LayoutInflater.from(mContext).inflate(R.layout.layout_toast, null));
            }
        } else {
            if (MyApp.isFastMode) {
                ToastUtil.customToastView(mContext, mContext.getResources().getString(R.string.toast_stop_fast_failed), Toast.LENGTH_SHORT
                        , (TextView) LayoutInflater.from(mContext).inflate(R.layout.layout_toast, null));
            } else {
                ToastUtil.customToastView(mContext, mContext.getResources().getString(R.string.toast_stop_fast_success), Toast.LENGTH_SHORT
                        , (TextView) LayoutInflater.from(mContext).inflate(R.layout.layout_toast, null));
                dismiss();
            }
        }
    }

}
