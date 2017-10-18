package com.zyl.myble;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.zyl.myble.MainActivity.TAG;
import static com.zyl.myble.R.id.tv_status;

/**
 * Created by zhuyuliang on 2017/10/18.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainPresenter {

    //申请扫描蓝牙的权限
    public static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    //申请打开蓝牙的权限
    public static final int REQUEST_ENABLE_BT = 0x011;

    //蓝牙适配器
    BluetoothAdapter mBluetoothAdapter;
    BluetoothLeScanner mBluetoothLeScanner;
    BluetoothLeAdvertiser advertiser;
    Handler mHandler;

    List<BLEDevice> devicelist = new ArrayList<>();
    StringBuffer builder = new StringBuffer();
    private boolean isScanBLE =false;

    MainActivity mainActivity;

    private Runnable scantimerunnable = new Runnable() {
        @Override
        public void run() {
            stopScanning();
        }
    };

    /**
     * 初始化
     */
    public MainPresenter(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        mHandler = new Handler();
    }

    /**
     * 申请权限
     */
    public void RequestPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (mainActivity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                mainActivity.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }
    }

    /**
     * 判断是否支持蓝牙
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void isSuppertBLE(){
        //判断是或否支持蓝牙
        if (!mainActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(mainActivity, "不支持蓝牙！", Toast.LENGTH_SHORT).show();
            mainActivity.finish();
        }

        //获取蓝牙相关适配器
        final BluetoothManager bluetoothManager = (BluetoothManager) mainActivity.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(mainActivity, "蓝牙获取错误！", Toast.LENGTH_SHORT).show();
            mainActivity.finish();
        }

        //判断蓝牙是否被打开
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                mainActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    /**
     * 扫描蓝牙
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void scanBLE() {
        if(isScanBLE)return;
        isScanBLE = true;
        devicelist.clear();

        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        BluetoothLeAdvertiser advertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        // TODO Auto-generated method stub
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .setConnectable(false).build();
        ParcelUuid pUuid = new ParcelUuid(UUID.fromString("00001000-0000-1000-8000-00805f9b34fb"));
        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .addServiceUuid(pUuid)
                .addServiceData(pUuid,
                        "Data".getBytes(Charset.forName("UTF-8"))).build();
        final AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
                if (settingsInEffect != null) {
                    Log.d(TAG, "onStartSuccess TxPowerLv=" + settingsInEffect.getTxPowerLevel() + " mode=" + settingsInEffect.getMode()
                            + " timeout=" + settingsInEffect.getTimeout());
                } else {
                    Log.e(TAG, "onStartSuccess, settingInEffect is null");
                }
                Log.e(TAG, "onStartSuccess settingsInEffect" + settingsInEffect);
            }

            @Override
            public void onStartFailure(int errorCode) {
                super.onStartFailure(errorCode);
                if (errorCode == ADVERTISE_FAILED_DATA_TOO_LARGE) {
                    Toast.makeText(mainActivity, "Operation failed due to an internal error", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Failed to start advertising as the advertise data to be broadcasted is larger than 31 bytes.");
                } else if (errorCode == ADVERTISE_FAILED_TOO_MANY_ADVERTISERS) {
                    Toast.makeText(mainActivity, "Operation failed due to an internal error", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Failed to start advertising because no advertising instance is available.");
                } else if (errorCode == ADVERTISE_FAILED_ALREADY_STARTED) {
                    Toast.makeText(mainActivity, "Operation failed due to an internal error", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Failed to start advertising as the advertising is already started");
                } else if (errorCode == ADVERTISE_FAILED_INTERNAL_ERROR) {
                    Toast.makeText(mainActivity, "Operation failed due to an internal error", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Operation failed due to an internal error");
                } else if (errorCode == ADVERTISE_FAILED_FEATURE_UNSUPPORTED) {
                    Toast.makeText(mainActivity, "This feature is not supported on this platform", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "This feature is not supported on this platform");
                } else {
                    Toast.makeText(mainActivity, "onStartFailure errorCode", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "onStartFailure errorCode" + errorCode);
                }
            }
        };
        advertiser.startAdvertising(settings, data, mAdvertiseCallback);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScanning();
            }
        }, 10000);

        mBluetoothLeScanner.startScan(mScanCallback);

    }

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (result == null || result.getDevice() == null
                    || TextUtils.isEmpty(result.getDevice().getName())) {
                mainActivity.setTv_status("没有搜索到蓝牙设备");
                return;
            }

            BluetoothDevice device = result.getDevice();
            Log.d(TAG, "Device name: " + device.getName());
            Log.d(TAG, "Device address: " + device.getAddress());
            Log.d(TAG, "Device service UUIDs: " + device.getUuids());
            if (builder.toString().contains(device.getName())) {
            } else {
                devicelist.add(new BLEDevice(device.getName(), device.getAddress()));
                builder.append("\n" + device.getName() + "&" + device.getAddress() + "\n");
                mHandler.removeCallbacks(scantimerunnable);
                mHandler.postDelayed(scantimerunnable, 10000);
            }
            ScanRecord record = result.getScanRecord();
            Log.d(TAG, "Record advertise flags: 0x" + Integer.toHexString(record.getAdvertiseFlags()));
            Log.d(TAG, "Record Tx power level: " + record.getTxPowerLevel());
            Log.d(TAG, "Record device name: " + record.getDeviceName());
            Log.d(TAG, "Record service UUIDs: " + record.getServiceUuids());
            Log.d(TAG, "Record service data: " + record.getServiceData());

            mainActivity.refreshList(devicelist);

            mainActivity.setTv_status("搜索结果，builder：" + devicelist.size());
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void stopScanning() {
        isScanBLE = false;
        if(builder!=null){
            builder.delete(0,builder.length());
        }
        if (mBluetoothLeScanner != null) {
            Log.d(TAG, "Stop scanning.");
            mBluetoothLeScanner.stopScan(mScanCallback);
        }
    }

    /**
     * 点击链接BLE
     * @param bleDevice
     */
    public void OnClickConnectBLE(BLEDevice bleDevice){
        Toast.makeText(mainActivity, bleDevice.getDevice_name(), Toast.LENGTH_LONG).show();
    }

}
