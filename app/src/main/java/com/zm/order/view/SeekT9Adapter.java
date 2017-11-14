package com.zm.order.view;

import android.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.couchbase.lite.Document;
import com.zm.order.R;
import com.zm.order.view.adapter.SeekT9DialogAdapter;

import java.util.ArrayList;
import java.util.List;

import bean.kitchenmanage.dishes.DishesC;
import bean.kitchenmanage.dishes.DishesKindC;
import bean.kitchenmanage.order.GoodsC;
import butterknife.BindView;
import butterknife.ButterKnife;
import model.CDBHelper;

/**
 * Created by lenovo on 2017/10/26.
 */

public class SeekT9Adapter extends BaseAdapter {

    private List<GoodsC> mData;
    private MainActivity activity;
    private SeekT9OnClickListener listener;
    private int number=1;
    private int point = 1;
    private float total;
    private String taste ;
    private List<String> tasteList;
    private SeekT9OrderItem orderItem;
    private boolean isName = false,isTaste = false;
    private int pos,p ;
    private DishesC dishesC;

    public SeekT9Adapter(MainActivity context) {
        this.activity = context;
    }

    public void setListener(SeekT9OnClickListener listener) {
        this.listener = listener;
    }

    public void setOrderItem(SeekT9OrderItem orderItem){
        this.orderItem = orderItem;
    }

    public void setmData(List<GoodsC> mData) {

        this.mData = mData;
    }

    private int getPosition(){
        return p;
    }

    public List<GoodsC> getmData(){
        return mData;
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
        dishesC =  CDBHelper.getObjById(activity.getApplicationContext(),mData.get(position).getDishesId(),DishesC.class);
        viewHolder.itemSeekInfo.setText(dishesC.getDishesName());
        viewHolder.itemSeekTv.setText(dishesC.getPrice() + "");
        viewHolder.viewShu.setText(""+mData.get(position).getDishesCount());
        viewHolder.itemSeekLn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dishesC =  CDBHelper.getObjById(activity.getApplicationContext(),mData.get(position).getDishesId(),DishesC.class);
                v.setBackgroundResource(R.color.lucency);
                if (listener != null) {
                    listener.OnClickListener(v,dishesC.getDishesName(), dishesC.getPrice(),position);
                }
            }
        });

        activity.setT9Adapter(this);

        if (activity.getGoodsList().size() != 0 ){
            for (int i = 0 ;i< activity.getGoodsList().size() ;i++){
                if (activity.getGoodsList().get(i).getDishesName().toString().equals(dishesC.getDishesName())) {
                    viewHolder.viewShu.setText(activity.getGoodsList().get(i).getDishesCount()+"");
                    mData.get(position).setDishesCount(Integer.parseInt(viewHolder.viewShu.getText().toString()));
                    break;
                }else {
                    mData.get(position).setDishesCount(0);
                    viewHolder.viewShu.setText(mData.get(position).getDishesCount()+"");
                }
            }
            number = Integer.parseInt(viewHolder.viewShu.getText().toString());
        }else{
            mData.get(position).setDishesCount(0);
            viewHolder.viewShu.setText(mData.get(position).getDishesCount()+"");
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
                dishesC =  CDBHelper.getObjById(activity.getApplicationContext(),mData.get(position).getDishesId(),DishesC.class);
                tasteList = new ArrayList<String>();
                if (dishesC.getTasteList() != null){
                    for (int i = 0; i <  dishesC.getTasteList().size(); i++){
                        Document document = CDBHelper.getDocByID(activity.getApplicationContext(),dishesC.getTasteList().get(i).toString());
                        tasteList.add(document.getString("tasteName"));
                    }

                    dialog(tasteList,position,s,viewHolder);

                }else{
                    setTJ(position,s,viewHolder);
                }





            }
        });

        viewHolder.viewJian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dishesC =  CDBHelper.getObjById(activity.getApplicationContext(),mData.get(position).getDishesId(),DishesC.class);
                if (mData.get(position).getDishesCount() > 0){
                    mData.get(position).setDishesCount(mData.get(position).getDishesCount()-1);
                }

                if (mData.get(position).getDishesCount() <= 0){
                    viewHolder.viewShu.setVisibility(View.GONE);
                    viewHolder.viewJian.setVisibility(View.GONE);
                }
                viewHolder.viewShu.setText(mData.get(position).getDishesCount()+"");
                if (activity.getGoodsList().size() != 0 ){
                    for (int i = 0 ;i< activity.getGoodsList().size() ;i++){
                        if (activity.getGoodsList().get(i).getDishesName().toString().equals(dishesC.getDishesName())) {
                            number = activity.getGoodsList().get(i).getDishesCount();
                            break;
                        }
                    }
                }

                number = Integer.parseInt(viewHolder.viewShu.getText().toString());
                if (activity.getGoodsList().size() != 0 ){
                    for (int i = 0 ;i< activity.getGoodsList().size() ;i++){
                        if (activity.getGoodsList().get(i).getDishesName().toString().equals(dishesC.getDishesName())) {
                            activity.getGoodsList().get(i).setDishesCount(number);
                            number = activity.getGoodsList().get(i).getDishesCount();
                            activity.getGoodsList().get(i).setAllPrice(number * dishesC.getPrice());
                            total = activity.getTotal();
                            total -= 1 * dishesC.getPrice();
                            activity.setTotal(total);
                            point = activity.getPoint();
                            if (number == 0){
                                point--;
                                number = 1;
                            }
                            activity.setPoint(point);
                            break;
                        }
                    }

                }

            }
        });

        return convertView;
    }



    private void setTJ(int position , SparseArray<Object> s,ViewHolder viewHolder){
        GoodsC goodsC = new GoodsC();
        mData.get(position).setDishesCount(mData.get(position).getDishesCount()+1);

        viewHolder.viewShu.setText(mData.get(position).getDishesCount()+"");
        if (!viewHolder.viewShu.getText().toString().equals("0")){
            viewHolder.viewShu.setVisibility(View.VISIBLE);
            viewHolder.viewJian.setVisibility(View.VISIBLE);
        }
        if (activity.getGoodsList().size() == 0 ){
            //如果选择器的数量不为零，当前的选择的菜品加入订单列表
            if (mData.get(position).getDishesCount() > 0) {


                goodsC.setDishesName(dishesC.getDishesName());
                if (tasteList.size() == 0){
                    goodsC.setDishesTaste(null);
                }else{
                    goodsC.setDishesTaste(tasteList.get(pos));
                }
                goodsC.setDishesCount(1);
                goodsC.setAllPrice(mData.get(position).getDishesCount() * dishesC.getPrice());
                goodsC.setDishesId(dishesC.get_id());
                DishesKindC dishesKind  = CDBHelper.getObjById(activity.getApplicationContext(),dishesC.getDishesKindId(), DishesKindC.class);
                goodsC.setDishesKindName(dishesKind.getKindName());
                Log.e("dishesKindName",dishesKind.getKindName());
                activity.getGoodsList().add(goodsC);

                CDBHelper.createAndUpdate(activity.getApplicationContext(), goodsC);
                //购物车计数器数据更新
                point =  activity.getPoint();
                point++;
                activity.setPoint(point);
                //计算总价
                total = activity.getTotal();
                total += 1 * dishesC.getPrice();
                activity.setTotal(total);

            }

        }else {

            mData.get(position).setDishesCount(Integer.parseInt(viewHolder.viewShu.getText().toString()));

            number = mData.get(position).getDishesCount();

            for (int i = 0; i< activity.getGoodsList().size();i++) {
                if (activity.getGoodsList().get(i).getDishesName().toString().equals(dishesC.getDishesName())){
                    activity.getGoodsList().get(i).setDishesCount(number++);
                    number = activity.getGoodsList().get(i).getDishesCount();
                    activity.getGoodsList().get(i).setAllPrice(number*dishesC.getPrice());
                    total = activity.getTotal();
                    total += 1 * dishesC.getPrice();
                    activity.setTotal(total);
                    isName = true;
                    //购物车计数器数据更新
                    point =  activity.getPoint();
                    if (point==0){
                        point++;
                        activity.setPoint(point);
                    }
                    break;
                }else{
                    isName = false;
                }
            }

            if (isName == false){

                number = mData.get(position).getDishesCount();

                if (number != -1) {//如果选择器的数量不为零，当前的选择的菜品加入订单列表

                    goodsC.setDishesName(dishesC.getDishesName());
                    if (tasteList.size() == 0){
                        goodsC.setDishesTaste(null);
                    }else{
                        goodsC.setDishesTaste(tasteList.get(pos));
                    }
                    goodsC.setDishesCount(1);
                    goodsC.setAllPrice(mData.get(position).getDishesCount() * dishesC.getPrice());
                    goodsC.setDishesId(dishesC.get_id());
                    DishesKindC dishesKind  = CDBHelper.getObjById(activity.getApplicationContext(),dishesC.getDishesKindId(), DishesKindC.class);
                    goodsC.setDishesKindName(dishesKind.getKindName());
                    Log.e("dishesKindName",dishesKind.getKindName());
                    activity.getGoodsList().add(goodsC);
                    CDBHelper.createAndUpdate(activity.getApplicationContext(), goodsC);
                    //购物车计数器数据更新
                    point =  activity.getPoint();
                    point++;
                    activity.setPoint(point);
                    //计算总价
                    total = activity.getTotal();
                    total += 1 * dishesC.getPrice();;
                    activity.setTotal(total);
                }


            }
        }
    }

    //自定义弹窗
    public void dialog(final List<String> mData, final int position , final SparseArray<Object> s, final ViewHolder viewHolder) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = View
                .inflate(activity, R.layout.view_t9_dialog, null);//设置弹窗布局
        builder.setView(view);
        builder.setCancelable(true);
        RecyclerView rlv_caipin = view.findViewById(R.id.rlv_caipin);
        rlv_caipin.setLayoutManager(new LinearLayoutManager(activity));
        SeekT9DialogAdapter seekT9DialogAdapter = new SeekT9DialogAdapter(activity,mData);

        seekT9DialogAdapter.setmOnItemOlickListener(new SeekT9DialogAdapter.OnItemOlickListener() {
            @Override
            public void onItemClick(int position) {
                pos = position;
            }
        });
        rlv_caipin.setAdapter(seekT9DialogAdapter);
        //取消或确定按钮监听事件处理
        final AlertDialog dialog = builder.create();
        Button btn_cancel = view
                .findViewById(R.id.view_caipin_but);//取消按钮
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });


        Button btn_comfirm = view
                .findViewById(R.id.view_caipin_but_ok);//确定按钮

        btn_comfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                taste = mData.get(pos);
                isTaste = true;
                dialog.dismiss();
                setTJ(position,s,viewHolder);
            }
        });
        dialog.show();
    }

    interface SeekT9OnClickListener {
        void OnClickListener(View view,String name, float price,int pos);
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
