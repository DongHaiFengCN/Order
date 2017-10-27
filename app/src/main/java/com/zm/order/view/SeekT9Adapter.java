package com.zm.order.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Document;
import com.zm.order.R;

import java.util.List;

import bean.kitchenmanage.dishes.DishesC;
import butterknife.BindView;
import butterknife.ButterKnife;
import model.DBFactory;
import model.DatabaseSource;
import model.IDBManager;

/**
 * Created by lenovo on 2017/10/26.
 */

public class SeekT9Adapter extends BaseAdapter {

    private List<DishesC> mData;
    private Context context;
    private SeekT9OnClickListener listener;
    public SeekT9Adapter(Context context) {
        this.context = context;
    }

    public void setListener(SeekT9OnClickListener listener){
        this.listener = listener;
    }

    public void setmData(List<DishesC> mData) {
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_seek, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.itemSeekInfo.setText(mData.get(position).getDishesName());
        viewHolder.itemSeekTv.setText(mData.get(position).getPrice()+"");
        viewHolder.itemSeekLn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (listener != null){
                   listener.OnClickListener(mData.get(position).getDishesName(),mData.get(position).getPrice());
               }
            }
        });
        return convertView;
    }

    interface SeekT9OnClickListener{
        void OnClickListener(String name,float price);
    }
    public class ViewHolder {
        @BindView(R.id.item_seek_info)
        TextView itemSeekInfo;
        @BindView(R.id.item_seek_tv)
        TextView itemSeekTv;
        @BindView(R.id.item_seek_ln)
        LinearLayout itemSeekLn;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
