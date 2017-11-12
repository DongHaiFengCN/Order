package com.zm.order.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zm.order.R;

import java.util.List;

import bean.kitchenmanage.promotion.PromotionRuleC;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/11/11 21:25
 * 修改人：donghaifeng
 * 修改时间：2017/11/11 21:25
 * 修改备注：
 */

public class RulesAdapter extends BaseAdapter {

    private PayActivity payActivity;

    private List<PromotionRuleC> list;

    public RulesAdapter(List<PromotionRuleC> list,PayActivity payActivity){

        this.payActivity = payActivity;
        this.list = list;

    }
    @Override
    public int getCount() {
        return list.isEmpty()?0:list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;

        if (view == null) {

            view =  payActivity.getLayoutInflater().inflate(R.layout.view_payactivity_promotion_rule_lv_dialog,null);
            viewHolder = new ViewHolder();
            viewHolder.a = view.findViewById(R.id.a);
            viewHolder.b = view.findViewById(R.id.b);
            view.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.a.setText("满"+list.get(i).getCounts());
        viewHolder.b.setText("打"+list.get(i).getCounts()+"折");
        return view;
    }

    public class ViewHolder {

        TextView a;
        TextView b;



    }
}
