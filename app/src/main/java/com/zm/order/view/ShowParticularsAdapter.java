package com.zm.order.view;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zm.order.R;

import java.util.Iterator;
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

    public void setGoodsCs(final List<GoodsC> goodsCs){
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
        viewHolder.kw = convertView.findViewById(R.id.item_show_kw);
        viewHolder.item_show_lin = convertView.findViewById(R.id.item_show_lin);

        viewHolder.dj.setText(goodsCs.get(position).getPrice()+"");
        viewHolder.mc.setText(goodsCs.get(position).getDishesName());
        viewHolder.sl.setText(goodsCs.get(position).getDishesCount()+"");
        if(goodsCs.get(position).getDishesTaste() != null){
            viewHolder.kw.setText(goodsCs.get(position).getDishesTaste());
        }else{
            viewHolder.kw.setText("");
        }



        return convertView;
    }

    class ViewHolder{
        private TextView mc,dj,sl,kw;
        private LinearLayout item_show_lin;

    }

    interface OnLinClickListener{
        void getLinClick(ImageView imageView);
    }
}
