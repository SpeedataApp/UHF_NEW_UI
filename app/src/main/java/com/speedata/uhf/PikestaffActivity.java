package com.speedata.uhf;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemProperties;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.speedata.libutils.SharedXmlUtil;
import com.speedata.uhf.floatball.ModeManager;

/**
 * 枪柄按键
 *
 * @author My_PC
 */
public class PikestaffActivity extends Activity {

    private CheckBox cbStartScan;
    private CheckBox cbStartUhf;
    private CheckBox cbStartUhfMore;
    private ImageView ivPikestaffBack;
    /**
     * 扫头模式
     */
    public static final int MODE_SCAN = 1;
    /**
     * 超高频单次模式
     */
    public static final int MODE_UHF = 2;
    /**
     * 超高频重复模式
     */
    public static final int MODE_UHF_RE = 3;
    String action = "updata_state";
    private ModeManager modeManager;
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setCheckBoxStatus();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pikestaff);
        cbStartScan = findViewById(R.id.cb_start_scan);
        cbStartUhf = findViewById(R.id.cb_start_uhf);
        cbStartUhfMore = findViewById(R.id.cb_start_uhf_re);
        ivPikestaffBack = findViewById(R.id.iv_pikestaff_back);
        ivPikestaffBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initData();
    }

    private void initData() {
        modeManager = ModeManager.getInstance(this);
        registerReceiver(receiver, new IntentFilter(action));
        cbStartScan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbStartUhf.setChecked(false);
                    cbStartUhfMore.setChecked(false);
                    SystemProperties.set("persist.sys.PistolKey", "scan");
                    modeManager.changeScanMode(ModeManager.MODE_SCAN);
                }
            }
        });
        cbStartUhf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbStartScan.setChecked(false);
                    cbStartUhfMore.setChecked(false);
                    SystemProperties.set("persist.sys.PistolKey", "uhf");
                    modeManager.changeScanMode(ModeManager.MODE_UHF);
                }
            }
        });
        cbStartUhfMore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbStartScan.setChecked(false);
                    cbStartUhf.setChecked(false);
                    SystemProperties.set("persist.sys.PistolKey", "uhf");
                    modeManager.changeScanMode(ModeManager.MODE_UHF_RE);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setCheckBoxStatus();
    }

    private void setCheckBoxStatus() {
        int mode = modeManager.getScanMode();
        if (mode == MODE_SCAN) {
            cbStartScan.setChecked(true);
        } else if (mode == MODE_UHF) {
            cbStartUhf.setChecked(true);
        } else {
            cbStartUhfMore.setChecked(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        super.onDestroy();
    }
}
