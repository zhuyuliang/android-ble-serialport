package com.zyl.myble;

import android.content.Intent;

/**
 * @author zhuyuliang
 * @message 蓝牙串口通信逻辑处理
 */

public class SerialPortMessagePresenter{

    public final static String TAG = "SerialPortMessageActivity";

    private SerialPortMessageActivity serialPortMessageActivity;

    private BDevice bDevice;

    /**
     * 初始化
     * @param serialPortMessageActivity
     */
    public SerialPortMessagePresenter(SerialPortMessageActivity serialPortMessageActivity){
        Intent intent = serialPortMessageActivity.getIntent();
        if(intent != null){
            bDevice = intent.getParcelableExtra(Constant.BDEVICE_KEY);
        }
    }

    /**
     * 获取蓝牙名称
     * @return
     */
    public String getName(){
        return bDevice.getDevice_name();
    }

    public boolean connectCommonBLE(String paramString) {
//        if ((myBluetoothAdapter == null) || (paramString == null))
//            return false;
//        if ((myBluetoothDeviceAddress != null)
//                && (paramString.equals(myBluetoothDeviceAddress))
//                && (myBluetoothGatt != null)) {
//            if (myBluetoothGatt.connect()) {
//                System.out.println("mBluetoothGatt²»Îª¿Õ");
//                return true;
//            }
//            return false;
//        }
//        BluetoothDevice localBluetoothDevice = myBluetoothAdapter
//                .getRemoteDevice(paramString);
//        if (localBluetoothDevice == null)
//            return false;
//        myBluetoothGatt = localBluetoothDevice.connectGatt(this, true,
//                myGattCallback);
//        myBluetoothDeviceAddress = paramString;
        return true;
    }

    public boolean connectBLE(){
        return true;
    }

    public boolean disconnect(){
        return true;
    }

}
