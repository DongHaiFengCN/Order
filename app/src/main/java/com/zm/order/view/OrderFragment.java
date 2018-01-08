package com.zm.order.view;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Ordering;
import com.zm.order.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.kitchenmanage.dishes.DishesC;
import bean.kitchenmanage.dishes.DishesKindC;
import bean.kitchenmanage.order.GoodsC;
import butterknife.BindView;
import butterknife.ButterKnife;
import model.CDBHelper;
import model.DishesMessage;

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

    private List<String> dishesIdList;
    //缓存disheskind 与 对应菜品数量的number集合

    private Map<String, float[]> dishesCollection = new HashMap<>();
    private Map<String, List<Document>> dishesObjectCollection;
    private boolean[] booleans;
    //*************************
    List<DishesKindC> dishesKindCList;

    String kindId;

    List<GoodsC> goodsCList;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getDefault().register(this);
        intiData1();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frame_order, null);
        ButterKnife.bind(this, view);
        return view;
    }

    private void intiData1() {


        //获取初始化数据
        dishesKindCList = ((MainActivity) getActivity()).getMyApp().getDishesKindCList();

        booleans = new boolean[dishesKindCList.size()];

        dishesObjectCollection = ((MainActivity) getActivity()).getMyApp().getDishesObjectCollection();


        //初始化数量
        for (Map.Entry<String, List<Document>> entry : dishesObjectCollection.entrySet()) {

            dishesCollection.put(entry.getKey(), new float[entry.getValue().size()]);

        }


        leftAdapter = new DishesKindAdapter();

        leftAdapter.setaBoolean(booleans);

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


            }
        });
        dishesRv.setAdapter(orderDragAdapter);

        dishesRv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Document document = (Document) orderDragAdapter.getItem(position);

                DishesC dishesC = CDBHelper.getObjById(getActivity().getApplication(), document.getId()
                        , DishesC.class);

                showDialog(dishesC, position, orderDragAdapter.getNumbers()[position]);
            }
        });
        orderList.performItemClick(orderList.getChildAt(0), 0, orderList
                .getItemIdAtPosition(0));

        orderDragAdapter.setChangerNumbersListener(new OrderDragAdapter.ChangerNumbersListener() {
            @Override
            public void getNumber(float[] numbers) {

                //更新缓存数据
                myNotifyDataSetChanged();

            }
        });

        orderDragAdapter.setTouchListener(new OrderDragAdapter.SubtractionTouchListener() {
            @Override
            public void setSubtractionTouchListener(String id) {

                List<String> stringList = new ArrayList<>();

                for (int i = 0; i < goodsCList.size(); i++) {

                    if(id.equals(goodsCList.get(i).getDishesId())){

                        stringList.add(goodsCList.get(i).getDishesTaste());

                    }

                }
                orderDragAdapter.setList(stringList);
            }

        });

        myNotifyDataSetChanged();
    }

    /**
     * 刷新所有展示数据
     */
    private void myNotifyDataSetChanged() {

        //获取order数据
        goodsCList = ((MainActivity) getActivity()).getGoodsList();


        //如果有数据，数值复制给dishesCollection
        if (!goodsCList.isEmpty()) {

            //遍历已存的goodsList
            for (GoodsC goodsC : goodsCList) {

                //依次获取每个Goodc对应的映射表包含的dishe集合
                List<Document> dishesCList = dishesObjectCollection.get(goodsC.getDishesKindId());

                //获取缓存的数量对应数组
                float[] floats = dishesCollection.get(goodsC.getDishesKindId());


                //遍历disheList 得到所在映射表的位置
                for (int i = 0; i < dishesCList.size(); i++) {

                    //找到对应的位置
                    if (dishesCList.get(i).getString("dishesName").equals(goodsC.getDishesName())) {


                        //数值不相等更新数值
                        if (floats[i] != goodsC.getDishesCount()) {

                            floats[i] = goodsC.getDishesCount();

                            //保存
                            dishesCollection.put(goodsC.getDishesKindId(), floats);


                            break;
                        }
                    }
                }
            }


        } else {
            //重置所有标记

            for (int i = 0; i < booleans.length; i++) {

                booleans[i] = false;

            }

            //重置数量

            //初始化数量
            for (Map.Entry<String, float[]> entry : dishesCollection.entrySet()) {

                float[] floats = dishesCollection.get(entry.getKey());

                for (int i = 0; i < floats.length; i++) {

                    floats[i] = 0f;
                }

            }

        }

        markDishesKindFlag();
        leftAdapter.notifyDataSetChanged();
        orderDragAdapter.notifyDataSetChanged();


    }

    private void markDishesKindFlag() {


        //更新DishesKind 标记
        for (int j = 0; j < dishesKindCList.size(); j++) {


            String id = dishesKindCList.get(j).get_id();


            float[] floats = dishesCollection.get(id);


            float count = 0f;

            for (float f : floats) {

                count += f;

            }

            if (count == 0f && booleans[j]) {

                booleans[j] = false;

            } else if (count > 0f && !booleans[j]) {

                booleans[j] = true;
            }

        }

    }


    /**
     * 菜品选择弹出框编辑模块
     */
    private void showDialog(final DishesC dishesC, final int position, float number) {


        view = LayoutInflater.from(getActivity()).inflate(R.layout.view_item_dialog, null);

        final TextView price_tv = view.findViewById(R.id.price);

        final AmountView amountView = view.findViewById(R.id.amount_view);

        amountView.setNumber(number + "");

        String all = MyBigDecimal.mul(amountView.getAmount() + "", dishesC.getPrice() + "", 2);

        price_tv.setText("总计 " + all + " 元");


        final DishesMessage dishesMessage = new DishesMessage();
        dishesMessage.setDishKindId(dishesC.getDishesKindId());
        dishesMessage.setOperation(true);
        dishesMessage.setSingle(false);
        final float[] l = new float[1];


        l[0] = Float.parseFloat(all);


        //增删选择器的数据改变的监听方法

        amountView.setChangeListener(new AmountView.ChangeListener() {
            @Override
            public void OnChange(float ls, boolean flag) {

                String all = MyBigDecimal.mul(ls + "", dishesC.getPrice() + "", 2);
                l[0] = Float.parseFloat(all);


                price_tv.setText("总计 " + l[0] + " 元");

            }
        });

        dishesMessage.setName(dishesC.getDishesName());

        dishesMessage.setDishesC(dishesC);


        AlertDialog.Builder builder = new AlertDialog
                .Builder(getActivity());
        builder.setTitle(dishesC.getDishesName());
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

                    myNotifyDataSetChanged();

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


            myNotifyDataSetChanged();

        }
    }

    public class DishesKindAdapter extends BaseAdapter {


        private int mSelect = 0; //选中项


        public void setaBoolean(boolean[] aBoolean) {
            this.aBoolean = aBoolean;
        }

        boolean aBoolean[];

        public void setNames(List<DishesKindC> names) {
            this.names = names;
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

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void setOrderList(String s) {

        myNotifyDataSetChanged();

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
