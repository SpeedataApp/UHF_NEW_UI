package com.speedata.uhf;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.speedata.libuhf.IUHFService;
import com.speedata.libuhf.UHFManager;
import com.speedata.libuhf.utils.SharedXmlUtil;
import com.speedata.libuhf.utils.StringUtils;

import java.text.DecimalFormat;


/**
 * 设置
 * Created by 张智超 on 2019/3/7
 *
 * @author 张智超
 */
public class SetActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvSetFreq;
    private TextView tvSetS2;
    private TextView tvSetInvCon;
    private EditText etPower;
    private EditText etFreqPoint;
    Intent intent;
    private IUHFService iuhfService;
    private CheckBox checkBoxService;
    private String model;
    private int freqRegion, s2Region, invConRegion;
    private TableLayout tableLayoutInvCon;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        Log.e("zzc:", "onCreate()");
        initView();
        initData();
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        iuhfService = UHFManager.getUHFService(this);
        //获取设备型号
        model = SharedXmlUtil.getInstance(this).read("model", "");

        //获取定频
        getFreq();
        //获取通话项
        getSession();
        //获取天线功率
        int ivp = iuhfService.getAntennaPower();
        if (ivp > 0) {
            etPower.setText("" + ivp);
        }
        String as3992 = "as3992";
        if (as3992.equals(model)) {
            etPower.setHint(getResources().getString(R.string.set_etpower));
        }
        //获取盘点模式
        if ("r2k".equals(model)) {
            tableLayoutInvCon.setVisibility(View.VISIBLE);
            invConRegion = iuhfService.getInvMode(0);
            switch (invConRegion) {
                case 0:
                    tvSetInvCon.setText("Only EPC");
                    break;
                case 1:
                    tvSetInvCon.setText("EPC + TID");
                    break;
                case 2:
                    tvSetInvCon.setText("EPC + USER");
                    break;
                default:
                    break;
            }
        } else {
            tableLayoutInvCon.setVisibility(View.GONE);
        }
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
        Button setBackBtn = (Button) findViewById(R.id.btn_set_back);
        setBackBtn.setOnClickListener(this);
        checkBoxService = (CheckBox) findViewById(R.id.check_service);
        Button setFreqBtn = findViewById(R.id.btn_set_freq);
        setFreqBtn.setOnClickListener(this);
        Button setSessionBtn = findViewById(R.id.btn_set_session);
        setSessionBtn.setOnClickListener(this);
        Button setPowerBtn = findViewById(R.id.btn_set_power);
        setPowerBtn.setOnClickListener(this);
        Button setInvConBtn = findViewById(R.id.btn_set_invent);
        setInvConBtn.setOnClickListener(this);
        tableLayoutInvCon = findViewById(R.id.set_tab2);

    }

    private void getFreq() {
        freqRegion = iuhfService.getFreqRegion();
        String r2k = "r2k";
        if (r2k.equals(model)) {
            if (freqRegion == IUHFService.REGION_CHINA_920_925) {
                tvSetFreq.setText("920_925");
            } else if (freqRegion == IUHFService.REGION_CHINA_840_845) {
                tvSetFreq.setText("840_845");
            } else if (freqRegion == IUHFService.REGION_CHINA_902_928) {
                tvSetFreq.setText("902_928");
            } else if (freqRegion == IUHFService.REGION_EURO_865_868) {
                tvSetFreq.setText("865_868");
            } else if (freqRegion == -1) {
                tvSetFreq.setText("...");
                Log.e("r2000_kt45", "read region setting read failed");
            } else {
                tvSetFreq.setText(getResources().getString(R.string.set_freq_item1));
                etFreqPoint.setText(String.valueOf(new DecimalFormat("0.000").format(freqRegion / 1000.0)));
            }
        } else {
            if (freqRegion == IUHFService.REGION_CHINA_920_925) {
                tvSetFreq.setText("920_925");
            } else if (freqRegion == IUHFService.REGION_CHINA_840_845) {
                tvSetFreq.setText("840_845");
            } else if (freqRegion == IUHFService.REGION_CHINA_902_928) {
                tvSetFreq.setText("902_928");
            } else if (freqRegion == IUHFService.REGION_EURO_865_868) {
                tvSetFreq.setText("865_868");
            } else {
                tvSetFreq.setText("...");
                Log.e("r2000_kt45", "read region setting read failed");
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void getSession() {
        s2Region = iuhfService.getQueryTagGroup();
        if (s2Region != -1) {
            tvSetS2.setText("s" + s2Region);
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
            case R.id.btn_set_back:
                //返回
                finish();
                break;
            case R.id.btn_set_freq:
                setFreq(freqRegion);
                break;
            case R.id.btn_set_session:
                setSession(s2Region);
                break;
            case R.id.btn_set_power:
                String power = etPower.getText().toString();
                setAntennaPower(power);
                break;
            case R.id.btn_set_invent:
                setInvCon(invConRegion);
                break;
            default:
                break;
        }
    }

    /**
     * @param region 设置定频
     */
    private void setFreq(int region) {
        int p = 4;
        if (region >= p) {
            return;
        }
        if (iuhfService.setFreqRegion(region) < 0) {
            Toast.makeText(this, getResources().getString(R.string.set_failed), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getResources().getString(R.string.set_success), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @param session 设置通话项
     */
    private void setSession(int session) {
        int setQueryTagGroup = iuhfService.setQueryTagGroup(0, session, 0);
        if (setQueryTagGroup == 0) {
            Toast.makeText(this, getResources().getString(R.string.set_success), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getResources().getString(R.string.set_failed), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @param power 设置天线功率
     */
    private void setAntennaPower(String power) {
        if (TextUtils.isEmpty(power)) {
            Toast.makeText(this, getResources().getString(R.string.toast1), Toast.LENGTH_SHORT).show();
            return;
        }
        int p = Integer.parseInt(power);
        int m = 33;
        if ((p < 0) || (p > m)) {
            Toast.makeText(this, getResources().getString(R.string.power_range), Toast.LENGTH_SHORT).show();
        } else {
            int rv = iuhfService.setAntennaPower(p);
            if (rv < 0) {
                Toast.makeText(this, getResources().getString(R.string.set_power_fail), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getResources().getString(R.string.set_power_ok), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setInvCon(int w) {
        //设置盘点内容
        iuhfService.cancelMask();
        SharedXmlUtil.getInstance(this).write("U8", false);
        int caddr = 0, csize = 6;
        int mode = iuhfService.setInvMode(w, caddr, csize);
        if (mode == 0) {
            Toast.makeText(this, getResources().getString(R.string.set_success), Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, getResources().getString(R.string.set_failed), Toast.LENGTH_SHORT).show();
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
                        //选择的定频列表位置
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
                    // 选择的S2列表位置
                    s2Region = bundle.getInt("position");
                    tvSetS2.setText(s2);

                }
                break;
            case 3:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    assert bundle != null;
                    String invCon = bundle.getString("InvCon");
                    //选择的盘点内容列表位置
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
