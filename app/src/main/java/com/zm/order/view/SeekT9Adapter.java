package com.zm.order.view;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zm.order.R;

import java.util.ArrayList;
import java.util.List;

import bean.Goods;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lenovo on 2017/10/26.
 */

public class SeekT9Adapter extends BaseAdapter {

    private List<Goods> mData;
    private MainActivity activity;
    private SeekT9OnClickListener listener;
    private int number=1;
    private int point = 2;
    private float total;
    private List<SparseArray<Object>> list = new ArrayList<>();
    private SeekT9OrderItem orderItem;
    private boolean isName = false;

    public SeekT9Adapter(MainActivity context) {
        this.activity = context;
    }

    public void setListener(SeekT9OnClickListener listener) {
        this.listener = listener;
    }

    public void setOrderItem(SeekT9OrderItem orderItem){
        this.orderItem = orderItem;
    }

    public void setmData(List<Goods> mData) {
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
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_seek, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.itemSeekInfo.setText(mData.get(position).getDishesC().getDishesName());
        viewHolder.itemSeekTv.setText(mData.get(position).getDishesC().getPrice() + "");
        viewHolder.viewShu.setText(""+mData.get(position).getCount());
        viewHolder.itemSeekLn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.setBackgroundResource(R.color.lucency);
                if (listener != null) {
                    listener.OnClickListener(v,mData.get(position).getDishesC().getDishesName(), mData.get(position).getDishesC().getPrice());
                }
            }
        });

        activity.setT9Adapter(this);
        if (activity.getOrderItem().size() != 0 || activity.getOrderItem() != null){
            for (int i = 0 ;i< activity.getOrderItem().size() ;i++){
                if (activity.getOrderItem().get(i).get(0).toString().equals(mData.get(position).getDishesC().getDishesName())) {
                    viewHolder.viewShu.setText(activity.getOrderItem().get(i).get(2)+"");
                    break;
                }
            }
            number = Integer.parseInt(viewHolder.viewShu.getText().toString());
        }
        String str = viewHolder.viewShu.getText().toString();
        if (str.equals("0")){
            viewHolder.viewShu.setVisibility(View.GONE);
            viewHolder.viewJian.setVisibility(View.GONE);
        }else {
            viewHolder.viewShu.setVisibility(View.VISIBLE);
            viewHolder.viewJian.setVisibility(View.VISIBLE);
        }

        final SparseArray<Object> s = new SparseArray<>();
        viewHolder.viewTj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mData.get(position).setCount(mData.get(position).getCount()+1);
                notifyDataSetChanged();
                if (!viewHolder.viewShu.getText().toString().equals("0")){
                    viewHolder.viewShu.setVisibility(View.VISIBLE);
                    viewHolder.viewJian.setVisibility(View.VISIBLE);
                }
                viewHolder.viewShu.setText(mData.get(position).getCount()+"");

                if (activity.getOrderItem().size() == 0 || activity.getOrderItem() == null){
                    if (number != -1) {//如果选择器的数量不为零，当前的选择的菜品加入订单列表
                        s.put(0, mData.get(position).getDishesC().getDishesName());
                        s.put(1, "默认");
                        s.put(2, 1+"");
                        s.put(3, mData.get(position).getDishesC().getPrice());
                        s.put(4, number * mData.get(position).getDishesC().getPrice());
                        activity.getOrderItem().add(s);
                        //购物车计数器数据更新
                        number =  activity.getPoint();
                        number++;
                        activity.setPoint(number);
                        //计算总价
                        total = activity.getTotal();
                        total += 1 * mData.get(position).getDishesC().getPrice();
                        activity.setTotal(total);
                    }
                }else {
                    number = Integer.parseInt(viewHolder.viewShu.getText().toString());
                    for (int i = 0; i< activity.getOrderItem().size();i++) {
                        if (activity.getOrderItem().get(i).get(0).toString().equals(mData.get(position).getDishesC().getDishesName())){
                            activity.getOrderItem().get(i).put(2, number++);
                            number = Integer.parseInt(activity.getOrderItem().get(i).get(2).toString());
                            total = activity.getTotal();
                            total += 1 * mData.get(position).getDishesC().getPrice();
                            activity.setTotal(total);
                            isName = true;
                            break;
                        }else{
                            isName = false;
                        }
                    }

                    if (isName == false){
                        if (number != -1) {//如果选择器的数量不为零，当前的选择的菜品加入订单列表
                            s.put(0, mData.get(position).getDishesC().getDishesName());
                            s.put(1, "默认");
                            s.put(2, 1+"");
                            s.put(3, mData.get(position).getDishesC().getPrice());
                            s.put(4, number * mData.get(position).getDishesC().getPrice());
                            activity.getOrderItem().add(s);
                            //购物车计数器数据更新
                            number =  activity.getPoint();
                            number++;
                            activity.setPoint(number);
                            //计算总价
                            total = activity.getTotal();
                            total += 1 * mData.get(position).getDishesC().getPrice();;
                            activity.setTotal(total);
                        }
                    }
                }



            }
        });


        viewHolder.viewJian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mData.get(position).getCount() > 0){
                    mData.get(position).setCount(mData.get(position).getCount()-1);

                }else{
                    viewHolder.viewShu.setVisibility(View.GONE);
                    viewHolder.viewJian.setVisibility(View.GONE);
                }
                viewHolder.viewShu.setText(mData.get(position).getCount()+"");

            }
        });

        return convertView;
    }

    interface SeekT9OnClickListener {
        void OnClickListener(View view,String name, float price);
    }

    interface SeekT9OrderItem{
        void seekT9OrderItem(SparseArray<Object> list,int point,float total);
    }

    public class ViewHolder {
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

}
