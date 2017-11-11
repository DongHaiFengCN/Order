package com.zm.order.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Array;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Log;
import com.zm.order.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;

import application.MyApplication;
import bean.kitchenmanage.order.GoodsC;
import bean.kitchenmanage.order.OrderC;
import bean.kitchenmanage.table.TableC;
import butterknife.BindView;
import butterknife.ButterKnife;
import model.CDBHelper;
import model.DBFactory;
import model.DatabaseSource;
import model.IDBManager;
import presenter.IMainPresenter;
import presenter.MainPresenterImpl;
import untils.AnimationUtil;
import untils.MyLog;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.activity_frame)
    FrameLayout activityFrame;
    private MyApplication myApp;
    private ListView order_lv;
    private TextView ok_tv;
    private TextView total_tv;
    private ImageView car_iv;
    private boolean flag = true;
    private ImageButton delet_bt;
    public  List<SparseArray<Object>> orderItem = new ArrayList<>();
    public  OrderAdapter o;
    private int point = 0;
    private TextView point_tv;
    private float total = 0.0f;
    private Fragment seekT9Fragment;
    private Fragment orderFragment;
    private SeekT9Adapter seekT9Adapter;
    private FragmentManager fm;//获得Fragment管理器
    private FragmentTransaction ft; //开启一个事务
    private boolean isFlag = true;
    private OrderAdapter orderAdapter;
    private IDBManager idbManager;
    private List<Document> orderList;
    private TableC tableC;
    List<HashMap> orderDishesList = new ArrayList<>();
    private List<Document> promotionCList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        //关键下面两句话，设置了回退按钮，及点击事件的效果
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        idbManager = DBFactory.get(DatabaseSource.CouchBase, getApplicationContext());
        myApp = (MyApplication) getApplication();
        tableC = myApp.getTable_sel_obj();


        initView();

        select(isFlag);
    }

    public void setOrderItem(SparseArray<Object> sparseArray){
        orderItem.add(sparseArray);
    }

    public void setT9Adapter(SeekT9Adapter seekT9Adapter){
        this.seekT9Adapter = seekT9Adapter;
    }

    public SeekT9Adapter getSeekT9Adapter(){
        return seekT9Adapter;
    }


    public void setOrderAdapter(OrderAdapter orderAdapter){
        this.orderAdapter = orderAdapter;
    }

    public OrderAdapter getOrderAdapter(){
        return orderAdapter;
    }

    public List<SparseArray<Object>> getOrderItem(){
        return orderItem;
    }
    public void setTotal(float total){
        this.total = total;
        total_tv.setText(total + "元");
    }

    public float getTotal(){
        return total;
    }
    public void setPoint(int point){
        this.point = point;
        if (point > 0){
            point_tv.setText(point + "");
            point_tv.setVisibility(View.VISIBLE);
        }else{
            point_tv.setVisibility(View.GONE);
        }

    }
    public int getPoint(){
        return point;
    }
    public void initView() {

        total_tv = (TextView) findViewById(R.id.total_tv);

        point_tv = (TextView) findViewById(R.id.point);

        car_iv = (ImageView) findViewById(R.id.car);

        ok_tv = (TextView) findViewById(R.id.ok_tv);

        final ImageView imageView = (ImageView) findViewById(R.id.shade);

        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.orderList);


        //获取屏幕尺寸

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int w = dm.widthPixels;
        int h = dm.heightPixels;

        //设置表单的容器的长度为视窗的一半高，由父类的节点获得

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) linearLayout
                .getLayoutParams();
        layoutParams.width = w;
        layoutParams.height = h / 2;
        linearLayout.setLayoutParams(layoutParams);
        o = new OrderAdapter( getOrderItem(), MainActivity.this);
        car_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //初始化订单的数据，绑定数据源的信息。
                o.notifyDataSetChanged();
                Iterator<SparseArray<Object>> iterator = getOrderItem().iterator();

                while (iterator.hasNext()){
                    SparseArray<Object> sparseArray = iterator.next();
                    if (sparseArray.get(2).toString().equals("0")){
                        iterator.remove();

                        break;
                    }

                }
                for (int i = 0;0<orderItem.size();i++){
                    if (orderItem.get(i).get(2).toString().equals("0")){
                        orderItem.remove(i);
                    }
                    break;
                }

                o.notifyDataSetChanged();
                order_lv = (ListView) findViewById(R.id.order_lv);
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
        });




        //清空按钮
        delet_bt = (ImageButton) findViewById(R.id.delet);

        delet_bt.setOnClickListener(new View.OnClickListener() {
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


                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    View view1 = getLayoutInflater().inflate(R.layout.view_pay_dialog,null);
                    builder.setView(view1);
                    builder.setCancelable(true);
                    final AlertDialog dialog = builder.create();
                    Button shi = view1.findViewById(R.id.view_pay_shi);
                    Button fou = view1.findViewById(R.id.view_pay_fou);
                    shi.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            saveOrder();

                            Intent intent = new Intent(MainActivity.this, PayActivity.class);
                            startActivityForResult(intent,1);
                            dialog.dismiss();
                        }
                    });
                    fou.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            saveOrder();

                            Intent intent = new Intent(MainActivity.this, DeskActivity.class);
                            startActivity(intent);
                            dialog.dismiss();
                            finish();
                        }
                    });
                    dialog.show();



                } else {

                    Toast.makeText(MainActivity.this, "订单为空！", Toast.LENGTH_SHORT).show();
                }


            }


        });
    }

    /**
     * 清空订单列表
     */

    private void clearOrder() {

        point = 0;
        point_tv.setVisibility(View.INVISIBLE);

        total_tv.setText("0元");
        total = 0;

        getOrderItem().clear();
        o.notifyDataSetChanged();
    }


    private void saveOrder(){
        try {
            CDBHelper.db.inBatch(new TimerTask() {
                @Override
                public void run() {
                    OrderC orderC = new OrderC(myApp.getCompany_ID());
                    for (int i = 0 ; i< getOrderItem().size();i++){

                        GoodsC goodsC = new GoodsC();
                        goodsC.setDishesName(getOrderItem().get(i).get(0).toString());
                        if (getOrderItem().get(i).get(1) == null){
                            goodsC.setDishesTaste(null);
                        }else {
                            goodsC.setDishesTaste(getOrderItem().get(i).get(1).toString());
                        }
                        goodsC.setDishesCount(Integer.parseInt(getOrderItem().get(i).get(2).toString()));
                        goodsC.setAllPrice(Float.parseFloat(getOrderItem().get(i).get(4).toString()));
                        CDBHelper.createAndUpdate(getApplicationContext(),goodsC);
                        orderC.addGoods(goodsC);
                    }
                    orderC.setAllPrice(total);
                    orderC.setOrderState(1);
                    orderC.setOrderType(1);
                    orderC.setTableNo(myApp.getTable_sel_obj().getTableNum());
                    CDBHelper.createAndUpdate(getApplicationContext(),orderC);

                }
            });
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }



    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK && requestCode == 1) {

            clearOrder();
            getSeekT9Adapter().notifyDataSetChanged();
            Log.e("Aaaa", getOrderItem().size()+"");

            //获取包含桌号xx的所有订单
          //  orderList = idbManager.getOrderListBelongToTable("tableNo", tableC.getTableNum());

            promotionCList = idbManager.getByClassName("PromotionC");

            Iterator<Document> i = orderList.iterator();
            orderDishesList.clear();
            while (i.hasNext()) {

                Document order = i.next();

                //当前桌下没有买单的订单的总价
                if (order.getInt("orderState") == 1) {

                    total += order.getFloat("allPrice");
                    Array a = order.getArray("goodsList");

                    List<Object> l = a.toList();

                    //获取当前订单下goods集合下所有的菜品

                    for (Object o : l) {

                        HashMap h = (HashMap) o;

                        orderDishesList.add(h);

                        MyLog.e("菜品：  " + h.get("dishesName").toString());

                    }

                }

            }
            Log.e("Aaaa", getOrderItem().size()+"");

            Log.e("Aaaa", orderDishesList.size()+"orderDishesList.size()");

            for (int a = 0 ; a < orderDishesList.size();a++){

                SparseArray<Object> sparseArray = new SparseArray<>();
                sparseArray.put(0,orderDishesList.get(a).get("dishesName").toString());
                if(orderDishesList.get(a).get("dishesTaste") == null){
                    sparseArray.put(1,null);
                }else{
                    sparseArray.put(1,orderDishesList.get(a).get("dishesTaste").toString());
                }

                sparseArray.put(2,orderDishesList.get(a).get("dishesCount").toString());
                sparseArray.put(3,(Float.parseFloat((orderDishesList.get(a).get("allPrice").toString()))/Float.parseFloat(orderDishesList.get(a).get("dishesCount").toString())));
                sparseArray.put(4,(Float.parseFloat((orderDishesList.get(a).get("allPrice").toString()))));
                getOrderItem().add(sparseArray);

            }
            setTotal(total);
            setPoint(getOrderItem().size());
            o.notifyDataSetChanged();
            Log.e("Aaaa", getOrderItem().size()+"");

            List<Document> doc = CDBHelper.getDocmentsByClassName(getApplicationContext(),"OrderC");
            for (Document d : doc){
                CDBHelper.deleDocument(getApplicationContext(),d);
            }
            



        }

    }




    //隐藏所有Fragment
    private void hidtFragment(FragmentTransaction fragmentTransaction){


        if (seekT9Fragment != null){
            fragmentTransaction.hide(seekT9Fragment);
        }
        if (orderFragment != null){
            fragmentTransaction.hide(orderFragment);
        }
    }

    private void select(boolean isTrue) {
        fm = getFragmentManager();
        ft = fm.beginTransaction();
        hidtFragment(ft);
        if(isTrue == true){
            if (seekT9Fragment == null){
                seekT9Fragment = new SeekT9Fragment();
                ft.add(R.id.activity_frame,seekT9Fragment);

            }else{
                ft.show(seekT9Fragment);;
            }
            isFlag = false;
        }else if (isTrue == false){
            if (orderFragment == null){
                orderFragment = new OrderFragment();
                ft.add(R.id.activity_frame,orderFragment);
            }else{
                ft.show(orderFragment);
            }
            isFlag = true;
        }
        ft.commit();

    }
    /**
     * 模拟原始数据
     *
     * @return
     */


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            select(isFlag);

        } /*else if (id == R.id.action_cancel) {

            myApp.cancleSharePreferences();
            Intent itent = new Intent();
            itent.setClass(MainActivity.this, LoginActivity.class);
            startActivity(itent);
            finish();

        }*/

        return super.onOptionsItemSelected(item);
    }



}
