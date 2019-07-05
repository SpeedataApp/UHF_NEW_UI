package com.speedata.uhf.libutils;

import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 *
 * @author 张明_
 * @date 2018/10/12
 * Email 741183142@qq.com
 */
public class ToastUtil {
    private static boolean isShow = true;//默认显示
    private static Toast mToast = null;//全局唯一的Toast
    /**
     * 写两个Toast对象是为了解决toast执行顺序有可能会抛出异常
     */
    private static Toast mTextToast;
    private static Toast mViewToast;

    /*private控制不应该被实例化*/
    private ToastUtil() {
        throw new UnsupportedOperationException("不能被实例化");
    }

    /**
     * 全局控制是否显示Toast
     *
     * @param isShowToast
     */
    public static void controlShow(boolean isShowToast) {
        isShow = isShowToast;
    }

    /**
     * 取消Toast显示
     */
    public void cancelToast() {
        if (isShow && mToast != null) {
            mToast.cancel();
            mTextToast.cancel();
            mViewToast.cancel();
        }
    }

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, CharSequence message) {
        if (isShow) {
            if (mTextToast == null) {
                mTextToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            } else {
                mTextToast.setText(message);
            }
            mTextToast.show();
        }
    }

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param resId   资源ID:getResources().getString(R.string.xxxxxx);
     */
    public static void showShort(Context context, int resId) {
        if (isShow) {
            if (mTextToast == null) {
                mTextToast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
            } else {
                mTextToast.setText(resId);
            }
            mTextToast.show();
        }
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, CharSequence message) {
        if (isShow) {
            if (mToast == null) {
                mTextToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            } else {
                mTextToast.setText(message);
            }
            mTextToast.show();
        }
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param resId   资源ID:getResources().getString(R.string.xxxxxx);
     */
    public static void showLong(Context context, int resId) {
        if (isShow) {
            if (mTextToast == null) {
                mTextToast = Toast.makeText(context, resId, Toast.LENGTH_LONG);
            } else {
                mTextToast.setText(resId);
            }
            mTextToast.show();
        }
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration 单位:毫秒
     */
    public static void show(Context context, CharSequence message, int duration) {
        if (isShow) {
            if (mTextToast == null) {
                mTextToast = Toast.makeText(context, message, duration);
            } else {
                mTextToast.setText(message);
            }
            mTextToast.show();
        }
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param resId    资源ID:getResources().getString(R.string.xxxxxx);
     * @param duration 单位:毫秒
     */
    public static void show(Context context, int resId, int duration) {
        if (isShow) {
            if (mTextToast == null) {
                mTextToast = Toast.makeText(context, resId, duration);
            } else {
                mTextToast.setText(resId);
            }
            mTextToast.show();
        }
    }

    /**
     * 自定义Toast的View
     *
     * @param context
     * @param message
     * @param duration 单位:毫秒
     * @param view     显示自己的View
     */
    public static void customToastView(Context context, int message, int duration, TextView view) {
        if (isShow) {
            if (mViewToast == null) {
                mViewToast = Toast.makeText(context, message, duration);
            }
            if (view != null) {
                view.setHeight(280);
                view.setWidth(460);
                view.setText(message);
                mViewToast.setView(view);
            }
            mViewToast.setGravity(Gravity.CENTER, 0, 0);
            mViewToast.show();
        }
    }

    /**
     * 自定义Toast的View
     *
     * @param context
     * @param message
     * @param duration 单位:毫秒
     * @param view     显示自己的View
     */
    public static void customToastView(Context context, String message, int duration, TextView view) {
        if (isShow) {
            if (mViewToast == null) {
                mViewToast = Toast.makeText(context, message, duration);
            }
            if (view != null) {
                view.setHeight(280);
                view.setWidth(460);
                view.setText(message);
                mViewToast.setView(view);
            }
            mViewToast.setGravity(Gravity.CENTER, 0, 0);
            mViewToast.show();
        }
    }

    /**
     * 自定义Toast的位置
     *
     * @param context
     * @param message
     * @param duration 单位:毫秒
     * @param gravity
     * @param xOffset
     * @param yOffset
     */
    public static void customToastGravity(Context context, CharSequence message, int duration, int gravity, int xOffset, int yOffset) {
        if (isShow) {
            if (mTextToast == null) {
                mTextToast = Toast.makeText(context, message, duration);
            } else {
                mTextToast.setText(message);
            }
            mTextToast.setGravity(gravity, xOffset, yOffset);
            mTextToast.show();
        }
    }

    /**
     * 自定义带图片和文字的Toast，最终的效果就是上面是图片，下面是文字
     *
     * @param context
     * @param message
     * @param iconResId 图片的资源id,如:R.drawable.icon
     * @param duration
     * @param gravity
     * @param xOffset
     * @param yOffset
     */
    public static void showToastWithImageAndText(Context context, CharSequence message, int iconResId, int duration, int gravity, int xOffset, int yOffset) {
        if (isShow) {
            if (mTextToast == null) {
                mTextToast = Toast.makeText(context, message, duration);
            } else {
                mTextToast.setText(message);
            }
            mTextToast.setGravity(gravity, xOffset, yOffset);
            LinearLayout toastView = (LinearLayout) mTextToast.getView();
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(iconResId);
            toastView.addView(imageView, 0);
            mTextToast.show();
        }
    }
}
