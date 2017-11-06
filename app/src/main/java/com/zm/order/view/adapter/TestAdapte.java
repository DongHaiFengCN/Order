package com.zm.order.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.couchbase.lite.Log;
import com.zm.order.R;
import com.zm.order.view.MainActivity;

import java.util.List;

import bean.Goods;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lenovo on 2017/11/3.
 */

public class TestAdapte extends BaseAdapter {
    private MainActivity activity;
    private List<Goods> mData;
    private List<String> mString;
    public  TestAdapte(MainActivity activity,List<Goods> myGoodsList)
    {

        mData = myGoodsList;
        this.activity = activity;
    }
    public void setmData(List<String> mString){
        this.mString = mString;
    }
    @Override
    public int getCount() {
        return mData == null?0:mData.size();
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

       final ViewHolder viewHolder;

        if (convertView == null) {


            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_seek, parent, false);
            viewHolder = new ViewHolder(convertView);
             convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }
        //viewHolder.itemSeekInfo.setText(mString.get(position));
        viewHolder.itemSeekInfo.setText(mData.get(position).getDishesC().getDishesName());
        viewHolder.viewShu.setText(""+mData.get(position).getCount());
        Log.e("AAA",viewHolder.viewShu.toString());
        viewHolder.viewTj.setOnClickListener(new MyAdapterListener(position,viewHolder,0));
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.item_seek_info)
        TextView itemSeekInfo;
        @BindView(R.id.item_seek_tv)
        TextView itemSeekTv;

        @BindView(R.id.item_seek_ln)
        LinearLayout itemSeekLn;

        @BindView(R.id.view_jian)
        RelativeLayout viewJian;
        @BindView(R.id.view_shu)
        TextView viewShu;
        @BindView(R.id.view_tj)
        RelativeLayout viewTj;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    class MyAdapterListener implements View.OnClickListener
    {
        private int pos;
        int i = 0;
        private ViewHolder viewHolder;
        private int listener;
        public MyAdapterListener(int pos,ViewHolder viewHolder,int listener){
            this.pos = pos;
            this.viewHolder = viewHolder;
            this.listener = listener;
        }
        @Override
        public void onClick(View v) {
            switch (listener){
                case 0:
                   // viewHolder.viewShu.setText(i+1+"");
                    mData.get(pos).setCount(mData.get(pos).getCount()+1);
                    notifyDataSetChanged();
                    break;
            }

        }
    }
}
