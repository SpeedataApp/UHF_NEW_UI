package com.speedata.uhf;

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
import android.os.IBinder;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.Log;

import com.speedata.libuhf.IUHFService;
import com.speedata.libuhf.UHFManager;
import com.speedata.libuhf.bean.SpdInventoryData;
import com.speedata.libuhf.interfaces.OnSpdBanMsgListener;
import com.speedata.libuhf.interfaces.OnSpdInventoryListener;
import com.speedata.uhf.floatball.FloatWarnManager;
import com.speedata.uhf.floatball.ModeManager;
import com.yhao.floatwindow.FloatWindow;

import java.text.Format;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

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
    /**
     * 按设备侧键触发的扫描广播
     */
    public static final String SCAN_BARCODE = "com.geomobile.se4500barcode";
    public static final String ACTION_SEND_EPC = "com.se4500.onDecodeComplete";
    public static final String ACTION_SEND_DATA = "com.spd.action.data";
    public static final String UPDATE = "uhf.update";
    private static final String TAG = "UHFService";
    private SoundPool soundPool;
    private int soundId;
    private boolean isStart = false;
    /**
     * 扫头扫描模式
     */
    public final int MODE_SCAN = 1;
    /**
     * 超高频单次模式
     */
    public final int MODE_UHF = 2;
    /**
     * 超高频重复模式
     */
    public final int MODE_UHF_RE = 3;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "===rece===action===" + action);
//            assert action != null;
            if (Objects.equals(action, Intent.ACTION_SCREEN_OFF)) {
                Log.d(TAG, "===熄屏了===" + action);
                IUHFService iuhfService = MyApp.getInstance().getIuhfService();
                if (iuhfService != null) {
                    iuhfService.inventoryStop();
                    iuhfService.closeDev();
                    MyApp.isOpenDev = false;
                    Log.d(TAG, "===熄屏下电了===" + action);
                }
            }
            if (Objects.equals(action, Intent.ACTION_SCREEN_ON)) {
                initUHF();
                if (openDev()) {
                    Log.d(TAG, "===亮屏了==上电成功===" + action);
                }
            }

            if (MyApp.isOpenServer) {
                switch (Objects.requireNonNull(action)) {
                    case START_SCAN:
                        //启动超高频扫描
                        if (openDev()) {
                            startScan();
                            if (MyApp.isLoop) {
                                creatTimer();
                            }
                        }
                        break;
                    case STOP_SCAN:
                        if (MyApp.isLongDown) {
                            //停止盘点
                            MyApp.getInstance().getIuhfService().inventoryStop();
                            isStart = false;
                            cancelTimer();
                            return;
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

    private Timer timer;
    private TimerTask myTimerTask;
    private int mTime = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "===onCreate===");
        setBuilder();
        initReceive();
        initUHF();
    }

    private void creatTimer() {
        if (timer == null) {
            if (MyApp.mLoopTime.isEmpty() || MyApp.mLoopTime == null || "".equals(MyApp.mLoopTime)) {
                MyApp.mLoopTime = "0";
            }
            final int loopTime = Integer.parseInt(MyApp.mLoopTime);
            if (loopTime == 0) {
                return;
            }
            timer = new Timer();
            if (myTimerTask != null) {
                myTimerTask.cancel();
            }
            mTime = 0;
            myTimerTask = new TimerTask() {
                @Override
                public void run() {
                    mTime++;
                    if (mTime >= loopTime) {
                        MyApp.getInstance().getIuhfService().inventoryStop();
                        isStart = false;
                        cancelTimer();
                    }
                }
            };
            timer.schedule(myTimerTask, 1000, 1000);
        }
    }

    private void cancelTimer() {
        if (myTimerTask != null) {
            myTimerTask.cancel();
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void initUHF() {
        if (soundPool == null) {
            soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
            soundId = soundPool.load("/system/media/audio/ui/VideoRecord.ogg", 0);
            Log.w("as3992_6C", "id is " + soundId);
        }
        Log.e(TAG, "initUHF");
        if (MyApp.getInstance().getIuhfService() != null) {
            openDev();
        }
    }

    /**
     * 注册广播
     */
    private void initReceive() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(START_SCAN);
        filter.addAction(STOP_SCAN);
        filter.addAction(UPDATE);

        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(receiver, filter);
    }

    private class ListThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                sleep(60000);
                if (linkedList != null) {
                    if (!linkedList.isEmpty()) {
                        String epc = (String) linkedList.removeFirst();
                        Log.d("zzcEpc", "= remove =" + epc);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private LinkedList linkedList;

    private void startScan() {
        final int mode = ModeManager.getInstance(MyService.this).getScanMode();
        MyApp.getInstance().getIuhfService().setOnInventoryListener(new OnSpdInventoryListener() {
            @Override
            public void getInventoryData(SpdInventoryData var1) {
                String epc = var1.getEpc();
                if (!epc.isEmpty() && isStart && !linkedList.contains(var1.getEpc())) {
                    sendEpc(epc);
                    linkedList.addLast(var1.getEpc());
//                    Log.d("zzcEpc", "= add =" + epc);
                    ListThread listThread = new ListThread();
                    listThread.start();
                    boolean isOnce = mode == MODE_UHF;
                    if (isOnce) {
                        //停止盘点
                        MyApp.getInstance().getIuhfService().inventoryStop();
                        isStart = false;
                    }
                }
            }
        });
        if (MyApp.getInstance().getIuhfService() != null) {
            switch (mode) {
                case MODE_SCAN:
                    //调用扫头扫描
                    SystemProperties.set("persist.sys.scanstopimme", "false");
                    sendBroadcast(new Intent(SCAN_BARCODE));
                    break;
                case MODE_UHF:
                case MODE_UHF_RE:
                    if (!isStart) {
                        MyApp.getInstance().getIuhfService().inventoryStart();
                        linkedList = new LinkedList();
                        isStart = true;
                    } else {
                        MyApp.getInstance().getIuhfService().inventoryStop();
                        if (linkedList != null) {
                            linkedList.clear();
                            linkedList = null;
                        }
                        isStart = false;
                        cancelTimer();
                    }
                    break;
                default:
                    break;
            }

        }
    }

    private void sendEpc(String epc) {
        soundPool.play(soundId, 1, 1, 0, 0, 1);
        switch (MyApp.mPrefix) {
            case 0:
                epc = "\n" + epc;
                break;
            case 1:
                epc = " " + epc;
                break;
            case 2:
                epc = "\r\n" + epc;
                break;
            case 3:
                epc = "" + epc;
                break;
            default:
                break;
        }
        switch (MyApp.mSuffix) {
            case 0:
                epc = epc + "\n";
                break;
            case 1:
                epc = epc + " ";
                break;
            case 2:
                epc = epc + "\r\n";
                break;
            case 3:
                epc = epc + "";
                break;
            default:
                break;
        }
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
        Log.d("zzc", "==MyService==openDev==" + MyApp.isOpenDev);
        if (!MyApp.isOpenDev) {
            if (MyApp.getInstance().getIuhfService() != null) {
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
            }
            MyApp.isOpenDev = false;
            return false;
        } else {
            return true;
        }
    }

    private void setBuilder() {
        UHFManager uhfManager = new UHFManager();
        uhfManager.setOnBanMsgListener(new OnSpdBanMsgListener() {
            @Override
            public void getBanMsg(String var1) {
                Log.e("zzc:UHFService", "====监听报警====");
                if (var1.contains("Low")) {
                    var1 = getResources().getString(R.string.low_power);
                } else if (var1.contains("High")) {
                    var1 = getResources().getString(R.string.high_temp);
                }
                FloatWarnManager.getInstance(getApplicationContext(), var1);
                FloatWarnManager floatWarnManager = FloatWarnManager.getFloatWarnManager();
                if (floatWarnManager != null) {
                    FloatWindow.get("FloatWarnTag").show();
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "===onDestroy===");
        soundPool.release();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }
}
