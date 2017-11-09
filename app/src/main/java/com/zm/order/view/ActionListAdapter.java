package com.zm.order.view;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.couchbase.lite.Document;
import com.zm.order.R;

import java.util.List;

import untils.MyLog;

/**
 * 项目名称：Order
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2017/11/1 10:54
 * 修改人：donghaifeng
 * 修改时间：2017/11/1 10:54
 * 修改备注：
 */

public class ActionListAdapter extends BaseAdapter {

    public void setListView(ListView listView) {
        this.listView = listView;
    }

    ListView listView;
    private List list;
    PayActivity payActivity;
    int [] flag;
    public ActionListAdapter(List list, PayActivity payActivity) {

        super();
        this.list = list;
        this.payActivity = payActivity;
        flag = new int[list.size()];
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
    public View getView(final int i, View view, ViewGroup viewGroup) {

        ViewHold viewHold = null;

        if(view == null){

            //加载布局管理器
            LayoutInflater inflater = LayoutInflater.from(payActivity);
            view = inflater.inflate(R.layout.view_payactivity_action_dialog_adapteritem,null);
            viewHold = new ViewHold();
            viewHold.actionName =view.findViewById(R.id.actionName_tv);
            viewHold.actionTime =view.findViewById(R.id.actionTime_tv);
            viewHold.actionType = view.findViewById(R.id.actionType_tv);
            viewHold.actionIsCheck_ck =view.findViewById(R.id.actionIsCheck_ck);

            view.setTag(viewHold);

        }else{

            viewHold = (ViewHold) view.getTag();
        }

        MyLog.e(i+"");
        Document document = (Document) list.get(i);
        viewHold.actionName.setText(document.getString("promotionName"));
        viewHold.actionTime.setText(document.getString("startTime")+"    /     "+document.getString("endTime"));

        if(document.getInt("promotionType") == 1){

            viewHold.actionType.setText("打折");

        }else if(document.getInt("promotionType") == 2){

            viewHold.actionType.setText("赠券");

        }


        viewHold.actionIsCheck_ck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(flag[i] == 0){

                    flag[i] = 1;

                }else{

                    flag[i] = 0;
                }
            }
        });

        if(flag[i] == 1){

            viewHold.actionIsCheck_ck.setChecked(true);

        }else {

            viewHold.actionIsCheck_ck.setChecked(false);
        }

        return view;
    }

    public int[] getFlag(){

        return flag;
    }

    class ViewHold{

        TextView actionName;

        TextView actionTime;

        TextView actionType;

        CheckBox actionIsCheck_ck;


    }
}
