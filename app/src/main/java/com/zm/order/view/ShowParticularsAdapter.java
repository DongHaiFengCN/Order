package com.zm.order.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zm.order.R;

import java.util.List;

import bean.kitchenmanage.order.GoodsC;

/**
 * Created by lenovo on 2017/12/13.
 */

public class ShowParticularsAdapter extends BaseAdapter {

    private Activity activity;
    private List<GoodsC> goodsCs;
    private OnLinClickListener linClickListener;

    public void setLinClickListener(OnLinClickListener linClickListener){
        this.linClickListener = linClickListener;
    }

    public ShowParticularsAdapter(Activity activity){
        this.activity = activity;
    }

    public void setGoodsCs(List<GoodsC> goodsCs){
        this.goodsCs = goodsCs;
    }
    @Override
    public int getCount() {
        return goodsCs == null ? 0 : goodsCs.size();
    }

    @Override
    public Object getItem(int position) {
        return goodsCs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(activity).inflate(R.layout.item_show_particulars,null);
            viewHolder = new ViewHolder();
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.dj = convertView.findViewById(R.id.item_show_dj);
        viewHolder.mc = convertView.findViewById(R.id.item_show_mc);
        viewHolder.sl = convertView.findViewById(R.id.item_show_sl);
        viewHolder.item_show_lin = convertView.findViewById(R.id.item_show_lin);
        viewHolder.item_show_img = convertView.findViewById(R.id.item_show_img);

        viewHolder.dj.setText(goodsCs.get(position).getAllPrice()+"");
        viewHolder.mc.setText(goodsCs.get(position).getDishesName());
        viewHolder.sl.setText(goodsCs.get(position).getDishesCount()+"");
        viewHolder.item_show_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //linClickListener.getLinClick(viewHolder.item_show_img);


            }
        });

        return convertView;
    }

    class ViewHolder{
        private TextView mc,dj,sl;
        private LinearLayout item_show_lin;
        private ImageView item_show_img;

    }

    interface OnLinClickListener{
        void getLinClick(ImageView imageView);
    }
}
