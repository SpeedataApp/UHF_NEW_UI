package com.speedata.uhf.adapter;

import android.widget.TextView;

/**
 * 寻卡列表管理类
 * Created by 张智超 on 2019/3/7
 */
public class UhfCardBean {
    private String tvepc;
    private String tvvRssi;
    private String epc;
    private int valid;
    private String rssi;
    private String tid_user;

    public UhfCardBean(String epc, int valid, String rssi, String tid_user) {
        this.epc = epc;
        this.valid = valid;
        this.rssi = rssi;
        this.tid_user = tid_user;
    }
    // 匹配item的TextView
    public String getTvepc() {
        tvepc = epc;
        return tvepc;
    }
    // 匹配item的TextView
    public String getTvvRssi() {
        tvvRssi = "COUNT:"+valid+"  RSSI:"+rssi;
        return tvvRssi;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public String getEpc() {
        return epc;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public String getRssi() {
        return rssi;
    }

    public void setValid(int valid) {
        this.valid = valid;
    }

    public int getValid() {
        return valid;
    }

    public void setTid_user(String tid_user) {
        this.tid_user = tid_user;
    }

    public String getTid_user() {
        return tid_user;
    }

}
