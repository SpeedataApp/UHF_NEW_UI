package com.speedata.uhf;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

public class UHFReceiver extends Activity {

    private TextView textView;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();
            if ("com.se4500.onDecodeComplete".equals(intentAction)) {
                Bundle bundle = intent.getExtras();
                String epc = bundle.getString("se4500");
                textView.setText(epc);
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receiver_uhf);

        //注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.se4500.onDecodeComplete");
        registerReceiver(receiver, filter);

        textView = (TextView) findViewById(R.id.tv_receiver);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销广播
        unregisterReceiver(receiver);
    }
}
