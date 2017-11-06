package com.zm.order.view;

import android.app.Activity;
import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zm.order.R;

import java.util.List;

/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/9/20 16:20
 * 修改人：donghaifeng
 * 修改时间：2017/9/20 16:20
 * 修改备注：
 */

public class OrderAdapter extends BaseAdapter {

    /**
     * SparseArray<Object> 中 0位置是菜品名字;1位置是菜品口味;2位置是菜品选择的数量;3单价}
     *
     */
    private List<SparseArray<Object>> orderItem;
    private MainActivity context;

    public void setOnchangeListener(OnchangeListener onchangeListener) {
        this.onchangeListener = onchangeListener;
    }

    public void setOrderItem(SparseArray<Object> sparseArray){
        orderItem.add(sparseArray);
        notifyDataSetChanged();

    }

    private OnchangeListener onchangeListener;

    public OrderAdapter(List<SparseArray<Object>> orderItem, MainActivity mainActivity) {
        this.orderItem = orderItem;
        this.context = mainActivity;
    }


    @Override
    public int getCount() {
        return orderItem.size();
    }

    @Override
    public Object getItem(int i) {

        return orderItem.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {


        ViewHold  viewHold = null;

        if(view == null){

            //加载布局管理器
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.view_orderitem_lv,null);
            viewHold = new ViewHold();
            viewHold.name =view.findViewById(R.id.name);
            viewHold.taste =view.findViewById(R.id.taste);
            viewHold.number =view.findViewById(R.id.amount_view);
            viewHold.number.getEtAmount().setEnabled(false);
            view.setTag(viewHold);

        }else{

            viewHold = (ViewHold) view.getTag();
        }
        viewHold.name.setText(orderItem.get(i).get(0).toString());

        viewHold.taste.setText(orderItem.get(i).get(1).toString());

        viewHold.number.setNumber(orderItem.get(i).get(2).toString());

        //设置item的点击事件
        viewHold.number.setChangeListener(new AmountView.ChangeListener() {
            @Override
            public void OnChange(int ls,boolean flag) {

                orderItem.get(i).put(2,ls);

                onchangeListener.onchangeListener(flag,(float)orderItem.get(i).get(3),ls);
                context.getSeekT9Adapter().notifyDataSetChanged();

              if(ls == 0){

                  orderItem.remove(i);

                  notifyDataSetChanged();

              }



            }
        });
        return view;
    }


    class ViewHold{

        TextView name;

        TextView taste;



        AmountView number;


    }

   // flag ？+ ：-,price 单价 ,sum 当前item的商品的个数。
    interface OnchangeListener{

       void onchangeListener(boolean flag,float price,int sum);
    }


}
