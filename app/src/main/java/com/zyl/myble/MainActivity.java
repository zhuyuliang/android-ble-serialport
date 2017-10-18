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
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button scan_btn;
    ListView ble_list;
    TextView tv_status;

    //蓝牙适配器
    BluetoothAdapter mBluetoothAdapter;
    static final int REQUEST_ENABLE_BT = 0x011;
    BluetoothLeScanner mBluetoothLeScanner;
    BluetoothLeAdvertiser advertiser;
    Handler mHandler;

    DeviceListAdapter mLeDeviceListAdapter;

    List<DevicxeList> devicelist = new ArrayList<>();

    public final static String TAG = "MainActivity";

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scan_btn = (Button) findViewById(R.id.scan_btn);
        scan_btn.setOnClickListener(this);
        //list
        ble_list = (ListView) findViewById(R.id.ble_list);
        tv_status = (TextView) findViewById(R.id.tv_status);
        mLeDeviceListAdapter = new DeviceListAdapter();
        ble_list.setAdapter(mLeDeviceListAdapter);

        //判断是或否支持蓝牙
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "不支持蓝牙！", Toast.LENGTH_SHORT).show();
            finish();
        }

        //获取蓝牙相关适配器
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "蓝牙获取错误！", Toast.LENGTH_SHORT).show();
            finish();
        }

        //判断蓝牙是否被打开
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }

        mHandler = new Handler();

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.scan_btn:
                scanBLE();
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (requestCode == RESULT_OK) {
                //蓝牙已经开启
                //mBluetoothAdapter.enable();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // TODO request success
                }else{
                    finish();
                }
                break;
        }
    }

    /**
     * 扫描蓝牙
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void scanBLE() {

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
                    Toast.makeText(MainActivity.this, "Operation failed due to an internal error", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Failed to start advertising as the advertise data to be broadcasted is larger than 31 bytes.");
                } else if (errorCode == ADVERTISE_FAILED_TOO_MANY_ADVERTISERS) {
                    Toast.makeText(MainActivity.this, "Operation failed due to an internal error", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Failed to start advertising because no advertising instance is available.");
                } else if (errorCode == ADVERTISE_FAILED_ALREADY_STARTED) {
                    Toast.makeText(MainActivity.this, "Operation failed due to an internal error", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Failed to start advertising as the advertising is already started");
                } else if (errorCode == ADVERTISE_FAILED_INTERNAL_ERROR) {
                    Toast.makeText(MainActivity.this, "Operation failed due to an internal error", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Operation failed due to an internal error");
                } else if (errorCode == ADVERTISE_FAILED_FEATURE_UNSUPPORTED) {
                    Toast.makeText(MainActivity.this, "This feature is not supported on this platform", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "This feature is not supported on this platform");
                } else {
                    Toast.makeText(MainActivity.this, "onStartFailure errorCode", Toast.LENGTH_LONG).show();
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
                tv_status.setText("没有搜索到蓝牙设备");
                return;
            }

            StringBuffer builder = new StringBuffer();

            BluetoothDevice device = result.getDevice();
            Log.d(TAG, "Device name: " + device.getName());
            Log.d(TAG, "Device address: " + device.getAddress());
            Log.d(TAG, "Device service UUIDs: " + device.getUuids());
            if (builder.toString().contains(device.getName())) {
            } else {
                builder.append("\n" + device.getName() + "&" + device.getAddress() + "\n");
            }
            ScanRecord record = result.getScanRecord();
            Log.d(TAG, "Record advertise flags: 0x" + Integer.toHexString(record.getAdvertiseFlags()));
            Log.d(TAG, "Record Tx power level: " + record.getTxPowerLevel());
            Log.d(TAG, "Record device name: " + record.getDeviceName());
            Log.d(TAG, "Record service UUIDs: " + record.getServiceUuids());
            Log.d(TAG, "Record service data: " + record.getServiceData());

            devicelist.add(new DevicxeList(device.getName(),device.getAddress()));
            mLeDeviceListAdapter.notifyDataSetChanged();

            tv_status.setText("搜索结果，builder：" + builder.toString());
        }
    };

    private void stopScanning() {
        if (mBluetoothLeScanner != null) {
            Log.d(TAG, "Stop scanning.");
            mBluetoothLeScanner.stopScan(mScanCallback);
        }
    }

    public class DeviceListAdapter extends BaseAdapter{
        public DeviceListAdapter(){
        }
        @Override
        public int getCount() {
            return devicelist.size();
        }
        @Override
        public Object getItem(int i) {
            return null;
        }
        @Override
        public long getItemId(int i) {
            return 0;
        }
        @Override
        public View getView(final int i, View convertView, ViewGroup viewGroup) {
            Holder holder;
            if (convertView == null) {
                holder = new Holder();
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.view_holder_ble_item, null);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_ble_name);
                holder.tv_code = (TextView) convertView.findViewById(R.id.tv_ble_code);
                holder.item_ll = (LinearLayout) convertView.findViewById(R.id.item_ll);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.tv_name.setText(devicelist.get(i).getDevice_name());
            holder.tv_code.setText(devicelist.get(i).getDevice_id());

            holder.item_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String str = devicelist.get(i).device_id;
                }
            });

            return convertView;
        }
    }

    /**
     * 容器
     */
    static class Holder {
        TextView tv_name;// 标题
        TextView tv_code;// 代码
        LinearLayout item_ll;
    }

    public class DevicxeList{
        public DevicxeList(String device_name,String device_id){
            this.device_name = device_name;
            this.device_id = device_id;
        }
        String device_name;
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

}
