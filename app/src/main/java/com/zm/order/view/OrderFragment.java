package com.zm.order.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Ordering;
import com.zm.order.R;
import com.zm.order.view.adapter.MyGridAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import bean.DishesKind;
import bean.Goods;
import bean.kitchenmanage.order.GoodsC;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import model.CDBHelper;
import model.DBFactory;
import model.DatabaseSource;
import model.IDBManager;
import presenter.IMainPresenter;
import presenter.MainPresenterImpl;
import untils.AnimationUtil;

/**
 * Created by lenovo on 2017/10/26.
 */

public class OrderFragment extends Fragment implements IMainView {

    @BindView(R.id.dishes_rv)
    ListView dishesRv;
    @BindView(R.id.order_list)
    ListView orderList;
    private DishesKindAdapter leftAdapter;
    private OrderDragAdapter orderDragAdapter;
    private List<Object> getDishesIdList;
    private List<String> tasteList;
    private View view;
    private String taste = "默认";
    private List<SparseArray<Object>> orderItem = new ArrayList<>();
    private int point = 0;
    private float total = 0.0f;
    private List<String> titleList = new ArrayList<>();
    List<Object> DishesIdList;
    private List<Goods> myGoodsList;
    private List<String> dishesIdList;
    private int pos;
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frame_order,null);
        ButterKnife.bind(this, view);
        myGoodsList = new ArrayList<>();
        //initData();
        initView();
        IMainPresenter iMainView = new MainPresenterImpl(this);
        iMainView.init();


        return view;

    }

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public void initView() {


       // final GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);//设置每行展示3个
        //dishesAdapter = new DishesAdapter(getActivity());

        tasteList = new ArrayList<>();

        leftAdapter = new DishesKindAdapter();
        titleList = CDBHelper.getIdsByWhere(getActivity(),
                Expression.property("className").equalTo("DishesKindC")
                        .and(Expression.property("isSetMenu").equalTo(false)),
                Ordering.property("kindName").ascending());
        leftAdapter.setNames(titleList);
        orderList.setAdapter(leftAdapter);
        //左侧点击事件监听
        orderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if(DishesIdList!=null)
                    DishesIdList.clear();
                leftAdapter.changeSelected(position);

                try {
                    CDBHelper.db.inBatch(new TimerTask() {
                        @Override
                        public void run() {
                            dishesIdList=new ArrayList<>();
                            //获取点击的菜品类别的Document
                            Document KindDocument= CDBHelper.getDocByID(getActivity(),titleList.get(position));
                            //获取此Document下的菜品Id号
                            if(KindDocument.getArray("dishesListId")!=null)
                            {
                                DishesIdList= KindDocument.getArray("dishesListId").toList();
                                //增强for循环读取id
                                for(Object DishesId:DishesIdList)
                                {
                                    if(DishesId==null)
                                        continue;
                                    dishesIdList.add(DishesId.toString());
                                }
                            }
                            orderDragAdapter = new OrderDragAdapter(getActivity(),KindDocument);

                            orderDragAdapter.setMlistDishesId(dishesIdList);

                            dishesRv.setAdapter(orderDragAdapter);
                            orderDragAdapter.setOnItemClickListener(new OrderDragAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(String name, float price,int position) {
                                    showDialog(name,price,position);
                                }
                            });
                        }
                    });
                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }

            }



        });

        orderList.performItemClick(orderList.getChildAt(0), 0, orderList
                .getItemIdAtPosition(0));





    }

    @Override
    public void showDishes(List<String> data, List<Integer> headPosition) {
//        dishesAdapter.setmData(data);
//        dishesAdapter.setHeadPosition(headPosition);
//        dishesAdapter.notifyDataSetChanged();

    }

    /**
     * 菜品选择弹出框编辑模块
     *
     * @param name  传入的菜品的名称
     * @param price 传入的菜品的价格
     */
    private void showDialog(final String name, final float price,int position) {

        final float[] l = {0.0f};
        getDishesIdList = new ArrayList<>();
        view = LayoutInflater.from(getActivity()).inflate(R.layout.view_item_dialog, null);

        final TextView price_tv = view.findViewById(R.id.price);

        final AmountView amountView = view.findViewById(R.id.amount_view);
        getDishesIdList.clear();
        tasteList.clear();
        price_tv.setText("总计 " +amountView.getAmount()*price+" 元");
        amountView.getAmount();
        l[0] = amountView.getAmount()*price;
        //增删选择器的数据改变的监听方法

        amountView.setChangeListener(new AmountView.ChangeListener() {
            @Override
            public void OnChange(int ls, boolean flag) {


                l[0] = ls * price;//实时计算当前菜品选择不同数量后的单品总价

                price_tv.setText("总计 " + l[0] + " 元");

            }
        });

        Document document = CDBHelper.getDocByID(getActivity().getApplicationContext(), dishesIdList.get(position).toString());
        if (document.getArray("tasteList") != null){
            getDishesIdList = document.getArray("tasteList").toList();
        }else{
            getDishesIdList = null;
        }

        if (getDishesIdList != null) {
            for (int a = 0; a < getDishesIdList.size(); a++) {
                Document document1 = CDBHelper.getDocByID(getActivity().getApplicationContext(), getDishesIdList.get(a).toString());
                tasteList.add(document1.getString("tasteName"));
            }

        }
        final GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);//设置每行展示3个
        final Document doc = CDBHelper.getDocByID(getActivity(),dishesIdList.get(position));
        RecyclerView recyclerView = view.findViewById(R.id.view_dialog_recycler);
        recyclerView.setLayoutManager(manager);
        MyGridAdapter myGridAdapter = new MyGridAdapter(getActivity(),tasteList);
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

                int sum = amountView.getAmount();

                if (sum != 0) {//如果选择器的数量不为零，当前的选择的菜品加入订单列表
                    GoodsC goodsC = new GoodsC();
                    goodsC.setDishesName(name);
                    if (tasteList.size() == 0){
                        goodsC.setDishesTaste(null);
                    }else{
                        goodsC.setDishesTaste(tasteList.get(pos));
                    }
                    goodsC.setDishesCount(sum);
                    goodsC.setAllPrice(sum * price);
                    goodsC.setDishesId(doc.getId());
                    DishesKind dishesKind  = CDBHelper.getObjById(getActivity().getApplicationContext(),doc.getString("dishesKindId"), DishesKind.class);
                    goodsC.setDishesKindName(dishesKind.getName());
                    Log.e("dishesKindName",dishesKind.getName());
                    ((MainActivity)getActivity()).getGoodsList().add(goodsC);
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
    public void showKindName(List<String> data) {


    }

    @Override
    public Context getIMainViewActivity() {
        return getActivity();
    }

    public class DishesKindAdapter extends BaseAdapter {

        private LayoutInflater listContainerLeft;
        private int mSelect = 0; //选中项
        public void setNames(List<String> names) {
            this.names = names;
        }

        private List<String> names;

        public DishesKindAdapter() {
        }


        @Override
        public int getCount() {
            return names == null ? 0 : names.size();
        }

        @Override
        public Object getItem(int i) {
            return names.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            listContainerLeft = LayoutInflater.from(getActivity());
            ListItemView listItemView = null;
            if (view == null) {
                listItemView = new ListItemView();
                view = listContainerLeft.inflate(R.layout.view_kindname_lv, null);
                listItemView.tv_title = view.findViewById(R.id.title);
                listItemView.imageView = view.findViewById(R.id.imageView);
                view.setTag(listItemView);
            } else {
                listItemView = (ListItemView) view.getTag();
            }
            if (mSelect == i) {
                view.setBackgroundResource(R.color.md_grey_50);  //选中项背景
                listItemView.imageView.setVisibility(View.VISIBLE);
            } else {
                view.setBackgroundResource(R.color.md_grey_100);  //其他项背景
                listItemView.imageView.setVisibility(View.INVISIBLE);
            }
            listItemView.tv_title.setText(CDBHelper.getDocByID(getActivity(),names.get(i)).getString("kindName"));

            return view;

        }


        public void changeSelected(int positon) { //刷新方法
            mSelect = positon;
            notifyDataSetChanged();
        }

        class ListItemView {

            TextView tv_title;
            ImageView imageView;
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
