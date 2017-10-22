package com.zyl.myble;

/**
 * Created by zhuyuliang on 2017/10/18.
 */

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 蓝牙设备实体类 BLE和普通蓝牙
 */
public class BDevice implements Parcelable {

    public BDevice(String device_name, String device_id, BluetoothDevice device, BluetoochType bluetoochType) {
        this.device_name = device_name;
        this.device_id = device_id;
        this.device = device;
        this.bluetoochType = bluetoochType;
    }

    protected BDevice(Parcel in) {
        device_name = in.readString();
        device_id = in.readString();
        device = in.readParcelable(BluetoothDevice.class.getClassLoader());
    }

    public static final Creator<BDevice> CREATOR = new Creator<BDevice>() {
        @Override
        public BDevice createFromParcel(Parcel in) {
            return new BDevice(in);
        }

        @Override
        public BDevice[] newArray(int size) {
            return new BDevice[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(device_name);
        parcel.writeString(device_id);
        parcel.writeParcelable(device, i);
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
