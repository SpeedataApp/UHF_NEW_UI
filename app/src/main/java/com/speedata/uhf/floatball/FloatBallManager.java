package com.speedata.uhf.floatball;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;

import com.speedata.uhf.BaseActivity;
import com.speedata.uhf.PikestaffActivity;
import com.speedata.uhf.R;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.PermissionListener;
import com.yhao.floatwindow.Screen;
import com.yhao.floatwindow.ViewStateListener;

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
 * @author :EchoXBR in  2019/3/20 下午3:20.
 * 功能描述:悬浮管理类
 */
public class FloatBallManager {
    private Context context;
    private static FloatBallManager floatBallManager;
    private final String TAG = "FloatBallManager";
    ImageView imageView;
    private static Point sPoint;
    String action = "updata_state";
    private ModeManager modeManager;
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setFloatBallStatus(modeManager.getScanMode());
        }
    };
    private ViewStateListener mViewStateListener = new ViewStateListener() {
        @Override
        public void onPositionUpdate(int x, int y) {
            Log.d(TAG, "onPositionUpdate: x=" + x + " y=" + y);
        }

        @Override
        public void onShow() {
            Log.d(TAG, "onShow");
        }

        @Override
        public void onHide() {
            Log.d(TAG, "onHide");
        }

        @Override
        public void onDismiss() {
            Log.d(TAG, "onDismiss");
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

    public static FloatBallManager getFloatBallManager() {
        return floatBallManager;
    }

    public FloatBallManager(Context context) {
        this.context = context;
    }

    public static FloatBallManager getInstance(Context context) {
        if (floatBallManager == null) {
            floatBallManager = new FloatBallManager(context);
            floatBallManager.initButton();
        }
        return floatBallManager;
    }


    public void initButton() {
        modeManager = ModeManager.getInstance(context);
        startFloatBtn();
        setFloatBallStatus(modeManager.getScanMode());
        context.registerReceiver(receiver, new IntentFilter(action));
    }


    public void startFloatBtn() {
        imageView = new ImageView(context);
        imageView.setImageResource(R.mipmap.icon_scan);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatListManager floatListManager = FloatListManager.getInstance(context);
                if (floatListManager != null) {
                    FloatWindow.get("FloatBallTag").hide();
                    FloatWindow.get("FloatListTag").show();
                }
            }
        });
        modeManager.setViewListener(imageView);
        FloatWindow
                .with(context)
                .setView(imageView)
                //设置悬浮控件宽高
                .setWidth(Screen.width, 0.15f)
                .setHeight(Screen.width, 0.15f)
                .setX(Screen.width, 0.8f)
                .setY(Screen.height, 0.3f)
                .setMoveType(MoveType.slide, (int) (getScreenWidth() * 0.15f / 2), (int) -(getScreenWidth() * 0.15f / 2))
                .setMoveStyle(500, new BounceInterpolator())
                .setViewStateListener(mViewStateListener)
                .setPermissionListener(mPermissionListener)
                .setDesktopShow(true)
                .setFilter(true, BaseActivity.class, PikestaffActivity.class)
                .setTag("FloatBallTag")
                .build();
    }

    private void setFloatBallStatus(int status) {
        switch (status) {
            case ModeManager.MODE_SCAN:
                imageView.setImageResource(R.mipmap.icon_scan);
                break;
            case ModeManager.MODE_UHF:
                imageView.setImageResource(R.mipmap.icon_uhf_one);
                break;
            case ModeManager.MODE_UHF_RE:
                imageView.setImageResource(R.mipmap.icon_uhf_re);
                break;
            default:
                imageView.setImageResource(R.mipmap.icon_home);
                break;
        }
    }

    /**
     * @return 获取屏幕宽度
     */
    private int getScreenWidth() {
        if (sPoint == null) {
            sPoint = new Point();
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getSize(sPoint);
        }
        return sPoint.x;
    }

    public void closeFloatBall() {
        FloatWindow.destroy("FloatBallTag");
        floatBallManager = null;
    }
}
