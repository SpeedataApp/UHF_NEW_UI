package com.speedata.uhf;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemProperties;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.speedata.libutils.SharedXmlUtil;
import com.speedata.uhf.floatball.ModeManager;

/**
 * 枪柄按键
 *
 * @author My_PC
 */
public class PikestaffActivity extends BaseActivity {

    private CheckBox cbStartScan;
    private CheckBox cbStartUhf;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pikestaff);
        cbStartScan = (CheckBox) findViewById(R.id.cb_start_scan);
        cbStartUhf = (CheckBox) findViewById(R.id.cb_start_uhf);
        initData();
    }

    private void initData() {
        cbStartScan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbStartUhf.setChecked(false);
                    SystemProperties.set("persist.sys.PistolKey", "scan");
                    SharedXmlUtil.getInstance(PikestaffActivity.this, "pikestaff_set").write("scan", true);
                    SharedXmlUtil.getInstance(PikestaffActivity.this, "pikestaff_set").write("uhf", false);
                    SharedXmlUtil.getInstance(PikestaffActivity.this, "rfid_float_button").write("current_mode", MODE_SCAN);
//                    sendBroadcast(new Intent("updata_state"));
                    ModeManager modeManager = new ModeManager(PikestaffActivity.this);
                    modeManager.changeScanMode(ModeManager.MODE_SCAN);
                }
            }
        });
        cbStartUhf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbStartScan.setChecked(false);
                    SystemProperties.set("persist.sys.PistolKey", "uhf");
                    SharedXmlUtil.getInstance(PikestaffActivity.this, "pikestaff_set").write("scan", false);
                    SharedXmlUtil.getInstance(PikestaffActivity.this, "pikestaff_set").write("uhf", true);
                    SharedXmlUtil.getInstance(PikestaffActivity.this, "rfid_float_button").write("current_mode", MODE_UHF_RE);
//                    sendBroadcast(new Intent("updata_state"));
                    ModeManager modeManager = new ModeManager(PikestaffActivity.this);
                    modeManager.changeScanMode(ModeManager.MODE_UHF_RE);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean isScan = SharedXmlUtil.getInstance(PikestaffActivity.this, "pikestaff_set").read("scan", true);
        int mode = SharedXmlUtil.getInstance(PikestaffActivity.this, "rfid_float_button").read("current_mode", MODE_SCAN);
        if (isScan || mode == MODE_SCAN) {
            cbStartScan.setChecked(true);
            SystemProperties.set("persist.sys.PistolKey", "scan");
        } else {
            cbStartUhf.setChecked(true);
            SystemProperties.set("persist.sys.PistolKey", "uhf");
        }
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
