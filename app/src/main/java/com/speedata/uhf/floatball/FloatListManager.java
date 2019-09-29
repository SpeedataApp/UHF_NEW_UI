package com.speedata.uhf.floatball;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.speedata.uhf.BaseActivity;
import com.speedata.uhf.HelloActivity;
import com.speedata.uhf.MyApp;
import com.speedata.uhf.PikestaffActivity;
import com.speedata.uhf.R;
import com.speedata.uhf.SetActivity;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.PermissionListener;
import com.yhao.floatwindow.Screen;
import com.yhao.floatwindow.ViewStateListener;

import android.os.Handler;

/**
 * //                            _ooOoo_
 * //                           o8888888o
 * //                           88" . "88
 * //                           (| -_- |)
 * //                            O\ = /O
 * //                        ____/`---'\____
 * //                      .   ' \\| |// `.
 * //                       / \\||| : |||// \
 * //                     / _||||| -:- |||||- \
 * //                       | | \\\ - /// | |
 * //                     | \_| ''\---/'' | |
 * //                      \ .-\__ `-` ___/-. /
 * //                   ___`. .' /--.--\ `. . __
 * //                ."" '< `.___\_<|>_/___.' >'"".
 * //               | | : `- \`.;`\ _ /`;.`/ - ` : | |
 * //                 \ \ `-. \_ __\ /__ _/ .-` / /
 * //         ======`-.____`-.___\_____/___.-`____.-'======
 * //                            `=---='
 * //
 * //         .............................................
 * //                  佛祖镇楼                  BUG辟易
 *
 * @author :zzc
 * @date 2019/05/28
 * 功能描述:悬浮列表管理类
 */
public class FloatListManager {
    private Context context;
    private static FloatListManager floatListManager;
    private final String TAG = "FloatBallManager";
    String action = "updata_state";
    private ModeManager modeManager;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            FloatWindow.get("FloatBallTag").show();
            FloatWindow.get("FloatListTag").hide();
        }
    };

    private ViewStateListener mViewStateListener = new ViewStateListener() {
        @Override
        public void onPositionUpdate(int x, int y) {
            Log.d(TAG, "onPositionUpdate: x=" + x + " y=" + y);
        }

        @Override
        public void onShow() {
            Log.d(TAG, "List onShow");
            if (handler != null) {
                handler.postDelayed(runnable, 5000);
            }
        }

        @Override
        public void onHide() {
            Log.d(TAG, "List onHide");
            if (handler != null) {
                handler.removeCallbacks(runnable);
            }
        }

        @Override
        public void onDismiss() {
            Log.d(TAG, "onDismiss");
            if (handler != null) {
                handler.removeCallbacks(runnable);
            }
        }

        @Override
        public void onMoveAnimStart() {
            Log.d(TAG, "onMoveAnimStart");
        }

        @Override
        public void onMoveAnimEnd() {
            Log.d(TAG, "onMoveAnimEnd");
        }

        @Override
        public void onBackToDesktop() {
            Log.d(TAG, "onBackToDesktop");
        }
    };
    private PermissionListener mPermissionListener = new PermissionListener() {
        @Override
        public void onSuccess() {
            Log.d(TAG, "onSuccess");
        }

        @Override
        public void onFail() {
            Log.d(TAG, "onFail");
        }
    };

    public static FloatListManager getFloatListManager() {
        return floatListManager;
    }

    private FloatListManager(Context context) {
        this.context = context;
    }

    static FloatListManager getInstance(Context context) {
        if (floatListManager == null) {
            floatListManager = new FloatListManager(context);
            floatListManager.initButton();
        }
        return floatListManager;
    }


    private void initButton() {
        modeManager = ModeManager.getInstance(context);
        startFloatList();
    }


    private void startFloatList() {
        @SuppressLint("InflateParams") View listWindow = LayoutInflater.from(context).inflate(R.layout.item_list_mode, null);
        LinearLayout llScanMode = listWindow.findViewById(R.id.ll_scan_mode);
        LinearLayout llUhfOneMode = listWindow.findViewById(R.id.ll_uhf_one);
        LinearLayout llUhfMoreMode = listWindow.findViewById(R.id.ll_uhf_more);
        llScanMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modeManager.changeScanMode(ModeManager.MODE_SCAN);
                FloatWindow.get("FloatBallTag").show();
                FloatWindow.get("FloatListTag").hide();
            }
        });
        llUhfOneMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modeManager.changeScanMode(ModeManager.MODE_UHF);
                FloatWindow.get("FloatBallTag").show();
                FloatWindow.get("FloatListTag").hide();
            }
        });
        llUhfMoreMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modeManager.changeScanMode(ModeManager.MODE_UHF_RE);
                FloatWindow.get("FloatBallTag").show();
                FloatWindow.get("FloatListTag").hide();
            }
        });
        FloatWindow
                .with(context)
                .setView(listWindow)
                //设置悬浮控件宽高
                .setWidth(Screen.width, 0.5f)
                .setHeight(Screen.width, 0.5f)
                .setX(Screen.width, 0.25f)
                .setY(Screen.height, 0.3f)
                .setMoveType(MoveType.inactive)
                .setViewStateListener(mViewStateListener)
                .setPermissionListener(mPermissionListener)
                .setDesktopShow(true)
                .setFilter(false, BaseActivity.class, HelloActivity.class, SetActivity.class, PikestaffActivity.class)
                .setTag("FloatListTag")
                .build();
    }


    public void closeFloatList() {
        FloatWindow.destroy("FloatListTag");
        floatListManager = null;
    }
}
