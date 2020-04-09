package com.speedata.uhf.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.speedata.libuhf.IUHFService;
import com.speedata.libuhf.bean.SpdReadData;
import com.speedata.libuhf.interfaces.OnSpdReadListener;
import com.speedata.libuhf.utils.StringUtils;
import com.speedata.uhf.R;
import com.speedata.libuhf.utils.ErrorStatus;

import java.util.Objects;

/**
 * 读卡
 *
 * @author 张智超_
 * @date 2019/3/13
 */

public class ReadCardDialog extends Dialog implements
        View.OnClickListener {

    private Button ok;
    private ImageView cancel;
    private TextView status;
    private EditText readAddr;
    private EditText readCount;
    private EditText password;
    private IUHFService iuhfService;
    private String currentTagEpc;
    private int whichChoose;
    private String model;
    private Context mContext;

    public ReadCardDialog(Context context, IUHFService iuhfService
            , int whichChoose, String currentTagEpc, String model) {
        super(context);
        // TODO Auto-generated constructor stub
        this.iuhfService = iuhfService;
        this.currentTagEpc = currentTagEpc;
        this.whichChoose = whichChoose;
        this.model = model;
        this.mContext = context;
    }

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置背景为透明
        Objects.requireNonNull(getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(R.layout.dialog_read);

        ok = (Button) findViewById(R.id.btn_read_ok);
        ok.setOnClickListener(this);
        cancel = (ImageView) findViewById(R.id.iv_read_cancle);
        cancel.setOnClickListener(this);

        status = (TextView) findViewById(R.id.textView_read_status);
        readAddr = (EditText) findViewById(R.id.editText_read_addr);
        readCount = (EditText) findViewById(R.id.editText_read_count);
        password = (EditText) findViewById(R.id.editText_rp);

        iuhfService.setOnReadListener(new OnSpdReadListener() {
            @Override
            public void getReadData(SpdReadData var1) {
                StringBuilder stringBuilder = new StringBuilder();
                byte[] epcData = var1.getEPCData();
                String hexString = StringUtils.byteToHexString(epcData, var1.getEPCLen());
                if (!TextUtils.isEmpty(hexString)) {
                    stringBuilder.append("EPC：").append(hexString).append("\n");
                }
                if (var1.getStatus() == 0) {
                    byte[] readData = var1.getReadData();
                    String readHexString = StringUtils.byteToHexString(readData, var1.getDataLen());
                    stringBuilder.append("ReadData:").append(readHexString).append("\n");
                } else {
                    stringBuilder.append(mContext.getResources().getString(R.string.read_fail)).append(":").append(ErrorStatus.getErrorStatus(mContext, var1.getStatus())).append("\n");
                }
                handler.sendMessage(handler.obtainMessage(1, stringBuilder));
            }
        });
        readCount.addTextChangedListener(new TextWatcher() {
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
                    readCount.setText("");
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.count_not_zero), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == ok) {
            final String strAddr = readAddr.getText().toString();
            final String strCount = readCount.getText().toString();
            final String strPasswd = password.getText().toString();
            if (TextUtils.isEmpty(strAddr) || TextUtils.isEmpty(strCount) || TextUtils.isEmpty(strPasswd)) {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.toast1), Toast.LENGTH_SHORT).show();
                return;
            }
            if (strPasswd.length() != 8) {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.toast5), Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                final int addr = Integer.parseInt(strAddr);
                final int count = Integer.parseInt(strCount);
                status.setText(R.string.read_status);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int readArea = iuhfService.readArea(whichChoose, addr, count, strPasswd);
                        if (readArea != 0) {
                            String err = mContext.getResources().getString(R.string.read_fail) + ":" + ErrorStatus.getErrorStatus(mContext, readArea) + "\n";
                            handler.sendMessage(handler.obtainMessage(1, err));
                        }
                    }
                }).start();
            } catch (NumberFormatException e) {
                handler.sendMessage(handler.obtainMessage(1, mContext.getResources().getString(R.string.toast3)));
            }

        } else if (v == cancel) {
            dismiss();
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    status.setText(msg.obj + "");
                    break;
                default:
                    break;
            }
        }
    };
}
