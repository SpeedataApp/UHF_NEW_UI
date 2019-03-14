package com.speedata.uhf.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.speedata.uhf.R;

import java.util.List;

/**
 * 寻卡列表适配器
 * Created by 张智超 on 2019/3/7
 * @author 张智超
 */
public class UhfCardAdapter extends ArrayAdapter<UhfCardBean> {
    private int newResourceId;

    public UhfCardAdapter(Context context, int resourceId, List<UhfCardBean> uhfCardBeanList) {
        super(context, resourceId, uhfCardBeanList);
        newResourceId = resourceId;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        UhfCardBean uhfCardBean = getItem(position);
        @SuppressLint("ViewHolder") View view = LayoutInflater.from(getContext()).inflate(newResourceId, parent, false);

        TextView tvepc = (TextView) view.findViewById(R.id.item_epc_tv);
        TextView tvvRssi = (TextView) view.findViewById(R.id.item_valid_rssi_tv);

        tvepc.setText(uhfCardBean.getTvepc());
        tvvRssi.setText(uhfCardBean.getTvvRssi());

        return view;
    }
}
