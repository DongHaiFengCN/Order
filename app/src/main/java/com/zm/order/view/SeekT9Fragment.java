package com.zm.order.view;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.zm.order.R;
import com.zm.order.view.adapter.MyGridAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import application.MyApplication;
import bean.kitchenmanage.dishes.DishesC;
import bean.kitchenmanage.dishes.DishesKindC;
import bean.kitchenmanage.order.GoodsC;
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
    Unbinder unbinder;
    @BindView(R.id.activity_seek_edit)
    EditText activitySeekEdit;
    @BindView(R.id.ibtn_key_1)
    RelativeLayout ibtnKey1;
    @BindView(R.id.ibtn_key_2)
    RelativeLayout ibtnKey2;
    @BindView(R.id.ibtn_key_3)
    RelativeLayout ibtnKey3;
    @BindView(R.id.ibtn_key_4)
    RelativeLayout ibtnKey4;
    @BindView(R.id.ibtn_key_5)
    RelativeLayout ibtnKey5;
    @BindView(R.id.ibtn_key_6)
    RelativeLayout ibtnKey6;
    @BindView(R.id.ibtn_key_7)
    RelativeLayout ibtnKey7;
    @BindView(R.id.ibtn_key_8)
    RelativeLayout ibtnKey8;
    @BindView(R.id.ibtn_key_9)
    RelativeLayout ibtnKey9;
    @BindView(R.id.ibtn_key_l)
    RelativeLayout ibtnKeyL;
    @BindView(R.id.ibtn_key_0)
    RelativeLayout ibtnKey0;
    @BindView(R.id.ibtn_key_r)
    RelativeLayout ibtnKeyR;
    @BindView(R.id.ibtn_key_del)
    RelativeLayout ibtnKeyDel;

    private String taste = "默认";
    private float total = 0.0f;
    public int point = 1, pos;
    private List<DishesC> mlistSearchDishesObj;
    private List<GoodsC> myGoodsList;
    private List<String> tasteList;
    private SeekT9Adapter seekT9Adapter;
    View view;
    private MainActivity mainActivity;
    private Handler mHandler = null;
    private MyApplication myapp;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_seek, container, false);

        unbinder = ButterKnife.bind(this, view);
        mHandler = new Handler();
        myapp = new MyApplication();

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
            public void OnClickListener(View view, String name, float price,int pos) {
                view.setBackgroundResource(R.color.lucency);
                showDialog(name, price,pos);
            }

        });


    }


    /**
     * 菜品选择弹出框编辑模块
     *
     * @param name  传入的菜品的名称
     * @param price 传入的菜品的价格
     */
    private void showDialog(final String name, final float price,int p) {
        final float[] l = {0.0f};
        tasteList = new ArrayList<>();
        view = LayoutInflater.from(getActivity()).inflate(R.layout.view_item_dialog, null);

        final TextView price_tv = view.findViewById(R.id.price);

        final DishesC dishesC = CDBHelper.getObjById(getActivity().getApplicationContext(),myGoodsList.get(p).getDishesId(),DishesC.class);
        final AmountView amountView = view.findViewById(R.id.amount_view);
        String all = MyBigDecimal.mul(amountView.getAmount()+"",price+"",2);
        price_tv.setText("总计 " + Float.parseFloat(all) + " 元");
        l[0] = Float.parseFloat(all);
        //增删选择器的数据改变的监听方法

        amountView.setChangeListener(new AmountView.ChangeListener() {
            @Override
            public void OnChange(float ls, boolean flag) {

                //实时计算当前菜品选择不同数量后的单品总价
                String all = MyBigDecimal.mul(ls+"",price+"",2);
                l[0] = Float.parseFloat(all);

                price_tv.setText("总计 " + l[0] + " 元");

            }
        });
        if (dishesC.getTasteList() != null) {
            for (int i = 0; i < dishesC.getTasteList().size(); i++) {
                Document document = CDBHelper.getDocByID(getActivity().getApplicationContext(), dishesC.getTasteList().get(i).toString());
                tasteList.add(document.getString("tasteName"));
            }

        }
        //设置每行展示3个
        final GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);
        RecyclerView recyclerView = view.findViewById(R.id.view_dialog_recycler);
        recyclerView.setLayoutManager(manager);
        MyGridAdapter myGridAdapter = new MyGridAdapter(getActivity(), tasteList);
        myGridAdapter.setmOnItemOlickListener(new MyGridAdapter.OnItemOlickListener() {
            @Override
            public void onItemClick(int position) {
                pos = position;
            }
        });
        recyclerView.setAdapter(myGridAdapter);
        AlertDialog.Builder builder = new AlertDialog
                .Builder(getActivity());
        builder.setTitle(name);
        builder.setView(view);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mainActivity = (MainActivity) getActivity();
                float sum = amountView.getAmount();
                //如果选择器的数量不为零，当前的选择的菜品加入订单列表
                if (sum != 0) {

                    GoodsC goodsC = new GoodsC();
                    goodsC.setDishesName(name);
                    if (tasteList.size() == 0) {
                        goodsC.setDishesTaste(null);
                    } else {
                        goodsC.setDishesTaste(tasteList.get(pos));
                    }
                    goodsC.setDishesCount(sum);
                    String all = MyBigDecimal.mul(sum+"",price+"",2);
                    goodsC.setAllPrice(Float.parseFloat(all));
                    goodsC.setDishesId(dishesC.get_id());
                    if (dishesC.getDishesKindId()!=null) {
                        DishesKindC dishesKindC = CDBHelper.getObjById(getActivity().getApplicationContext(), dishesC.getDishesKindId(), DishesKindC.class);
                        goodsC.setDishesKindName(dishesKindC.getKindName());
                        Log.e("dishesKindName", dishesKindC.getKindName());
                    }
                    mainActivity.getGoodsList().add(goodsC);
                    //购物车计数器数据更新
                    point = (((MainActivity) getActivity()).getPoint());
                    point++;
                    ((MainActivity) getActivity()).setPoint(point);

                    //计算总价
                    total = ((MainActivity) getActivity()).getTotal();
                    total += l[0];
                    ((MainActivity) getActivity()).setTotal(total);

                    //刷新订单数据源
                    ((MainActivity) getActivity()).getSeekT9Adapter().notifyDataSetChanged();

                } else {

                    Toast.makeText(getActivity(), "没有选择商品数量！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    // 查询方法
    public void search(final String search) {


        try {
            CDBHelper.db.inBatch(new TimerTask() {
                @Override
                public void run() {
                    if(search.length()<2)
                        return;

                    myGoodsList.clear();

                    List<DishesC> dishesCs = CDBHelper.getObjByWhere(getActivity().getApplicationContext()
                            , Expression.property("className").equalTo("DishesC")
                            .and(Expression.property("dishesNameCode9").like("%"+search + "%"))
                            , null, DishesC.class);
                    for (DishesC obj : dishesCs) {
                            GoodsC goodsObj = new GoodsC();
                            goodsObj.setDishesCount(0);
                            goodsObj.setDishesId(obj.get_id());
                            myGoodsList.add(goodsObj);
                    }
                    seekT9Adapter.setmData(myGoodsList);
                    seekT9Adapter.notifyDataSetChanged();
                }
            });
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }


//        List<Document> documentList=CDBHelper.getDocmentsByWhere((getActivity().getApplicationContext(), Expression.property("className").equalTo("DishesC")
//                .and(Expression.property("dishesNameCode9").like(search+"%")),null,DishesC.class);


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


                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                View view1 = View
                        .inflate(getActivity(), R.layout.custom_dc_dialog, null);//设置弹窗布局
                alert.setView(view1);
                alert.setCancelable(true);
                final EditText cm = view1.findViewById(R.id.custom_dc_c);//菜名
                final EditText jg = view1.findViewById(R.id.custom_dc_t);//价格
                //取消或确定按钮监听事件处理
                final AlertDialog dialog = alert.create();
                Button btn_cancel = view1
                        .findViewById(R.id.custom_dc_qx);//取消按钮
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });


                Button btn_comfirm = view1
                        .findViewById(R.id.custom_dc_qd);//确定按钮

                btn_comfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        GoodsC goods = new GoodsC(myapp.getCompany_ID());
                        if (!cm.getText().toString().equals("")&&cm.getText() != null){
                            goods.setDishesName(cm.getText().toString());
                            if (!jg.getText().toString().equals("")&&jg.getText() != null){
                                goods.setAllPrice(Float.parseFloat(jg.getText().toString()));
                                goods.setDishesCount(1);
                                //sgoods.setDishesId();
                                ((MainActivity)getActivity()).getGoodsList().add(goods);
                                //购物车计数器数据更新
                                point = (((MainActivity) getActivity()).getPoint());
                                point++;
                                ((MainActivity) getActivity()).setPoint(point);

                                //计算总价
                                total = ((MainActivity) getActivity()).getTotal();
                                total += Float.parseFloat(jg.getText().toString());
                                ((MainActivity) getActivity()).setTotal(total);


                                dialog.dismiss();
                            }else{
                                Toast.makeText(getActivity(),"价格为空",Toast.LENGTH_LONG).show();
                            }

                        }else{
                            Toast.makeText(getActivity(),"菜名为空",Toast.LENGTH_LONG).show();
                        }

                    }
                });
                dialog.show();

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
                    search("oo");
                    activitySeekEdit.getText().delete(length - 1, length);
                }

                break;

            default:
                break;
        }
    }

}
