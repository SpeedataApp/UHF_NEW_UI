package com.speedata.uhf;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.speedata.libuhf.utils.SharedXmlUtil;
import com.speedata.uhf.floatball.ModeManager;

/**
 * 枪柄按键
 *
 * @author My_PC
 */
public class PikestaffActivity extends Activity implements CompoundButton.OnCheckedChangeListener {

    private CheckBox cbStartScan;
    private CheckBox cbStartUhf;
    private CheckBox cbStartUhfMore;
    private CheckBox[] cbLeft = new CheckBox[5];
    private CheckBox[] cbRight = new CheckBox[5];
    private ImageView ivPikestaffBack;
    private ScrollView scSD60RT;
    private ScrollView scSD55uhf;
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
    /**
     * home键
     */
    public static final int MODE_HOME = 4;
    /**
     * 返回键
     */
    public static final int MODE_BACK = 5;
    String action = "updata_state";
    private ModeManager modeManager;
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setCheckBoxStatus();
        }
    };
    SharedXmlUtil sharedXmlUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pikestaff);
        initView();
        initData();
        ivPikestaffBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        cbStartScan = findViewById(R.id.cb_start_scan);
        cbStartUhf = findViewById(R.id.cb_start_uhf);
        cbStartUhfMore = findViewById(R.id.cb_start_uhf_re);
        ivPikestaffBack = findViewById(R.id.iv_pikestaff_back);
        cbLeft[0] = findViewById(R.id.cb_start_scan_left);
        cbLeft[1] = findViewById(R.id.cb_start_uhf_left);
        cbLeft[2] = findViewById(R.id.cb_start_uhf_re_left);
        cbLeft[3] = findViewById(R.id.home_cb_left);
        cbLeft[4] = findViewById(R.id.back_cb_left);
        cbRight[0] = findViewById(R.id.cb_start_scan_right);
        cbRight[1] = findViewById(R.id.cb_start_uhf_right);
        cbRight[2] = findViewById(R.id.cb_start_uhf_re_right);
        cbRight[3] = findViewById(R.id.home_cb_right);
        cbRight[4] = findViewById(R.id.back_cb_right);
        scSD60RT = findViewById(R.id.sc_sd60rt);
        scSD55uhf = findViewById(R.id.sc_sd55uhf);
    }

    private void initData() {
        modeManager = ModeManager.getInstance(this);
        registerReceiver(receiver, new IntentFilter(action));
        cbStartScan.setOnCheckedChangeListener(this);
        cbStartUhf.setOnCheckedChangeListener(this);
        cbStartUhfMore.setOnCheckedChangeListener(this);
        for (int i = 0; i < 5; i++) {
            cbLeft[i].setOnCheckedChangeListener(this);
            cbRight[i].setOnCheckedChangeListener(this);
        }
//        if (Build.MODEL.contains("SD55")){
//            scSD60RT.setVisibility(View.GONE);
//            scSD55uhf.setVisibility(View.VISIBLE);
//        }else {
//            scSD60RT.setVisibility(View.VISIBLE);
//            scSD55uhf.setVisibility(View.GONE);
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        sharedXmlUtil = SharedXmlUtil.getInstance(this, "rfid_float_button");
        setCheckBoxStatus();
    }

    private void setCheckBoxStatus() {
        int mode = modeManager.getScanMode();
        if (mode == MODE_SCAN) {
            cbStartScan.setChecked(true);
            cbLeft[0].setChecked(true);
        } else if (mode == MODE_UHF) {
            cbStartUhf.setChecked(true);
            cbLeft[1].setChecked(true);
        } else if (mode == MODE_UHF_RE){
            cbStartUhfMore.setChecked(true);
            cbLeft[2].setChecked(true);
        }else if (mode == MODE_HOME){
            cbLeft[3].setChecked(true);
        }else if (mode == MODE_BACK){
            cbLeft[4].setChecked(true);
        }else {
            for (int i = 0; i < 5; i++) {
                cbLeft[i].setChecked(false);
            }
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_start_scan:
                if (isChecked) {
                    cbStartUhf.setChecked(false);
                    cbStartUhfMore.setChecked(false);
                    SystemProperties.set("persist.sys.PistolKey", "scan");
                    modeManager.changeScanMode(ModeManager.MODE_SCAN);
                }
                break;
            case R.id.cb_start_uhf:
                if (isChecked) {
                    cbStartScan.setChecked(false);
                    cbStartUhfMore.setChecked(false);
                    SystemProperties.set("persist.sys.PistolKey", "uhf");
                    modeManager.changeScanMode(ModeManager.MODE_UHF);
                }
                break;
            case R.id.cb_start_uhf_re:
                if (isChecked) {
                    cbStartScan.setChecked(false);
                    cbStartUhf.setChecked(false);
                    SystemProperties.set("persist.sys.PistolKey", "uhf");
                    modeManager.changeScanMode(ModeManager.MODE_UHF_RE);
                }
                break;
            case R.id.cb_start_scan_left:
            case R.id.cb_start_uhf_left:
            case R.id.cb_start_uhf_re_left:
            case R.id.home_cb_left:
            case R.id.back_cb_left:
                for (int i = 0; i < 5; i++) {
                    cbLeft[i].setChecked(false);
                }
                buttonView.setChecked(isChecked);
                break;
            case R.id.cb_start_scan_right:
            case R.id.cb_start_uhf_right:
            case R.id.cb_start_uhf_re_right:
            case R.id.home_cb_right:
            case R.id.back_cb_right:
                for (int i = 0; i < 5; i++) {
                    cbRight[i].setChecked(false);
                }
                buttonView.setChecked(isChecked);
                break;
            default:
                break;
        }
        switch (buttonView.getId()) {
            case R.id.cb_start_scan_left:
            case R.id.cb_start_scan_right:
                SystemProperties.set("persist.sys.PistolKey", "scan");
                modeManager.changeScanMode(ModeManager.MODE_SCAN);
                break;
            case R.id.cb_start_uhf_left:
            case R.id.cb_start_uhf_right:
                SystemProperties.set("persist.sys.PistolKey", "uhf");
                modeManager.changeScanMode(ModeManager.MODE_UHF);
                break;
            case R.id.cb_start_uhf_re_left:
            case R.id.cb_start_uhf_re_right:
                SystemProperties.set("persist.sys.PistolKey", "uhf");
                modeManager.changeScanMode(ModeManager.MODE_UHF_RE);
                break;
            case R.id.home_cb_left:
            case R.id.home_cb_right:
                sharedXmlUtil.write("current_mode",MODE_HOME);
                break;
            case R.id.back_cb_left:
            case R.id.back_cb_right:
                sharedXmlUtil.write("current_mode",MODE_BACK);
                break;
            default:
                break;
        }
    }

}
