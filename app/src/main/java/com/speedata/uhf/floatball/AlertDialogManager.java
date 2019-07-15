package com.speedata.uhf.floatball;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.speedata.libuhf.UHFManager;
import com.speedata.libuhf.interfaces.OnSpdBanMsgListener;
import com.speedata.uhf.NewMainActivity;
import com.speedata.uhf.R;

/**
 * @author zzc
 */
public class AlertDialogManager {

    private static Context mContext;
    private static AlertDialogManager alertDialogManager;
    private static AlertDialog alertDialog;

    private AlertDialogManager() {

    }

    private AlertDialogManager(Context mContext) {
        AlertDialogManager.mContext = mContext;
    }

    public static AlertDialogManager getAlertDialogManager() {
        return alertDialogManager;
    }

    public static AlertDialogManager getAlertInstance(Context mContext) {
        if (getAlertDialogManager() == null) {
            alertDialogManager = new AlertDialogManager(mContext);
        }
        return alertDialogManager;
    }

    public static void setBuilder(String var1) {
        if (var1.contains("Low")) {
            var1 = mContext.getResources().getString(R.string.low_power);
        } else if (var1.contains("High")) {
            var1 = mContext.getResources().getString(R.string.high_temp);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(var1);
        builder.setMessage(R.string.dialog_exit);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                releaseAlertDialogManager();
                dialog.dismiss();
                System.exit(0);
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    public static void releaseAlertDialogManager() {
        alertDialogManager = null;
    }
}
