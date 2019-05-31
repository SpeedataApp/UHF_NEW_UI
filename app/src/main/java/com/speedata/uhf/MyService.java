package com.speedata.uhf;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.speedata.libuhf.IUHFService;
import com.speedata.libuhf.UHFManager;
import com.speedata.libuhf.bean.SpdInventoryData;
import com.speedata.libuhf.interfaces.OnSpdInventoryListener;

/**
 * 接受广播  触发盘点，返回EPC
 *
 * @author My_PC
 */
public class MyService extends Service {

    /**
     * 按设备侧键触发的扫描广播
     */
    public static final String START_SCAN = "com.spd.action.start_uhf";
    public static final String STOP_SCAN = "com.spd.action.stop_uhf";
    public static final String ACTION_SEND_EPC = "com.se4500.onDecodeComplete";
    public static final String ACTION_SEND_DATA = "com.spd.action.data";
    public static final String UPDATE = "uhf.update";
    private static final String TAG = "UHFService";
    private SoundPool soundPool;
    private int soundId;
    private boolean isStart = false;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (MyApp.isOpenServer) {
                String action = intent.getAction();

                Log.d(TAG, "===rece===action" + action);
                assert action != null;
                switch (action) {
                    case START_SCAN:
                        //启动超高频扫描
                        if (openDev()) {
                            if (isStart) {
                                return;
                            }
                            MyApp.getInstance().getIuhfService().inventoryStart();
                            isStart = true;
                        }
                        break;
                    case STOP_SCAN:
                        if (isStart) {
                            MyApp.getInstance().getIuhfService().inventoryStop();
                            isStart = false;
                        }
                        break;
                    case UPDATE:
                        initUHF();
                        break;
                    default:
                        break;
                }
            }
        }
    };

    public MyService() {
    }


    private UhfBinder mBinder = new UhfBinder();

    class UhfBinder extends Binder {

        void initUHF() {
            Log.d(TAG, "initUHF");
            initUHF();
        }

        public int releaseUHF() {
            Log.d("MyService", "getProgress executed");
            return 0;
        }
        //在服务中自定义getProgress()方法，待会活动中调用此方法

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }//普通服务的不同之处，onBind()方法不在打酱油，而是会返回一个实例


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "===onCreate===");
        initReceive();
        initUHF();
    }

    private void initUHF() {
        if (soundPool == null) {
            soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
            soundId = soundPool.load("/system/media/audio/ui/VideoRecord.ogg", 0);
            Log.w("as3992_6C", "id is " + soundId);
        }
        Log.e(TAG, "initUHF");
        MyApp.getInstance().getIuhfService().setOnInventoryListener(new OnSpdInventoryListener() {
            @Override
            public void getInventoryData(SpdInventoryData var1) {

                String epc = var1.getEpc();
                if (!epc.isEmpty() && isStart) {
                    Log.d(TAG, "===inventoryStop===");
                    sendEpc(var1.getEpc());
                    //停止盘点
                    MyApp.getInstance().getIuhfService().inventoryStop();
                    isStart = false;
                }
            }
        });
        openDev();
    }

    /**
     * 注册广播
     */
    private void initReceive() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(START_SCAN);
        filter.addAction(STOP_SCAN);
        filter.addAction(UPDATE);
        registerReceiver(receiver, filter);
    }

    private void sendEpc(String epc) {
        soundPool.play(soundId, 1, 1, 0, 0, 1);
        Intent intent = new Intent();
        intent.setAction(ACTION_SEND_EPC);
        Bundle bundle = new Bundle();
        bundle.putString("se4500", epc);
        intent.putExtras(bundle);
        sendBroadcast(intent);
        Log.d(TAG, "===SendEpc===" + epc);
    }

    /**
     * 上电开串口
     */
    private boolean openDev() {
        if (!MyApp.isOpenDev) {
            final int i = MyApp.getInstance().getIuhfService().openDev();
            if (i != 0) {
                new AlertDialog.Builder(this).setTitle(R.string.DIA_ALERT).setMessage(R.string.DEV_OPEN_ERR).setPositiveButton(R.string.DIA_CHECK, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "===openDev===失败" + i);
                    }
                }).show();
                MyApp.isOpenDev = false;
                return false;
            } else {
                Log.d(TAG, "===openDev===成功");
                MyApp.isOpenDev = true;
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "===onDestroy===");
        soundPool.release();
    }
}
