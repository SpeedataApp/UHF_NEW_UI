package com.speedata.uhf;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.speedata.libuhf.IUHFService;
import com.speedata.libuhf.utils.SharedXmlUtil;
import com.speedata.uhf.dialog.InventorySettingDialog;

import java.text.DecimalFormat;


/**
 * 设置
 * Created by 张智超 on 2019/3/7
 *
 * @author 张智超
 */
public class InvSetActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mIvQuitSet;
    private TextView tvSetFreq;
    private TextView tvSetS2;
    private TextView tvSetInvCon;
    private EditText etPower;
    private EditText etFreqPoint;
    Intent intent;
    Bundle bundle;
    private IUHFService iuhfService;
    private CheckBox checkBoxService;
    private String model;
    private int freqRegion, s2Region, invConRegion;
    private TableLayout tableLayoutInvCon;
    private Button algorithmSetBtn, setBackBtn;
    private Boolean isExistServer;
    private Button setFreqBtn, setSessionBtn, setPowerBtn, setInvConBtn;
    private TextView tvPrefix, tvSuffix;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        MyApp.getInstance().setIuhfService();
        Log.e("zzc:", "onCreate()");
        initView();
        initData();
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        iuhfService = MyApp.getInstance().getIuhfService();
        //获取设备型号
        model = SharedXmlUtil.getInstance(this).read("model", "");
        //判断服务是否存在
        isExistServer = SharedXmlUtil.getInstance(this).read("server", false);
        if (!isExistServer) {
            checkBoxService.setEnabled(false);
            tvPrefix.setEnabled(false);
            tvSuffix.setEnabled(false);
        }
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
        switch (MyApp.mPrefix) {
            case 0:
                tvPrefix.setText("换行");
                break;
            case 1:
                tvPrefix.setText("空格");
                break;
            case 2:
                tvPrefix.setText("回车换行");
                break;
            case 3:
                tvPrefix.setText("无");
                break;
            default:
                tvPrefix.setText("无");
                break;
        }
        switch (MyApp.mSuffix) {
            case 0:
                tvSuffix.setText("换行");
                break;
            case 1:
                tvSuffix.setText("空格");
                break;
            case 2:
                tvSuffix.setText("回车换行");
                break;
            case 3:
                tvSuffix.setText("无");
                break;
            default:
                tvSuffix.setText("无");
                break;
        }
    }

    public void initView() {
        mIvQuitSet = (ImageView) findViewById(R.id.set_title_iv);
        tvSetFreq = (TextView) findViewById(R.id.set_freq_tv);
        tvSetS2 = (TextView) findViewById(R.id.set_s2_tv);
        tvSetInvCon = (TextView) findViewById(R.id.set_onlyepc_tv);
        mIvQuitSet.setOnClickListener(this);
        tvSetFreq.setOnClickListener(this);
        tvSetS2.setOnClickListener(this);
        tvSetInvCon.setOnClickListener(this);
        etPower = (EditText) findViewById(R.id.et_power);
        etFreqPoint = (EditText) findViewById(R.id.et_freq_point);
        setBackBtn = (Button) findViewById(R.id.btn_set_back);
        setBackBtn.setOnClickListener(this);
        checkBoxService = (CheckBox) findViewById(R.id.check_service);
        setFreqBtn = findViewById(R.id.btn_set_freq);
        setFreqBtn.setOnClickListener(this);
        setSessionBtn = findViewById(R.id.btn_set_session);
        setSessionBtn.setOnClickListener(this);
        setPowerBtn = findViewById(R.id.btn_set_power);
        setPowerBtn.setOnClickListener(this);
        setInvConBtn = findViewById(R.id.btn_set_invent);
        setInvConBtn.setOnClickListener(this);
        tableLayoutInvCon = findViewById(R.id.set_tab2);
        algorithmSetBtn = findViewById(R.id.btn_algorithm_set);
        algorithmSetBtn.setOnClickListener(this);
        tvPrefix = findViewById(R.id.set_server_prefix);
        tvPrefix.setOnClickListener(this);
        tvSuffix = findViewById(R.id.set_server_suffix);
        tvSuffix.setOnClickListener(this);

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

    private void unEnabled() {
        setFreqBtn.setEnabled(false);
        setSessionBtn.setEnabled(false);
        setPowerBtn.setEnabled(false);
        setInvConBtn.setEnabled(false);
        setBackBtn.setEnabled(false);
        algorithmSetBtn.setEnabled(false);
        mIvQuitSet.setEnabled(false);
        tvSetFreq.setEnabled(false);
        tvSetS2.setEnabled(false);
        tvSetInvCon.setEnabled(false);
    }

    private void enabled() {
        setFreqBtn.setEnabled(true);
        setSessionBtn.setEnabled(true);
        setPowerBtn.setEnabled(true);
        setInvConBtn.setEnabled(true);
        setBackBtn.setEnabled(true);
        algorithmSetBtn.setEnabled(true);
        mIvQuitSet.setEnabled(true);
        tvSetFreq.setEnabled(true);
        tvSetS2.setEnabled(true);
        tvSetInvCon.setEnabled(true);
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
            case R.id.btn_algorithm_set:
                InventorySettingDialog inventorySettingDialog = new InventorySettingDialog(this, iuhfService);
                inventorySettingDialog.setTitle(getResources().getString(R.string.algorithm_set));
                inventorySettingDialog.show();
                break;
            case R.id.set_server_prefix:
                intent = new Intent();
                bundle = new Bundle();
                bundle.putString("send_fix", "prefix");
                intent.putExtras(bundle);
                intent.setClass(this, PopSetServiceActivity.class);
                startActivityForResult(intent, 4);
                break;
            case R.id.set_server_suffix:
                intent = new Intent();
                bundle = new Bundle();
                bundle.putString("send_fix", "suffix");
                intent.putExtras(bundle);
                intent.setClass(this, PopSetServiceActivity.class);
                startActivityForResult(intent, 5);
                break;
            default:
                break;
        }
    }

    /**
     * @param region 设置定频
     */
    private void setFreq(final int region) {
        int p = 4;
        if (region >= p) {
            return;
        }
        unEnabled();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                final int i = iuhfService.setFreqRegion(region);
                if (i < 0) {
                    Toast.makeText(InvSetActivity.this, getResources().getString(R.string.set_failed), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(InvSetActivity.this, getResources().getString(R.string.set_success), Toast.LENGTH_SHORT).show();
                }
                enabled();
            }
        });
    }

    /**
     * @param session 设置通话项
     */
    private void setSession(final int session) {
        unEnabled();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                int setQueryTagGroup = iuhfService.setQueryTagGroup(0, session, 0);
                if (setQueryTagGroup == 0) {
                    Toast.makeText(InvSetActivity.this, getResources().getString(R.string.set_success), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(InvSetActivity.this, getResources().getString(R.string.set_failed), Toast.LENGTH_SHORT).show();
                }
                enabled();
            }
        });
    }

    /**
     * @param power 设置天线功率
     */
    private void setAntennaPower(final String power) {
        if (TextUtils.isEmpty(power)) {
            Toast.makeText(this, getResources().getString(R.string.toast1), Toast.LENGTH_SHORT).show();
            return;
        }
        unEnabled();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                int p = Integer.parseInt(power);
                int m = 33;
                if ((p < 0) || (p > m)) {
                    Toast.makeText(InvSetActivity.this, getResources().getString(R.string.power_range), Toast.LENGTH_SHORT).show();
                } else {
                    int rv = iuhfService.setAntennaPower(p);
                    if (rv < 0) {
                        Toast.makeText(InvSetActivity.this, getResources().getString(R.string.set_power_fail), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(InvSetActivity.this, getResources().getString(R.string.set_power_ok), Toast.LENGTH_SHORT).show();
                    }
                }
                enabled();
            }
        });

    }

    private void setInvCon(final int w) {
        unEnabled();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                //设置盘点内容
                iuhfService.cancelMask();
                SharedXmlUtil.getInstance(InvSetActivity.this).write("U8", false);
                int caddr = 0, csize = 6;
                int mode = iuhfService.setInvMode(w, caddr, csize);
                if (mode == 0) {
                    Toast.makeText(InvSetActivity.this, getResources().getString(R.string.set_success), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(InvSetActivity.this, getResources().getString(R.string.set_failed), Toast.LENGTH_SHORT).show();
                }
                enabled();
            }
        });
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
            case 4:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    assert bundle != null;
                    String fix = bundle.getString("prefix");
                    //选择的前缀列表位置
                    MyApp.mPrefix = bundle.getInt("position");
                    tvPrefix.setText(fix);
                }
                break;
            case 5:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    assert bundle != null;
                    String fix = bundle.getString("prefix");
                    //选择的后缀列表位置
                    MyApp.mSuffix = bundle.getInt("position");
                    tvSuffix.setText(fix);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (checkBoxService.isChecked()) {
            SharedXmlUtil.getInstance(this).write("server", false);
            stopService(new Intent(this, MyService.class));
        }
        super.onDestroy();
    }
}
