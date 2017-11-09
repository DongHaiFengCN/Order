package com.zm.order.view;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.zm.order.R;
import com.zm.order.view.adapter.SeekT9DialogAdapter;

import java.util.ArrayList;
import java.util.List;

import bean.Goods;
import bean.kitchenmanage.dishes.DishesC;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import model.CDBHelper;

/**
 * Created by lenovo on 2017/10/26.
 */

public class SeekT9Fragment extends Fragment {

    @BindView(R.id.activity_seek_list)
    ListView activitySeekList;
    /* @BindView(R.id.activity_seek_list)
     RecyclerView activitySeekList;*/
    @BindView(R.id.activity_seek_edit)
    EditText activitySeekEdit;
    @BindView(R.id.ibtn_key_1)
    ImageView ibtnKey1;
    @BindView(R.id.ibtn_key_2)
    ImageView ibtnKey2;
    @BindView(R.id.ibtn_key_3)
    ImageView ibtnKey3;
    @BindView(R.id.ibtn_key_4)
    ImageView ibtnKey4;
    @BindView(R.id.ibtn_key_5)
    ImageView ibtnKey5;
    @BindView(R.id.ibtn_key_6)
    ImageView ibtnKey6;
    @BindView(R.id.ibtn_key_7)
    ImageView ibtnKey7;
    @BindView(R.id.ibtn_key_8)
    ImageView ibtnKey8;
    @BindView(R.id.ibtn_key_9)
    ImageView ibtnKey9;
    @BindView(R.id.ibtn_key_l)
    ImageView ibtnKeyL;
    @BindView(R.id.ibtn_key_0)
    ImageView ibtnKey0;
    @BindView(R.id.ibtn_key_r)
    ImageView ibtnKeyR;
    @BindView(R.id.ibtn_key_del)
    ImageView ibtnKeyDel;
    Unbinder unbinder;

    private String taste = "默认";
    private float total = 0.0f;
    public int point = 1;
    private List<DishesC> mlistSearchDishesObj;
    private List<Goods> myGoodsList;
    private List<String> tasteList;
    private SeekT9Adapter seekT9Adapter;
    View view;
    private MainActivity mainActivity;
    private Handler mHandler = null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_seek, container, false);

        unbinder = ButterKnife.bind(this, view);
       /* point_tv = getActivity().findViewById(R.id.point);
        total_tv = getActivity().findViewById(R.id.total_tv);*/
        mHandler = new Handler();
        initView();
        return view;

    }

    public void initView() {
        seekT9Adapter = new SeekT9Adapter((MainActivity) getActivity());

        mlistSearchDishesObj = new ArrayList<>();
        myGoodsList = new ArrayList<>();

        activitySeekList.setAdapter(seekT9Adapter);

        seekT9Adapter.setListener(new SeekT9Adapter.SeekT9OnClickListener() {
            @Override
            public void OnClickListener(View view, String name, float price) {
                view.setBackgroundResource(R.color.lucency);
                showDialog(name, price);
            }

        });


    }


    /**
     * 菜品选择弹出框编辑模块
     *
     * @param name  传入的菜品的名称
     * @param price 传入的菜品的价格
     */
    private void showDialog(final String name, final float price) {
        final float[] l = {0.0f};
        tasteList = new ArrayList<>();
        view = LayoutInflater.from(getActivity()).inflate(R.layout.view_item_dialog, null);

        final TextView price_tv = view.findViewById(R.id.price);


        final AmountView amountView = view.findViewById(R.id.amount_view);
        price_tv.setText("总计 " + amountView.getAmount() * price + " 元");
        l[0] = amountView.getAmount() * price;
        //增删选择器的数据改变的监听方法

        amountView.setChangeListener(new AmountView.ChangeListener() {
            @Override
            public void OnChange(int ls, boolean flag) {


                l[0] = ls * price;//实时计算当前菜品选择不同数量后的单品总价

                price_tv.setText("总计 " + l[0] + " 元");

            }
        });
        for (int a = 0; a < myGoodsList.size(); a++) {
            if (myGoodsList.get(a).getDishesC().getTasteList() != null) {
                for (int i = 0; i < myGoodsList.get(a).getDishesC().getTasteList().size(); i++) {
                    Document document = CDBHelper.getDocByID(getActivity().getApplicationContext(), myGoodsList.get(a).getDishesC().getTasteList().get(i).toString());
                    tasteList.add(document.getString("tasteName"));
                }

            } else {

            }
        }

        RecyclerView recyclerView = view.findViewById(R.id.view_dialog_recycler);

        RadioGroup group = view.findViewById(R.id.radioGroup);
        MyGridAdapter myGridAdapter = new MyGridAdapter();
        recyclerView.setAdapter(myGridAdapter);
        //选择口味
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {

                if (i == R.id.Spicy) {

                    taste = "微辣";

                } else if (i == R.id.hot) {

                    taste = "辣";

                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog
                .Builder(getActivity());
        builder.setTitle(name);
        builder.setView(view);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mainActivity = (MainActivity) getActivity();
                int sum = amountView.getAmount();

                if (sum != 0) {//如果选择器的数量不为零，当前的选择的菜品加入订单列表
                    final SparseArray<Object> s = new SparseArray<>();//查下这个怎么用
                    s.put(0, name);
                    s.put(1, taste);
                    s.put(2, sum + "");
                    s.put(3, price);
                    s.put(4, sum * price);
                    mainActivity.getOrderItem().add(s);
                    //购物车计数器数据更新
                    point = (((MainActivity) getActivity()).getPoint());
                    point++;
                    ((MainActivity) getActivity()).setPoint(point);

                    //计算总价
                    total = ((MainActivity) getActivity()).getTotal();
                    total += l[0];
                    ((MainActivity) getActivity()).setTotal(total);

                    //刷新订单数据源
                    //o.notifyDataSetChanged();

                } else {

                    Toast.makeText(getActivity(), "没有选择商品数量！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    // 查询方法
    public void search(final String search) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {

                myGoodsList.clear();
//        List<Document> documentList=CDBHelper.getDocmentsByWhere((getActivity().getApplicationContext(), Expression.property("className").equalTo("DishesC")
//                .and(Expression.property("dishesNameCode9").like(search+"%")),null,DishesC.class);

                List<DishesC> dishesCs = CDBHelper.getObjByWhere(getActivity().getApplicationContext(), Expression.property("className").equalTo("DishesC")
                        .and(Expression.property("dishesNameCode9").like(search + "%")), null, DishesC.class);
                for (DishesC obj : dishesCs) {
                    //mlistSearchDishesObj.add(obj);
                    if (obj.getTasteList() != null)
                        Log.e("T9Fragment", "kouwei size=" + obj.getTasteList().size());
                    Goods goodsObj = new Goods();
                    goodsObj.setCount(0);
                    goodsObj.setDishesC(obj);
                    myGoodsList.add(goodsObj);


                }
                seekT9Adapter.setmData(myGoodsList);
                seekT9Adapter.notifyDataSetChanged();
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.activity_seek_edit, R.id.ibtn_key_1, R.id.ibtn_key_2, R.id.ibtn_key_3, R.id.ibtn_key_4, R.id.ibtn_key_5, R.id.ibtn_key_6, R.id.ibtn_key_7, R.id.ibtn_key_8, R.id.ibtn_key_9, R.id.ibtn_key_l, R.id.ibtn_key_0, R.id.ibtn_key_r, R.id.ibtn_key_del})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.activity_seek_edit:

                break;

            case R.id.ibtn_key_1:

                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "1");
                search(activitySeekEdit.getText().toString());
                break;

            case R.id.ibtn_key_2:

                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "2");
                search(activitySeekEdit.getText().toString());

                break;

            case R.id.ibtn_key_3:

                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "3");
                search(activitySeekEdit.getText().toString());
                break;

            case R.id.ibtn_key_4:

                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "4");
                search(activitySeekEdit.getText().toString());
                break;

            case R.id.ibtn_key_5:

                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "5");
                search(activitySeekEdit.getText().toString());
                break;

            case R.id.ibtn_key_6:

                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "6");
                search(activitySeekEdit.getText().toString());
                break;

            case R.id.ibtn_key_7:

                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "7");
                search(activitySeekEdit.getText().toString());
                break;

            case R.id.ibtn_key_8:

                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "8");
                search(activitySeekEdit.getText().toString());
                break;

            case R.id.ibtn_key_9:

                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "9");
                search(activitySeekEdit.getText().toString());
                break;

            case R.id.ibtn_key_l:

                break;

            case R.id.ibtn_key_0:

                activitySeekEdit.getText().insert(activitySeekEdit.getSelectionEnd(), "0");
                search(activitySeekEdit.getText().toString());
                break;

            case R.id.ibtn_key_r:

                break;
            case R.id.ibtn_key_del:
                int length = activitySeekEdit.getSelectionEnd();
                if (length > 1) {
                    activitySeekEdit.getText().delete(length - 1, length);
                    search(activitySeekEdit.getText().toString());
                }
                if (length == 1) {
                    search("o");
                    activitySeekEdit.getText().delete(length - 1, length);
                }

                break;
        }
    }

    static class MyGridAdapter extends RecyclerView.Adapter {


        private int index = -1;
        private Activity activity;
        private List<String> tasteList;

        private OnItemOlickListener mOnItemOlickListener = null;


        public interface OnItemOlickListener {

            void onItemClick(int position);
        }

        public void setmOnItemOlickListener(OnItemOlickListener listener) {
            this.mOnItemOlickListener = listener;
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            HolderView holderView;
            View convertView = LayoutInflater.from(activity).inflate(R.layout.item_dialog_girdview, null);
            holderView = new HolderView(convertView);
            return holderView;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final HolderView holderView = (HolderView) holder;
            holderView.itemRcTv.setText(tasteList.get(position));
            holderView.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    index = holderView.getLayoutPosition();
                    notifyDataSetChanged();
                    holderView.itemView.setTag(position);
                    if (mOnItemOlickListener != null) {
                        mOnItemOlickListener.onItemClick((int) v.getTag());
                    }
                }
            });
            if (position == index) {
                holderView.itemRcCk.setChecked(true);
            } else {
                holderView.itemRcCk.setChecked(false);
            }
        }


        @Override
        public int getItemCount() {
            return tasteList == null ? 0 : tasteList.size();
        }

        class HolderView extends RecyclerView.ViewHolder {

            @BindView(R.id.item_rc_tv)
            TextView itemRcTv;
            @BindView(R.id.item_rc_ck)
            CheckBox itemRcCk;
            public HolderView(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }
        }
    }
}
