package com.speedata.uhf;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;

import com.speedata.libuhf.UHFManager;
import com.speedata.libuhf.interfaces.OnSpdBanMsgListener;
import com.speedata.uhf.floatball.FloatWarnManager;
import com.speedata.uhf.libutils.SharedXmlUtil;
import com.yhao.floatwindow.FloatWindow;


/**
 * @author zzc
 */
public class BaseActivity extends Activity {
    public static boolean isLowPower = false;
    public static boolean isHighTemp = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //强制为竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setBuilder();
    }

    private void setBuilder() {
        UHFManager uhfManager = new UHFManager();
        uhfManager.setOnBanMsgListener(new OnSpdBanMsgListener() {
            @Override
            public void getBanMsg(String var1) {
                Log.e("zzc:UHFService", "====监听报警====");
                if (var1.contains("Low")) {
                    isLowPower = true;
                    var1 = BaseActivity.this.getResources().getString(R.string.low_power);
                } else if (var1.contains("High")) {
                    isHighTemp = true;
                    var1 = BaseActivity.this.getResources().getString(R.string.high_temp);
                }
                final String finalVar = var1;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FloatWarnManager.getInstance(getApplicationContext(), finalVar);
                        FloatWarnManager floatWarnManager = FloatWarnManager.getFloatWarnManager();
                        if (floatWarnManager != null) {
                            FloatWindow.get("FloatWarnTag").show();
                        }
                    }
                });
            }
        });

    }
}
