package com.zm.order.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.couchbase.lite.Document;
import com.zm.order.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import model.DBFactory;
import model.DatabaseSource;
import model.IDBManager;

/**
 * Created by lenovo on 2017/10/26.
 */

public class SeekT9Adapter extends BaseAdapter {

    private List<String> mData;
    private Context context;
    private IDBManager idbManager;

    public SeekT9Adapter(Context context) {
        idbManager = DBFactory.get(DatabaseSource.CouchBase, context);
    }

    public void setmData(List<String> mData) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_seek, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.itemSeekInfo = convertView.findViewById(R.id.item_seek_info);
        viewHolder.itemSeekTv = convertView.findViewById(R.id.item_seek_tv);
        Document document = (Document) idbManager.getById(mData.get(position));
        viewHolder.itemSeekInfo.setText(document.getString("name"));

        return convertView;
    }


    static

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
