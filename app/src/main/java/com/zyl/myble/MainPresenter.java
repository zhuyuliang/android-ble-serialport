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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

/**
 * Created by zhuyuliang on 2017/10/18.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainPresenter {

    //申请扫描蓝牙的权限
    public static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    //申请打开蓝牙的权限
    public static final int REQUEST_ENABLE_BT = 0x011;

    //蓝牙管理类
    private BluetoothManager bluetoothManager;
    //蓝牙适配器
    private BluetoothAdapter mBluetoothAdapter;
    //蓝牙扫描
    private BluetoothLeScanner mBluetoothLeScanner;

    //BLE使用
    private BluetoothLeAdvertiser advertiser;

    //普通蓝牙
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothReceiver bluetoothReceiver;

    Handler mHandler;
    List<BDevice> devicelist = new ArrayList<>();
    StringBuffer builder = new StringBuffer();
    private boolean isScanBLE = false;
    private boolean isScanBluetooch = false;

    MainActivity mainActivity;

    private Runnable scantimerunnable = new Runnable() {
        @Override
        public void run() {
            stopBLEScanning();
        }
    };

    private Runnable scancommontimerunnable = new Runnable() {
        @Override
        public void run() {
            stopScanCommonBluetooch();
        }
    };

    /**
     * 初始化
     */
    public MainPresenter(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        mHandler = new Handler();
        bluetoothManager = (BluetoothManager) mainActivity.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    /**
     * 申请权限
     */
    public void RequestPermissions() {
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
    public boolean isSuppertBLE() {
        //判断是或否支持BLE蓝牙
        if (!mainActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(mainActivity, "不支持BLE蓝牙！", Toast.LENGTH_SHORT).show();
            mainActivity.finish();
        }
        return true;
    }

    /**
     * 判断是否支持蓝牙设备
     */
    public boolean isSupportBluetooch(){
        //获取蓝牙相关适配器
        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(mainActivity, "蓝牙获取错误！", Toast.LENGTH_SHORT).show();
            mainActivity.finish();
        }
        return true;
    }

    /**
     * 判断是否打开蓝牙设备
     */
    public void isOpenBluetooch(){
        //判断蓝牙是否被打开
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                if (!mBluetoothAdapter.isEnabled()) {
                    //申请打开
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    mainActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            }else{
                //已经打开
            }
        } else {
            Toast.makeText(mainActivity, "蓝牙获取错误！", Toast.LENGTH_SHORT).show();
            mainActivity.finish();
        }
    }

    /**
     * 是否在扫描中
     * @return
     */
    public boolean isScanining(){
        if(isScanBLE || isScanBluetooch) {
            return true;
        }else{
            return false;
        }
    }

    /**
     * 中断扫描蓝牙
     */
    public void BreakScanBluetooch(){
        if(isScanBLE){
            BreakOffBLEScanning();
        }
        if(isScanBluetooch){
            BreakScanCommonBluetooch();
        }
    }

    /** TODO BLE ***/
    /**
     * 扫描蓝牙BLE
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void scanBLE() {
        if (isScanBLE){
            BreakOffBLEScanning();
            return;
        }
        isScanBLE = true;
        devicelist.clear();
        mainActivity.refreshList();
        mainActivity.setStatus("BLE扫描中...",true);

        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        advertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
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

        mHandler.postDelayed(scantimerunnable, 10000);

        mBluetoothLeScanner.startScan(mScanCallback);

    }

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (result == null || result.getDevice() == null
                    || TextUtils.isEmpty(result.getDevice().getName())) {
                return;
            }

            BluetoothDevice device = result.getDevice();
            Log.d(TAG, "Device name: " + device.getName());
            Log.d(TAG, "Device address: " + device.getAddress());
            Log.d(TAG, "Device service UUIDs: " + device.getUuids());
            if (builder.toString().contains(device.getName())) {
            } else {
                devicelist.add(new BDevice(device.getName(), device.getAddress(),device, BDevice.BluetoochType.BLEBluetooch));
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

            mainActivity.refreshList();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void stopBLEScanning() {
        isScanBLE = false;
        if (builder != null) {
            builder.delete(0, builder.length());
        }
        if (mBluetoothLeScanner != null) {
            Log.d(TAG, "Stop scanning.");
            mBluetoothLeScanner.stopScan(mScanCallback);
            mainActivity.setStatus("准备扫描",false);
            Toast.makeText(mainActivity, "扫描完成! 搜索结果：" + devicelist.size(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 中断扫描
     */
    private void BreakOffBLEScanning(){
        isScanBLE = false;
        if (builder != null) {
            builder.delete(0, builder.length());
        }
        mHandler.removeCallbacks(scantimerunnable);
        if (mBluetoothLeScanner != null) {
            mBluetoothLeScanner.stopScan(mScanCallback);
            mainActivity.setStatus("准备扫描",false);
        }
    }

    /**
     * 点击链接BLE
     *
     * @param bdDevice
     */
    public void OnClickConnectBLE(BDevice bdDevice) {
        BreakOffBLEScanning();
        Toast.makeText(mainActivity, "BLE:" + bdDevice.getDevice_name(), Toast.LENGTH_LONG).show();
    }

    /** TODO end BLE ***/

    /** TODO 普通蓝牙搜索 ***/

    /**
     * 扫描普通蓝牙
     */
    public void scanCommonBluetooch() {
        if (isScanBluetooch)
        {
            BreakScanCommonBluetooch();
            return;
        }
        isScanBluetooch = true;
        devicelist.clear();
        mainActivity.refreshList();
        mainActivity.setStatus("普通扫描中...",true);
        mHandler.postDelayed(scancommontimerunnable, 10000);
        if(bluetoothAdapter != null) {
            bluetoothAdapter.startDiscovery();
        }
    }

    class BluetoothReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {
                Log.e(TAG, "找到新设备了");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device == null) {
                    return;
                }
                if (builder.toString().contains(device.getName())) {
                } else {
                    devicelist.add(new BDevice(device.getName(), device.getAddress(),device, BDevice.BluetoochType.CommonBluetooch));
                    builder.append("\n" + device.getName() + "&" + device.getAddress() + "\n");
                    mHandler.removeCallbacks(scancommontimerunnable);
                    mHandler.postDelayed(scancommontimerunnable, 10000);
                }
                mainActivity.refreshList();
            }
        }
    }

    /**
     * 停止扫描普通蓝牙
     */
    public void stopScanCommonBluetooch(){
        isScanBluetooch = false;
        if (builder != null) {
            builder.delete(0, builder.length());
        }
        if(bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }
        mHandler.removeCallbacks(scancommontimerunnable);
        mainActivity.setStatus("准备扫描",false);
        Toast.makeText(mainActivity, "扫描完成! 搜索结果：" + devicelist.size(), Toast.LENGTH_LONG).show();
    }

    /**
     * 中断扫描普通蓝牙
     */
    public void BreakScanCommonBluetooch(){
        isScanBluetooch = false;
        if (builder != null) {
            builder.delete(0, builder.length());
        }
        if(bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }
        mHandler.removeCallbacks(scancommontimerunnable);
        mainActivity.setStatus("准备扫描",false);
    }

    /**
     * 注册广播
     */
    public void RegisterBluetoochReceiver(){
        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        mainActivity.registerReceiver(bluetoothReceiver = new BluetoothReceiver(), intentFilter);
    }

    /**
     * 移除广播
     */
    public void removeBluetoochReceiver(){
        mainActivity.unregisterReceiver(bluetoothReceiver);
    }

    /**
     * 点击链接普通蓝牙
     *
     * @param bdDevice
     */
    public void OnClickConnectBluetooch(BDevice bdDevice) {
        BreakScanCommonBluetooch();
        Toast.makeText(mainActivity, "普通蓝牙:" + bdDevice.getDevice_name(), Toast.LENGTH_LONG).show();
    }

    /** TODO end 普通蓝牙搜索 ***/


    /**
     * 点击链接蓝牙
     *
     * @param bdDevice
     */
    public void OnClickConnect(BDevice bdDevice) {
          switch (bdDevice.getBluetoochType()){
              case BLEBluetooch:
                  OnClickConnectBLE(bdDevice);
                  break;
              case CommonBluetooch:
                  OnClickConnectBluetooch(bdDevice);
                  break;
          }
    }

}
