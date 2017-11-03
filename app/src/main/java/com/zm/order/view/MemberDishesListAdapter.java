package com.zm.order.view;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zm.order.R;

import java.util.List;

/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/11/1 10:54
 * 修改人：donghaifeng
 * 修改时间：2017/11/1 10:54
 * 修改备注：
 */

public class MemberDishesListAdapter extends BaseAdapter {
    private List list;
    PayActivity payActivity;
    public MemberDishesListAdapter(List list,PayActivity payActivity) {

        super();
        this.list = list;
        this.payActivity = payActivity;
    }

    @Override
    public int getCount() {

        return list.isEmpty()?0:list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHold viewHold = null;

        if(view == null){

            //加载布局管理器
            LayoutInflater inflater = LayoutInflater.from(payActivity);
            view = inflater.inflate(R.layout.view_payactivity_memberdishes_sale_dialog_adapteritem,null);
            viewHold = new ViewHold();
            viewHold.dishesname =view.findViewById(R.id.dishesname_tv);
            viewHold.dishestotalprice =view.findViewById(R.id.dishestotalprice_tv);
            viewHold.saleprice = view.findViewById(R.id.saleprice_tv);
            viewHold.issale =view.findViewById(R.id.issale_tv);

            view.setTag(viewHold);

        }else{

            viewHold = (ViewHold) view.getTag();
        }

        SparseArray sparseArray = (SparseArray) list.get(i);
        viewHold.dishesname.setText(sparseArray.get(0).toString());
        viewHold.dishestotalprice.setText(sparseArray.get(4).toString());
        viewHold.saleprice.setText(sparseArray.get(6).toString());

        if((int)sparseArray.get(5) == 1){

            viewHold.issale.setVisibility(View.VISIBLE);
        }


        return view;
    }

    class ViewHold{

        TextView dishesname;

        TextView dishestotalprice;

        TextView saleprice;


        ImageView issale;


    }
}
