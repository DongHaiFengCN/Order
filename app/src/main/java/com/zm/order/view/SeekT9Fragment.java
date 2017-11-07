package com.zm.order.view;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.zm.order.R;
import com.zm.order.view.adapter.TestAdapte;

import java.util.ArrayList;
import java.util.List;

import application.MyApplication;
import bean.Goods;
import bean.Order;
import bean.kitchenmanage.dishes.DishesC;
import bean.kitchenmanage.order.GoodsC;
import bean.kitchenmanage.order.OrderC;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import model.CDBHelper;

/**
 * Created by lenovo on 2017/10/26.
 */

public class SeekT9Fragment extends Fragment{

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
    private TestAdapte testAdapte;
    private SeekT9Adapter seekT9Adapter ;
    private TextView total_tv;
    private TextView point_tv;
    View view;
    private boolean isName = false;
    private MainActivity mainActivity ;
    private List<String> strings;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_seek, container, false);

        unbinder = ButterKnife.bind(this, view);
        point_tv = getActivity().findViewById(R.id.point);
        total_tv = getActivity().findViewById(R.id.total_tv);
        initView();
        //1111
        return view;

    }
    public void initView() {
        seekT9Adapter = new SeekT9Adapter((MainActivity)getActivity());

        mlistSearchDishesObj = new ArrayList<>();
        myGoodsList  = new ArrayList<>();

        activitySeekList.setAdapter(seekT9Adapter);

        seekT9Adapter.setListener(new SeekT9Adapter.SeekT9OnClickListener() {
            @Override
            public void OnClickListener(View view,String name, float price) {
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

        view = LayoutInflater.from(getActivity()).inflate(R.layout.view_item_dialog, null);

        final TextView price_tv = view.findViewById(R.id.price);

        final AmountView amountView = view.findViewById(R.id.amount_view);
        amountView.getAmount();

        //增删选择器的数据改变的监听方法

        amountView.setChangeListener(new AmountView.ChangeListener() {
            @Override
            public void OnChange(int ls, boolean flag) {


                l[0] = ls * price;//实时计算当前菜品选择不同数量后的单品总价

                price_tv.setText("总计 " + l[0] + " 元");

            }
        });


        RadioGroup group = view.findViewById(R.id.radioGroup);

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
                mainActivity = (MainActivity)getActivity();
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
                    point =  (((MainActivity) getActivity()).getPoint());
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
    public void search(String search) {
        //mlistSearchDishesObj.clear();
        myGoodsList.clear();

/*
        List<Document> documentList=CDBHelper.getDocmentsByWhere((getActivity().getApplicationContext(), Expression.property("className").equalTo("DishesC")
                .and(Expression.property("dishesNameCode9").like(search+"%")),null,DishesC.class);
        for(Document doc: documentList)
        {
            //mlistSearchDishesObj.add(obj);
            if(obj.getTasteIdList()!=null)
            Log.e("T9Fragment","kouwei size="+obj.getTasteIdList().size());
            Goods goodsObj =new Goods();
            goodsObj.setCount(0);
            goodsObj.setDishesC(obj);
            myGoodsList.add(goodsObj);


        }*/
        seekT9Adapter.setmData(myGoodsList);
        seekT9Adapter.notifyDataSetChanged();
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
                if (length > 1)
                {
                    activitySeekEdit.getText().delete(length - 1, length);
                    search(activitySeekEdit.getText().toString());
                }
                if (length == 1)
                {
                    search("o");
                    activitySeekEdit.getText().delete(length - 1, length);
                }

                break;
        }
    }
}
