package com.zm.order.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Expression;
import com.couchbase.lite.Ordering;
import com.zm.order.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import application.MyApplication;
import bean.kitchenmanage.dishes.DishesC;
import bean.kitchenmanage.dishes.DishesKindC;
import bean.kitchenmanage.order.GoodsC;
import bean.kitchenmanage.order.OrderC;
import bean.kitchenmanage.table.AreaC;
import bean.kitchenmanage.user.CompanyC;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.internal.Utils;
import model.CDBHelper;
import untils.BluetoothUtil;
import untils.PrintUtils;
import untils.Tool;

import static com.gprinter.service.AllService.TAG;

/**
 * Created by lenovo on 2017/12/13.
 */

public class ShowParticularsActivity extends Activity {

    @BindView(R.id.show_listView)
    ListView showListView;
    @BindView(R.id.show_but_dc)
    LinearLayout showButDc;
    @BindView(R.id.show_but_md)
    LinearLayout showButMd;
    @BindView(R.id.show_tv_sl)
    TextView showTvSl;
    @BindView(R.id.show_img)
    ImageView showImg;

    ShowParticularsAdapter adatper;
    private List<GoodsC> goodsCList;
    private List<GoodsC> goodsCListT;
    private MyApplication myapp;
    private float all = 0f;
    private ImageView getShowImg;

    private BluetoothAdapter btAdapter;
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private List<OrderC> orderCList;

    public static final String TAG = "ShowParticularsActivity";
    private  boolean printerToKitchen(GoodsC obj, int type , String areaName ,String TableName){

        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        ButterKnife.bind(this);
        myapp = (MyApplication) getApplication();
        goodsCList = new ArrayList<>();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setAll();
        adatper = new ShowParticularsAdapter(this);
        adatper.setGoodsCs(goodsCList);
        showListView.setAdapter(adatper);
        showListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {

                        //点击订单OrderC
                        OrderC order = CDBHelper.getObjById(getApplicationContext(), goodsCList.get(position).getOrder(), OrderC.class);
                        if (order.getOrderCType() == 0 && goodsCList.get(position).getGoodsType() != 2){
                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ShowParticularsActivity.this);
                            View view1 = LayoutInflater.from(ShowParticularsActivity.this).inflate(R.layout.activity_tv_dialog,null);
                            alertDialog.setView(view1);
                            TextView title = view1.findViewById(R.id.dialog_tuicai_title);
                            title.setText(goodsCList.get(position).getDishesName());
                            final EditText editText = view1.findViewById(R.id.dialog_ed_sl);
                            editText.setText(goodsCList.get(position).getDishesCount()+"");
                            editText.clearFocus();
                            editText.setFocusableInTouchMode(false);
                            editText.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View view, MotionEvent motionEvent) {
                                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                                        editText.setFocusableInTouchMode(true);
                                        editText.requestFocus();
                                        editText.setText(goodsCList.get(position).getDishesCount()+"");
                                        editText.selectAll();
                                    }
                                    return false;
                                }
                            });


                            final AlertDialog builder = alertDialog.create();
                            Button tc = view1.findViewById(R.id.dialog_tuicai);
                            tc.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (editText.getText().toString().equals("")){

                                        Toast.makeText(ShowParticularsActivity.this,"输入不得为空",Toast.LENGTH_LONG).show();

                                    }else {

                                        if (goodsCList.get(position).getDishesCount() >= Float.parseFloat(editText.getText().toString()))
                                        {


                                            GoodsC goodsC = goodsCList.get(position);
                                            float count = Float.parseFloat(editText.getText().toString());
                                            boolean f = false;
                                            GoodsC goods = new GoodsC(myapp.getCompany_ID());
                                            String goodsID = CDBHelper.createAndUpdate(getApplicationContext(),goods);
                                            goods.set_id(goodsID);
                                            OrderC order = new OrderC(myapp.getCompany_ID());
                                            String OrderId = CDBHelper.createAndUpdate(getApplicationContext(),order);
                                            order.set_id(OrderId);
                                            for (OrderC orderC : orderCList)
                                            {
                                                Iterator<GoodsC> goodsCIterator = orderC.getGoodsList().iterator();
                                                while (goodsCIterator.hasNext())
                                                {
                                                    GoodsC goodsC1 = goodsCIterator.next();

                                                    Log.e(TAG,goodsC1.getDishesCount()+"");

                                                    if (orderC.getOrderCType() != 1){

                                                        if (goodsC1.getDishesName().toString().equals(goodsC.getDishesName().toString())) {
                                                            if (goodsC1.getDishesTaste() != null){
                                                                if (goodsC1.getDishesTaste().equals(goodsC.getDishesTaste())) {

                                                                    if (goodsC1.getDishesCount() == count) {

                                                                        goodsC1.setGoodsType(1);
                                                                        goodsC1.setDishesCount(MyBigDecimal.sub(goodsC1.getDishesCount(),Float.parseFloat(editText.getText().toString()),2));


                                                                        goods.setGoodsType(1);
                                                                        goods.setDishesName(goodsC1.getDishesName());
                                                                        goods.setPrice(goodsC1.getPrice());
                                                                        goods.setDishesCount(-goodsC1.getDishesCount());

                                                                        Log.e(TAG, "aA");
                                                                        f = true;
                                                                        break;

                                                                    } else if (goodsC1.getDishesCount() > count) {


                                                                        float singlePrice = MyBigDecimal.div(goodsC.getPrice(), goodsC.getDishesCount(), 2);
                                                                        goods.setDishesCount(-count);
                                                                        goods.setDishesName(goodsC1.getDishesName());
                                                                        goods.setGoodsType(1);
                                                                        goods.setPrice(-MyBigDecimal.mul(singlePrice, count, 1));

                                                                        Log.e(TAG, "B" + orderC.getGoodsList().size());
                                                                        f = true;
                                                                        break;

                                                                    } else {
                                                                        count = MyBigDecimal.sub(count, goodsC1.getDishesCount(), 1);
                                                                        goodsC1.setGoodsType(1);

                                                                        goods.setDishesCount(-goodsC1.getDishesCount());
                                                                        goods.setDishesName(goodsC1.getDishesName());
                                                                        goods.setPrice(-goodsC1.getPrice());
                                                                        goods.setGoodsType(1);

                                                                        order.setAllPrice(goodsC.getPrice());
                                                                        order.setOrderState(1);
                                                                        order.setDeviceType(1);
                                                                        order.setOrderCType(1);
                                                                        order.addGoods(goods);

                                                                        CDBHelper.createAndUpdate(getApplicationContext(),goods);
                                                                        CDBHelper.createAndUpdate(getApplicationContext(),order);

                                                                        Log.e(TAG, "C");
                                                                        f = false;
                                                                        break;

                                                                    }
                                                                }
                                                            }else {

                                                                if (goodsC1.getDishesCount() == count) {
                                                                    goodsC1.setGoodsType(1);

                                                                    goods.setGoodsType(1);
                                                                    goods.setDishesName(goodsC1.getDishesName());
                                                                    goods.setPrice(-goodsC1.getPrice());
                                                                    goods.setDishesCount(-goodsC1.getDishesCount());

                                                                    Log.e(TAG, "aA");
                                                                    f = true;
                                                                    break;

                                                                } else if (goodsC1.getDishesCount() > count) {


                                                                    float singlePrice = MyBigDecimal.div(goodsC.getPrice(), goodsC.getDishesCount(), 2);
                                                                    //生成一个新goods
                                                                    goods.setDishesCount(-count);
                                                                    goods.setDishesName(goodsC1.getDishesName());
                                                                    goods.setGoodsType(1);
                                                                    goods.setPrice(-MyBigDecimal.mul(singlePrice, count, 1));

                                                                    Log.e(TAG, "B" + orderC.getGoodsList().size());
                                                                    f = true;
                                                                    break;

                                                                } else {
                                                                    count = MyBigDecimal.sub(count, goodsC1.getDishesCount(), 1);

                                                                    goodsC1.setGoodsType(1);
                                                                    goods.setDishesCount(-goodsC1.getDishesCount());
                                                                    goods.setDishesName(goodsC1.getDishesName());
                                                                    goods.setPrice(-goodsC1.getPrice());
                                                                    goods.setGoodsType(1);

                                                                    order.setAllPrice(goods.getPrice());
                                                                    order.setOrderState(1);
                                                                    order.setDeviceType(1);
                                                                    order.setOrderCType(1);
                                                                    order.addGoods(goods);

                                                                    CDBHelper.createAndUpdate(getApplicationContext(), goods);
                                                                    CDBHelper.createAndUpdate(getApplicationContext(), order);

                                                                    Log.e(TAG, "C");
                                                                    f = false;
                                                                    break;

                                                                }
                                                            }
                                                        }

                                                    }
                                                }
                                                if (f){
                                                    Log.e(TAG,"D");
                                                    goods.setOrder(order.get_id());
                                                    order.setOrderState(1);
                                                    order.setDeviceType(1);
                                                    order.setOrderCType(1);
                                                    order.addGoods(goods);
                                                    order.setAllPrice(goods.getPrice());
                                                    order.setCreatedTime(getFormatDate());
                                                    order.setTableNo(myapp.getTable_sel_obj().getTableNum());
                                                    order.setTableName(myapp.getTable_sel_obj().getTableName());
                                                    AreaC areaC = CDBHelper.getObjById(getApplicationContext(),myapp.getTable_sel_obj().getAreaId(), AreaC.class);
                                                    order.setAreaName(areaC.getAreaName());
                                                    CDBHelper.createAndUpdate(getApplicationContext(),goods);
                                                    CDBHelper.createAndUpdate(getApplicationContext(),order);
                                                    break;

                                                }


                                            }
                                            setAll();
                                            adatper.notifyDataSetChanged();
                                            builder.dismiss();

                                        } else {
                                            Toast.makeText(ShowParticularsActivity.this, "菜品数量大于退菜数量，不可以退菜！", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            });


                            Button zc = view1.findViewById(R.id.dialog_zc);
                            zc.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (editText.getText().toString().equals("")){

                                        Toast.makeText(ShowParticularsActivity.this,"输入不得为空",Toast.LENGTH_LONG).show();

                                    }else {

                                        //1\
                                        GoodsC goodsC = goodsCList.get(position);
                                        //2\
                                        OrderC orderC = CDBHelper.getObjById(getApplicationContext(), goodsC.getOrder(), OrderC.class);
                                        float allP = 0;
                                        for (int i = 0; i < orderC.getGoodsList().size(); i++)
                                        {
                                            if (orderC.getGoodsList().get(i).getDishesName().equals(goodsC.getDishesName()))
                                            {
                                                orderC.getGoodsList().get(i).setGoodsType(2);
                                                orderC.getGoodsList().get(i).setDishesName(goodsC.getDishesName() + "(赠)");
                                                orderC.getGoodsList().get(i).setPrice(0);

                                            }
                                            allP += orderC.getGoodsList().get(i).getPrice();
                                        }
                                        orderC.setAllPrice(allP);
                                        CDBHelper.createAndUpdate(getApplicationContext(), orderC);
                                        setAll();
                                        adatper.notifyDataSetChanged();
                                        builder.dismiss();
                                    }
                                }
                            });

                            Button shi = view1.findViewById(R.id.dialog_tuicai_qd);
                            shi.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (editText.getText().toString().equals("")){

                                        Toast.makeText(ShowParticularsActivity.this,"输入不得为空",Toast.LENGTH_LONG).show();

                                    }else{
                                        if (Float.parseFloat(editText.getText().toString()) > goodsCList.get(position).getDishesCount()){
                                            GoodsC goodsC = goodsCList.get(position);
                                            float count = Float.parseFloat(editText.getText().toString());
                                            Log.e(TAG,"orderCList---"+orderCList.size());
                                            for (int i = orderCList.size()-1; i >= 0 ; i--){

                                                Iterator<GoodsC> goodsCIterator = orderCList.get(i).getGoodsList().iterator();
                                                while (goodsCIterator.hasNext()){
                                                    GoodsC goodsC1  = goodsCIterator.next();
                                                    if (goodsC1.getDishesName().equals(goodsC.getDishesName())){
                                                        if (goodsC1.getDishesTaste() != null){
                                                            if (goodsC1.getDishesTaste().equals(goodsC.getDishesTaste())){
                                                                count = MyBigDecimal.sub(count,goodsC1.getDishesCount(),1);
                                                                Log.e(TAG,"count"+count);
                                                            }

                                                        }else {
                                                            count =  MyBigDecimal.sub(count,goodsC1.getDishesCount(),1);
                                                            Log.e(TAG,"count"+count);
                                                        }
                                                    }
                                                }

                                            }

                                            OrderC order = new OrderC(myapp.getCompany_ID());
                                            String orderID = CDBHelper.createAndUpdate(getApplicationContext(),order);
                                            order.set_id(orderID);
                                            GoodsC goodsC2 = new GoodsC(myapp.getCompany_ID());
                                            String goodsID = CDBHelper.createAndUpdate(getApplicationContext(),goodsC2);
                                            goodsC2.set_id(goodsID);
                                            goodsC2.setDishesCount(count);
                                            goodsC2.setPrice(MyBigDecimal.mul(count,MyBigDecimal.div(goodsC.getPrice(),goodsC.getDishesCount(),2),1));
                                            goodsC2.setDishesName(goodsC.getDishesName());
                                            goodsC2.setOrder(order.get_id());
                                            order.setOrderState(1);
                                            order.setDeviceType(1);
                                            order.setOrderCType(0);
                                            order.setAllPrice(goodsC2.getPrice());
                                            order.addGoods(goodsC2);
                                            order.setCreatedTime(getFormatDate());
                                            order.setTableNo(myapp.getTable_sel_obj().getTableNum());
                                            order.setTableName(myapp.getTable_sel_obj().getTableName());
                                            AreaC areaC = CDBHelper.getObjById(getApplicationContext(),myapp.getTable_sel_obj().getAreaId(), AreaC.class);
                                            order.setAreaName(areaC.getAreaName());
                                            CDBHelper.createAndUpdate(getApplicationContext(),goodsC2);
                                            CDBHelper.createAndUpdate(getApplicationContext(),order);


                                            setAll();
                                            adatper.notifyDataSetChanged();
                                            builder.dismiss();
                                        }else{
                                            Toast.makeText(ShowParticularsActivity.this,"输入的数量少于点的数量，请重新输入",Toast.LENGTH_LONG).show();
                                        }


                                    }
                                }
                            });
                            Button fou = view1.findViewById(R.id.dialog_tuicai_qx);
                            fou.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    builder.dismiss();
                                }
                            });
                            builder.show();
                        }
                    }
                });



    }
    private void setAll()
    {
        goodsCList.clear();
        all = 0f;
        orderCList = CDBHelper.getObjByWhere(getApplicationContext(),
                Expression.property("className").equalTo("OrderC")
                        .and(Expression.property("tableNo").equalTo(myapp.getTable_sel_obj().getTableNum()))
                        .and(Expression.property("orderState").equalTo(1))
                , Ordering.property("createdTime").descending()
                , OrderC.class);
        boolean flag = false;

        for (OrderC orderC : orderCList)
        {
                for (GoodsC goodsb : orderC.getGoodsList())
                {
                    flag = false;
                    if(orderC.getOrderCType()==0)//正常菜订单
                    {
                        all = MyBigDecimal.add(all,orderC.getAllPrice(),1);
                    }
                    for (GoodsC goodsC : goodsCList)
                    {
                        if (goodsC.getDishesName().equals(goodsb.getDishesName()))
                        {

                            if (goodsb.getDishesTaste() != null) {

                                if (goodsb.getDishesTaste().equals(goodsC.getDishesTaste())) {

                                    float count = MyBigDecimal.add(goodsC.getDishesCount(), goodsb.getDishesCount(), 1);
                                    goodsC.setDishesCount(count);
                                    flag = true;
                                }

                            } else {

                                float count = MyBigDecimal.add(goodsC.getDishesCount(), goodsb.getDishesCount(), 1);
                                goodsC.setDishesCount(count);

                                flag = true;
                            }

                            break;
                        }
                    }
                    if (!flag) {

                        goodsCList.add(goodsb);

                    }
                }
        }
        showTvSl.setText(goodsCList.size() + "道菜，总计："+all+"元");

    }

   /* private void setTui(){

        List<OrderC> orderCList = CDBHelper.getObjByWhere(getApplicationContext(),
                Expression.property("className").equalTo("OrderC")
                        .and(Expression.property("tableNo").equalTo(myapp.getTable_sel_obj().getTableNum()))
                        .and(Expression.property("orderState").equalTo(1))
                , null
                , OrderC.class);
        for (OrderC orderC : orderCList){

            boolean flag = false;

            if (orderC.getOrderCType() == 1){
                for (GoodsC goodsB : orderC.getGoodsList()){
                    flag = false;
                    goodsB.setDishesName(goodsB.getDishesName()+"(退)");
                    Log.e(TAG,goodsB.getDishesCount()+"---goodsB.getDishesCount()\n");

                    for (GoodsC goodsC : goodsCListT){


                        if (goodsC.getDishesName().equals(goodsB.getDishesName()) ){

                            if (goodsB.getDishesTaste() != null){

                                if (goodsB.getDishesTaste().equals(goodsC.getDishesTaste())){

                                    float add = MyBigDecimal.add(Math.abs(goodsC.getAllPrice()),Math.abs(goodsB.getAllPrice()),1);
                                    goodsC.setAllPrice(add);
                                    float count = MyBigDecimal.add(Math.abs(goodsC.getDishesCount()),Math.abs(goodsB.getDishesCount()),1);
                                    goodsC.setDishesCount(count);
                                    flag = true;
                                }

                            }else{

                                float add = MyBigDecimal.add(Math.abs(goodsC.getAllPrice()),Math.abs(goodsB.getAllPrice()),1);
                                goodsC.setAllPrice(add);
                                float count = MyBigDecimal.add(Math.abs(goodsC.getDishesCount()),Math.abs(goodsB.getDishesCount()),1);
                                Log.e(TAG,count+"count");
                                goodsC.setDishesCount(count);
                                flag = true;
                            }

                            break;
                        }
                    }
                    if (!flag)
                    {
                        goodsB.setDishesCount(Math.abs(goodsB.getDishesCount()));
                        goodsB.setAllPrice(Math.abs(goodsB.getAllPrice()));
                        goodsCListT.add(goodsB);
                    }
                }
            }
        }

    }*/


    @OnClick({R.id.show_but_dc, R.id.show_but_md,R.id.show_img,R.id.show_but_dy})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {

            case R.id.show_but_dc:
                intent = new Intent(ShowParticularsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.show_but_md:
                intent = new Intent(ShowParticularsActivity.this, PayActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.show_img:
                finish();
                break;

            case R.id.show_but_dy:

                if (Tool.isFastDoubleClick()) {
                    Toast.makeText(ShowParticularsActivity.this,"点的太快",Toast.LENGTH_LONG).show();
                    return;
                }else{
                    setPrintOrder();
                }


                break;

            default:

                break;
        }
    }


    private String setPrintOrder(){

        btAdapter = BluetoothUtil.getBTAdapter();
        if(btAdapter != null){

            device = BluetoothUtil.getDevice(btAdapter);
            if (device != null){
                try {
                    socket = BluetoothUtil.getSocket(device);
                    PrintUtils.setOutputStream(socket.getOutputStream());

                } catch (IOException e) {
                    e.printStackTrace();
                }

                onPrint();
                return "打印成功";
            }


        }
        return "";
    }

    private void onPrint() {


        String waiter = myapp.getUsersC().getEmployeeName();
        List<CompanyC> companyCs = CDBHelper.getObjByClass(getApplicationContext(),CompanyC.class);
        AreaC areaCs = CDBHelper.getObjById(getApplicationContext(),myapp.getTable_sel_obj().getAreaId(),AreaC.class);
        String tableNumber = myapp.getTable_sel_obj().getTableNum();
        PrintUtils.selectCommand(PrintUtils.RESET);
        PrintUtils.selectCommand(PrintUtils.LINE_SPACING_DEFAULT);
        PrintUtils.selectCommand(PrintUtils.ALIGN_CENTER);
        if (companyCs.size() != 0){
            PrintUtils.printText(companyCs.get(0).getPointName()+"\n\n");
        }
        PrintUtils.selectCommand(PrintUtils.DOUBLE_HEIGHT_WIDTH);
        PrintUtils.printText(areaCs.getAreaName()+"/"+myapp.getTable_sel_obj().getTableName()+"桌\n\n");
        PrintUtils.selectCommand(PrintUtils.NORMAL);
        PrintUtils.selectCommand(PrintUtils.ALIGN_LEFT);
        PrintUtils.printText(PrintUtils.printTwoData("订单编号", OrderId()+"\n"));
        PrintUtils.printText(PrintUtils.printTwoData("下单时间", getFormatDate()+"\n"));
        PrintUtils.printText(PrintUtils.printTwoData("人数："+myapp.getTable_sel_obj().getCurrentPersions(), "收银员："+waiter+"\n"));
        PrintUtils.printText("--------------------------------\n");
        PrintUtils.selectCommand(PrintUtils.BOLD);
        PrintUtils.printText(PrintUtils.printThreeData("项目", "数量", "金额\n"));
        PrintUtils.printText("--------------------------------\n");
        PrintUtils.selectCommand(PrintUtils.BOLD_CANCEL);

        for (int j = 0; j < goodsCList.size(); j++) {

            GoodsC goodsC = goodsCList.get(j);
            String taste = "";
            if (goodsC.getDishesTaste() != null){
                taste = "("+goodsC.getDishesTaste()+")";
            }

            PrintUtils.printText(PrintUtils.printThreeData(goodsC.getDishesName()+taste,goodsC.getDishesCount()+"", goodsC.getPrice()+"\n"));


        }

        PrintUtils.printText("--------------------------------\n");
        PrintUtils.printText(PrintUtils.printTwoData("合计", all+"\n"));
        PrintUtils.printText("--------------------------------\n");
        PrintUtils.printText("\n\n\n\n");
        PrintUtils.closeOutputStream();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * @return 订单号
     */
    public String OrderId(){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        return formatter.format(date);
    }

    /**
     *
     * @return 时间格式 yyyy-MM-dd HH:mm:ss
     */
    public String getFormatDate(){
        Date date = new Date();
        if(date != null){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return formatter.format(date);
        }

        return null;
    }




}
