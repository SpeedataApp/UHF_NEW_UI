package com.speedata.uhf;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.speedata.libuhf.IUHFService;
import com.speedata.libuhf.UHFManager;
import com.speedata.libuhf.bean.SpdInventoryData;
import com.speedata.libuhf.interfaces.OnSpdInventoryListener;

import java.util.Objects;

/**
 * 搜索声音寻卡
 * Created by 张智超 on 2019/3/8
 *
 * @author My_PC
 */
public class SearchDirectionActivity extends BaseActivity implements View.OnClickListener {

    private SoundPool soundPool;
    private int soundId;
    private int soundId1;
    private IUHFService iuhfService;

    private String epcToStr;
    private ImageView imageViewSearch;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_card);
        initView();
        initData();
    }

    public void initView() {
        //返回
        ImageView mIvQuit = (ImageView) findViewById(R.id.search_title_iv);
        //停止
        Button mStopBtn = (Button) findViewById(R.id.search_stop);
        mStopBtn.setOnClickListener(this);
        mIvQuit.setOnClickListener(this);
        imageViewSearch = findViewById(R.id.search_card);
    }

    public void initSoundPool() {
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);

        soundId = soundPool.load("/system/media/audio/ui/VideoRecord.ogg", 0);
        Log.w("as3992_6C", "id is " + soundId);

        soundId1 = soundPool.load(this, R.raw.scankey, 0);

    }

    public void initData() {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(this).load(R.drawable.bg_search_card).apply(options).into(imageViewSearch);
        iuhfService = MyApp.getInstance().getIuhfService();
        initSoundPool();
        //取消掩码
        iuhfService.selectCard(1, "", false);
        getBundle();
    }

    public void getBundle() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
//        assert bundle != null;
        epcToStr = Objects.requireNonNull(bundle).getString("epcNumber");
    }

    @Override
    protected void onResume() {
        super.onResume();
        initSoundPool();
        iuhfService.inventoryStart();
        iuhfService.setOnInventoryListener(new OnSpdInventoryListener() {
            @Override
            public void getInventoryData(SpdInventoryData var1) {
                handler.sendMessage(handler.obtainMessage(1, var1));
                Log.w("as3992_6C", "id is " + soundId);
            }

            @Override
            public void onInventoryStatus(int status) {
                iuhfService.inventoryStart();
            }
        });
    }

    //新的Listener回调参考代码

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                if (!TextUtils.isEmpty(epcToStr)) {
                    SpdInventoryData spdInventoryData = (SpdInventoryData) msg.obj;
                    String epc = spdInventoryData.getEpc();
                    if (epc.equals(epcToStr)) {
                        int rssi = Integer.parseInt(spdInventoryData.getRssi());
                        int i = -60;
                        int j = -40;
                        if (rssi > i) {
                            if (rssi > j) {
                                soundPool.play(soundId1, 1, 1, 0, 0, 3);
                            } else {
                                soundPool.play(soundId1, 0.6F, 0.6F, 0, 0, 2);
                            }

                        } else {
                            soundPool.play(soundId1, 0.3F, 0.3F, 0, 0, 1);
                        }
                    }
                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_title_iv:
            case R.id.search_stop:
                iuhfService.inventoryStop();
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStop() {
        Log.w("stop", "im stopping");
        soundPool.release();
        super.onStop();
    }
}
