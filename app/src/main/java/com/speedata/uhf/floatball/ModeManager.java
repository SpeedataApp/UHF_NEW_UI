package com.speedata.uhf.floatball;

import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.widget.ImageView;

import com.speedata.libuhf.utils.SharedXmlUtil;

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
 * @author :EchoXBR in  2019/3/19 下午6:40.
 * 功能描述:模式
 */
public class ModeManager {

    /**
     * 扫头模式
     */
    public static final int MODE_SCAN = 1;
    /**
     * 超高频单次模式
     */
    public static final int MODE_UHF = 2;
    /**
     * 超高频重复模式
     */
    public static final int MODE_UHF_RE = 3;

    /**
     * home键
     */
    public static final int MODE_HOME = 4;
    /**
     * 返回键
     */
    public static final int MODE_BACK = 5;
    /**
     * 不自定义
     */
    public static final int MODE_NONE = 6;

    private static ModeManager manager;
    SharedXmlUtil sharedXmlUtil;
    private Context context;
    private ImageView imageView;

    private ModeManager(Context context) {
        this.context = context;
        sharedXmlUtil = SharedXmlUtil.getInstance(context);
    }

    public static ModeManager getInstance(Context context) {
        if (manager == null) {
            manager = new ModeManager(context);
        }
        return manager;
    }

    public void setViewListener(ImageView view) {
        imageView = view;

    }

    /**
     * 通知修改button状态
     */
    private void changeView() {
        context.sendBroadcast(new Intent("updata_state"));
    }

    /**
     * @param mode
     *         1为激光，2为单次UHF，3为连续UHF
     *
     * @return 非0为非法数值
     */
    public int changeScanMode(int mode) {

        int result = 0;
        switch (mode) {
            case MODE_SCAN:
                SystemProperties.set("persist.sys.PistolKey", "scan");
                sharedXmlUtil.write("current_mode", MODE_SCAN);
                changeView();
                break;
            case MODE_UHF:
                SystemProperties.set("persist.sys.PistolKey", "uhf");
                sharedXmlUtil.write("current_mode", MODE_UHF);
                changeView();
                break;
            case MODE_UHF_RE:
                SystemProperties.set("persist.sys.PistolKey", "uhf");
                sharedXmlUtil.write("current_mode", MODE_UHF_RE);
                changeView();
                break;
            case MODE_HOME:
                sharedXmlUtil.write("current_mode", MODE_HOME);
                changeView();
                break;
            case MODE_BACK:
                sharedXmlUtil.write("current_mode", MODE_BACK);
                changeView();
                break;
            default:
                sharedXmlUtil.write("current_mode", MODE_NONE);
                changeView();
                result = -1;
                break;
        }
        return result;
    }


    public int getScanMode() {
        return sharedXmlUtil.read("current_mode", MODE_SCAN);
    }
}
