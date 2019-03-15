package com.speedata.uhf;

import android.app.Activity;
import android.os.SystemProperties;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.speedata.libutils.SharedXmlUtil;

/**
 * 枪柄按键
 *
 * @author My_PC
 */
public class PikestaffActivity extends Activity {

    private CheckBox cbStartScan;
    private CheckBox cbStartUhf;

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
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean isScan = SharedXmlUtil.getInstance(PikestaffActivity.this, "pikestaff_set").read("scan", true);
        if (isScan) {
            cbStartScan.setChecked(true);
            SystemProperties.set("persist.sys.PistolKey", "scan");
        }else {
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
