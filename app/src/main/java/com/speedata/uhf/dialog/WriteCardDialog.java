package com.speedata.uhf.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.speedata.libuhf.IUHFService;
import com.speedata.libuhf.bean.SpdWriteData;
import com.speedata.libuhf.interfaces.OnSpdWriteListener;
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

        ok = (Button) findViewById(R.id.btn_write_ok);
        ok.setOnClickListener(this);
        cancel = (ImageView) findViewById(R.id.iv_write_cancle);
        cancel.setOnClickListener(this);

        status = (TextView) findViewById(R.id.textView_write_status);

        writeAddr = (EditText) findViewById(R.id.editText_write_addr);
        writeCount = (EditText) findViewById(R.id.editText_write_count);
        writePasswd = (EditText) findViewById(R.id.editText_write_passwd);
        writeContent = (EditText) findViewById(R.id.et_content);

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
                    stringBuilder.append("WriteSuccess" + "\n");
                    handler.sendMessage(handler.obtainMessage(1, stringBuilder));
                } else {
                    stringBuilder.append("WriteError：").append(var1.getStatus()).append("\n");
                }
                if (!isSuccess) {
                    handler.sendMessage(handler.obtainMessage(1, stringBuilder));
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
            final String strContent = writeContent.getText().toString();
            if (TextUtils.isEmpty(strAddr) || TextUtils.isEmpty(strCount) || TextUtils.isEmpty(strPasswd)
                    || TextUtils.isEmpty(strContent)) {
                Toast.makeText(mContext, R.string.toast1, Toast.LENGTH_SHORT).show();
                return;
            }
            final byte[] write = StringUtils.stringToByte(strContent);
            final int addr = Integer.parseInt(strAddr);
            final int count = Integer.parseInt(strCount);
            status.setText(R.string.write_status);
            isSuccess = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int writeArea = iuhfService.writeArea(whichChoose, addr, count, strPasswd, write);
                    if (writeArea != 0) {
                        handler.sendMessage(handler.obtainMessage(1,R.string.toast2));
                    }
                }
            }).start();

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
            if (msg.what == 1) {
                status.setText(msg.obj + "");
            }
        }
    };
}