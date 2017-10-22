package com.zyl.myble;

/**
 * Created by zhuyuliang on 2017/10/18.
 */

import android.bluetooth.BluetoothDevice;
import android.os.ParcelUuid;

/**
 * 蓝牙设备实体类 BLE和普通蓝牙
 */
public class BDevice {

    public BDevice(String device_name, String device_id, BluetoothDevice device, BluetoochType bluetoochType) {
        this.device_name = device_name;
        this.device_id = device_id;
        this.device = device;
        this.bluetoochType = bluetoochType;
    }

    enum BluetoochType {
        BLEBluetooch,CommonBluetooch;
    }

    //设备名称
    String device_name;
    //设备地址
    String device_id;
    //BLE
    BluetoothDevice device;
    //蓝牙类型
    BluetoochType bluetoochType;

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

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public BluetoochType getBluetoochType() {
        return bluetoochType;
    }

    public void setBluetoochType(BluetoochType bluetoochType) {
        this.bluetoochType = bluetoochType;
    }
}
