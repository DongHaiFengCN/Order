package com.zm.order.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Expression;
import com.zm.order.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import application.MyApplication;
import bean.kitchenmanage.dishes.DishesC;
import bean.kitchenmanage.dishes.DishesKindC;
import bean.kitchenmanage.order.GoodsC;
import bean.kitchenmanage.order.OrderC;
import bean.kitchenmanage.order.RetreatOrderC;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import model.CDBHelper;
import untils.BluetoothUtil;
import untils.PrintUtils;

/**
 * Created by lenovo on 2017/12/13.
 */

public class ShowParticularsActivity extends Activity {

    @BindView(R.id.show_listView)
    ListView showListView;
    @BindView(R.id.show_but_dc)
    Button showButDc;
    @BindView(R.id.show_but_md)
    Button showButMd;
    @BindView(R.id.show_tv_sl)
    TextView showTvSl;
    @BindView(R.id.show_img)
    ImageView showImg;

    ShowParticularsAdapter adatper;
    private List<GoodsC> goodsCList;
    private MyApplication myapp;
    private float all = 0f;
    private ImageView getShowImg;
    private OrderC orderC;
    private BluetoothAdapter btAdapter;
    private BluetoothDevice device;
    private BluetoothSocket socket;

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

        adatper.setLinClickListener(new ShowParticularsAdapter.OnLinClickListener() {
            @Override
            public void getLinClick(ImageView imageView) {
                getShowImg = imageView;
            }
        });
        showListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ShowParticularsActivity.this);
                View view1 = LayoutInflater.from(ShowParticularsActivity.this).inflate(R.layout.activity_tv_dialog,null);
                alertDialog.setView(view1);
                TextView title = view1.findViewById(R.id.dialog_tuicai_title);
                title.setText("请选择退菜");
                final AlertDialog builder = alertDialog.create();

                Button shi = view1.findViewById(R.id.dialog_tuicai_qd);
                shi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(ShowParticularsActivity.this);
                        final EditText editText = new EditText(ShowParticularsActivity.this);
                        editText.setText(goodsCList.get(position).getDishesCount()+"");
                        editText.setKeyListener(new DigitsKeyListener(false,true));
                        builder.setTitle("请输入退菜数量")
                                .setView(editText)
                                .setCancelable(false)
                                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (goodsCList.get(position).getDishesCount() > 0.0){
                                            //1\
                                            GoodsC goodsC = goodsCList.get(position);
                                            //2\
                                            OrderC orderC = CDBHelper.getObjById(getApplicationContext(),goodsC.getOrder(),OrderC.class);
                                            //3\
                                            //打印goodslist
                                            {
                                                Log.e("orderC.getGoodsList()",orderC.getGoodsList().size()+"");
                                            }

                                            for (int i = 0;i < orderC.getGoodsList().size(); i++){
                                                if (orderC.getGoodsList().get(i).getDishesName().equals(goodsC.getDishesName())){
                                                    float sl = MyBigDecimal.sub(goodsCList.get(position).getDishesCount(),Float.parseFloat(editText.getText().toString()),1);
                                                    if (sl > -1){

                                                        if (sl == 0.0){
                                                            orderC.getGoodsList().get(i).setGoodsType(1);
                                                            orderC.getGoodsList().get(i).setDishesName(orderC.getGoodsList().get(i).getDishesName()+"(退)");
                                                            float all = MyBigDecimal.sub(orderC.getAllPrice(),orderC.getGoodsList().get(i).getAllPrice(),1);
                                                            orderC.setAllPrice(all);
                                                            orderC.addOtherGoods(orderC.getGoodsList().get(i));
                                                            orderC.getGoodsList().remove(i);

                                                        }else{

                                                            DishesC dishesC = CDBHelper.getObjById(getApplicationContext(),orderC.getGoodsList().get(i).getDishesId(),DishesC.class);
                                                            float allPrice = MyBigDecimal.mul(dishesC.getPrice(),sl,1);
                                                            orderC.getGoodsList().get(i).setAllPrice(allPrice);
                                                            float allPrice1 = MyBigDecimal.mul(dishesC.getPrice(),Float.parseFloat(editText.getText().toString()),1);
                                                            orderC.getGoodsList().get(i).setDishesCount(sl);
                                                            orderC.setAllPrice(MyBigDecimal.sub(orderC.getAllPrice(),allPrice1,1));
                                                            orderC.addOtherGoods(orderC.getGoodsList().get(i));
                                                        }



                                                    }else{
                                                        Toast.makeText(ShowParticularsActivity.this,"你输入的数量大于你点的数量，请重新输入！",Toast.LENGTH_LONG).show();
                                                    }

                                                }
                                            }

                                            //打印goodslist
                                            {
                                                Log.e("orderC.getGoodsList()",orderC.getGoodsList().size()+"");
                                            }
                                            //4\保存orderC

                                            CDBHelper.createAndUpdate(getApplicationContext(),orderC);
                                            //5\ 创建退菜记录
                                            RetreatOrderC retreatOrderC = new RetreatOrderC(myapp.getCompany_ID());
                                            retreatOrderC.setState(0);
                                            retreatOrderC.setOrderCId(orderC.get_id());
                                            //6
                                            goodsCList.remove(position);
                                            setAll();
                                            adatper.notifyDataSetChanged();
                                            dialog.dismiss();
                                        }else{
                                            Toast.makeText(ShowParticularsActivity.this,"菜品数量为0，不可以退菜！",Toast.LENGTH_LONG).show();
                                        }


                                    }
                                });
                        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();

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
        });

    }

    private void setAll(){
        goodsCList.clear();
        all = 0f;
        List<OrderC> orderCList = CDBHelper.getObjByWhere(getApplicationContext(),
                Expression.property("className").equalTo("OrderC")
                        .and(Expression.property("tableNo").equalTo(myapp.getTable_sel_obj().getTableNum()))
                        .and(Expression.property("orderState").equalTo(1))
                , null
                , OrderC.class);

        boolean flag = false;
        for (OrderC orderC : orderCList) {
            this.orderC = orderC;
            for (GoodsC goodsb : orderC.getGoodsList()) {

                flag = false;

                for (GoodsC goodsC : goodsCList){
                    if (goodsC.getDishesName().equals(goodsb.getDishesName()) ){

                        if (goodsb.getDishesTaste() != null){

                            if (goodsb.getDishesTaste().equals(goodsC.getDishesTaste())){
                                float add = MyBigDecimal.add(goodsC.getAllPrice(),goodsb.getAllPrice(),1);
                                goodsC.setAllPrice(add);
                                float count = MyBigDecimal.add(goodsC.getDishesCount(),goodsb.getDishesCount(),1);
                                goodsC.setDishesCount(count);
                                flag = true;
                            }

                        }else{
                            float add = MyBigDecimal.add(goodsC.getAllPrice(),goodsb.getAllPrice(),1);
                            goodsC.setAllPrice(add);
                            float count = MyBigDecimal.add(goodsC.getDishesCount(),goodsb.getDishesCount(),1);
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

            all += orderC.getAllPrice();
        }


        showTvSl.setText("共：" + goodsCList.size() + "道菜，共："+all+"元");
    }


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

                setPrintOrder();

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

        String tableNumber = myapp.getTable_sel_obj().getTableNum();
        PrintUtils.selectCommand(PrintUtils.RESET);
        PrintUtils.selectCommand(PrintUtils.LINE_SPACING_DEFAULT);
        PrintUtils.selectCommand(PrintUtils.ALIGN_CENTER);
        PrintUtils.printText("肴点点\n\n");
        PrintUtils.selectCommand(PrintUtils.DOUBLE_HEIGHT_WIDTH);
        PrintUtils.printText(tableNumber+"号桌\n\n");
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

            PrintUtils.printText(PrintUtils.printThreeData(goodsC.getDishesName()+taste,goodsC.getDishesCount()+"", goodsC.getAllPrice()+"\n"));


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
