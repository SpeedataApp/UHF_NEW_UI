package com.speedata.uhf.libutils;

/**
 * @author zzc
 * @date 2019/06/17
 */
public class ErrorStatus {
    public static String STATUS = "";

    ErrorStatus() {

    }

    /**
     *
     * @param errorCode 错误码
     * @return 返回状态解释
     */
    public static String getErrorStatus(int errorCode) {
        switch (errorCode) {
            case -20000:
                STATUS = "内存分配失败";
                break;
            case -19999:
                STATUS = "没有搜索到设备";
                break;
            case -19998:
                STATUS = "获取设备信息失败";
                break;
            case -19997:
                STATUS = "设备序列号越界";
                break;
            case -19996:
                STATUS = "关闭已经关闭的设备";
                break;
            case -19995:
                STATUS = "读取标签的标志是2";
                break;
            case -19994:
                STATUS = "读取标签mac错误";
                break;
            case -19993:
            case 4:
                STATUS = "未发现标签";
                break;
            case -19992:
                STATUS = "写数据不匹配";
                break;
            case -19991:
                STATUS = "读标签失败";
                break;
            case -19990:
            case -1:
                STATUS = "操作失败";
                break;
            case -19989:
                STATUS = "读密码错误";
                break;
            case -19988:
                STATUS = "销毁密码错误";
                break;
            case -19987:
                STATUS = "销毁标签错误";
                break;
            case -19986:
                STATUS = "设置权限错误";
                break;
            case -19972:
                STATUS = "MAC固件接受到错误包";
                break;
            case -9999:
                STATUS = "射频读写器已经被打开，并在操作中";
                break;
            case -9998:
                STATUS = "提供的内存空间过小";
                break;
            case -9997:
                STATUS = "一般错误";
                break;
            case -9996:
                STATUS = "总线驱动加载错误";
                break;
            case -9995:
                STATUS = "总线驱动版本不支持";
                break;
            case -9994:
                STATUS = "保留";
                break;
            case -9993:
                STATUS = "天线端口错误";
                break;
            case -9992:
                STATUS = "射频读写器初始化失败";
                break;
            case -9991:
                STATUS = "参数不可用";
                break;
            case -9990:
                STATUS = "没有可用的射频读写器";
                break;
            case -9989:
                STATUS = "未初始化";
                break;
            case -9987:
                STATUS = "操作取消";
                break;
            case -9986:
                STATUS = "内存分配失败";
                break;
            case -9985:
                STATUS = "设备忙，设备正在执行前一操作";
                break;
            case -9984:
                STATUS = "射频读写器出现错误";
                break;
            case -9983:
                STATUS = "射频读写器不存在";
                break;
            case -9982:
                STATUS = "类库函数函数不可用";
                break;
            case -9981:
                STATUS = "射频读写器 MAC 固件无应答";
                break;
            case -9980:
                STATUS = "不可以对非易失性的 MAC 寄存器更新";
                break;
            case -9979:
                STATUS = "不可以在非易失性的 MAC 寄存器写入数据";
                break;
            case -9978:
                STATUS = "MAC 组件阻止了对非易失性寄存器的写入";
                break;
            case -9977:
                STATUS = "接收数据溢出错误";
                break;
            case -9976:
                STATUS = "MAC 组件返回错误的值";
                break;
            case 3:
                STATUS = "指令失败";
                break;
            default:
                STATUS = errorCode + "";
                break;
        }
        return STATUS;
    }
}
