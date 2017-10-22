package com.zyl.myble;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
    private TextView scan_type_tv;
    //扫描结果列表
    private ListView ble_list;
    private ProgressBar progressbar;

    //adappter
    private DeviceListAdapter mLeDeviceListAdapter;

    private MainPresenter mainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scan_btn = (Button) findViewById(R.id.scan_btn);
        scan_type_tv = (TextView) findViewById(R.id.scan_type_tv);
        scan_btn.setOnClickListener(this);
        //list
        ble_list = (ListView) findViewById(R.id.ble_list);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);

        //初始化控制器
        mainPresenter = new MainPresenter(this);

        mLeDeviceListAdapter = new DeviceListAdapter(this, mainPresenter, mainPresenter.devicelist);
        ble_list.setAdapter(mLeDeviceListAdapter);

        //申请敏感权限
        mainPresenter.RequestPermissions();
        //判断蓝牙设备是否支持
        mainPresenter.isSupportBluetooch();
        //判断是否打开蓝牙
        mainPresenter.isOpenBluetooch();
        //判断是否支持BLE
        mainPresenter.isSuppertBLE();

    }

    /**
     * 设置蓝牙设备状态
     *
     * @param str
     */
    public void setStatus(String str, boolean isprogressbar) {
        scan_type_tv.setText(str);
        if (isprogressbar) {
            progressbar.setVisibility(View.VISIBLE);
            scan_btn.setText("停止扫描");
        }else {
            progressbar.setVisibility(View.GONE);
            scan_btn.setText("开始扫描");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainPresenter.removeBluetoochReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainPresenter.RegisterBluetoochReceiver();
    }

    public void refreshList() {
        mLeDeviceListAdapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.scan_btn:
                //判断搜索ble还是普通蓝牙
                if(mainPresenter.isScanining()){
                    mainPresenter.BreakScanBluetooch();
                }else{
                    showDialogForSelectScanType();
                }
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

    /**
     * 弹框选择搜索类型
     */
    private void showDialogForSelectScanType(){
        final String items[] = {"CommonBluetooch", "BLE"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("扫描类型");
        builder.setIcon(R.mipmap.ic_launcher);
        // 设置列表显示，注意设置了列表显示就不要设置builder.setMessage()了，否则列表不起作用。
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(items[which].equals("BLE")){
                    mainPresenter.scanBLE();
                }else if(items[which].equals("CommonBluetooch")){
                    mainPresenter.scanCommonBluetooch();
                }
            }
        });
        builder.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 跳转串口操作界面
     * @param bDevice
     */
    public void startActivity(BDevice bDevice){
        Intent intent = new Intent(this,SerialPortMessageActivity.class);
        intent.putExtra(Constant.BDEVICE_KEY,bDevice);
        startActivity(intent);
    }


}
