package com.zm.order.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.zm.order.R;

import java.util.ArrayList;
import java.util.List;

import model.CDBHelper;

/**
 * Created by zyf on 2017/5/8.
 * 左边ListView适配器
 */

public class AdapterLeft extends BaseAdapter {

    private static final String TAG = AdapterLeft.class.getSimpleName();
    private Context mContext;
    private List<String> mDataList = new ArrayList<>();
    private int mSelection = 0;

    public AdapterLeft(Context mContext) {
        this.mContext = mContext;

    }

    public void setmDataList(List<String> mDataList){
        this.mDataList = mDataList;
    }

    @Override
    public int getCount() {
        if (null != mDataList)
            return mDataList.size();
        return 0;
    }

    @Override
    public Object getItem(int i) {
        if (null != mDataList)
            return mDataList.get(i);
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (null == view){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_left, null);
            viewHolder.textContent = (TextView) view.findViewById(R.id.text_content);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (null != viewHolder.textContent && mSelection == position){
            viewHolder.textContent.setTextColor(Color.RED);
        }else {
            viewHolder.textContent.setTextColor(Color.BLACK);
        }
        if (null != viewHolder.textContent && null != mDataList && mDataList.size()>0){
            viewHolder.textContent.setText(CDBHelper.getDocByID(mContext,mDataList.get(position)).getString("kindName"));
        }else {
            Log.i(TAG, "getView: null == mDataList");
        }
        return view;
    }

    public int getSelection() {
        return mSelection;
    }

    public void setSelection(int selection) {
        mSelection = selection;
        notifyDataSetChanged();
    }

    class ViewHolder{
        TextView textHead;
        TextView textContent;
    }
}
