package com.drivingassistant.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.drivingassistant.R;
import com.drivingassistant.model.entity.UserLocation;

import java.util.ArrayList;

public class LocationListAdapter extends BaseAdapter {

    //Dữ liệu liên kết bởi Adapter là một mảng các sản phẩm
    final ArrayList<UserLocation> listLocation;

    public LocationListAdapter(ArrayList<UserLocation> listLocation) {
        this.listLocation= listLocation;
    }

    @Override
    public int getCount() {
        //Trả về tổng số phần tử, nó được gọi bởi ListView
        return listLocation.size();
    }

    @Override
    public Object getItem(int position) {
        //Trả về dữ liệu ở vị trí position của Adapter, tương ứng là phần tử
        //có chỉ số position trong listProduct
        return listLocation.get(position);
    }

    @Override
    public long getItemId(int position) {
        //Trả về một ID của phần
        return listLocation.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //convertView là View của phần tử ListView, nếu convertView != null nghĩa là
        //View này được sử dụng lại, chỉ việc cập nhật nội dung mới
        //Nếu null cần tạo mới

        View viewLocation;
        if (convertView == null) {
            viewLocation = View.inflate(parent.getContext(), R.layout.location_row, null);
        } else viewLocation = convertView;

        //Bind sữ liệu phần tử vào View
        UserLocation location = (UserLocation) getItem(position);
        ((TextView) viewLocation.findViewById(R.id.time)).setText(String.format("At: %s", location.time));
        ((TextView) viewLocation.findViewById(R.id.location)).setText(location.location);

        return viewLocation;
    }
}