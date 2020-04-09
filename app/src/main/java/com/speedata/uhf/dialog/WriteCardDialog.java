package com.speedata.uhf.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.speedata.libuhf.IUHFService;
import com.speedata.libuhf.bean.SpdWriteData;
import com.speedata.libuhf.interfaces.OnSpdWriteListener;
import com.speedata.libuhf.utils.ErrorStatus;
import com.speedata.libuhf.utils.StringUtils;
import com.speedata.uhf.R;


import java.util.Objects;

/**
 * 写卡
 *
 * @author 张智超_
 * @date 2019/3/13
 */
public class WriteCardDialog extends Dialog implements
        View.OnClickListener {

    private Button ok;
    private ImageView cancel;
    private TextView status;
    private EditText writeAddr;
    private EditText writeCount;
    private EditText writePasswd;
    private IUHFService iuhfService;
    private Context mContext;
    private int whichChoose;
    private String currentTagEpc;
    private String model;
    private EditText writeContent;
    private boolean isSuccess = false;
    private CheckBox cbLoop;
    private EditText etLoopTime;
    private int loopTime = 500;

    public WriteCardDialog(Context context, IUHFService iuhfService,
                           int whichChoose, String currentTagEpc, String model) {
        super(context);
        this.iuhfService = iuhfService;
        this.mContext = context;
        this.whichChoose = whichChoose;
        this.currentTagEpc = currentTagEpc;
        this.model = model;
        // TODO Auto-generated constructor stub
    }

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置背景为透明
        Objects.requireNonNull(getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(R.layout.dialog_write);
        initView();
        iuhfService.setOnWriteListener(new OnSpdWriteListener() {
            @Override
            public void getWriteData(SpdWriteData var1) {
                StringBuilder stringBuilder = new StringBuilder();
                byte[] epcData = var1.getEPCData();
                String hexString = StringUtils.byteToHexString(epcData, var1.getEPCLen());
                if (!TextUtils.isEmpty(hexString)) {
                    stringBuilder.append("EPC：").append(hexString).append("\n");
                }
                if (var1.getStatus() == 0) {
                    //状态判断，已经写卡成功了就不返回错误码了
                    isSuccess = true;
                    stringBuilder.append(mContext.getResources().getString(R.string.write_success)).append("\n");
                    handler.sendMessage(handler.obtainMessage(1, stringBuilder));
                } else {
                    stringBuilder.append(mContext.getResources().getString(R.string.write_fail)).append(":").append(ErrorStatus.getErrorStatus(mContext,var1.getStatus())).append("\n");
                }
                if (!isSuccess) {
                    handler.sendMessage(handler.obtainMessage(1, stringBuilder));
                }

            }
        });
        writeCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String count = s.toString();
                if ("0".equals(count)) {
                    writeCount.setText("");
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.count_not_zero), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == ok) {
            final String strAddr = writeAddr.getText().toString();
            final String strCount = writeCount.getText().toString();
            final String strPasswd = writePasswd.getText().toString();
            final String strContent = writeContent.getText().toString().replace(" ", "");
            String time = etLoopTime.getText().toString();
            if (TextUtils.isEmpty(strAddr) || TextUtils.isEmpty(strCount) || TextUtils.isEmpty(strPasswd)
                    || TextUtils.isEmpty(strContent) || (cbLoop.isChecked() && TextUtils.isEmpty(time))) {
                Toast.makeText(mContext, R.string.toast1, Toast.LENGTH_SHORT).show();
                return;
            }
            if (strPasswd.length() != 8) {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.toast5), Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                loopTime = Integer.parseInt(time);
            }catch (NumberFormatException e) {
                handler.sendMessage(handler.obtainMessage(1, mContext.getResources().getString(R.string.toast_loop_sleep)));
                return;
            }
            try {
                final byte[] write = StringUtils.stringToByte(strContent);
                final int addr = Integer.parseInt(strAddr);
                final int count = Integer.parseInt(strCount);
                if (count * 4 != strContent.length()) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.toast6), Toast.LENGTH_SHORT).show();
                    return;
                }
                ok.setEnabled(false);
                WriteThread writeThread = new WriteThread(addr, count, strPasswd, write);
                writeThread.start();
            } catch (NumberFormatException e) {
                handler.sendMessage(handler.obtainMessage(1, mContext.getResources().getString(R.string.toast3)));
            }
        } else if (v == cancel) {
            cbLoop.setChecked(false);
            dismiss();
        }
    }


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                status.setText(msg.obj + "");
            }
        }
    };

    private void initView() {
        ok = (Button) findViewById(R.id.btn_write_ok);
        ok.setOnClickListener(this);
        cancel = (ImageView) findViewById(R.id.iv_write_cancle);
        cancel.setOnClickListener(this);

        status = (TextView) findViewById(R.id.textView_write_status);

        writeAddr = (EditText) findViewById(R.id.editText_write_addr);
        writeCount = (EditText) findViewById(R.id.editText_write_count);
        writePasswd = (EditText) findViewById(R.id.editText_write_passwd);
        writeContent = (EditText) findViewById(R.id.et_content);

        etLoopTime = findViewById(R.id.et_loop_time);
        cbLoop = findViewById(R.id.cb_loop_send);
    }

    private class WriteThread extends Thread {

        private int addr, count;
        private String strPasswd;
        private byte[] write;
        int i = 0;

        WriteThread(int addr, int count, String strPasswd, byte[] write) {
            this.addr = addr;
            this.count = count;
            this.strPasswd = strPasswd;
            this.write = write;
        }

        @Override
        public void run() {
            do {
                Handler mHand = new Handler(Looper.getMainLooper());
                mHand.post(new Runnable() {
                    @Override
                    public void run() {
                        status.setText(R.string.write_status);
                    }
                });
                isSuccess = false;
                int writeArea = iuhfService.writeArea(whichChoose, addr, count, strPasswd, write);
                if (writeArea != 0) {
//                    handler.sendMessage(handler.obtainMessage(1, mContext.getResources().getString(R.string.toast2)));
                    String err = mContext.getResources().getString(R.string.write_fail) + ":" + ErrorStatus.getErrorStatus(mContext, writeArea) + "\n";
                    handler.sendMessage(handler.obtainMessage(1, err));
                }
                Log.d("zzc:", "==write==" + i++);
                try {
                    Thread.sleep(loopTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (cbLoop.isChecked());
            Handler mHandler = new Handler(Looper.getMainLooper());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    ok.setEnabled(true);
                    Log.d("zzc:", "==write  run  end==" + i++);
                }
            });
        }
    }

    @Override
    protected void onStop() {
        cbLoop.setChecked(false);
        super.onStop();
    }
}