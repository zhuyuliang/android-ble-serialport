package com.zyl.myble;

/**
 * Created by zhuyuliang on 2017/10/18.
 */

/**
 * 蓝牙设备实体类
 */
public class BLEDevice {
    public BLEDevice(String device_name, String device_id) {
        this.device_name = device_name;
        this.device_id = device_id;
    }

    //设备名称
    String device_name;
    //设备地址
    String device_id;

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }
}
