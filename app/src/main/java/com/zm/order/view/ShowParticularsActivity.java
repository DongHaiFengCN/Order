package com.zm.order.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Ordering;
import com.tencent.bugly.crashreport.CrashReport;
import com.zm.order.R;

import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

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
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action1;
import untils.BluetoothUtil;
import untils.PrintUtils;
import untils.Tool;

import static com.gprinter.service.AllService.TAG;
import static model.CDBHelper.getFormatDate;

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
    @BindView(R.id.show_tv_area)
    TextView showTvArea;

    @BindView(R.id.show_img)
    ImageView showImg;

    ShowParticularsAdapter adatper;
    private List<GoodsC> goodsCList;
    private MyApplication myapp;
    private float all = 0f;

    private BluetoothAdapter btAdapter;
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private List<OrderC> orderCList;
    private String areaName, tableName;
    private int selActionId;
    private boolean isSupDishesCheck = false;
    private float supCount;
    private ProgressDialog proDialog =null;

    public static final String TAG = "ShowParticularsActivity";

    private boolean printerToKitchen(GoodsC obj, int type, String areaName, String TableName) {

        return false;
    }
    //追加菜品
    private void addDishes(int pos, float counts) {
        GoodsC oldGoods = goodsCList.get(pos);

        OrderC newOrderObj = new OrderC(myapp.getCompany_ID());
        String orderId = CDBHelper.createAndUpdate(getApplicationContext(), newOrderObj);
        newOrderObj.set_id(orderId);

        GoodsC newGoods = null;
        try {
            newGoods = (GoodsC) oldGoods.clone();//向下转型----P2没有被实例化
            newGoods.setDishesCount(counts);
            newGoods.setOrder(orderId);
        } catch (CloneNotSupportedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (orderCList.size() > 1) {
            newOrderObj.setOrderNum(orderCList.get(0).getOrderNum() + 1);
            newOrderObj.setSerialNum(orderCList.get(0).getSerialNum());
        } else {
            newOrderObj.setOrderNum(1);
            newOrderObj.setSerialNum(Tool.getOrderSerialNum(this));
        }
        newOrderObj.setAllPrice(MyBigDecimal.mul(newGoods.getPrice(), counts, 1));
        newOrderObj.setOrderState(1);//未买单
        newOrderObj.setOrderCType(0);//正常
        newOrderObj.setDeviceType(1);//点餐宝
        newOrderObj.setCreatedTime(getFormatDate());
        newOrderObj.setTableNo(myapp.getTable_sel_obj().getTableNum());
        newOrderObj.setTableName(tableName);
        newOrderObj.setAreaName(areaName);
        List<GoodsC> tmpList = new ArrayList<>();
        tmpList.add(newGoods);
        newOrderObj.setGoodsList(tmpList);
        CDBHelper.createAndUpdate(getApplicationContext(), newOrderObj);
    }
    //退菜
    private void retreatDishes(int pos, float counts) {
        GoodsC oldGoods = goodsCList.get(pos);
        Log.e("Show",""+oldGoods.getDishesCount());
        GoodsC newGoods = null;
        try {
            newGoods = (GoodsC) oldGoods.clone();//向下转型----P2没有被实例化

        } catch (CloneNotSupportedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        newGoods.setDishesCount(counts);
        removeGoodsFromOrder(newGoods, 0);//修改正常老订单

        OrderC newOrderObj = new OrderC(myapp.getCompany_ID());
        String orderId = CDBHelper.createAndUpdate(getApplicationContext(), newOrderObj);
        newOrderObj.set_id(orderId);
        if (orderCList.size() > 1) {
            newOrderObj.setOrderNum(orderCList.get(0).getOrderNum() + 1);
            newOrderObj.setSerialNum(orderCList.get(0).getSerialNum());
        } else {
            newOrderObj.setOrderNum(1);
            newOrderObj.setSerialNum(Tool.getOrderSerialNum(this));
        }
        newOrderObj.setAllPrice(MyBigDecimal.mul(oldGoods.getPrice(), counts, 1));
        newOrderObj.setOrderState(1);//未买单
        newOrderObj.setOrderCType(1);//退菜订单
        newOrderObj.setDeviceType(1);//点餐宝
        newOrderObj.setCreatedTime(getFormatDate());
        newOrderObj.setTableNo(myapp.getTable_sel_obj().getTableNum());
        newOrderObj.setTableName(tableName);
        newOrderObj.setAreaName(areaName);

        newGoods.setOrder(orderId);
        newGoods.setGoodsType(1);//置成退菜类型
        newGoods.setDishesName(oldGoods.getDishesName() + "(退)");
        List<GoodsC> tmpList = new ArrayList<>();
        tmpList.add(newGoods);
        newOrderObj.setGoodsList(tmpList);
        CDBHelper.createAndUpdate(getApplicationContext(), newOrderObj);

    }

    private void giveDishes(int pos, float counts) {

        GoodsC oldGoods = goodsCList.get(pos);
        GoodsC newGoods = null;
        try {
            newGoods = (GoodsC) oldGoods.clone();//向下转型----P2没有被实例化
        } catch (CloneNotSupportedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        newGoods.setDishesCount(counts);
        removeGoodsFromOrder(newGoods, 0);//修改正常老订单

        OrderC newOrderObj = new OrderC(myapp.getCompany_ID());
        String orderId = CDBHelper.createAndUpdate(getApplicationContext(), newOrderObj);
        newOrderObj.set_id(orderId);
        if (orderCList.size() > 1) {
            newOrderObj.setOrderNum(orderCList.get(0).getOrderNum() + 1);
            newOrderObj.setSerialNum(orderCList.get(0).getSerialNum());
        } else {
            newOrderObj.setOrderNum(1);
            newOrderObj.setSerialNum(Tool.getOrderSerialNum(this));
        }
        newOrderObj.setAllPrice(MyBigDecimal.mul(oldGoods.getPrice(), counts, 1));
        newOrderObj.setOrderState(1);//未买单
        newOrderObj.setOrderCType(2);//赠菜订单
        newOrderObj.setDeviceType(1);//点餐宝
        newOrderObj.setCreatedTime(getFormatDate());
        newOrderObj.setTableNo(myapp.getTable_sel_obj().getTableNum());
        newOrderObj.setTableName(tableName);
        newOrderObj.setAreaName(areaName);

        newGoods.setOrder(orderId);
        newGoods.setGoodsType(2);//置成赠菜类型
        newGoods.setDishesName(oldGoods.getDishesName() + "(赠)");
        List<GoodsC> tmpList = new ArrayList<>();
        tmpList.add(newGoods);
        newOrderObj.setGoodsList(tmpList);
        CDBHelper.createAndUpdate(getApplicationContext(), newOrderObj);

    }

    private void backDishes(int pos, float counts) {
        GoodsC oldGoods = goodsCList.get(pos);
        GoodsC newGoods = null;
        try {
            newGoods = (GoodsC) oldGoods.clone();//向下转型----P2没有被实例化
        } catch (CloneNotSupportedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        newGoods.setDishesCount(counts);
        removeGoodsFromOrder(newGoods, 2);//修改赠菜老订单

        OrderC newOrderObj = new OrderC(myapp.getCompany_ID());
        String orderId = CDBHelper.createAndUpdate(getApplicationContext(), newOrderObj);
        newOrderObj.set_id(orderId);
        if (orderCList.size() > 1) {
            newOrderObj.setOrderNum(orderCList.get(0).getOrderNum() + 1);
            newOrderObj.setSerialNum(orderCList.get(0).getSerialNum());
        } else {
            newOrderObj.setOrderNum(1);
            newOrderObj.setSerialNum(Tool.getOrderSerialNum(this));
        }
        newOrderObj.setAllPrice(MyBigDecimal.mul(oldGoods.getPrice(), counts, 1));
        newOrderObj.setOrderState(1);//未买单
        newOrderObj.setOrderCType(0);//添菜订单
        newOrderObj.setDeviceType(1);//点餐宝
        newOrderObj.setCreatedTime(getFormatDate());
        newOrderObj.setTableNo(myapp.getTable_sel_obj().getTableNum());
        newOrderObj.setTableName(tableName);
        newOrderObj.setAreaName(areaName);

        newGoods.setOrder(orderId);
        newGoods.setGoodsType(0);

        String dishesName = oldGoods.getDishesName();
        dishesName = dishesName.substring(0, dishesName.length() - 3);
        newGoods.setDishesName(dishesName);
        List<GoodsC> tmpList = new ArrayList<>();
        tmpList.add(newGoods);
        newOrderObj.setGoodsList(tmpList);
        CDBHelper.createAndUpdate(getApplicationContext(), newOrderObj);
    }

    private void retreatDishesFromZC(int pos, float counts) {
        GoodsC oldGoods = goodsCList.get(pos);
        GoodsC newGoods = null;
        try {
            newGoods = (GoodsC) oldGoods.clone();//向下转型----P2没有被实例化
        } catch (CloneNotSupportedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        newGoods.setDishesCount(counts);
        removeGoodsFromOrder(newGoods, 2);//修改老订单 ，2代表从赠菜中移除

        OrderC newOrderObj = new OrderC(myapp.getCompany_ID());
        String orderId = CDBHelper.createAndUpdate(getApplicationContext(), newOrderObj);
        newOrderObj.set_id(orderId);
        if (orderCList.size() > 1) {
            newOrderObj.setOrderNum(orderCList.get(0).getOrderNum() + 1);
            newOrderObj.setSerialNum(orderCList.get(0).getSerialNum());
        } else {
            newOrderObj.setOrderNum(1);
            newOrderObj.setSerialNum(Tool.getOrderSerialNum(this));
        }
        newOrderObj.setAllPrice(MyBigDecimal.mul(oldGoods.getPrice(), counts, 1));
        newOrderObj.setOrderState(1);//未买单
        newOrderObj.setOrderCType(1);//退菜订单
        newOrderObj.setDeviceType(1);//点餐宝
        newOrderObj.setCreatedTime(getFormatDate());
        newOrderObj.setTableNo(myapp.getTable_sel_obj().getTableNum());
        newOrderObj.setTableName(tableName);
        newOrderObj.setAreaName(areaName);

        newGoods.setOrder(orderId);
        newGoods.setGoodsType(1);//置成退菜类型

        String dishesName = oldGoods.getDishesName();
        dishesName = dishesName.substring(0, dishesName.length() - 3);
        newGoods.setDishesName(dishesName + "(退)");
        List<GoodsC> tmpList = new ArrayList<>();
        tmpList.add(newGoods);
        newOrderObj.setGoodsList(tmpList);
        CDBHelper.createAndUpdate(getApplicationContext(), newOrderObj);
    }

    private void removeGoodsFromOrder(GoodsC retreateObj, int type)
    {
        float  retreateCounts = retreateObj.getDishesCount();//数量
        String retreateTaste  = retreateObj.getDishesTaste();//口味
        String retreate =  retreateObj.getDishesName();
        for (int i = 0; i < orderCList.size(); i++)
        {
            if(retreateCounts<=0)
                break;
            OrderC orderObj = orderCList.get(i);
            if (orderObj.getOrderCType() != type)
                continue;

            List<GoodsC> oldGoodsList = orderObj.getGoodsList();
            for (int j = 0; j < oldGoodsList.size(); j++)
            {
                if(retreateCounts<=0)
                    break;

                GoodsC oldGoods = oldGoodsList.get(j);

                if (retreate.equals(oldGoods.getDishesName()))//名字相同
                {
                    if (TextUtils.isEmpty(retreateTaste)&&TextUtils.isEmpty(oldGoods.getDishesTaste()))//口味都为空
                    {
                        if (retreateCounts >= oldGoods.getDishesCount())//退出菜品数量超出原有数量
                        {
                            float retreatePrice = MyBigDecimal.mul(oldGoods.getPrice(), oldGoods.getDishesCount(), 1);
                            orderObj.getGoodsList().remove(oldGoods);
                            j--;
                            if (orderObj.getGoodsList().size() == 0)
                            {
                                CDBHelper.deleteObj(getApplicationContext(), orderObj);
                            } else {
                                float lastPrice = MyBigDecimal.sub(orderObj.getAllPrice(), retreatePrice, 1);
                                orderObj.setAllPrice(lastPrice);
                                CDBHelper.createAndUpdate(getApplicationContext(), orderObj);
                            }
                            retreateCounts = MyBigDecimal.sub(retreateCounts,oldGoods.getDishesCount(),1);
                        } else //数量上有剩余菜品
                        {
                            float retreatePrice = MyBigDecimal.mul(retreateObj.getPrice(), retreateCounts, 1);
                            float lastPrice = MyBigDecimal.sub(orderObj.getAllPrice(), retreatePrice, 1);
                            orderObj.setAllPrice(lastPrice);

                            float  lastCount = MyBigDecimal.sub(oldGoods.getDishesCount(),retreateCounts,1);
                            oldGoods.setDishesCount(lastCount);
                            oldGoods.setCreatedTime(getFormatDate());
                            CDBHelper.createAndUpdate(getApplicationContext(), orderObj);
                            retreateCounts = 0;
                        }
                    } else  if (!TextUtils.isEmpty(retreateTaste)&&!TextUtils.isEmpty(oldGoods.getDishesTaste())&&retreateTaste.equals(oldGoods.getDishesTaste()))//口味都不为空且相等
                    {

                        if (retreateCounts >= oldGoods.getDishesCount())//退出菜品数量超出原有数量
                        {
                            float retreatePrice = MyBigDecimal.mul(oldGoods.getPrice(), oldGoods.getDishesCount(), 1);
                            orderObj.getGoodsList().remove(oldGoods);
                            j--;
                            if (orderObj.getGoodsList().size() == 0)
                            {
                                CDBHelper.deleteObj(getApplicationContext(), orderObj);
                            } else {
                                float lastPrice = MyBigDecimal.sub(orderObj.getAllPrice(), retreatePrice, 1);
                                orderObj.setAllPrice(lastPrice);
                                CDBHelper.createAndUpdate(getApplicationContext(), orderObj);
                            }
                            retreateCounts = MyBigDecimal.sub(retreateCounts,oldGoods.getDishesCount(),1);
                        }
                        else //数量上有剩余菜品
                        {
                            float retreatePrice = MyBigDecimal.mul(retreateObj.getPrice(), retreateCounts, 1);
                            float lastPrice = MyBigDecimal.sub(orderObj.getAllPrice(), retreatePrice, 1);
                            orderObj.setAllPrice(lastPrice);

                            float  lastCount = MyBigDecimal.sub(oldGoods.getDishesCount(),retreateCounts,1);
                            oldGoods.setDishesCount(lastCount);
                            oldGoods.setCreatedTime(getFormatDate());
                            CDBHelper.createAndUpdate(getApplicationContext(), orderObj);
                            retreateCounts = 0;
                        }
                    }
                }
            }
        }
    }

    /**
     * 对正常菜品进行弹框处理
     *
     * @param pos
     */
    private void normalDishesDialog(final int pos) {
        final int position = pos;
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ShowParticularsActivity.this);
        final View view1 = LayoutInflater.from(ShowParticularsActivity.this).inflate(R.layout.activity_tv_dialog, null);
        alertDialog.setView(view1);
        final AlertDialog dialog = alertDialog.create();
        dialog.setCancelable(false);
        final TextView title = view1.findViewById(R.id.dialog_dishesName);
        title.setText(goodsCList.get(position).getDishesName() + "(已点数量 " + goodsCList.get(pos).getDishesCount() + ")");
        final TextView dialog_count = view1.findViewById(R.id.dialog_count);
        final EditText editText = view1.findViewById(R.id.dialog_ed_sl);
        editText.setText(1.0 + "");
        editText.clearFocus();
        editText.setFocusableInTouchMode(false);
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    editText.setFocusableInTouchMode(true);
                    editText.requestFocus();
                    editText.selectAll();
                }
                return false;
            }
        });
        selActionId = R.id.dialog_add_zc;
        //根据ID找到RadioGroup实例
        RadioGroup group = (RadioGroup) view1.findViewById(R.id.dialog_radio);
        //绑定一个匿名监听器
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                selActionId = arg1;
                modificationUnit(view1,title,position,editText);
                if (selActionId == R.id.dialog_add_zc){
                    editText.setText(1.0 + "");
                    dialog_count.setText("追加数量");
                }else if (selActionId == R.id.dialog_delete_tc){
                    editText.setText(1.0 + "");
                    dialog_count.setText("退菜数量");
                }else if (selActionId == R.id.dialog_give_zc){

                    editText.setText(1.0 + "");
                    dialog_count.setText("赠送数量");
                }

            }
        });
        Button btnOk = view1.findViewById(R.id.dialog_tuicai_qd);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editText.getText())) {
                    Toast.makeText(getApplicationContext(), "数量不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (editText.getText().toString().equals(".")){
                    Toast.makeText(getApplicationContext(), "数量不能为.", Toast.LENGTH_SHORT).show();
                    return;
                }
                final float tmpCount = Float.parseFloat(editText.getText().toString());
                if (selActionId == R.id.dialog_add_zc) //添菜
                {

                    if (tmpCount <= 0) {
                        Toast.makeText(getApplicationContext(), "数量要求大于0", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    addDishes(position, tmpCount);
                    printerToKitchen(goodsCList.get(position), 0 ,areaName,tableName);
                    setAll();
                } else if (selActionId == R.id.dialog_delete_tc)//退菜
                {

                    if (tmpCount <= 0) {
                        Toast.makeText(getApplicationContext(), "数量要求大于0", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (isSupDishesCheck){
                        if (tmpCount > supCount) {
                            Toast.makeText(getApplicationContext(), "退菜数量不能大于原始数量", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }else{

                        if (tmpCount > goodsCList.get(position).getDishesCount()) {
                            Toast.makeText(getApplicationContext(), "退菜数量不能大于原始数量", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    try {

                        CDBHelper.db.inBatch(new TimerTask() {
                                                 @Override
                                                 public void run() {

                                                     GoodsC obj = goodsCList.get(position);
                                                     if (isSupDishesCheck){
                                                         //1
                                                         //2
                                                         final Document doc = CDBHelper.getDocByID(getApplicationContext(),obj.getDishesId());
                                                         for (OrderC orderC : orderCList)
                                                         {
                                                             if (orderC.getOrderCType() != 0){
                                                                 continue;
                                                             }
                                                             for (GoodsC goodsObj : orderC.getGoodsList())
                                                             {
                                                                 if (goodsObj.getDishesId() ==null){
                                                                     continue;
                                                                 }
                                                                 if (goodsObj.getDishesId().equals(obj.getDishesId()))
                                                                 {
                                                                     if (TextUtils.isEmpty(goodsObj.getDishesTaste())&&TextUtils.isEmpty(obj.getDishesTaste())){

                                                                         goodsObj.setDishesCount(MyBigDecimal.mul(goodsObj.getDishesCount(),doc.getFloat("supCount"),1));
                                                                         goodsObj.setPrice(doc.getFloat("supPrice"));
                                                                         goodsObj.setDishesId(doc.getString("supDishesId"));
                                                                         goodsObj.setDishesName(doc.getString("supDishesName"));
                                                                         CDBHelper.createAndUpdate(getApplicationContext(),orderC);
                                                                     }else{
                                                                         if (!TextUtils.isEmpty(goodsObj.getDishesTaste())&&!TextUtils.isEmpty(obj.getDishesTaste())&&goodsObj.getDishesTaste().equals(obj.getDishesTaste())){
                                                                             goodsObj.setDishesCount(MyBigDecimal.mul(goodsObj.getDishesCount(),doc.getFloat("supCount"),1));
                                                                             goodsObj.setPrice(doc.getFloat("supPrice"));
                                                                             goodsObj.setDishesId(doc.getString("supDishesId"));
                                                                             goodsObj.setDishesName(doc.getString("supDishesName"));
                                                                             CDBHelper.createAndUpdate(getApplicationContext(),orderC);
                                                                         }
                                                                     }
                                                                 }
                                                             }
                                                         }
                                                         obj.setDishesName(doc.getString("supDishesName"));
                                                         obj.setPrice(doc.getFloat("supPrice"));
                                                         obj.setDishesCount(goodsCList.get(position).getDishesCount());
                                                         obj.setDishesId(doc.getString("supDishesId"));
                                                         isSupDishesCheck = false;

                                                     }

                                                     retreatDishes(position, tmpCount);

                                                 }
                                             }
                        );
                    } catch (CouchbaseLiteException e) {
                        e.printStackTrace();
                        CrashReport.postCatchedException(e);
                    }

                    printerToKitchen(goodsCList.get(position), 1 ,areaName,tableName);
                    setAll();
                } else {

                    if (tmpCount <= 0) {
                        Toast.makeText(getApplicationContext(), "数量要求大于0", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (tmpCount > goodsCList.get(position).getDishesCount()) {
                        Toast.makeText(getApplicationContext(), "退菜数量不能大于原始数量", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    giveDishes(position, tmpCount);
                    printerToKitchen(goodsCList.get(position), 2 ,areaName,tableName);
                    setAll();
                }

                dialog.dismiss();

                adatper.notifyDataSetChanged();

            }
        });

        Button btnCancel = view1.findViewById(R.id.dialog_tuicai_qx);//退菜

        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                dialog.dismiss();

            }

        });

        dialog.show();

    }

    private void giveDishesDialog(int pos)//对赠菜的处理窗口
    {
        final int position = pos;

        final AlertDialog.Builder builder = new AlertDialog.Builder(ShowParticularsActivity.this);
        final View view1 = LayoutInflater.from(ShowParticularsActivity.this).inflate(R.layout.activity_tv_dialog, null);
        builder.setView(view1);
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        final TextView title = view1.findViewById(R.id.dialog_dishesName);
        title.setText(goodsCList.get(position).getDishesName() + "(已点数量 " + goodsCList.get(pos).getDishesCount() + ")");
        final TextView dialog_count = view1.findViewById(R.id.dialog_count);
        final EditText editText = view1.findViewById(R.id.dialog_ed_sl);
        editText.setText(1.0 + "");
        editText.clearFocus();
        editText.setFocusableInTouchMode(false);
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    editText.setFocusableInTouchMode(true);
                    editText.requestFocus();
                    editText.selectAll();
                }
                return false;
            }
        });

        selActionId = R.id.dialog_add_zc;
        //根据ID找到RadioGroup实例
        RadioGroup group = (RadioGroup) view1.findViewById(R.id.dialog_radio);
        //绑定一个匿名监听器
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                selActionId = arg1;
                if (selActionId == R.id.dialog_add_zc){
                    editText.setText(1.0 + "");
                    dialog_count.setText("恢复数量");
                }else{
                    editText.setText(1.0 + "");
                    dialog_count.setText("退菜数量");
                }

            }
        });
        RadioButton rb1 = (RadioButton) view1.findViewById(R.id.dialog_add_zc);
        rb1.setText("恢复价格");
        RadioButton rb2 = (RadioButton) view1.findViewById(R.id.dialog_delete_tc);
        rb2.setVisibility(View.GONE);
        RadioButton rb3 = (RadioButton) view1.findViewById(R.id.dialog_give_zc);
        rb3.setText("退菜处理");

        Button btnOk = view1.findViewById(R.id.dialog_tuicai_qd);//
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(editText.getText())) {
                    Toast.makeText(getApplicationContext(), "数量不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (editText.getText().toString().equals(".")){
                    Toast.makeText(getApplicationContext(), "数量不能为.", Toast.LENGTH_SHORT).show();
                    return;
                }
                float tmpCount = Float.parseFloat(editText.getText().toString());
                if (selActionId == R.id.dialog_add_zc) //恢复价格
                {
                    if (tmpCount <= 0) {
                        Toast.makeText(getApplicationContext(), "数量要求大于0", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (tmpCount > goodsCList.get(position).getDishesCount()) {
                        Toast.makeText(getApplicationContext(), "不能大于原始数量", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    backDishes(position, tmpCount);
                    printerToKitchen(goodsCList.get(position), 0 ,areaName,tableName);
                    setAll();
                } else //退菜处理
                {
                    if (tmpCount <= 0) {
                        Toast.makeText(getApplicationContext(), "数量要求大于0", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (tmpCount > goodsCList.get(position).getDishesCount()) {
                        Toast.makeText(getApplicationContext(), "不能大于原始数量", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    retreatDishesFromZC(position, tmpCount);
                    printerToKitchen(goodsCList.get(position), 1 ,areaName,tableName);

                    setAll();
                }
                dialog.dismiss();
                adatper.notifyDataSetChanged();
            }
        });

        Button btnCancel = view1.findViewById(R.id.dialog_tuicai_qx);//退菜
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });
        dialog.show();
    }

    /**
     * 转换为辅助单位
     */
    private void modificationUnit(View view, final TextView title, final int position,final EditText editText){
        final CheckBox checkBox = view.findViewById(R.id.dialog_delete_supDishes);
        final float unitCount = goodsCList.get(position).getDishesCount();
        if (selActionId == R.id.dialog_add_zc){
            checkBox.setVisibility(View.GONE);
            checkBox.setChecked(false);
            editText.setText(""+1.0);
            title.setText(goodsCList.get(position).getDishesName() + "(已点数量 " + unitCount+ ")");
        }else if (selActionId == R.id.dialog_delete_tc)
        {
            GoodsC obj = goodsCList.get(position);
            if (obj.getDishesId() == null){
                return;
            }
            final Document doc = CDBHelper.getDocByID(getApplicationContext(),obj.getDishesId());

            if (doc.getBoolean("haveSupDishes"))
            {
                checkBox.setVisibility(View.VISIBLE);

                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){
                            supCount = MyBigDecimal.mul(doc.getFloat("supCount"),unitCount,1);
                            title.setText(goodsCList.get(position).getDishesName() + "(已点数量 " + supCount + ")");
                            editText.setText(""+supCount);
                            isSupDishesCheck = true;
                        }else{
                            editText.setText(""+unitCount);
                            title.setText(goodsCList.get(position).getDishesName() + "(已点数量 " + unitCount+ ")");
                            isSupDishesCheck = false;
                        }
                    }
                });



            }else{
                checkBox.setVisibility(View.GONE);
            }
        }else{
            checkBox.setVisibility(View.GONE);
            checkBox.setChecked(false);
            editText.setText(""+1.0);
            title.setText(goodsCList.get(position).getDishesName() + "(已点数量 " + unitCount+ ")");
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        ButterKnife.bind(this);
        myapp = (MyApplication) getApplication();
        tableName = myapp.getTable_sel_obj().getTableName();
        AreaC areaC = CDBHelper.getObjById(getApplicationContext(), myapp.getTable_sel_obj().getAreaId(), AreaC.class);
        areaName = areaC.getAreaName();

        goodsCList = new ArrayList<>();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        adatper = new ShowParticularsAdapter(this, goodsCList);
        showListView.setAdapter(adatper);
        initData();

        showListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {

                //点击订单OrderC
                GoodsC obj = goodsCList.get(position);
                switch (obj.getGoodsType()) {
                    case 0:
                        normalDishesDialog(position);

                        break;
                    case 2:
                        giveDishesDialog(position);
                        break;
                    case 3:
                        normalDishesDialog(position);
                        break;
                    default:
                        break;
                }
                adatper.notifyDataSetChanged();

            }
        });
    }

    private void initData()
    {
        proDialog = new ProgressDialog( ShowParticularsActivity.this,R.style.CustomDialog);
        proDialog.setTitle("");
        proDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        proDialog.show();
        myapp.mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                goodsCList.clear();
                all = 0f;
                List<OrderC> orderCList0 =new ArrayList<>();//
                List<OrderC> orderCList1 =new ArrayList<>();//
                List<OrderC> orderCList2 =new ArrayList<>();//

                orderCList = CDBHelper.getObjByWhere(getApplicationContext(),
                        Expression.property("className").equalTo("OrderC")
                                .and(Expression.property("tableNo").equalTo(myapp.getTable_sel_obj().getTableNum()))
                                .and(Expression.property("orderState").equalTo(1))
                        , Ordering.property("createdTime").descending()
                        , OrderC.class);

                boolean flag = false;
                for (OrderC orderC : orderCList) {
                    if (orderC.getOrderCType() == 0)//0，正常菜订单
                    {
                        all = MyBigDecimal.add(all, orderC.getAllPrice(), 1);
                        orderCList0.add(orderC);

                    }
                    else if(orderC.getOrderCType() ==1)
                    {
                        orderCList1.add(orderC);
                    }
                    else
                        orderCList2.add(orderC);
                }
                orderCList.clear();
                orderCList.addAll(orderCList1);
                orderCList.addAll(orderCList2);
                orderCList.addAll(orderCList0);
                 //
                  for (OrderC orderC : orderCList)
                  {
                      List<GoodsC> goodsCList1 = orderC.getGoodsList();
                      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                      GoodsC temp ;
                      for (int i = 0 ; i < goodsCList1.size()-1 ;i++){
                          for (int j = i+1 ; j < goodsCList1.size() ;j++){
                              ParsePosition pos1 = new ParsePosition(0);
                              ParsePosition pos2 = new ParsePosition(0);
                              Date goodsTime1 = sdf.parse(goodsCList1.get(i).getCreatedTime(),pos1);
                              Date goodsTime2 = sdf.parse(goodsCList1.get(j).getCreatedTime(),pos2);
                              if(goodsTime1.before(goodsTime2)){
                                  temp = goodsCList1.get(i);
                                  goodsCList1.set(i,goodsCList1.get(j));
                                  goodsCList1.set(j,temp);
                              }
                          }
                      }
                    for (GoodsC goodsb : goodsCList1) {
                        flag = false;
                        for (GoodsC goodsC : goodsCList) {

                            if (goodsC.getDishesName().equals(goodsb.getDishesName())) {
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
                            GoodsC objClone = null;
                            try {
                                objClone = (GoodsC) goodsb.clone();
                            } catch (CloneNotSupportedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            goodsCList.add(objClone);
                            Log.e(TAG,"goods--"+objClone.getCreatedTime());

                        }
                    }
                }

                uiHandler.obtainMessage(0).sendToTarget();
            }
            }
        );
    }
    /**
     * 查询所有订单并合并
     */
    private void setAll() {
        goodsCList.clear();
        all = 0f;
        List<OrderC> orderCList0 =new ArrayList<>();//
        List<OrderC> orderCList1 =new ArrayList<>();//
        List<OrderC> orderCList2 =new ArrayList<>();//

        orderCList = CDBHelper.getObjByWhere(getApplicationContext(),
                Expression.property("className").equalTo("OrderC")
                        .and(Expression.property("tableNo").equalTo(myapp.getTable_sel_obj().getTableNum()))
                        .and(Expression.property("orderState").equalTo(1))
                , Ordering.property("createdTime").descending()
                , OrderC.class);

        boolean flag = false;
        for (OrderC orderC : orderCList) {
            if (orderC.getOrderCType() == 0)//0，正常菜订单
            {
                all = MyBigDecimal.add(all, orderC.getAllPrice(), 1);
                orderCList0.add(orderC);
            }
            else if(orderC.getOrderCType() ==1)
            {
                orderCList1.add(orderC);
            }
            else
                orderCList2.add(orderC);
        }
        orderCList.clear();
        orderCList.addAll(orderCList1);
        orderCList.addAll(orderCList2);
        orderCList.addAll(orderCList0);
        for (OrderC orderC : orderCList) {

            List<GoodsC> goodsCList1 = orderC.getGoodsList();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            GoodsC temp ;
            for (int i = 0 ; i < goodsCList1.size()-1 ;i++){
                for (int j = i+1 ; j < goodsCList1.size() ;j++){
                    ParsePosition pos1 = new ParsePosition(0);
                    ParsePosition pos2 = new ParsePosition(0);
                    Date goodsTime1 = sdf.parse(goodsCList1.get(i).getCreatedTime(),pos1);
                    Date goodsTime2 = sdf.parse(goodsCList1.get(j).getCreatedTime(),pos2);
                    if(goodsTime1.before(goodsTime2)){

                        temp = goodsCList1.get(i);
                        goodsCList1.set(i,goodsCList1.get(j));
                        goodsCList1.set(j,temp);
                    }
                }
            }

            for (GoodsC goodsb : goodsCList1) {
                flag = false;
                for (GoodsC goodsC : goodsCList) {

                    if (goodsC.getDishesName().equals(goodsb.getDishesName())) {
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
                    GoodsC objClone = null;
                    try {
                        objClone = (GoodsC) goodsb.clone();
                    } catch (CloneNotSupportedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    goodsCList.add(objClone);
                }
            }
        }
        showTvArea.setText(areaName+",   "+ myapp.getTable_sel_obj().getTableName());
        showTvSl.setText(goodsCList.size() + "道菜，总计：" + all + "元");
    }

    @OnClick({R.id.show_but_dc, R.id.show_but_md, R.id.show_img, R.id.show_but_dy})
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
                    Toast.makeText(ShowParticularsActivity.this,"点击太快，请稍候",Toast.LENGTH_LONG).show();
                    return;
                } else {
                    setPrintOrder();
                }


                break;

            default:

                break;
        }
    }


    private String setPrintOrder() {

        btAdapter = BluetoothUtil.getBTAdapter();
        if (btAdapter != null) {

            device = BluetoothUtil.getDevice(btAdapter);
            if (device != null) {
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
        List<CompanyC> companyCs = CDBHelper.getObjByClass(getApplicationContext(), CompanyC.class);
        PrintUtils.selectCommand(PrintUtils.RESET);
        PrintUtils.selectCommand(PrintUtils.LINE_SPACING_DEFAULT);
        PrintUtils.selectCommand(PrintUtils.ALIGN_CENTER);
        if (companyCs.size() != 0) {
            PrintUtils.printText(companyCs.get(0).getPointName() + "\n\n");
        }
        PrintUtils.selectCommand(PrintUtils.DOUBLE_HEIGHT_WIDTH);
        PrintUtils.printText(areaName + "/" + tableName + "\n\n");
        PrintUtils.selectCommand(PrintUtils.NORMAL);
        PrintUtils.selectCommand(PrintUtils.ALIGN_LEFT);
        PrintUtils.printText(PrintUtils.printTwoData("订单编号", orderCList.get(0).getSerialNum() + "\n"));
        PrintUtils.printText(PrintUtils.printTwoData("下单时间", getFormatDate() + "\n"));
        PrintUtils.printText(PrintUtils.printTwoData("人数：" + myapp.getTable_sel_obj().getCurrentPersions(), "收银员：" + waiter + "\n"));
        PrintUtils.printText("--------------------------------\n");
        PrintUtils.selectCommand(PrintUtils.BOLD);
        PrintUtils.printText(PrintUtils.printThreeData("项目", "数量", "金额\n"));
        PrintUtils.printText("--------------------------------\n");
        PrintUtils.selectCommand(PrintUtils.BOLD_CANCEL);

        for (int j = 0; j < goodsCList.size(); j++) {

            GoodsC goodsC = goodsCList.get(j);
            String taste = "";
            if (goodsC.getDishesTaste() != null) {
                taste = "(" + goodsC.getDishesTaste() + ")";
            }

            PrintUtils.printText(PrintUtils.printThreeData(goodsC.getDishesName() + taste, goodsC.getDishesCount() + "", MyBigDecimal.mul(goodsC.getPrice(),goodsC.getDishesCount(),1) + "\n"));


        }

        PrintUtils.printText("--------------------------------\n");
        PrintUtils.printText(PrintUtils.printTwoData("合计", all + "\n"));
        PrintUtils.printText("--------------------------------\n");
        PrintUtils.printText("\n\n\n\n");
        PrintUtils.closeOutputStream();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Handler uiHandler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {

            switch (msg.what)
            {
                case 0:
                    proDialog.dismiss();
                    showTvArea.setText(areaName+",   "+ myapp.getTable_sel_obj().getTableName());
                    showTvSl.setText(goodsCList.size() + "道菜，总计：" + all + "元");
                    adatper.notifyDataSetChanged();
                    break;

                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };



}
