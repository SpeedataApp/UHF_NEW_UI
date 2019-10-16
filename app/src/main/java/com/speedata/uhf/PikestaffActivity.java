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
    /**
     * 不自定义
     */
    public static final int MODE_NONE = 6;
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
        if (Build.MODEL.contains("SD55")) {
            scSD60RT.setVisibility(View.GONE);
            scSD55uhf.setVisibility(View.VISIBLE);
        } else {
            scSD60RT.setVisibility(View.VISIBLE);
            scSD55uhf.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        sharedXmlUtil = SharedXmlUtil.getInstance(this);
        setCheckBoxStatus();
    }

    private void setCheckBoxStatus() {
        if (Build.MODEL.contains("SD55")) {
            int modeLeft = sharedXmlUtil.read("current_mode_left", MODE_SCAN);
            if (modeLeft == 6) {
                for (int i = 0; i < 5; i++) {
                    cbLeft[i].setChecked(false);
                }
            } else {
                cbLeft[modeLeft - 1].setChecked(true);
            }
            int modeRight = sharedXmlUtil.read("current_mode_right", MODE_SCAN);
            if (modeRight == 6) {
                for (int i = 0; i < 5; i++) {
                    cbRight[i].setChecked(false);
                }
            } else {
                cbRight[modeRight - 1].setChecked(true);
            }
        } else {
            int mode = modeManager.getScanMode();
            if (mode == MODE_SCAN) {
                cbStartScan.setChecked(true);
            } else if (mode == MODE_UHF) {
                cbStartUhf.setChecked(true);
            } else if (mode == MODE_UHF_RE) {
                cbStartUhfMore.setChecked(true);
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
        mutex(buttonView, isChecked);
    }

    private void mutex(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_start_scan:
                cbStartUhf.setChecked(false);
                cbStartUhfMore.setChecked(false);
                buttonView.setChecked(isChecked);
                modeManager.changeScanMode(ModeManager.MODE_SCAN);
                break;
            case R.id.cb_start_uhf:
                cbStartScan.setChecked(false);
                cbStartUhfMore.setChecked(false);
                buttonView.setChecked(isChecked);
                modeManager.changeScanMode(ModeManager.MODE_UHF);
                break;
            case R.id.cb_start_uhf_re:
                cbStartScan.setChecked(false);
                cbStartUhf.setChecked(false);
                buttonView.setChecked(isChecked);
                modeManager.changeScanMode(ModeManager.MODE_UHF_RE);
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
                setLeftKey(buttonView, isChecked);
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
                setRightKey(buttonView, isChecked);
                break;
            default:
                break;
        }
    }

    private void setLeftKey(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            switch (buttonView.getId()) {
                case R.id.cb_start_scan_left:
                    sharedXmlUtil.write("current_mode_left", MODE_SCAN);
                    SystemProperties.set("persist.sys.KeyF4", "scan");
//                    modeManager.changeScanMode(ModeManager.MODE_SCAN);
                    break;
                case R.id.cb_start_uhf_left:
                    sharedXmlUtil.write("current_mode_left", MODE_UHF);
                    SystemProperties.set("persist.sys.KeyF4", "uhf");
//                    modeManager.changeScanMode(ModeManager.MODE_UHF);
                    break;
                case R.id.cb_start_uhf_re_left:
                    sharedXmlUtil.write("current_mode_left", MODE_UHF_RE);
                    SystemProperties.set("persist.sys.KeyF4", "uhf");
//                    modeManager.changeScanMode(ModeManager.MODE_UHF_RE);
                    break;
                case R.id.home_cb_left:
                    sharedXmlUtil.write("current_mode_left", MODE_HOME);
                    SystemProperties.set("persist.sys.KeyF4", "home");
//                    modeManager.changeScanMode(ModeManager.MODE_HOME);
                    break;
                case R.id.back_cb_left:
                    sharedXmlUtil.write("current_mode", MODE_BACK);
                    sharedXmlUtil.write("current_mode_left", MODE_BACK);
                    SystemProperties.set("persist.sys.KeyF4", "back");
//                    modeManager.changeScanMode(ModeManager.MODE_BACK);
                    break;
                default:
                    break;
            }
        } else {
            sharedXmlUtil.write("current_mode_left", MODE_NONE);
            SystemProperties.set("persist.sys.KeyF4", "none");
//            modeManager.changeScanMode(ModeManager.MODE_NONE);
        }
    }

    private void setRightKey(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            switch (buttonView.getId()) {
                case R.id.cb_start_scan_right:
                    sharedXmlUtil.write("current_mode_right", MODE_SCAN);
                    SystemProperties.set("persist.sys.KeyF5", "scan");
                    break;
                case R.id.cb_start_uhf_right:
                    sharedXmlUtil.write("current_mode_right", MODE_UHF);
                    SystemProperties.set("persist.sys.KeyF5", "uhf");
                    break;
                case R.id.cb_start_uhf_re_right:
                    sharedXmlUtil.write("current_mode_right", MODE_UHF_RE);
                    SystemProperties.set("persist.sys.KeyF5", "uhf");
                    break;
                case R.id.home_cb_right:
                    sharedXmlUtil.write("current_mode_right", MODE_HOME);
                    SystemProperties.set("persist.sys.KeyF5", "home");
                    break;
                case R.id.back_cb_right:
                    sharedXmlUtil.write("current_mode_right", MODE_BACK);
                    SystemProperties.set("persist.sys.KeyF5", "back");
                    break;
                default:
                    break;
            }
        } else {
            sharedXmlUtil.write("current_mode_right", MODE_NONE);
            SystemProperties.set("persist.sys.KeyF5", "none");
        }
    }

}
