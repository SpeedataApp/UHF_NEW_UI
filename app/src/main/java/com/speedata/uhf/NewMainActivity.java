package com.speedata.uhf;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.speedata.libuhf.IUHFService;
import com.speedata.libuhf.UHFManager;
import com.speedata.libuhf.bean.SpdInventoryData;
import com.speedata.libuhf.interfaces.OnSpdInventoryListener;
import com.speedata.libuhf.utils.CommonUtils;
import com.speedata.libuhf.utils.SharedXmlUtil;
import com.speedata.uhf.adapter.UhfCardAdapter;
import com.speedata.uhf.adapter.UhfCardBean;
import com.speedata.uhf.dialog.DefaultSettingDialog;
import com.speedata.uhf.excel.EPCBean;
import com.speedata.uhf.floatball.FloatWarnManager;
import com.speedata.uhf.libutils.ToastUtil;
import com.speedata.uhf.libutils.excel.ExcelUtils;
import com.yhao.floatwindow.FloatWindow;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jxl.write.Colour;

/**
 * 主界面
 * The home page
 *
 * @author zzc
 */
public class NewMainActivity extends BaseActivity implements View.OnClickListener {

    private TextView mEtSearch;
    private AlertDialog alertDialog1;
    private Button mFindBtn;
    private LinearLayout mLlFind, mLlListBg, mLlPause;
    private ListView mListViewCard;
    private TextView mTvListMsg;
    private ToggleButton mTbtnSound;
    private TextView tagNumTv;
    private TextView speedTv;
    private TextView totalTime;
    private TextView mVersionTv;
    private Button btnExport;
    private Button mBtSearch;
    private ImageView mIvSet;
    private ImageView mIvMenu;

    private UhfCardAdapter uhfCardAdapter;
    private List<UhfCardBean> uhfCardBeanList = new ArrayList<>();
    private KProgressHUD kProgressHUD;

    private boolean inSearch = false;
    private IUHFService iuhfService;
    private SoundPool soundPool;
    private int soundId;
    private String model;
    private long mkeyTime = 0;
    private long scant = 0;
    private long startCheckingTime;
    private static final String CHARGING_PATH = "/sys/class/misc/bq25601/regdump/";
    private File file;
    private BufferedWriter writer;

    public static final String START_SCAN = "com.spd.action.start_uhf";
    public static final String STOP_SCAN = "com.spd.action.stop_uhf";
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (!MyApp.isOpenServer) {
                String action = intent.getAction();
                switch (Objects.requireNonNull(action)) {
                    case START_SCAN:
                        //启动超高频扫描 Start uhf scan
                        if (inSearch) {
                            return;
                        }
                        startUhf();
                        break;
                    case STOP_SCAN:
                        if (inSearch) {
                            stopUhf();
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUhf();
        initView();
        initData();
        initReceive();
    }


    public void initView() {
        setContentView(R.layout.activity_main);
        mIvMenu = findViewById(R.id.iv_menu);
        mIvMenu.setOnClickListener(this);
        mIvSet = (ImageView) findViewById(R.id.iv_set);
        mBtSearch = (Button) findViewById(R.id.bt_search);
        mEtSearch = findViewById(R.id.et_search);
        mEtSearch.setOnClickListener(this);
        mIvSet.setOnClickListener(this);
        mBtSearch.setOnClickListener(this);
        mFindBtn = (Button) findViewById(R.id.btn_find);
        mFindBtn.setOnClickListener(this);
        mLlFind = (LinearLayout) findViewById(R.id.ll_find_layout);
        mLlPause = (LinearLayout) findViewById(R.id.ll_pause_layout);
        mTvListMsg = (TextView) findViewById(R.id.tv_list_msg);
        mTbtnSound = (ToggleButton) findViewById(R.id.t_btn_sound);
        tagNumTv = (TextView) findViewById(R.id.tv_number);
        speedTv = (TextView) findViewById(R.id.speed_tv);
        totalTime = (TextView) findViewById(R.id.totalTime);
        btnExport = (Button) findViewById(R.id.btn_export);
        btnExport.setOnClickListener(this);

        mLlListBg = (LinearLayout) findViewById(R.id.ll_list_bg);
        mListViewCard = (ListView) findViewById(R.id.lv_card);

        mLlFind.setVisibility(View.VISIBLE);
        mLlPause.setVisibility(View.GONE);
        mTvListMsg.setVisibility(View.GONE);
        mLlListBg.setVisibility(View.VISIBLE);
        mListViewCard.setVisibility(View.GONE);
        mVersionTv = (TextView) findViewById(R.id.tv_version_model);
        mVersionTv.setText(CommonUtils.getAppVersionName(this));
    }

    public void initSoundPool() {
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        soundId = soundPool.load("/system/media/audio/ui/VideoRecord.ogg", 0);
        Log.w("as3992_6C", "id is " + soundId);
    }

    public void initData() {
        model = UHFManager.getUHFModel();
        // 加载适配器 Load adapter
        uhfCardAdapter = new UhfCardAdapter(this, R.layout.item_uhf_card, uhfCardBeanList);
        mListViewCard.setAdapter(uhfCardAdapter);
        // 列表回调item点击事件 List callback item click event
        mListViewCard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (inSearch) {
                    return;
                }

                String epcStr = uhfCardBeanList.get(position).getEpc();
                boolean u8 = SharedXmlUtil.getInstance(NewMainActivity.this).read("U8", false);
                if (u8) {
                    epcStr = epcStr.substring(0, 24);
                }
                int res = iuhfService.selectCard(1, epcStr, true);
                if (res == 0) {
                    // 选卡成功 Selected card success
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("epcName", epcStr);
                    bundle.putString("model", model);
                    intent.putExtras(bundle);
                    intent.setClass(NewMainActivity.this, CurrentCardActivity.class);
                    startActivity(intent);

                }
            }
        });
        MyApp.isOpenServer = false;
        file = new File(CHARGING_PATH);
        mVersionTv.append("-" + model);
    }

    /**
     * 注册广播
     * Registered broadcasting
     */
    private void initReceive() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(START_SCAN);
        filter.addAction(STOP_SCAN);
        registerReceiver(receiver, filter);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            boolean cn = "CN".equals(NewMainActivity.this.getApplicationContext().getResources().getConfiguration().locale.getCountry());
            switch (msg.what) {
                case 1:
                    scant++;
                    long iop = System.currentTimeMillis();
                    if (mTbtnSound.isChecked()) {
                        soundPool.play(soundId, 1, 1, 0, 0, 1);
                    }
                    Log.e("zzc", "=soundPool.play=====" + (System.currentTimeMillis() - iop));
                    SpdInventoryData var1 = (SpdInventoryData) msg.obj;
                    int j;
                    for (j = 0; j < uhfCardBeanList.size(); j++) {
                        if (var1.epc.equals(uhfCardBeanList.get(j).getEpc())) {
                            int v = uhfCardBeanList.get(j).getValid() + 1;
                            uhfCardBeanList.get(j).setValid(v);
                            uhfCardBeanList.get(j).setRssi(var1.rssi);
                            break;
                        }
                    }
                    if (j == uhfCardBeanList.size()) {
                        uhfCardBeanList.add(new UhfCardBean(var1.epc, 1, var1.rssi, var1.tid));
                    }
                    uhfCardAdapter.notifyDataSetChanged();
                    updateRateCount();
                    break;

                case 2:
                    kProgressHUD.dismiss();
                    if (cn) {
                        Toast.makeText(NewMainActivity.this, "导出完成", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NewMainActivity.this, "Export the complete", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case 3:
                    kProgressHUD.dismiss();
                    if (cn) {
                        Toast.makeText(NewMainActivity.this, "导出过程中出现问题！请重试", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NewMainActivity.this, "There is a problem in exporting! Please try again", Toast.LENGTH_SHORT).show();
                    }
                case -1:
                    int status = (int) msg.obj;
                    if (cn) {
                        Toast.makeText(NewMainActivity.this, "盘点中断:" + status, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NewMainActivity.this, "Inventory interrupt:" + status, Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }

        }
    };

    /**
     * 开始盘点
     * Start inventory
     */
    private void startUhf() {
        if (iuhfService == null) {
            return;
        }
        try {
            writer = new BufferedWriter(new FileWriter(file, false));
            writer.write("otgon");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //取消掩码 Cancel the mask
        iuhfService.selectCard(1, "", false);
        // 盘点回调函数 callback function
        iuhfService.setOnInventoryListener(new OnSpdInventoryListener() {
            @Override
            public void getInventoryData(SpdInventoryData var1) {
                handler.sendMessage(handler.obtainMessage(1, var1));
                Log.d("UHFService", "回调");
            }

            @Override
            public void onInventoryStatus(int status) {
                handler.sendMessage(handler.obtainMessage(-1, status));
                Log.d("UHFService", "盘点失败" + status);
                iuhfService.inventoryStart();
            }
        });
        iuhfService.inventoryStart();
        inSearch = true;
        mLlFind.setVisibility(View.GONE);
        mLlPause.setVisibility(View.VISIBLE);
        mTvListMsg.setVisibility(View.VISIBLE);
        mLlListBg.setVisibility(View.GONE);
        mListViewCard.setVisibility(View.VISIBLE);
        scant = 0;
        uhfCardBeanList.clear();
        startCheckingTime = System.currentTimeMillis();
        mFindBtn.setText(R.string.Stop_Search_Btn);
        mIvMenu.setEnabled(false);
        mBtSearch.setEnabled(false);
        mIvSet.setEnabled(false);
        mEtSearch.setEnabled(false);
        btnExport.setEnabled(false);
        btnExport.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_gray_shape));
        btnExport.setTextColor(getResources().getColor(R.color.text_gray));
        uhfCardAdapter.notifyDataSetChanged();
        updateRateCount();
    }

    /**
     * 停止盘点
     * Stop inventory
     */
    private void stopUhf() {
        if (iuhfService == null) {
            return;
        }
        iuhfService.inventoryStop();
        inSearch = false;
        mFindBtn.setText(R.string.Start_Search_Btn);
        mIvMenu.setEnabled(true);
        mBtSearch.setEnabled(true);
        mIvSet.setEnabled(true);
        mEtSearch.setEnabled(true);
        btnExport.setEnabled(true);
        btnExport.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_b_g_select));
        btnExport.setTextColor(getResources().getColor(R.color.text_white));
        try {
            writer = new BufferedWriter(new FileWriter(file, false));
            writer.write("otgoff");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 处理监听事件
     * listener events
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_set:
                if (MyApp.isFastMode && model.contains(UHFManager.FACTORY_XINLIAN)) {
                    //开启快速模式禁用设置  Disable Settings if fast mode is enabled
                    ToastUtil.customToastView(this, getResources().getString(R.string.toast_stop_fast_tips), Toast.LENGTH_SHORT
                            , (TextView) LayoutInflater.from(this).inflate(R.layout.layout_toast, null));
                } else {
                    //设置  Enter the Settings
                    Intent intent = new Intent(this, InvSetActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.bt_search:
                //搜索 search
                String epc = mEtSearch.getText().toString();
                Log.d("zzc", "epc:" + epc);
                if (epc.isEmpty()) {
                    Toast.makeText(this, getResources().getString(R.string.search_toast), Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent1 = new Intent(this, SearchDirectionActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("epcNumber", mEtSearch.getText().toString());
                intent1.putExtras(bundle);
                startActivity(intent1);
                break;
            case R.id.btn_find:
                //寻卡  inventory
                if (inSearch) {
                    stopUhf();
                } else {
                    startUhf();
                }
                break;
            case R.id.btn_export:
                //导出  export
                kProgressHUD = KProgressHUD.create(this)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setCancellable(false)
                        .setAnimationSpeed(2)
                        .setDimAmount(0.5f)
                        .show();
                if (uhfCardBeanList.size() > 0) {
                    ExportThread exportThread = new ExportThread();
                    exportThread.start();
                } else {
                    kProgressHUD.dismiss();
                    boolean cn = "CN".equals(this.getApplicationContext().getResources().getConfiguration().locale.getCountry());
                    if (cn) {
                        Toast.makeText(this, "没有数据，请先盘点", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "No data, please take stock", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.et_search:
                String[] items = {getResources().getString(R.string.search_dialog_item)};
                Log.d("zzc", "changdu " + uhfCardBeanList.size());
                if (uhfCardBeanList.size() != 0) {
                    items = new String[uhfCardBeanList.size()];
                    for (int i = 0; i < uhfCardBeanList.size(); i++) {
                        items[i] = uhfCardBeanList.get(i).getEpc();
                    }
                }
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setTitle(getResources().getString(R.string.search_dialog_title));
                alertBuilder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (uhfCardBeanList.size() != 0) {
                            mEtSearch.setText(uhfCardBeanList.get(i).getEpc());
                        }
                        alertDialog1.dismiss();
                    }
                });
                alertDialog1 = alertBuilder.create();
                alertDialog1.show();
                break;
            case R.id.iv_menu:
                DefaultSettingDialog defaultSettingDialog = new DefaultSettingDialog(this, iuhfService);
                defaultSettingDialog.show();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //初始化声音线程  Initializes the sound thread
        initSoundPool();
        MyApp.isOpenServer = false;
    }

    @Override
    protected void onPause() {
        stopUhf();
        MyApp.isOpenServer = true;
        if (iuhfService != null) {
            //更新回调
            //Update the callback
            sendUpddateService();
        }
        super.onPause();
    }

    /**
     * 上电开串口
     * Power on and open serial port
     */
    private boolean openDev() {
        if (!MyApp.isOpenDev) {
            if (iuhfService.openDev() != 0) {
                Toast.makeText(this, "Open serialport failed", Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(this).setTitle(R.string.DIA_ALERT).setMessage(R.string.DEV_OPEN_ERR).setPositiveButton(R.string.DIA_CHECK, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
                MyApp.isOpenDev = false;
                return false;
            } else {
                Log.d("UHFService", "上电成功");
            }
        }
        MyApp.isOpenDev = true;
        return true;
    }


    /**
     * 更新显示数据
     * Update display data
     */
    private void updateRateCount() {
        Log.e("zzc", "==updateRateCount==");
        long mlEndTime = System.currentTimeMillis();

        double rate = Math.ceil((scant * 1.0) * 1000 / (mlEndTime - startCheckingTime));

        long totalTimeCount = mlEndTime - startCheckingTime;

        speedTv.setText(String.format("%s" + getResources().getString(R.string.num), String.valueOf(rate)));

        tagNumTv.setText(String.format("%s", String.valueOf(uhfCardBeanList.size())));

        totalTime.setText(String.format(getResources().getString(R.string.spend_time) + "%s", String.valueOf(getTimeFromMillisecond(totalTimeCount))));
    }

    private void sendUpddateService() {
        Intent intent = new Intent();
        intent.setAction("uhf.update");
        this.sendBroadcast(intent);
    }

    @Override
    protected void onStop() {

        Log.w("stop", "im stopping");
        if (inSearch) {
            iuhfService.inventoryStop();
            inSearch = false;
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (iuhfService != null) {
            if (!SharedXmlUtil.getInstance(this).read("server", false)) {
                Log.e("zzc:", "==onDestroy()==下电");
                MyApp.getInstance().releaseIuhfService();
                MyApp.isOpenDev = false;
            }
        }
        soundPool.release();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        super.onDestroy();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.ACTION_DOWN:
                int s = 2000;
                if ((System.currentTimeMillis() - mkeyTime) > s) {
                    mkeyTime = System.currentTimeMillis();
                    boolean cn = "CN".equals(getApplicationContext().getResources().getConfiguration().locale.getCountry());
                    if (cn) {
                        Toast.makeText(getApplicationContext(), "再按一次退出", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Click again to exit", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 从时间(毫秒)中提取出时间(时:分:秒)
     * extract time from time (ms) (hours: minutes: seconds)
     * 时间格式:  时:分:秒.毫秒
     * time format: time: minutes: seconds. Milliseconds
     *
     * @param millisecond 毫秒 Milliseconds
     * @return 时间字符串 Time string
     */
    public static String getTimeFromMillisecond(Long millisecond) {
        String milli;
        int num = 100;
        //根据时间差来计算小时数
        //Calculate the hours according to the time difference
        long hours = millisecond / (60 * 60 * 1000);
        //根据时间差来计算分钟数
        //Calculate the minutes according to the time difference
        long minutes = (millisecond - hours * (60 * 60 * 1000)) / (60 * 1000);
        //根据时间差来计算秒数
        //Calculate the seconds according to the time difference
        long second = (millisecond - hours * (60 * 60 * 1000) - minutes * (60 * 1000)) / 1000;
        //根据时间差来计算毫秒数
        //Calculate the milliseconds according to the time difference
        long milliSecond = millisecond - hours * (60 * 60 * 1000) - minutes * (60 * 1000) - second * 1000;
        if (milliSecond < num) {
            milli = "0" + milliSecond;
        } else {
            milli = "" + milliSecond;
        }
        if (hours == 0) {
            if (minutes == 0) {
                return second + "." + milli + "s";
            }
            return minutes + "m: " + second + "." + milli + "s";
        }
        return hours + "h: " + minutes + "m: " + second + "." + milli + "s";
    }

    private class ExportThread extends Thread {
        @Override
        public void run() {
            List<EPCBean> epcBeanList = new ArrayList<>();
            for (UhfCardBean uhfCardBean : uhfCardBeanList) {
                EPCBean epcBean = new EPCBean();
                epcBean.setEPC(uhfCardBean.getEpc());
                epcBean.setTID_USER(uhfCardBean.getTidUser());
                epcBeanList.add(epcBean);
            }
            if (epcBeanList.size() > 0) {
                try {
                    ExcelUtils.getInstance()
                            //设置表格名称    Set table name
                            .setSHEET_NAME("UHFMsg")
                            //设置标题字体颜色  Set the title font color
                            .setFONT_COLOR(Colour.BLUE)
                            //设置标题字体大小  Set the font size of the title
                            .setFONT_TIMES(8)
                            //设置标题字体是否斜体    Sets whether the title font is italic or not
                            .setFONT_BOLD(true)
                            //设置标题背景颜色      Set the title background color
                            .setBACKGROND_COLOR(Colour.GRAY_25)
                            //设置excel内容     Set excel content
                            .setContent_list_Strings(epcBeanList)
                            .setWirteExcelPath(Environment.getExternalStorageDirectory() + File.separator + "UHFMsg.xls")
                            .createExcel(NewMainActivity.this);
                    handler.sendMessage(handler.obtainMessage(2));
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendMessage(handler.obtainMessage(3));
                }
            } else {
                handler.sendMessage(handler.obtainMessage(3));
            }
        }
    }

    private void initUhf() {
        iuhfService = MyApp.getInstance().getIuhfService();
        if (iuhfService != null) {
            SystemClock.sleep(1000);
        } else {
            MyApp.getInstance().setIuhfService();
            iuhfService = MyApp.getInstance().getIuhfService();
            if (iuhfService == null) {
                FloatWarnManager.getInstance(getApplicationContext(), getResources().getString(R.string.dialog_module_none));
                FloatWarnManager floatWarnManager = FloatWarnManager.getFloatWarnManager();
                if (floatWarnManager != null) {
                    FloatWindow.get("FloatWarnTag").show();
                }
                return;
            }
            try {
                MyApp.isOpenDev = openDev();
                if (MyApp.isOpenDev) {
                    MyApp.getInstance().initParam();
                } else {
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            SystemClock.sleep(100);
            Log.d("UHFService", "startService==main==");
            startService(new Intent(this, MyService.class));
            SharedXmlUtil.getInstance(this).write("server", true);
            SystemClock.sleep(1000);
        }
    }
}
