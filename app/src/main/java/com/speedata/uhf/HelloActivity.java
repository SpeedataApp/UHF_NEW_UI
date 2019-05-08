package com.speedata.uhf;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.speedata.libuhf.IUHFService;
import com.speedata.libuhf.UHFManager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author zzc
 */
public class HelloActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);
        final Intent it = new Intent(this, NewMainActivity.class);
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                //执行意图
                startActivity(it);
            }
        };
        //3秒后跳转，这里根据自己需要设定时间
        timer.schedule(task, 1000);

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
