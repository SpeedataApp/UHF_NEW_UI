package com.speedata.uhf;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.speedata.libuhf.IUHFService;
import com.speedata.libuhf.bean.SpdWriteData;
import com.speedata.libuhf.interfaces.OnSpdWriteListener;
import com.speedata.libuhf.utils.StringUtils;
import com.speedata.libuhf.utils.ErrorStatus;

/**
 * 卡片属性设置弹框
 * Created by 张智超 on 2019/3/6
 *
 * @author My_PC
 */
public class PopAttrSetActivity extends BaseActivity {

    /**
     * password
     */
    private EditText pwdinit, pwdnew;
    private RadioButton rbtnPwdKill, rbtnPwdAcc;
    /**
     * epc
     */
    private EditText passwd;
    private EditText newepc;
    private EditText newepclength;
    /**
     * lock
     */
    private RadioGroup rgSpace1, rgSpace2;
    private RadioGroup rgType1, rgType2;
    private RadioButton rbSpaceKill, rbSpaceAcc, rbSpaceEpc, rbSpaceTid, rbSpaceUser;
    private RadioButton typeUnlock, typeLock, typePermaUnlock, typePermaLock;
    private EditText newLockPwd;

    private IUHFService iuhfService;

    private boolean isSetPassword = false;
    private boolean isSetEpc = false;
    private boolean isSetLock = false;

    private boolean isSuccess = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_attr_set);
        // 让此界面的宽度以及高度撑满整个屏幕
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        initView();
        initData();
    }

    public void initView() {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relative_layout);
        relativeLayout.setOnClickListener(new Click());

        pwdinit = (EditText) findViewById(R.id.et_pwdset_init);
        pwdnew = (EditText) findViewById(R.id.et_pwdset_new);
        rbtnPwdAcc = (RadioButton) findViewById(R.id.rbtn_pwd_acc);
        rbtnPwdKill = (RadioButton) findViewById(R.id.rbtn_pwd_kill);

        passwd = (EditText) findViewById(R.id.editText_epc_passwd);
        newepc = (EditText) findViewById(R.id.editText_epc_newepc);
        newepclength = (EditText) findViewById(R.id.editText_epc_epclength);

        rgSpace1 = (RadioGroup) findViewById(R.id.space_rg1);
        rgSpace2 = (RadioGroup) findViewById(R.id.space_rg2);
        rbSpaceKill = (RadioButton) findViewById(R.id.rbtn_kill);
        rbSpaceKill.setOnCheckedChangeListener(new ChangeChecked());
        rbSpaceAcc = (RadioButton) findViewById(R.id.rbtn_acc);
        rbSpaceAcc.setOnCheckedChangeListener(new ChangeChecked());
        rbSpaceEpc = (RadioButton) findViewById(R.id.rbtn_epc);
        rbSpaceEpc.setOnCheckedChangeListener(new ChangeChecked());
        rbSpaceTid = (RadioButton) findViewById(R.id.rbtn_tid);
        rbSpaceTid.setOnCheckedChangeListener(new ChangeChecked());
        rbSpaceUser = (RadioButton) findViewById(R.id.rbtn_user);
        rbSpaceUser.setOnCheckedChangeListener(new ChangeChecked());
        rgType1 = (RadioGroup) findViewById(R.id.type_rg1);
        rgType2 = (RadioGroup) findViewById(R.id.type_rg2);
        typeUnlock = (RadioButton) findViewById(R.id.type_unlock);
        typeUnlock.setOnCheckedChangeListener(new ChangeChecked());
        typeLock = (RadioButton) findViewById(R.id.type_lock);
        typeLock.setOnCheckedChangeListener(new ChangeChecked());
        typePermaUnlock = (RadioButton) findViewById(R.id.type_perma_unlock);
        typePermaUnlock.setOnCheckedChangeListener(new ChangeChecked());
        typePermaLock = (RadioButton) findViewById(R.id.type_perma_lock);
        typePermaLock.setOnCheckedChangeListener(new ChangeChecked());
        newLockPwd = (EditText) findViewById(R.id.et_lock_pwd);

        Button btnBack = (Button) findViewById(R.id.btn_back);
        Button reset = (Button) findViewById(R.id.btn_reset);
        btnBack.setOnClickListener(new Click());
        reset.setOnClickListener(new Click());
        Button btnPasswordSet = findViewById(R.id.btn_pwd_ok);
        Button btnEpcSet = findViewById(R.id.btn_epc_ok);
        Button btnLockSet = findViewById(R.id.btn_lock_ok);
        btnPasswordSet.setOnClickListener(new Click());
        btnEpcSet.setOnClickListener(new Click());
        btnLockSet.setOnClickListener(new Click());

    }

    public void initData() {
        iuhfService = MyApp.getInstance().getIuhfService();

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
                    stringBuilder.append(getResources().getString(R.string.set_success)).append("\n");
                    handler.sendMessage(handler.obtainMessage(1, stringBuilder));
                } else {
                    stringBuilder.append(getResources().getString(R.string.set_failed)).append(ErrorStatus.getErrorStatus(PopAttrSetActivity.this, var1.getStatus())).append("\n");
                }
                if (!isSuccess) {
                    handler.sendMessage(handler.obtainMessage(1, stringBuilder));
                }
            }
        });
    }

    public class Click implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_reset:
                    //重置
                    pwdinit.setText(getResources().getString(R.string.VALUE_ZERO));
                    rbtnPwdKill.setChecked(true);
                    pwdnew.setText("");
                    passwd.setText(getResources().getString(R.string.VALUE_ZERO));
                    newepc.setText("");
                    newepclength.setText("");
                    rbSpaceKill.setChecked(true);
                    typeUnlock.setChecked(true);
                    newLockPwd.setText(getResources().getString(R.string.VALUE_ZERO));
                    break;
                case R.id.relative_layout:
                    //添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数
                    break;
                case R.id.btn_back:
                    finish();
                    break;
                case R.id.btn_pwd_ok:
                    setPassword();
                    break;
                case R.id.btn_epc_ok:
                    setEpc();
                    break;
                case R.id.btn_lock_ok:
                    setLock();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 检测修改哪项
     */
    private void setCheck() {
        String pwdnewStr = pwdnew.getText().toString();
        isSetPassword = !TextUtils.isEmpty(pwdnewStr);

        String newepcStr = newepc.getText().toString();
        String newepclengthStr = newepclength.getText().toString();
        isSetEpc = !TextUtils.isEmpty(newepcStr) || !TextUtils.isEmpty(newepclengthStr);

        String nweLockPwdStr = newLockPwd.getText().toString();
        isSetLock = !TextUtils.isEmpty(nweLockPwdStr);
    }

    /**
     * 设置密码
     */
    private void setPassword() {
        final String curpass = pwdinit.getText().toString();
        final String newpass = pwdnew.getText().toString().replace(" ", "");
        if (TextUtils.isEmpty(curpass) || TextUtils.isEmpty(newpass)) {
            Toast.makeText(this, getResources().getString(R.string.toast1), Toast.LENGTH_SHORT).show();
            return;
        }
        int whichint = 0;
        if (rbtnPwdAcc.isChecked()) {
            whichint = 1;
        }
        final int which = whichint;

        isSuccess = false;
        new Thread(() -> {
            int setPassword = iuhfService.setPassword(which, curpass, newpass);
            if (setPassword != 0) {
                String err = getResources().getString(R.string.set_failed) + ":" + ErrorStatus.getErrorStatus(PopAttrSetActivity.this, setPassword) + "\n";
                handler.sendMessage(handler.obtainMessage(2, err));
//                    handler.sendMessage(handler.obtainMessage(2, getResources().getString(R.string.toast2)));
            }
        }).start();
    }

    /**
     * 设置EPC
     */
    private void setEpc() {
        final String password = passwd.getText().toString().replace(" ", "");
        final String epcstr = newepc.getText().toString().replace(" ", "");
        String countstr = newepclength.getText().toString();
        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(epcstr) || TextUtils.isEmpty(countstr)) {
            Toast.makeText(PopAttrSetActivity.this, getResources().getString(R.string.toast1), Toast.LENGTH_SHORT).show();
            return;
        }
        final byte[] write = StringUtils.stringToByte(epcstr);
        final int epcLen;
        try {
            epcLen = Integer.parseInt(countstr, 10);
        } catch (NumberFormatException e) {
            Toast.makeText(PopAttrSetActivity.this, getResources().getString(R.string.toast4), Toast.LENGTH_SHORT).show();
            return;
        }

        isSuccess = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                int writeArea = iuhfService.setNewEpc(password, epcLen, write);
                if (writeArea != 0) {
                    String err = getResources().getString(R.string.set_failed) + ":" + ErrorStatus.getErrorStatus(PopAttrSetActivity.this, writeArea) + "\n";
                    handler.sendMessage(handler.obtainMessage(3, err));
//                    handler.sendMessage(handler.obtainMessage(3, getResources().getString(R.string.toast2)));
                }
            }
        }).start();
    }

    /**
     * 设置锁
     */
    private void setLock() {
        int space = 0;
        int type = 0;
        if (rbSpaceAcc.isChecked()) {
            space = 1;
        } else if (rbSpaceEpc.isChecked()) {
            space = 2;
        } else if (rbSpaceTid.isChecked()) {
            space = 3;
        } else if (rbSpaceUser.isChecked()) {
            space = 4;
        }
        final int lockSpace = space;
        if (typeLock.isChecked()) {
            type = 1;
        } else if (typePermaUnlock.isChecked()) {
            type = 2;
        } else if (typePermaLock.isChecked()) {
            type = 3;
        }
        final int lockType = type;
        final String lockNewPwd = newLockPwd.getText().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int reval = iuhfService.setLock(lockType, lockSpace, lockNewPwd);
                if (reval != 0) {
                    String err = getResources().getString(R.string.set_failed) + ":" + ErrorStatus.getErrorStatus(PopAttrSetActivity.this, reval) + "\n";
                    handler.sendMessage(handler.obtainMessage(4, err));
//                    handler.sendMessage(handler.obtainMessage(4, getResources().getString(R.string.toast2)));
                }
            }
        }).start();

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Toast.makeText(PopAttrSetActivity.this, "" + msg.obj, Toast.LENGTH_SHORT).show();
            } else if (msg.what == 2) {
                Toast.makeText(PopAttrSetActivity.this, getResources().getString(R.string.set_password) + msg.obj, Toast.LENGTH_SHORT).show();
            } else if (msg.what == 3) {
                Toast.makeText(PopAttrSetActivity.this, getResources().getString(R.string.set_epc) + msg.obj, Toast.LENGTH_SHORT).show();
            } else if (msg.what == 4) {
                Toast.makeText(PopAttrSetActivity.this, getResources().getString(R.string.set_lock) + msg.obj, Toast.LENGTH_SHORT).show();
            }
        }
    };

    public class ChangeChecked implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.rbtn_kill:
                case R.id.rbtn_acc:
                    if (isChecked) {
                        //通过设置组check，实现两组不同行的单选按钮互斥
                        rgSpace2.check(-1);
                    }
                    break;
                case R.id.rbtn_epc:
                case R.id.rbtn_tid:
                case R.id.rbtn_user:
                    if (isChecked) {
                        rgSpace1.check(-1);
                    }
                    break;
                case R.id.type_unlock:
                case R.id.type_lock:
                    if (isChecked) {
                        rgType2.check(-1);
                    }
                    break;
                case R.id.type_perma_unlock:
                case R.id.type_perma_lock:
                    if (isChecked) {
                        rgType1.check(-1);
                    }
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
     *
     * @param event 点击事件
     * @return 返回值
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

}
