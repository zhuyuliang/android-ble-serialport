package com.zyl.myble;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zhuyuliang on 2017/10/18.
 *
 * @messge 蓝牙设备列表适配器
 */

public class DeviceListAdapter extends BaseAdapter {

    private Context mContext;
    private List<BDevice> devicelist;
    private MainPresenter mainPresenter;

    public DeviceListAdapter(Context context,MainPresenter presenter, List<BDevice> devicelist) {
        this.mContext = context;
        this.devicelist = devicelist;
        this.mainPresenter = presenter;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.view_holder_ble_item, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_ble_name);
            holder.tv_code = (TextView) convertView.findViewById(R.id.tv_ble_code);
            holder.item_ll = (LinearLayout) convertView.findViewById(R.id.item_ll);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.tv_name.setText(devicelist.get(i).getDevice_name());
        holder.tv_code.setText(devicelist.get(i).getDevice_id());

        //点击连接
        holder.item_ll.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                mainPresenter.OnClickConnect(devicelist.get(i));
            }
        });

        return convertView;
    }
}

/**
 * 容器
 */
class Holder {
    TextView tv_name;// 标题
    TextView tv_code;// 代码
    LinearLayout item_ll;
}
