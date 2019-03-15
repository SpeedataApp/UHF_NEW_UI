package com.speedata.uhf;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.speedata.libuhf.IUHFService;
import com.speedata.libuhf.UHFManager;
import com.speedata.libuhf.utils.SharedXmlUtil;
import com.speedata.libuhf.utils.StringUtils;

import java.text.DecimalFormat;

import static com.speedata.uhf.R.layout.activity_set;

/**
 * 设置
 * Created by 张智超 on 2019/3/7
 *
 * @author 张智超
 */
public class SetActivity extends Activity implements View.OnClickListener {

    private TextView tvSetFreq;
    private TextView tvSetS2;
    private TextView tvSetInvCon;
    private EditText etPower;
    private EditText etFreqPoint;
    Intent intent;
    private IUHFService iuhfService;
    private CheckBox checkBoxService;

    /**
     * 选择的定频列表位置
     */
    private int freqRegion;
    /**
     * 选择的S2列表位置
     */
    private int s2Region;
    /**
     * 选择的盘点内容列表位置
     */
    private int invConRegion;

    private boolean isOK = true;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_set);
        initView();
        initData();
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        iuhfService = UHFManager.getUHFService(this);
        //获取设备型号
        String model = SharedXmlUtil.getInstance(SetActivity.this).read("modle", "");

        //获取定频
        int re = iuhfService.getFreqRegion();
        String r2k = "r2k";
        if (r2k.equals(model)) {
            if (re == IUHFService.REGION_CHINA_920_925) {
                tvSetFreq.setText("920_925");
            } else if (re == IUHFService.REGION_CHINA_840_845) {
                tvSetFreq.setText("840_845");
            } else if (re == IUHFService.REGION_CHINA_902_928) {
                tvSetFreq.setText("902_928");
            } else if (re == IUHFService.REGION_EURO_865_868) {
                tvSetFreq.setText("865_868");
            } else if (re == -1) {
                tvSetFreq.setText("...");
                Log.e("r2000_kt45", "read region setting read failed");
            } else {
                tvSetFreq.setText("当前状态为定频");
                etFreqPoint.setText(String.valueOf(new DecimalFormat("0.000").format(re / 1000.0)));
            }
        } else {
            if (re == IUHFService.REGION_CHINA_920_925) {
                tvSetFreq.setText("920_925");
            } else if (re == IUHFService.REGION_CHINA_840_845) {
                tvSetFreq.setText("840_845");
            } else if (re == IUHFService.REGION_CHINA_902_928) {
                tvSetFreq.setText("902_928");
            } else if (re == IUHFService.REGION_EURO_865_868) {
                tvSetFreq.setText("865_868");
            } else {
                tvSetFreq.setText("...");
                Log.e("r2000_kt45", "read region setting read failed");
            }
        }

        //获取天线功率
        int ivp = iuhfService.getAntennaPower();
        if (ivp > 0) {
            etPower.setText("" + ivp);
        }
        String as3992 = "as3992";
        if (as3992.equals(model)) {
            etPower.setHint("0关天线1开天线");
        }

        getSession();
    }

    public void initView() {
        ImageView mIvQuitSet = (ImageView) findViewById(R.id.set_title_iv);
        tvSetFreq = (TextView) findViewById(R.id.set_freq_tv);
        tvSetS2 = (TextView) findViewById(R.id.set_s2_tv);
        tvSetInvCon = (TextView) findViewById(R.id.set_onlyepc_tv);
        mIvQuitSet.setOnClickListener(this);
        tvSetFreq.setOnClickListener(this);
        tvSetS2.setOnClickListener(this);
        tvSetInvCon.setOnClickListener(this);
        etPower = (EditText) findViewById(R.id.et_power);
        etFreqPoint = (EditText) findViewById(R.id.et_freq_point);
        Button setOkBtn = (Button) findViewById(R.id.btn_set_ok);
        setOkBtn.setOnClickListener(this);
        checkBoxService = (CheckBox) findViewById(R.id.check_service);

    }

    @SuppressLint("SetTextI18n")
    private void getSession() {
        int queryTagGroup = iuhfService.getQueryTagGroup();
        if (queryTagGroup != -1) {
            tvSetS2.setText("s" + queryTagGroup);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.set_title_iv:
                //退出
                finish();
                break;
            case R.id.set_freq_tv:
                //选择定频
                intent = new Intent(this, PopSetFreqActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.set_s2_tv:
                //选择s2
                intent = new Intent(this, PopSetS2Activity.class);
                startActivityForResult(intent, 2);
                break;
            case R.id.set_onlyepc_tv:
                //选择onlyepc
                intent = new Intent(this, PopSetInvContentActivity.class);
                startActivityForResult(intent, 3);
                break;

            case R.id.btn_set_ok:
                //确定
                //设置参数
                setData();
                if (isOK) {
                    Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "设置失败", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void setData() {
        //设置定频
        int p = 4;
        if (freqRegion >= p) {
            isOK = false;
        } else {
            if (iuhfService.setFreqRegion(freqRegion) < 0) {
                isOK = false;
            }
        }

        // 设置s2
        int setQueryTagGroup = iuhfService.setQueryTagGroup(0, s2Region, 0);
        if (setQueryTagGroup != 0) {
            isOK = false;
        }
        //设置天线功率
        int ivp = Integer.parseInt(etPower.getText().toString());
        int m = 33;
        if ((ivp < 0) || (ivp > m)) {
            isOK = false;
        } else {
            int rv = iuhfService.setAntennaPower(ivp);
            if (rv < 0) {
                isOK = false;
            }
        }

        //设置盘点内容
        int w = invConRegion;
        int wp3 = 3;
        int wp4 = 4;
        if (w == wp3) {
            //读取U8标签代码epc+bid
            iuhfService.mask(1, 516, 1, StringUtils.stringToByte("80"));
            SharedXmlUtil.getInstance(this).write("U8", true);
        } else if (w == wp4) {
            //读取U8标签代码epc+bid+tid
            iuhfService.mask(1, 516, 1, StringUtils.stringToByte("80"));
            iuhfService.setInvMode(1, 0, 6);
            SharedXmlUtil.getInstance(this).write("U8", true);
        } else {
            iuhfService.cancelMask();
            SharedXmlUtil.getInstance(this).write("U8", false);
            int caddr = 0, csize = 0;
            if (w != 0) {
                caddr = 1;
                csize = 1;
            }
            iuhfService.setInvMode(w, caddr, csize);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String freq = bundle.getString("freq");
                        freqRegion = bundle.getInt("position");
                        tvSetFreq.setText(freq);
                    }
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    assert bundle != null;
                    String s2 = bundle.getString("S2");
                    s2Region = bundle.getInt("position");
                    tvSetS2.setText(s2);
                }
                break;
            case 3:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    assert bundle != null;
                    String invCon = bundle.getString("InvCon");
                    invConRegion = bundle.getInt("position");
                    tvSetInvCon.setText(invCon);
                }
                break;
            default:
                break;
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
