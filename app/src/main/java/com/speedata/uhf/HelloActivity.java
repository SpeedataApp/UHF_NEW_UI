package com.speedata.uhf;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.speedata.libuhf.IUHFService;
import com.speedata.libuhf.UHFManager;
import com.speedata.libuhf.utils.SharedXmlUtil;

/**
 * @author zzc
 */
public class HelloActivity extends Activity {

    private IUHFService iuhfService;
    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //强制为竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_hello);
        final Intent it = new Intent(this, NewMainActivity.class);
        final String xinghao = SystemProperties.get("ro.product.model");
        runnable = new Runnable() {
            @Override
            public void run() {
                if (MyApp.getInstance().getIuhfService() != null) {
                    SystemClock.sleep(2000);
                    startActivity(it);
                } else {
                    MyApp.getInstance().setIuhfService();
                    iuhfService = MyApp.getInstance().getIuhfService();
                    if (iuhfService == null) {
                        return;
                    }
                    try {
                        if (iuhfService != null) {
                            MyApp.isOpenDev = openDev();
                            if (MyApp.isOpenDev) {
                                initParam();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    SystemClock.sleep(100);
                    if ("SD60".equals(xinghao) || "SD60RT".equals(xinghao) || xinghao.contains("KT50") || xinghao.contains("KT55")
                            || "SD55L".equals(xinghao) || "SD55UHF".equals(xinghao)) {
                        Log.d("UHFService", "startService==main==");
                        startService(new Intent(HelloActivity.this, MyService.class));
                        SharedXmlUtil.getInstance(HelloActivity.this).write("server", true);
                    }
                    SystemClock.sleep(1000);
                    startActivity(it);
                }
            }
        };
        handler.postDelayed(runnable, 500);
    }

    private void initParam() {
        int i;
        i = iuhfService.setFreqRegion(SharedXmlUtil.getInstance(this).read(MyApp.UHF_FREQ, 1));
        Log.d("zzc:", "===isFirstInit===setFreqRegion:" + i);
        SystemClock.sleep(600);
        Log.d("zzc:", "===isFirstInit===setFreqRegion:" + iuhfService.getFreqRegion());
        i = iuhfService.setAntennaPower(SharedXmlUtil.getInstance(this).read(MyApp.UHF_POWER, 30));
        Log.d("zzc:", "===isFirstInit===setAntennaPower:" + i);
        SystemClock.sleep(100);
        if (!UHFManager.getUHFModel().equals(UHFManager.FACTORY_YIXIN)) {
            i = iuhfService.setQueryTagGroup(0, SharedXmlUtil.getInstance(this).read(MyApp.UHF_SESSION, 0), 0);
            Log.d("zzc:", "===isFirstInit===setQueryTagGroup:" + i);
            SystemClock.sleep(100);
            i = iuhfService.setInvMode(SharedXmlUtil.getInstance(this).read(MyApp.UHF_INV_CON, 0), 0, 6);
            Log.d("zzc:", "===isFirstInit===setInvMode:" + i);
        }
        if (UHFManager.getUHFModel().contains(UHFManager.FACTORY_XINLIAN)) {
            iuhfService.setLowpowerScheduler(SharedXmlUtil.getInstance(this).read(MyApp.UHF_INV_TIME, 50), SharedXmlUtil.getInstance(this).read(MyApp.UHF_INV_SLEEP, 0));
        }
        SystemClock.sleep(100);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
        finish();
        super.onPause();
    }

    /**
     * 上电开串口
     */
    private boolean openDev() {
        if (!MyApp.isOpenDev) {
            if (iuhfService.openDev() != 0) {
                Toast.makeText(this, "Open serialport failed", Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(this).setTitle(R.string.DIA_ALERT).setMessage(R.string.DEV_OPEN_ERR).setPositiveButton(R.string.DIA_CHECK, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
                return false;
            }
            return true;
        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
