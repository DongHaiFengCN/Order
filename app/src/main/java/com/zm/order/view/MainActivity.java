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
import com.couchbase.lite.Document;
import com.couchbase.lite.Log;
import com.zm.order.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import application.MyApplication;
import bean.kitchenmanage.order.GoodsC;
import bean.kitchenmanage.order.OrderC;
import butterknife.BindView;
import butterknife.ButterKnife;
import model.CDBHelper;
import presenter.IMainPresenter;
import presenter.MainPresenterImpl;
import untils.AnimationUtil;

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

        myApp = (MyApplication) getApplication();


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


               OrderC orderC = new OrderC();
               for (int i = 0 ; i< getOrderItem().size();i++){

                   GoodsC goodsC = new GoodsC();
                   goodsC.setDishesName(getOrderItem().get(i).get(0).toString());
                   if (getOrderItem().get(i).get(1) == null){
                       goodsC.setDishesTaste(null);
                   }else {
                       goodsC.setDishesTaste(getOrderItem().get(i).get(1).toString());
                   }
                   goodsC.setDishesCount(Integer.parseInt(getOrderItem().get(i).get(2).toString()));
                   Log.e("Aaaa",getOrderItem().get(i).get(1)+"");
                   goodsC.setAllPrice((Float) getOrderItem().get(i).get(4));
                   goodsC.setChannelId(myApp.getCompany_ID());
                   goodsC.setClassName("GoodsC");
                   CDBHelper.createAndUpdate(getApplicationContext(),goodsC);
                   orderC.addGoods(goodsC);
               }
               orderC.setAllPrice(total);
               orderC.setOrderState(1);
               orderC.setOrderType(1);
               orderC.setTableNo(myApp.getTable_sel_obj().getTableNum());
               orderC.setChannelId(myApp.getCompany_ID());
               orderC.setClassName("OrderC");
               CDBHelper.createAndUpdate(getApplicationContext(),orderC);


                Intent intent = new Intent(MainActivity.this, PayActivity.class);
                startActivity(intent);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {

            clearOrder();


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
