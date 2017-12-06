package com.zm.order.view;

import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zm.order.R;

import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;

import bean.kitchenmanage.dishes.DishesC;
import bean.kitchenmanage.order.GoodsC;
import model.CDBHelper;

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
    private List<GoodsC> goodsCs;
    private MainActivity context;
    private int Price = 0;
    private Handler mHandler= new Handler();

    public void setOnchangeListener(OnchangeListener onchangeListener) {
        this.onchangeListener = onchangeListener;
    }

    public void setOrderItem(SparseArray<Object> sparseArray){
        orderItem.add(sparseArray);
        notifyDataSetChanged();

    }

    private OnchangeListener onchangeListener;

    public OrderAdapter(){}

    public OrderAdapter(List<GoodsC> goodsCs, MainActivity mainActivity) {
        this.goodsCs = goodsCs;
        this.context = mainActivity;
    }
   /* public OrderAdapter(List<SparseArray<Object>> orderItem, MainActivity mainActivity) {
        this.orderItem = orderItem;
        this.context = mainActivity;
    }*/


    @Override
    public int getCount() {
        return goodsCs == null ? 0 : goodsCs.size();
    }

    @Override
    public Object getItem(int i) {

        return goodsCs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {


       final ViewHold  viewHold;

        if(view == null){

            //加载布局管理器
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.view_orderitem_lv,null);
            viewHold = new ViewHold();
            viewHold.name =view.findViewById(R.id.name);
            viewHold.taste =view.findViewById(R.id.taste);
            viewHold.price = view.findViewById(R.id.price);
            viewHold.number =view.findViewById(R.id.amount_view);
            viewHold.number.getEtAmount().setEnabled(false);
            view.setTag(viewHold);

        }else{

            viewHold = (ViewHold) view.getTag();
        }

        viewHold.name.setText(goodsCs.get(i).getDishesName());
        if (goodsCs.get(i).getDishesTaste() == null){
            viewHold.taste.setText("");
        }else{
            viewHold.taste.setText(goodsCs.get(i).getDishesTaste());
        }
        context.setOrderAdapter(this);

        viewHold.number.setNumber(goodsCs.get(i).getDishesCount()+"");



        if (goodsCs.get(i).getDishesId() != null){
            DishesC dishesC = null;
            dishesC =  CDBHelper.getObjById(context.getApplicationContext(),goodsCs.get(i).getDishesId(), DishesC.class);


            //设置item的点击事件
                final DishesC finalDishesC = dishesC;
                viewHold.number.setChangeListener(new AmountView.ChangeListener() {
                @Override
                public void OnChange(float ls,boolean flag) {


                    goodsCs.get(i).setDishesCount(ls);
                    goodsCs.get(i).setAllPrice(ls * finalDishesC.getPrice());

                    onchangeListener.onchangeListener(flag, finalDishesC.getPrice() ,ls);

                    context.getSeekT9Adapter().notifyDataSetChanged();

                  if(ls == 0){

                      goodsCs.get(i).setDishesCount(0);
                      context.getSeekT9Adapter().notifyDataSetChanged();
                      goodsCs.remove(i);
                      notifyDataSetChanged();

                  }



                }
            });
        }else{
            final float price = goodsCs.get(i).getAllPrice();
            //设置item的点击事件
            viewHold.number.setChangeListener(new AmountView.ChangeListener() {
                @Override
                public void OnChange(float ls,boolean flag) {

                    goodsCs.get(i).setDishesCount(ls);
                    goodsCs.get(i).setAllPrice(ls * price );

                    onchangeListener.onchangeListener(flag, price ,ls);


                    if(ls == 0){

                        goodsCs.get(i).setDishesCount(0);
                        goodsCs.remove(i);
                        notifyDataSetChanged();

                    }



                }
            });

        }
        return view;
    }


    class ViewHold{

        TextView name;

        TextView taste;

        TextView price;


        AmountView number;


    }

   // flag ？+ ：-,price 单价 ,sum 当前item的商品的个数。
    interface OnchangeListener{

       void onchangeListener(boolean flag,float price,float sum);
    }


}
