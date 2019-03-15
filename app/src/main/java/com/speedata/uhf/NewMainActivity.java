package com.speedata.uhf;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import com.speedata.libuhf.utils.SharedXmlUtil;
import com.speedata.uhf.adapter.UhfCardAdapter;
import com.speedata.uhf.adapter.UhfCardBean;
import com.speedata.uhf.excel.EPCBean;
import com.speedata.uhf.libutils.excel.ExcelUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jxl.write.Colour;

/**
 * 主界面
 *
 * @author zzc
 */
public class NewMainActivity extends Activity implements View.OnClickListener {

    /**
     * 搜索输入框
     */
    private EditText mEtSearch;
    /**
     * 寻卡
     */
    private Button mFindBtn;
    private LinearLayout mLlFind, mLlListBg;
    private RelativeLayout mRlPause;
    /**
     * 寻卡列表
     */
    private ListView mListViewCard;
    private TextView mTvListMsg;
    /**
     * 声音开关
     */
    private ToggleButton mTbtnSound;
    private TextView tagNumTv;
    private TextView speedTv;
    private TextView totalTime;
    /**
     * 导出
     */
    private Button btnExport;

    private UhfCardAdapter uhfCardAdapter;
    private List<UhfCardBean> uhfCardBeanList = new ArrayList<>();
    private KProgressHUD kProgressHUD;

    private boolean inSearch = false;
    private IUHFService iuhfService;
    private SoundPool soundPool;
    private int soundId;
    private String model;
    /**
     * 退出计时
     */
    private long mkeyTime = 0;
    private long scant = 0;
    /**
     * 盘点命令下发后截取的系统时间
     */
    private long startCheckingTime;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        UHFManager.setStipulationLevel(0);

        try {
            iuhfService = UHFManager.getUHFService(NewMainActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
            boolean cn = "CN".equals(getApplicationContext().getResources().getConfiguration().locale.getCountry());
            if (cn) {
                Toast.makeText(getApplicationContext(), "模块不存在", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Module does not exist", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        model = SharedXmlUtil.getInstance(NewMainActivity.this).read("modle", "");

        initView();
        initData();
//        EventBus.getDefault().register(this);

    }

    public void initView() {
        setContentView(R.layout.activity_main);

        //设置
        ImageView mIvSet = (ImageView) findViewById(R.id.iv_set);
        //搜索按钮
        Button mBtSearch = (Button) findViewById(R.id.bt_search);
        mEtSearch = (EditText) findViewById(R.id.et_search);
        mIvSet.setOnClickListener(this);
        mBtSearch.setOnClickListener(this);
        mFindBtn = (Button) findViewById(R.id.btn_find);
        mFindBtn.setOnClickListener(this);
        mLlFind = (LinearLayout) findViewById(R.id.ll_find_layout);
        mRlPause = (RelativeLayout) findViewById(R.id.rl_pause_layout);
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
        mRlPause.setVisibility(View.GONE);
        mTvListMsg.setVisibility(View.GONE);
        mLlListBg.setVisibility(View.VISIBLE);
        mListViewCard.setVisibility(View.GONE);
//        btnExport.setEnabled(false);
    }

    public void initSoundPool() {
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);

        soundId = soundPool.load("/system/media/audio/ui/VideoRecord.ogg", 0);
        Log.w("as3992_6C", "id is " + soundId);
    }

    public void initData() {
        //上电
        try {
            if (iuhfService != null) {
                if (openDev()) {
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 加载适配器

        uhfCardAdapter = new UhfCardAdapter(this, R.layout.item_uhf_card, uhfCardBeanList);
        mListViewCard.setAdapter(uhfCardAdapter);
        // 列表回调item点击事件
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
                    // 选卡成功 set_current_tag_epc
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

    }

    //新的Listener回调参考代码

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            boolean cn = "CN".equals(NewMainActivity.this.getApplicationContext().getResources().getConfiguration().locale.getCountry());
            switch (msg.what) {
                case 1:
                    scant++;
                    if (mTbtnSound.isChecked()) {
                        soundPool.play(soundId, 1, 1, 0, 0, 1);
                    }
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
                        if (!mTbtnSound.isChecked()) {
                            soundPool.play(soundId, 1, 1, 0, 0, 1);
                        }
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
                default:
                    break;
            }

        }
    };


    /**
     * 处理监听事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_set:
                //设置
                Intent intent = new Intent(this, SetActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_search:
                //搜索
                Intent intent1 = new Intent(this, SearchDirectionActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("epcNumber", mEtSearch.getText().toString());
                intent1.putExtras(bundle);
                startActivity(intent1);
                break;
            case R.id.btn_find:
                //寻卡
                //盘点选卡
                if (inSearch) {
                    inSearch = false;
                    iuhfService.inventoryStop();
                    mFindBtn.setText(R.string.Start_Search_Btn);
                    btnExport.setEnabled(true);
                    btnExport.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_blue_shape));
                    btnExport.setTextColor(getResources().getColor(R.color.text_white));
                } else {
                    inSearch = true;
                    mLlFind.setVisibility(View.GONE);
                    mRlPause.setVisibility(View.VISIBLE);
                    mTvListMsg.setVisibility(View.VISIBLE);
                    mLlListBg.setVisibility(View.GONE);
                    mListViewCard.setVisibility(View.VISIBLE);

                    scant = 0;
                    uhfCardBeanList.clear();
                    //取消掩码
                    iuhfService.selectCard(1, "", false);
                    iuhfService.inventoryStart();
                    // 盘点回调函数
                    iuhfService.setOnInventoryListener(new OnSpdInventoryListener() {
                        @Override
                        public void getInventoryData(SpdInventoryData var1) {
                            handler.sendMessage(handler.obtainMessage(1, var1));
                            Log.d("UHFService", "回调");
                        }
                    });
                    startCheckingTime = System.currentTimeMillis();
                    mFindBtn.setText(R.string.Stop_Search_Btn);
                    btnExport.setEnabled(false);
                    btnExport.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_gray_shape));
                    btnExport.setTextColor(getResources().getColor(R.color.text_gray));
                }
                break;
            case R.id.btn_export:
                //导出
                kProgressHUD = KProgressHUD.create(this)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setCancellable(false)
                        .setAnimationSpeed(2)
                        .setDimAmount(0.5f)
                        .show();
                if (uhfCardBeanList.size() > 0) {
                    new Thread(new Runnable() {
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
                                            .setSHEET_NAME("UHFMsg")//设置表格名称
                                            .setFONT_COLOR(Colour.BLUE)//设置标题字体颜色
                                            .setFONT_TIMES(8)//设置标题字体大小
                                            .setFONT_BOLD(true)//设置标题字体是否斜体
                                            .setBACKGROND_COLOR(Colour.GRAY_25)//设置标题背景颜色
                                            .setContent_list_Strings(epcBeanList)//设置excel内容
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
                    }).start();

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
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //初始化声音线程
        initSoundPool();

    }


    /**
     * 上电开串口
     */
    private boolean openDev() {
        if (iuhfService.openDev() != 0) {
            Toast.makeText(this, "Open serialport failed", Toast.LENGTH_SHORT).show();
            new AlertDialog.Builder(this).setTitle(R.string.DIA_ALERT).setMessage(R.string.DEV_OPEN_ERR).setPositiveButton(R.string.DIA_CHECK, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).show();
            return true;
        }
        return false;
    }


    /**
     * 更新显示数据
     */
    private void updateRateCount() {

        long mlEndTime = System.currentTimeMillis();

        double rate = Math.ceil((scant * 1.0) * 1000 / (mlEndTime - startCheckingTime));

        long totalTimeCount = mlEndTime - startCheckingTime;

        speedTv.setText(String.format("%s次/s", String.valueOf(rate)));

        tagNumTv.setText(String.format("%s", String.valueOf(uhfCardBeanList.size())));

        totalTime.setText(String.format("耗时%ss", String.valueOf(getTimeFromMillisecond(totalTimeCount))));


    }


    @Override
    protected void onStop() {

        Log.w("stop", "im stopping");
        if (inSearch) {
            iuhfService.inventoryStop();
            inSearch = false;
        }
        soundPool.release();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (iuhfService!=null){
            iuhfService.closeDev();
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
     * 时间格式:  时:分:秒.毫秒
     *
     * @param millisecond 毫秒
     * @return 时间字符串
     */
    public static String getTimeFromMillisecond(Long millisecond) {
        String milli;
        int num = 100;
        //根据时间差来计算小时数
        long hours = millisecond / (60 * 60 * 1000);
        //根据时间差来计算分钟数
        long minutes = (millisecond - hours * (60 * 60 * 1000)) / (60 * 1000);
        //根据时间差来计算秒数
        long second = (millisecond - hours * (60 * 60 * 1000) - minutes * (60 * 1000)) / 1000;
        //根据时间差来计算秒数
        long milliSecond = millisecond - hours * (60 * 60 * 1000) - minutes * (60 * 1000) - second * 1000;
        if (milliSecond < num) {
            milli = "0" + milliSecond;
        } else {
            milli = "" + milliSecond;
        }
        if (hours == 0) {
            if (minutes == 0) {
                return second + "." + milli;
            }
            return minutes + ": " + second + "." + milli;
        }
        return hours + ": " + minutes + ": " + second + "." + milli;
    }
}
