package com.speedata.uhf;

import android.os.SystemProperties;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

/**
 * 枪柄按键
 * @author My_PC
 */
public class PikestaffActivity extends AppCompatActivity {

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

    private void initData(){
        cbStartScan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cbStartUhf.setChecked(false);
                    SystemProperties.set("persist.sys.PistolKey","scan");
                }
            }
        });
        cbStartUhf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cbStartScan.setChecked(false);
                    SystemProperties.set("persist.sys.PistolKey","uhf");
                }
            }
        });
    }

}
