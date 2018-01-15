package com.zm.order.view;

import android.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.zm.order.R;
import com.zm.order.view.adapter.SeekT9DialogAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import application.MyApplication;
import bean.kitchenmanage.dishes.DishesC;
import bean.kitchenmanage.dishes.DishesKindC;
import bean.kitchenmanage.order.GoodsC;
import butterknife.BindView;
import butterknife.ButterKnife;
import model.CDBHelper;

import static model.CDBHelper.getFormatDate;

/**
 * Created by lenovo on 2017/10/26.
 */

public class SeekT9Adapter extends BaseAdapter {

    private static final String TAG = "SeekT9Adapter";
    private List<GoodsC> mGoodsList;
    private MainActivity activity;
    private SeekT9OnClickListener listener;
    //private float number=1;
    private int point = 1;
    private float total;

    private List<String> tasteList;
    private String m_taste;

    private MyApplication myapp;
    private EditText editText;

    public SeekT9Adapter(MainActivity context, EditText editText, List<GoodsC> mData) {
        this.activity = context;
        this.editText = editText;
        myapp = (MyApplication) activity.getApplication();
        this.mGoodsList = mData;
    }

    public void setListener(SeekT9OnClickListener listener) {
        this.listener = listener;
    }


    @Override
    public int getCount() {
        return mGoodsList == null ? 0 : mGoodsList.size();
    }

    @Override
    public Object getItem(int position) {
        return mGoodsList.get(position);
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


        viewHolder.itemSeekInfo.setText(mGoodsList.get(position).getDishesName());
        viewHolder.itemSeekTv.setText(mGoodsList.get(position).getPrice() + "");
        viewHolder.viewShu.setText("" + mGoodsList.get(position).getDishesCount());

        viewHolder.itemSeekLn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("item click", "position = " + position);
                v.setBackgroundResource(R.color.lucency);
                if (listener != null) {
                    listener.OnClickListener(v, mGoodsList.get(position).getDishesName(), mGoodsList.get(position).getPrice(), position);
                }
            }
        });

        final String str = viewHolder.viewShu.getText().toString();
        Log.e("viewShu", str);
        if (str.equals("0.0")) {
            viewHolder.viewShu.setVisibility(View.INVISIBLE);
            viewHolder.viewJian.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.viewShu.setVisibility(View.VISIBLE);
            viewHolder.viewJian.setVisibility(View.VISIBLE);
        }

        viewHolder.viewTj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    CDBHelper.db.inBatch(new TimerTask() {
                        @Override
                        public void run() {

                            m_taste = null;
                            if (tasteList == null)
                                tasteList = new ArrayList<String>();
                            else
                                tasteList.clear();

                            DishesC dishesC = CDBHelper.getObjById(activity.getApplicationContext(), mGoodsList.get(position).getDishesId(), DishesC.class);

                            if (dishesC.getTasteList() != null) {
                                for (int i = 0; i < dishesC.getTasteList().size(); i++) {
                                    Document document = CDBHelper.getDocByID(activity.getApplicationContext(), dishesC.getTasteList().get(i).toString());
                                    tasteList.add(document.getString("tasteName"));
                                }

                                selTasteDialog(tasteList, position, viewHolder);
                                activity.getOrderAdapter().notifyDataSetChanged();

                            } else {

                                setAdd(position, viewHolder);
                                activity.getOrderAdapter().notifyDataSetChanged();
                            }
                        }
                    });
                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }


            }
        });

        viewHolder.viewJian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                  if (mGoodsList.get(position).getDishesCount() > 0) {
                    mGoodsList.get(position).setDishesCount(mGoodsList.get(position).getDishesCount() - 1);
                }

                if (mGoodsList.get(position).getDishesCount() <= 0) {
                    viewHolder.viewShu.setVisibility(View.INVISIBLE);
                    viewHolder.viewJian.setVisibility(View.INVISIBLE);
                }
                viewHolder.viewShu.setText(mGoodsList.get(position).getDishesCount() + "");

                GoodsC obj = mGoodsList.get(position);
                setSub(obj);

            }

        });


        return convertView;
    }

    private void setSub(GoodsC goodsObj)
    {
        if (activity.getGoodsList().size() != 0)
        {
            for (int i = 0; i < activity.getGoodsList().size(); i++)
            {
                if (activity.getGoodsList().get(i).getGoodsType() == 3){
                    continue;
                }
                if (activity.getGoodsList().get(i).getDishesId().equals(goodsObj.getDishesId()))//名称相同
                {
                    if(TextUtils.isEmpty(goodsObj.getDishesTaste()))
                    {
                        activity.getGoodsList().get(i).setDishesCount(activity.getGoodsList().get(i).getDishesCount()-1);
                        float tmpCount = activity.getGoodsList().get(i).getDishesCount();
                        if (tmpCount== 0.0)
                        {
                            activity.getGoodsList().remove(i);
                            i--;
                            point = activity.getPoint();
                            point--;
                            activity.setPoint(point);
                        }
                        total = activity.getTotal();
                        total = MyBigDecimal.sub(total,goodsObj.getPrice(),2);
                        activity.setTotal(total);
                        break;
                    }
                    else
                    {
                        if(goodsObj.getDishesTaste().equals(activity.getGoodsList().get(i).getDishesTaste()))
                        {
                            activity.getGoodsList().get(i).setDishesCount(activity.getGoodsList().get(i).getDishesCount()-1);
                            float tmpCount = activity.getGoodsList().get(i).getDishesCount();
                            if (tmpCount== 0.0)
                            {
                                activity.getGoodsList().remove(i);
                                i--;
                                point = activity.getPoint();
                                point--;
                                activity.setPoint(point);
                            }
                            total = activity.getTotal();
                            total = MyBigDecimal.sub(total,goodsObj.getPrice(),2);
                            activity.setTotal(total);
                            break;
                        }
                    }

                }


            }

        }
        activity.getOrderAdapter().notifyDataSetChanged();
    }
    private void setAdd(int position, ViewHolder viewHolder) {
        boolean isInsert = true;
        editText.setText("");
        mGoodsList.get(position).setDishesCount(mGoodsList.get(position).getDishesCount() + 1);
        viewHolder.viewShu.setText(mGoodsList.get(position).getDishesCount() + "");

        GoodsC goodsObj = new GoodsC(myapp.getCompany_ID());
        goodsObj.setDishesName(mGoodsList.get(position).getDishesName());
        goodsObj.setDishesTaste(m_taste);
        goodsObj.setDishesCount(1);
        goodsObj.setPrice(mGoodsList.get(position).getPrice());
        goodsObj.setGoodsType(0);
        goodsObj.setDishesId(mGoodsList.get(position).getDishesId());
        goodsObj.setDishesKindId(mGoodsList.get(position).getDishesKindId());
        goodsObj.setCreatedTime(getFormatDate());
        if (!viewHolder.viewShu.getText().toString().equals("0")) {
            viewHolder.viewShu.setVisibility(View.VISIBLE);
            viewHolder.viewJian.setVisibility(View.VISIBLE);
        }
        if (activity.getGoodsList().size() == 0)//购物车为空
        {

            //当前的选择的菜品加入订单列表
            activity.getGoodsList().add(goodsObj);
            //购物车计数器数据更新
            point = activity.getPoint();
            point++;
            activity.setPoint(point);
            //计算总价
            total = activity.getTotal();
            total = MyBigDecimal.add(total, goodsObj.getPrice(), 2);
            activity.setTotal(total);


        } else {//购物车不为空，合并或者直插入，要看名称与口味是否一致

            for (int i = 0; i < activity.getGoodsList().size(); i++)//+for
            {
                if (activity.getGoodsList().get(i).getGoodsType() == 3){
                    continue;
                }
                if (activity.getGoodsList().get(i).getDishesId().equals(goodsObj.getDishesId()))//名称相等
                {
                    if (activity.getGoodsList().get(i).getDishesTaste() != null)//口味不为空
                    {
                        if (activity.getGoodsList().get(i).getDishesTaste().equals(goodsObj.getDishesTaste()))//口味相等
                        {
                            activity.getGoodsList().get(i).setDishesCount(activity.getGoodsList().get(i).getDishesCount() + 1);

                            total = activity.getTotal();
                            total = MyBigDecimal.add(total, goodsObj.getPrice(), 2);
                            activity.setTotal(total);
                            isInsert = false;
                            //购物车计数器数据更新
                            point = activity.getPoint();
                            if (point == 0) {
                                point++;
                                activity.setPoint(point);
                            } else {
                                point = activity.getGoodsList().size();
                                activity.setPoint(point);
                            }
                            break;
                        }

                    }//口味为空
                    else {
                        activity.getGoodsList().get(i).setDishesCount(activity.getGoodsList().get(i).getDishesCount() + 1);
                        total = activity.getTotal();
                        total = MyBigDecimal.add(total, goodsObj.getPrice(), 2);
                        activity.setTotal(total);
                        isInsert = false;
                        //购物车计数器数据更新
                        point = activity.getPoint();
                        if (point == 0) {
                            point++;
                            activity.setPoint(point);
                        } else {
                            point = activity.getGoodsList().size();
                            activity.setPoint(point);
                        }
                        break;
                    }

                }
            }//-for

            if (isInsert == true) {

                activity.getGoodsList().add(goodsObj);
                //购物车计数器数据更新
                point = activity.getPoint();
                point++;
                activity.setPoint(point);
                //计算总价
                total = activity.getTotal();
                total = MyBigDecimal.add(total, goodsObj.getPrice(), 2);
                activity.setTotal(total);
            }
        }
    }

    //自定义弹窗
    public void selTasteDialog(final List<String> mData, final int position, final ViewHolder viewHolder) {

        m_taste = tasteList.get(0);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = View
                .inflate(activity, R.layout.view_t9_dialog, null);//设置弹窗布局
        builder.setView(view);
        builder.setCancelable(true);
        RecyclerView rlv_caipin = view.findViewById(R.id.rlv_caipin);
        rlv_caipin.setLayoutManager(new LinearLayoutManager(activity));
        SeekT9DialogAdapter seekT9DialogAdapter = new SeekT9DialogAdapter(activity, mData);

        seekT9DialogAdapter.setmOnItemOlickListener(new SeekT9DialogAdapter.OnItemOlickListener() {
            @Override
            public void onItemClick(int position) {

                m_taste = tasteList.get(position);
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
            public void onClick(View v) {

                dialog.dismiss();
                setAdd(position, viewHolder);
            }
        });
        dialog.show();
    }


    interface SeekT9OnClickListener {
        void OnClickListener(View view, String name, float price, int pos);
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
