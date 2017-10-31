package com.zm.order.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Expression;
import com.couchbase.lite.Log;
import com.zm.order.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bean.Order;
import bean.kitchenmanage.dishes.DishesC;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import model.CDBHelper;
import presenter.IMainPresenter;
import presenter.MainPresenterImpl;
import untils.AnimationUtil;
import untils.MyLog;

/**
 * Created by lenovo on 2017/10/26.
 */

public class SeekT9Fragment extends Fragment{

    @BindView(R.id.activity_seek_list)
    ListView activitySeekList;
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
    private ArrayList<String> list = new ArrayList<>();

    private boolean flag = true;
    private String taste = "默认";
    private float total = 0.0f;
   // private OrderAdapter o;
    public int point = 0;
    private List<DishesC> mlistSearchDishesObj;
    //private List<SparseArray<Object>> orderItem =new ArrayList<>();
    //
    private SeekT9Adapter seekT9Adapter ;
    private TextView total_tv;
    /*private ImageButton delet_bt,car_iv;
    private TextView ok_tv;
    private ListView order_lv;
    private ImageView imageView;
    private LinearLayout linearLayout;*/
   private TextView point_tv;
   View view;
    private MainActivity mainActivity ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_seek, container, false);

        unbinder = ButterKnife.bind(this, view);
        point_tv = getActivity().findViewById(R.id.point);
        total_tv = getActivity().findViewById(R.id.total_tv);
        /*ok_tv = getActivity().findViewById(R.id.ok_tv);
        imageView = getActivity().findViewById(R.id.shade);
        linearLayout = getActivity().findViewById(R.id.orderList);
        car_iv = getActivity().findViewById(R.id.car);
        order_lv = getActivity().findViewById(R.id.order_lv);
        //清空按钮
        delet_bt = getActivity().findViewById(R.id.delet);*/
        initView();

        return view;

    }
    public void initView() {
        seekT9Adapter = new SeekT9Adapter(getActivity());
        mlistSearchDishesObj = new ArrayList<>();
        //初始化订单的数据，绑定数据源的信息。
        /*o = new OrderAdapter(orderItem, getActivity());

        order_lv.setAdapter(o);

        //监听orderItem的增加删除，设置总价以及总数量, flag ？+ ：-,price 单价 ,sum 当前item的个数。

        o.setOnchangeListener(new OrderAdapter.OnchangeListener() {
            @Override
            public void onchangeListener(boolean flag, float price, int sum) {

                if (flag) {

                    total += price;

                    total_tv.setText(total + "元");


                } else {

                    total -= price;

                    total_tv.setText(total + "元");

                    if (sum == 0) {

                        point--;

                        point_tv.setText(point + "");

                        if (point == 0) {

                            point_tv.setVisibility(View.INVISIBLE);
                        }


                    }


                }
            }
        });

        //获取屏幕尺寸

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int w = dm.widthPixels;
        int h = dm.heightPixels;

        //设置表单的容器的长度为视窗的一半高，由父类的节点获得

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) linearLayout
                .getLayoutParams();
        layoutParams.width = w;
        layoutParams.height = h / 2;
        linearLayout.setLayoutParams(layoutParams);


        car_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (flag) {

                    linearLayout.setAnimation(AnimationUtil.moveToViewLocation());
                    linearLayout.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                    imageView.animate()
                            .alpha(1f)
                            .setDuration(400)
                            .setListener(null);
                    flag = false;

                } else {

                    linearLayout.setAnimation(AnimationUtil.moveToViewBottom());
                    linearLayout.setVisibility(View.GONE);

                    imageView.animate()
                            .alpha(0f)
                            .setDuration(400)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    imageView.setVisibility(View.GONE);
                                }
                            });

                    flag = true;

                }
            }
        });*/

        seekT9Adapter.setmData(mlistSearchDishesObj);
        seekT9Adapter.notifyDataSetChanged();

        activitySeekList.setAdapter(seekT9Adapter);
        seekT9Adapter.setListener(new SeekT9Adapter.SeekT9OnClickListener() {
            @Override
            public void OnClickListener(String name, float price) {
                showDialog(name, price);
            }
        });


       /* delet_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                clearOrder();
            }
        });

        //提交按钮
        ok_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (total > 0) {

                    Intent intent = new Intent(getActivity(), PayActivity.class);
                    intent.putExtra("Order", (Serializable) orderItem);
                    intent.putExtra("total", total);
                    startActivityForResult(intent, 1);

                    //如果order列表开启状态就关闭
                    if (!flag) {
                        linearLayout.setAnimation(AnimationUtil.moveToViewBottom());
                        linearLayout.setVisibility(View.GONE);
                        imageView.animate()
                                .alpha(0f)
                                .setDuration(400)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        imageView.setVisibility(View.GONE);
                                    }
                                });

                        flag = true;
                    }

                } else {

                    Toast.makeText(getActivity(), "订单为空！", Toast.LENGTH_SHORT).show();
                }


            }


        });*/
    }

    public void SetPoint(int point){
        this.point = point;
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
                    mainActivity.setOrderItem().add(s);
                    point =  (((MainActivity) getActivity()).getPoint());
                    SetPoint(point);
                    point_tv.setText(point + "");
                    //刷新订单数据源
                    //o.notifyDataSetChanged();

                 /*   //购物车计数器数据更新
                    point++;
                    point_tv.setText(point + "");
                    point_tv.setVisibility(View.VISIBLE);

                    //计算总价
                    total += l[0];
                    total_tv.setText(total + "元");*/
                } else {

                    Toast.makeText(getActivity(), "没有选择商品数量！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.show();
    }


    /**
     * 清空订单列表
     */

   /* private void clearOrder() {

        point = 0;
        point_tv.setVisibility(View.INVISIBLE);

        total_tv.setText("0元");
        total = 0;

        orderItem.clear();
        o.notifyDataSetChanged();
    }*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    // 查询方法
    public void search(String search) {
        mlistSearchDishesObj.clear();
        List<DishesC> documentList=CDBHelper.getObjByWhere(getActivity().getApplicationContext(), Expression.property("className").equalTo("DishesC")
                .and(Expression.property("dishesNameCode9").like(search+"%")),null,DishesC.class);
        for(DishesC obj:documentList)
        {
            mlistSearchDishesObj.add(obj);
        }
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
