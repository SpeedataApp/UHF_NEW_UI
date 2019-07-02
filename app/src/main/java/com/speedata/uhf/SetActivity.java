package com.speedata.uhf;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.speedata.libuhf.IUHFService;
import com.speedata.libuhf.utils.SharedXmlUtil;


/**
 * 设置跳转界面
 * Created by 张智超 on 2019/3/7
 *
 * @author 张智超
 */
public class SetActivity extends BaseActivity {

    Intent intent;
    private IUHFService iuhfService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);
        final Intent it = new Intent(this, InvSetActivity.class);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (MyApp.getInstance().getIuhfService() == null) {
                    MyApp.getInstance().setIuhfService();
                    iuhfService = MyApp.getInstance().getIuhfService();
                    try {
                        if (iuhfService != null) {
                            if (openDev()) {
                                return;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.d("UHFService", "startService");
                    startService(new Intent(SetActivity.this, MyService.class));
                    SharedXmlUtil.getInstance(SetActivity.this).write("server", true);
                    startActivity(it);
                } else if (SharedXmlUtil.getInstance(SetActivity.this).read("server", false)) {
                    SystemClock.sleep(1000);
                    startActivity(it);
                } else if (!SharedXmlUtil.getInstance(SetActivity.this).read("server", false)) {
                    Log.d("UHFService", "startService");
                    startService(new Intent(SetActivity.this, MyService.class));
                    SharedXmlUtil.getInstance(SetActivity.this).write("server", true);
                    SystemClock.sleep(1000);
                    startActivity(it);
                }
            }
        }).start();
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
        super.onPause();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 上电开串口
     */
    private boolean openDev() {
        if (iuhfService.openDev() != 0) {
            Toast.makeText(this, "Open serialport failed", Toast.LENGTH_SHORT).show();
            new AlertDialog.Builder(this).setTitle(R.string.DIA_ALERT).setMessage(R.string.DEV_OPEN_ERR).setPositiveButton(R.string.DIA_CHECK, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).show();
            return true;
        }
        return false;
    }
}
