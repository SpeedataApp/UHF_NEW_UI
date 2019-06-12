package com.speedata.uhf;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.speedata.libuhf.utils.SharedXmlUtil;

/**
 * @author zzc
 */
public class HelloActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);
        final Intent it = new Intent(this, NewMainActivity.class);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (MyApp.getInstance().getIuhfService() != null) {
                    SystemClock.sleep(2000);
                    startActivity(it);
                } else {
                    MyApp.getInstance().setIuhfService();
                    Log.d("UHFService", "startService");
                    startService(new Intent(HelloActivity.this, MyService.class));
                    SharedXmlUtil.getInstance(HelloActivity.this).write("server", true);
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
}
