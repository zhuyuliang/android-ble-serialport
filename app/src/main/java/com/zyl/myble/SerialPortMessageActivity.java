package com.zyl.myble;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

/**
 * @author zhuyuliang
 * @message 蓝牙串口通信界面
 */

public class SerialPortMessageActivity extends AppCompatActivity implements View.OnClickListener {

    public final static String TAG = "SerialPortMessageActivity";

    ActionBar actionBar;

    private SerialPortMessagePresenter serialPortMessagePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spmessage);

        //初始化控制器
        serialPortMessagePresenter = new SerialPortMessagePresenter(this);

        //设置actionbar
        actionBar = getSupportActionBar();
        actionBar.setTitle("蓝牙:" + serialPortMessagePresenter.getName());
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_spmessage, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //断开连接
                serialPortMessagePresenter.disconnect();
                finish();
                return true;
            case R.id.action_breakconnect:
                //断开连接
                serialPortMessagePresenter.disconnect();
                return true;
            case R.id.action_clear:
                Toast.makeText(this, "clear", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_io:
                Toast.makeText(this, "io_Settings", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_info:
                Toast.makeText(this, "info", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        }
    }


}
