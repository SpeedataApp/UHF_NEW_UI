package com.speedata.uhf.adapter;

public class UhfInfoBean {
    private String epc;
    private String tid;
    private String rssi;

    public UhfInfoBean(String epc, String tid, String rssi) {
        this.epc = epc;
        this.tid = tid;
        this.rssi = rssi;
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    @Override
    public String toString() {
        return "UhfInfoBean{" +
                "epc='" + epc + '\'' +
                ", tid='" + tid + '\'' +
                ", rssi='" + rssi + '\'' +
                '}';
    }
}
