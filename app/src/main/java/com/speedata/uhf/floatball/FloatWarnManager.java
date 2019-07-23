package com.speedata.uhf.floatball;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.speedata.libuhf.UHFManager;
import com.speedata.uhf.BaseActivity;
import com.speedata.uhf.R;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.PermissionListener;
import com.yhao.floatwindow.Screen;
import com.yhao.floatwindow.ViewStateListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
public class FloatWarnManager {
    private Context context;
    private String message;
    private static FloatWarnManager floatWarnManager;
    private final String TAG = "FloatWarnManager";
    private static final String CHARGING_PATH = "/sys/class/misc/bq25601/regdump/";
    private File file;
    private BufferedWriter writer;

    private ViewStateListener mViewStateListener = new ViewStateListener() {
        @Override
        public void onPositionUpdate(int x, int y) {
            Log.d(TAG, "onPositionUpdate: x=" + x + " y=" + y);
        }

        @Override
        public void onShow() {
            Log.d(TAG, "onShow");
            UHFManager.closeUHFService();
            try {
                file = new File(CHARGING_PATH);
                writer = new BufferedWriter(new FileWriter(file, false));
                writer.write("otgoff");
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onHide() {
            Log.d(TAG, "onHide");
            closeFloatWarn();
            System.exit(0);
        }

        @Override
        public void onDismiss() {
            Log.d(TAG, "onDismiss");
            System.exit(0);
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

    public static FloatWarnManager getFloatWarnManager() {
        return floatWarnManager;
    }

    private FloatWarnManager(Context context, String message) {
        this.context = context;
        this.message = message;
    }

    public static FloatWarnManager getInstance(Context context, String message) {
        if (floatWarnManager == null) {
            floatWarnManager = new FloatWarnManager(context, message);
            floatWarnManager.startFloatList();
        }
        return floatWarnManager;
    }

    private void startFloatList() {
        @SuppressLint("InflateParams") View warnWindow = LayoutInflater.from(context).inflate(R.layout.item_warning, null);
        TextView tvWarnMessage = warnWindow.findViewById(R.id.item_msg);
        tvWarnMessage.setText(message);
        Button btnOk = warnWindow.findViewById(R.id.item_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatWindow.get("FloatWarnTag").hide();
                FloatListManager floatListManager = FloatListManager.getInstance(context);
                FloatBallManager floatBallManager = FloatBallManager.getInstance(context);
                if (floatListManager != null) {
                    FloatWindow.get("FloatListTag").hide();
                }
                if (floatBallManager != null) {
                    FloatWindow.get("FloatBallTag").hide();
                }
            }
        });
        FloatWindow
                .with(context)
                .setView(warnWindow)
                //设置悬浮控件宽高
                .setWidth(Screen.width, 1f)
                .setHeight(Screen.height, 1f)
                .setX(0)
                .setY(0)
                .setMoveType(MoveType.inactive)
                .setViewStateListener(mViewStateListener)
                .setPermissionListener(mPermissionListener)
                .setDesktopShow(true)
                .setFilter(false, BaseActivity.class)
                .setTag("FloatWarnTag")
                .build();
    }


    public void closeFloatWarn() {
        FloatWindow.destroy("FloatWarnTag");
        floatWarnManager = null;
    }
}
