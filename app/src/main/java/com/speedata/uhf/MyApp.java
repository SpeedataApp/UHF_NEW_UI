package com.speedata.uhf;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import com.speedata.libuhf.IUHFService;
import com.speedata.libuhf.UHFManager;
import com.speedata.libuhf.utils.SharedXmlUtil;
import com.speedata.uhf.adapter.UhfCardBean;
import com.speedata.uhf.adapter.UhfInfoBean;
import com.speedata.uhf.floatball.FloatBallManager;
import com.speedata.uhf.floatball.FloatListManager;
import com.speedata.uhf.floatball.FloatWarnManager;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;
import com.yhao.floatwindow.FloatWindow;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 张明_
 * @date 2018/3/15
 */
public class MyApp extends Application {
    /**
     * 单例   Single case
     */
    private static MyApp m_application;
    private IUHFService iuhfService;
    public static boolean isStart = false;
    public static boolean isOpenDev = false;
    public static boolean isOpenServer = true;
    public static int mPrefix = 3;
    public static int mSuffix = 3;
    public static boolean isLoop = false;
    public static String mLoopTime = "0";
    public static boolean isLongDown = false;
    /**
     * 是否过滤
     */
    public static boolean isFilter = false;
    /**
     * 程序启动初始化一次的标志，运行过程中不再初始化
     * The flag that the program starts and initializes once, not again during execution
     */
    public static boolean isFirstInit = true;
    /**
     * 是否启动快速模式
     * Whether to start fast mode
     */
    public static boolean isFastMode = false;
    public final static String UHF_FREQ = "uhf_freq";
    public final static String UHF_SESSION = "uhf_session";
    public final static String UHF_POWER = "uhf_power";
    public final static String UHF_INV_CON = "uhf_inv_con";
    public final static String UHF_INV_TIME = "uhf_inv_time";
    public final static String UHF_INV_SLEEP = "uhf_inv_sleep";
    public final static String ACTION_SEND_CUSTOM = "action_send_custom";
    public final static String ACTION_KEY_EPC = "action_key_epc";
    public final static String ACTION_KEY_TID = "action_key_tid";
    public final static String ACTION_KEY_RSSI = "action_key_rssi";
    /**
     * 是否焦点显示epc
     */
    public final static String IS_FOCUS_SHOW = "isFocusShow";
    public final static String EPC_OR_TID = "focus_switch_tid";
    /**
     * 缓存列表
     */
    public static List<UhfInfoBean> list = new ArrayList<>();

    public static MyApp getInstance() {
        return m_application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("APP", "onCreate");
        m_application = this;
        Context context = getApplicationContext();
        // 获取当前包名   Gets the current package name
        String packageName = context.getPackageName();
        // 获取当前进程名  Gets the current process name
        String processName = getProcessName(android.os.Process.myPid());
        // 设置是否为上报进程    Set whether it is an escalation process
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        // 初始化Bugly     Initialize Bugly
        Bugly.init(getApplicationContext(), "d39e936d74", true, strategy);

        Log.d("UHFService", "MyApp onCreate");
    }

    public IUHFService getIuhfService() {
        Log.d("zzc:", "IUHFService===" + iuhfService);
        return iuhfService;
    }

    public void releaseIuhfService() {
        if (iuhfService != null) {
            iuhfService.closeDev();
            iuhfService = null;
            UHFManager.closeUHFService();
            isFirstInit = true;
        }
    }

    public void setIuhfService() {
        try {
            iuhfService = UHFManager.getUHFService(getApplicationContext());
            Log.d("UHFService", "iuhfService初始化: " + iuhfService);
        } catch (Exception e) {
            e.printStackTrace();
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    FloatWarnManager.getInstance(getApplicationContext(), getResources().getString(R.string.dialog_module_none));
                    FloatWarnManager floatWarnManager = FloatWarnManager.getFloatWarnManager();
                    if (floatWarnManager != null) {
                        FloatWindow.get("FloatWarnTag").show();
                    }
                }
            });
        }

    }

    public void initParam() {
        int i;
        i = iuhfService.setFreqRegion(SharedXmlUtil.getInstance(this).read(MyApp.UHF_FREQ, 1));
        Log.d("zzc:", "===isFirstInit===setFreqRegion:" + i);
        SystemClock.sleep(600);
        Log.d("zzc:", "===isFirstInit===setFreqRegion:" + iuhfService.getFreqRegion());
        i = iuhfService.setAntennaPower(SharedXmlUtil.getInstance(this).read(MyApp.UHF_POWER, 30));
        Log.d("zzc:", "===isFirstInit===setAntennaPower:" + i);
        SystemClock.sleep(100);
        if (!UHFManager.getUHFModel().equals(UHFManager.FACTORY_YIXIN)) {
            i = iuhfService.setQueryTagGroup(0, SharedXmlUtil.getInstance(this).read(MyApp.UHF_SESSION, 0), 0);
            Log.d("zzc:", "===isFirstInit===setQueryTagGroup:" + i);
            SystemClock.sleep(100);
            i = iuhfService.setInvMode(SharedXmlUtil.getInstance(this).read(MyApp.UHF_INV_CON, 1), 0, 12);
            Log.d("zzc:", "===isFirstInit===setInvMode:" + i);
        }
        if (UHFManager.getUHFModel().contains(UHFManager.FACTORY_XINLIAN)) {
            iuhfService.setLowpowerScheduler(SharedXmlUtil.getInstance(this).read(MyApp.UHF_INV_TIME, 50), SharedXmlUtil.getInstance(this).read(MyApp.UHF_INV_SLEEP, 0));
        }
        SystemClock.sleep(100);
    }

    /**
     * 获取进程号对应的进程名
     * Gets the process name corresponding to the process number
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void onTerminate() {
        stopService(new Intent(this, MyService.class));
        SharedXmlUtil.getInstance(this).write("server", false);
        releaseIuhfService();
        MyApp.isOpenDev = false;

        if (FloatBallManager.getFloatBallManager() != null) {
            FloatBallManager.getFloatBallManager().closeFloatBall();
        }
        if (FloatListManager.getFloatListManager() != null) {
            FloatListManager.getFloatListManager().closeFloatList();
        }
        SharedXmlUtil.getInstance(this).write("floatWindow", "close");
        super.onTerminate();
    }
}
