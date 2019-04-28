package com.speedata.uhf.adapter;

/**
 * 寻卡列表管理类
 * @author 张智超
 * @date 2019/3/7
 */
public class UhfCardBean {
    private String tvepc;
    private String tvvRssi;
    private String tvTidUser;
    private String epc;
    private int valid;
    private String rssi;
    private String tidUser;

    public UhfCardBean(String epc, int valid, String rssi, String tidUser) {
        this.epc = epc;
        this.valid = valid;
        this.rssi = rssi;
        this.tidUser = tidUser;
    }

    /**
     * 匹配item的TextView
     * @return tvepc
     */
    String getTvepc() {
        tvepc = epc;
        return tvepc;
    }

    /**
     * 匹配item的TextView
     * @return tvvRssi
     */
    String getTvvRssi() {
        tvvRssi = "COUNT:"+valid+"  RSSI:"+rssi;
        return tvvRssi;
    }

    /**
     * 匹配item的TextView
     * @return tvTidUser
     */
    public String getTvTidUser() {
        tvTidUser ="T/U:"+tidUser;
        return tvTidUser;
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

    public void setTidUser(String tidUser) {
        this.tidUser = tidUser;
    }

    public String getTidUser() {
        return tidUser;
    }

}
