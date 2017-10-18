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

/**
 * @author zhuyuliang
 * @message 蓝牙串口通信主程序
 */

//支持android21以上的系统
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public final static String TAG = "MainActivity";

    //扫描按键
    private Button scan_btn;
    //扫描结果列表
    private ListView ble_list;
    //扫描状态
    private TextView tv_status;
    //adappter
    private DeviceListAdapter mLeDeviceListAdapter;

    private MainPresenter mainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scan_btn = (Button) findViewById(R.id.scan_btn);
        scan_btn.setOnClickListener(this);
        tv_status = (TextView) findViewById(R.id.tv_status);
        //list
        ble_list = (ListView) findViewById(R.id.ble_list);

        //初始化控制器
        mainPresenter = new MainPresenter(this);

        mLeDeviceListAdapter = new DeviceListAdapter(this, mainPresenter, mainPresenter.devicelist);
        ble_list.setAdapter(mLeDeviceListAdapter);

        mainPresenter.RequestPermissions();
        mainPresenter.isSuppertBLE();

    }

    /**
     * 设置蓝牙设备状态
     * @param str
     */
    public void setTv_status(String str){
        tv_status.setText(str);
    }

    public void refreshList(List<BLEDevice> bleDevices){
        mLeDeviceListAdapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.scan_btn:
                mainPresenter.scanBLE();
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MainPresenter.REQUEST_ENABLE_BT) {
            if (requestCode == RESULT_OK) {
                // TODO 蓝牙已经开启
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MainPresenter.PERMISSION_REQUEST_COARSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // TODO request success
                } else {
                    finish();
                }
                break;
        }
    }


}
