package com.zm.order.view;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Ordering;
import com.zm.order.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;


import bean.kitchenmanage.dishes.DishesC;
import bean.kitchenmanage.dishes.DishesKindC;
import bean.kitchenmanage.order.GoodsC;
import butterknife.BindView;
import butterknife.ButterKnife;
import model.CDBHelper;
import model.DishesMessage;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by lenovo on 2017/10/26.
 */

public class OrderFragment extends Fragment {

    @BindView(R.id.dishes_rv)
    ListView dishesRv;
    @BindView(R.id.order_list)
    ListView orderList;
    private DishesKindAdapter leftAdapter;
    private OrderDragAdapter orderDragAdapter;
    private View view;

    private List<String> titleList = new ArrayList<>();
    List<Object> DishesIdList;
    private List<String> dishesIdList;


    //缓存disheskind 与 对应菜品数量的number集合

    private Map<String, float[]> dishesCollection = new HashMap<>();

    private Map<String, List<DishesC>> dishesObjectCollection = new HashMap<>();

    //*************************
    List<DishesKindC> dishesKindCList;

    String kindId;

    List<GoodsC> goodsCList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frame_order, null);
        ButterKnife.bind(this, view);
        intiData1();
        //initData();
        return view;
    }

    private void intiData1() {

        dishesKindCList = CDBHelper.getObjByWhere(getActivity().getApplication()
                , Expression.property("className").equalTo("DishesKindC")
                        .and(Expression.property("isSetMenu").equalTo(false)), Ordering.property("kindName")
                        .ascending(), DishesKindC.class);


        //初始化菜品数量维护映射表
        for (DishesKindC dishesKindC : dishesKindCList) {

            int count = dishesKindC.getDishesListId().size();

            List<String> disheList = dishesKindC.getDishesListId();

            List<DishesC> dishesCS = new ArrayList<>();

            for (int i = 0; i < count; i++) {


                DishesC dishesC = CDBHelper.getObjById(getActivity().getApplication(), disheList.get(i), DishesC.class);
                if (dishesC != null) {

                    dishesCS.add(dishesC);
                }


            }

            //初始化disheKind对应的dishes实体类映射
            dishesObjectCollection.put(dishesKindC.get_id(), dishesCS);

            //初始化dishekind对应的dishes的数量映射
            dishesCollection.put(dishesKindC.get_id(), new float[dishesCS.size()]);

            //获取order数据
            goodsCList = ((MainActivity) getActivity()).getGoodsList();

            //如果有数据，数值复制给dishesCollection
            if (!goodsCList.isEmpty()) {


                for (GoodsC goodsC : goodsCList) {




                }

            }


        }

        leftAdapter = new DishesKindAdapter();

        leftAdapter.setNames(dishesKindCList);

        orderList.setAdapter(leftAdapter);

        orderDragAdapter = new OrderDragAdapter(getActivity());


        orderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                leftAdapter.changeSelected(position);

                kindId = dishesKindCList.get(position).get_id();

                orderDragAdapter.setMessage(dishesObjectCollection.get(kindId)
                        , dishesCollection.get(kindId));
                dishesRv.setAdapter(orderDragAdapter);
            }
        });

        orderList.performItemClick(orderList.getChildAt(0), 0, orderList
                .getItemIdAtPosition(0));

        orderDragAdapter.setChangerNumbersListener(new OrderDragAdapter.ChangerNumbersListener() {
            @Override
            public void getNumber(float[] numbers) {

                //更新缓存数据
                dishesCollection.put(kindId, numbers);


            }
        });





     /*   Observable.create(new Observable.OnSubscribe<DishesKindC>() {


            @Override
            public void call(Subscriber<? super DishesKindC> subscriber) {

                dishesKindCList = CDBHelper.getObjByWhere(getActivity().getApplication()
                        , Expression.property("className").equalTo("DishesKindC")
                                .and(Expression.property("isSetMenu").equalTo(false)), Ordering.property("kindName")
                                .ascending(), DishesKindC.class);


                for (int i = 0; i < dishesKindCList.size(); i++) {

                    subscriber.onNext(dishesKindCList.get(i));
                    titleList.add(dishesKindCList.get(i).getKindName());

                }
                subscriber.onCompleted();

            }
        }).observeOn(Schedulers.newThread()).toMap(new Func1<DishesKindC, String>() {

            @Override
            public String call(DishesKindC dishesKindC) {

                return dishesKindC.getKindName();
            }
        }).subscribe(new Action1<Map<String, DishesKindC>>() {

            @Override
            public void call(Map<String, DishesKindC> stringDishesKindCMap) {

              //  stringDishesKindCMap1 = stringDishesKindCMap;

                ArrayAdapter<String> adapter = new ArrayAdapter(getActivity().getApplication(),android.R.layout.simple_expandable_list_item_1,titleList);

                LayoutInflater inflater = (LayoutInflater) getActivity().getApplication().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v1 = inflater.inflate(android.R.layout.simple_expandable_list_item_1, null);


                Iterator<Map.Entry<String, DishesKindC>> entries = stringDishesKindCMap.entrySet().iterator();

                orderList.setAdapter(adapter);

          *//*      while (entries.hasNext()) {

                    Map.Entry<String, DishesKindC> entry = entries.next();

                    Log.e("测试一下：","Key = " + entry.getKey() + ", Value = " +entry.getValue().get_id());

                }*//*

            }
        });*/


    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void initData() {

        leftAdapter = new DishesKindAdapter();
       /* titleList = CDBHelper.getIdsByWhere(getActivity(),
                Expression.property("className").equalTo("DishesKindC")
                        .and(Expression.property("isSetMenu").equalTo(false)),
                Ordering.property("kindName").ascending());*/


        // leftAdapter.setNames(titleList);

        orderList.setAdapter(leftAdapter);
      /*  //左侧点击事件监听
        orderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (DishesIdList != null) {
                    DishesIdList.clear();
                }

                leftAdapter.changeSelected(position);

                try {
                    CDBHelper.db.inBatch(new TimerTask() {
                        @Override
                        public void run() {
                            dishesIdList = new ArrayList<>();
                            //获取点击的菜品类别的Document
                            Document KindDocument = CDBHelper.getDocByID(getActivity(), titleList.get(position));
                            //获取此Document下的菜品Id号
                            if (KindDocument.getArray("dishesListId") != null) {
                                DishesIdList = KindDocument.getArray("dishesListId").toList();
                                //增强for循环读取id
                                for (Object DishesId : DishesIdList) {
                                    if (DishesId == null) {
                                        continue;
                                    }

                                    dishesIdList.add(DishesId.toString());
                                }
                            }
                            if (orderDragAdapter == null) {
                                orderDragAdapter = new OrderDragAdapter(getActivity());
                            }

                            orderDragAdapter.setMlistDishesId(dishesIdList);

                            if (stringHashMap.get(position) != null) {

                                orderDragAdapter.setNumbers(stringHashMap.get(position));


                            }


                            orderDragAdapter.setChangerNumbersListener(new OrderDragAdapter.ChangerNumbersListener() {
                                @Override
                                public void getNumber(float[] numbers) {

                                    stringHashMap.put(position, numbers);


                                    //如果有被选择的菜品
                                    leftAdapter.getaBoolean()[position] = isKindNameClick(numbers);
                                    leftAdapter.notifyDataSetChanged();


                                }
                            });

                            dishesRv.setAdapter(orderDragAdapter);

                            dishesRv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                    DishesC dishesC = CDBHelper.getObjById(getActivity().getApplication(), dishesIdList.get(i), DishesC.class);
                                    showDialog(dishesC.getDishesName(), dishesC.getPrice(), i);

                                }
                            });

                        }
                    });
                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }

            }


        });*/

        orderList.performItemClick(orderList.getChildAt(0), 0, orderList
                .getItemIdAtPosition(0));


    }

    private boolean isKindNameClick(float[] numbers) {
        for (int i = 0; i < numbers.length; i++) {

            if (numbers[i] != 0.0f) {

                return true;
            }
        }
        return false;
    }


    /**
     * 菜品选择弹出框编辑模块
     *
     * @param name  传入的菜品的名称
     * @param price 传入的菜品的价格
     */
    private void showDialog(final String name, final float price, final int position) {

        view = LayoutInflater.from(getActivity()).inflate(R.layout.view_item_dialog, null);

        final TextView price_tv = view.findViewById(R.id.price);

        final AmountView amountView = view.findViewById(R.id.amount_view);

        String all = MyBigDecimal.mul(amountView.getAmount() + "", price + "", 2);

        price_tv.setText("总计 " + Float.parseFloat(all) + " 元");


        final DishesMessage dishesMessage = new DishesMessage();

        dishesMessage.setOperation(true);
        dishesMessage.setSingle(false);
        final float[] l = new float[1];


        l[0] = Float.parseFloat(all);

        dishesMessage.setTotal(l[0]);


        //增删选择器的数据改变的监听方法

        amountView.setChangeListener(new AmountView.ChangeListener() {
            @Override
            public void OnChange(float ls, boolean flag) {

                String all = MyBigDecimal.mul(ls + "", price + "", 2);
                l[0] = Float.parseFloat(all);//实时计算当前菜品选择不同数量后的单品总价

                dishesMessage.setTotal(l[0]);

                price_tv.setText("总计 " + l[0] + " 元");

            }
        });

        DishesC dishesC = CDBHelper.getObjById(getActivity().getApplicationContext(), dishesIdList.get(position).toString(), DishesC.class);

        dishesMessage.setName(dishesC.getDishesName());

        dishesMessage.setDishesC(dishesC);


        AlertDialog.Builder builder = new AlertDialog
                .Builder(getActivity());
        builder.setTitle(name);
        builder.setView(view);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", null);

        final AlertDialog alertDialog = builder.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (amountView.getAmount() > 0f) {

                    orderDragAdapter.updata(position, amountView.getAmount());

                    dishesMessage.setCount(amountView.getAmount());

                    EventBus.getDefault().postSticky(dishesMessage);

                    alertDialog.dismiss();

                } else {

                    Toast.makeText(getActivity(), "数量必须大于0！", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {


        }
    }

    public class DishesKindAdapter extends BaseAdapter {


        private int mSelect = 0; //选中项

        public void setaBoolean(boolean[] aBoolean) {
            this.aBoolean = aBoolean;
        }

        public boolean[] getaBoolean() {
            return aBoolean;
        }

        boolean aBoolean[];

        public void setNames(List<DishesKindC> names) {
            this.names = names;
            aBoolean = new boolean[names.size()];
        }


        private List<DishesKindC> names;

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

            LayoutInflater listContainerLeft;
            listContainerLeft = LayoutInflater.from(getActivity());
            ListItemView listItemView = null;
            if (view == null) {
                listItemView = new ListItemView();
                view = listContainerLeft.inflate(R.layout.view_kindname_lv, null);
                listItemView.tv_title = view.findViewById(R.id.title);
                listItemView.imageView = view.findViewById(R.id.imageView);
                listItemView.imagePoint = view.findViewById(R.id.imagePoint);
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

            if (aBoolean[i]) {

                listItemView.imagePoint.setVisibility(View.VISIBLE);
            } else {

                listItemView.imagePoint.setVisibility(View.INVISIBLE);
            }
            listItemView.tv_title.setText(names.get(i).getKindName());

            return view;

        }


        public void changeSelected(int positon) { //刷新方法
            mSelect = positon;
            notifyDataSetChanged();
        }

        class ListItemView {

            TextView tv_title;
            ImageView imageView;
            ImageView imagePoint;
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
