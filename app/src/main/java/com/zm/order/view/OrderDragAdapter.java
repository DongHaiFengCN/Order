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

import model.CDBHelper;

/**
 * Created by lenovo on 2017/10/30.
 */

public class OrderDragAdapter extends BaseAdapter {
    private List<String> mlistDishesId;
    private Context context;
    private Document kindDocument;
    private OnItemClickListener mOnItemClickListener = null;
    public Document getKindDocument() {
        return kindDocument;
    }

    public void setKindDocument(Document kindDocument) {

        this.kindDocument = kindDocument;
    }

    public OrderDragAdapter(Context context,Document kindDocument){
        this.context = context;
        this.kindDocument = kindDocument;
    }

    public  void setMlistDishesId(List<String> mlistDishesId){
        this.mlistDishesId = mlistDishesId;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }


    @Override
    public int getCount() {
        return mlistDishesId == null ? 0 :mlistDishesId.size();
    }

    @Override
    public Object getItem(int position) {
        return mlistDishesId.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        HolderView view;
        if (convertView == null) {
            view = new HolderView();
            convertView = LayoutInflater.from(context).inflate(R.layout.view_item_recl, parent, false);
            view.name = convertView.findViewById(R.id.item_info);
            view.price = convertView.findViewById(R.id.price_tv);
            view.select_ln =convertView.findViewById(R.id.select_ln);
            convertView.setTag(view);
        }else{

            view = (HolderView) convertView.getTag();
        }
        final Document doc = CDBHelper.getDocByID(context,mlistDishesId.get(position));
        if(doc != null){
            view.name.setText(doc.getString("dishesName"));
            view.price.setText(doc.getFloat("price")+" 元/份");

            view.select_ln.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(doc.getString("dishesName"),doc.getFloat("price"),position);
                }
            });
        }
        return convertView;
    }


    public void insert(int dragSrcPosition, int dragPosition)
    {

        String id=mlistDishesId.get(dragPosition);
        mlistDishesId.set(dragPosition,mlistDishesId.get(dragSrcPosition));
        mlistDishesId.set(dragSrcPosition,id);
        notifyDataSetChanged();
    }


    class HolderView{

        TextView name;
        TextView price;
        LinearLayout select_ln;


    }

    public  interface OnItemClickListener {

        void onItemClick(String name,float price,int position);
    }
}
