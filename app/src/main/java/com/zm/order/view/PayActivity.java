package com.zm.order.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Array;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.zm.order.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import application.MyApplication;
import bean.kitchenmanage.order.CheckOrderC;
import bean.kitchenmanage.order.PayDetailC;
import bean.kitchenmanage.order.PromotionDetailC;
import bean.kitchenmanage.promotion.PromotionC;
import bean.kitchenmanage.table.TableC;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import model.CDBHelper;
import model.DBFactory;
import model.DatabaseSource;
import model.IDBManager;
import model.ProgressBarasyncTask;
import untils.MyLog;

/**
 * @author 董海峰
 * @date 2017/10/25
 */

public class PayActivity extends AppCompatActivity {

    @BindView(R.id.fact_tv)
    TextView factTv;
    @BindView(R.id.discount_tv)
    TextView discountTv;
    @BindView(R.id.total_tv)
    TextView totalTv;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.associator_tv)
    TextView associatorTv;
    @BindView(R.id.action_tv)
    TextView actionTv;
    @BindView(R.id.associator)
    LinearLayout associator;
    @BindView(R.id.discount)
    LinearLayout discount;
    @BindView(R.id.action)
    LinearLayout action;
    @BindView(R.id.ivalipay)
    ImageView ivalipay;
    @BindView(R.id.ivwechat)
    ImageView ivwechat;
    @BindView(R.id.cash)
    ImageView cash;


    private AlertDialog.Builder dialog;
    private AlertDialog dg;
    private Bitmap bitmap = null;
    private static final int DISTCOUNT = 0;
    private static final int SALE = 1;
    private float total = 0.0f;

    private Intent stashItent;

    private List<Document> orderList;

    private IDBManager idbManager;

    private  MyApplication myApplication;

    private int disrate;

    private float restPay = 0f;

    private List<Document> promotionCList;

    //更新总价
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == RESULT_OK) {
                //显示原价
                totalTv.setText(total + "");
            }

        }
    };

    //每增加一种支付方式创建一个支付详情，例如充值卡余额不足，剩下的部分用的现金。
    private List<PayDetailC> payDetailList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pay);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //取消分割阴影
        getSupportActionBar().setElevation(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(0);
        }

        idbManager = DBFactory.get(DatabaseSource.CouchBase, getApplicationContext());

        myApplication = (MyApplication) getApplication();

        new Thread(new Runnable() {
            @Override
            public void run() {

                    getAll();
                    Message m = handler.obtainMessage();
                    m.what = RESULT_OK;
                    handler.sendMessage(m);

            }
        }).start();

        //创建打印dialog
        dialog = new AlertDialog.Builder(PayActivity.this);
        dialog.setView(getLayoutInflater().inflate(R.layout.view_print_dialog, null)).create();

        //显示原价
        totalTv.setText(total + "");

        StringBuilder stringBuilder = new StringBuilder("实际支付：");

        //显示操作后价格
        factTv.setText(stringBuilder.append(total));

    }

    //测试抹零付款基础的数据
    public void show(){

       List<CheckOrderC> l = CDBHelper.getObjByClass(getApplicationContext(),CheckOrderC.class);

        Iterator i = l.iterator();

        while (i.hasNext()){
            CheckOrderC c = (CheckOrderC) i.next();

            if(!c.getTableNo().equals(myApplication.getTable_sel_obj().getTableNum())){

                i.remove();
            }

        }



    }


    /**
     * 准备所有的数据
     *
     * @param
     */
    private void getAll() {

        //支付宝收款码,网络获取**********

        String alipayId = "qwhhh";

        //转化二维码
        bitmap = encodeAsBitmap(alipayId);

        //获取餐桌编号
        final TableC tableC = myApplication.getTable_sel_obj();

        orderList = idbManager.getOrderListBelongToTable("tableNo", tableC.getTableNum());

        promotionCList =  idbManager.getByClassName("PromotionC");

        Iterator<Document> i = orderList.iterator();

        while (i.hasNext()) {

            Document order = i.next();

            //当前桌下没有买单的订单的总价
            if (order.getInt("orderState") == 1) {

                total += order.getFloat("allPrice");

            }else{

                //展示没有下单的订单，提交订单的时候将 orderState 设置 0 已买单

                i.remove();
            }

        }



        //加获取所有的订单下的菜品
        List<Document> list= new ArrayList<>();
        for (int i1 = 0; i1 < orderList.size(); i1++) {

            Document order = orderList.get(i1);

            Array a = order.getArray("goodsList");



            for(int j = 0 ; j< order.getArray("goodsList").count();j++){


            }



        }


    }

    public void showDialog() {

        dg = dialog.show();
    }

    public void closeDialog() {

        dg.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pay, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                finish();

                break;

            case R.id.reset:


                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * onActivityResult的方法获取
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //账单抹零返回参数
        if (requestCode == DISTCOUNT && resultCode == RESULT_OK) {


            Discount(data);


        } else if (requestCode == SALE && resultCode == RESULT_OK) {//会员账单返回

            int flag = data.getIntExtra("CardTypeFlag", 3);

            //充值
            if (flag == 2) {

                Rechange(data);

            } else if (flag == 1) {

                Sale(data);

            } else {

                Toast.makeText(PayActivity.this, "其他 ", Toast.LENGTH_SHORT).show();

            }

        }
    }

    /**
     * 账单减免
     *
     * @param data
     */

    private void Discount(Intent data) {


        //处理完成的总价
        total = data.getFloatExtra("Total", 0);

        //展示差额
        discountTv.setText("- " + data.getFloatExtra("Margin", 0) + "元");

        //设置实际处理后的价格
        factTv.setText("实际支付：" + total + "元");

        //设置会员按钮不可用
        associator.setEnabled(false);

        //未设置会员的优惠信息时展示不可用
        if (TextUtils.isEmpty(associatorTv.getText().toString())) {

            associatorTv.setText("减免后不可选");
        }
    }

    /**
     * 会员折扣卡处理逻辑
     *
     * @param data
     */
    private void Sale(Intent data) {

        //折扣
        float saleTotal = 0.0f;

        IDBManager idbManager = DBFactory.get(DatabaseSource.CouchBase, this);

        //返回的会员菜品

        List<String> stringList = data.getStringArrayListExtra("DishseList");

        //会员电话
        final String tel = data.getStringExtra("tel");


        List<String> memberDishes = new ArrayList<>();

        //初始化会员菜名
        for (String id : stringList) {

            Document document = (Document) idbManager.getById(id);
            memberDishes.add(document.getString("dishesName"));
        }

        //获取折扣率
        final int disrate = data.getIntExtra("disrate", 3);

        //MyLog.e("折扣率："+disrate);

        //遍历订单中包含的会员菜品

        //List list = (List) stashItent.getSerializableExtra("Order");

        //


/*
        for (int j = 0; j < list.size(); j++) {

           // SparseArray<Object> s = (SparseArray<Object>) list.get(j);

            String name = (String) s.get(0);

            MyLog.e("订单菜名：" + name);


            for (int i = 0; i < memberDishes.size(); i++) {

                //  MyLog.e("会员菜名："+memberDishes.get(i));

                if (name.equals(memberDishes.get(i))) {

                    //5号位置设置折扣菜品状态  0不打折 1打折
                    s.put(5, 1);

                    // MyLog.e("订单中包含打折的菜品名称："+name);

                    float sum = (float) s.get(4);

                    // MyLog.e("折前价格："+sum);

                    sum = (sum * disrate) / 100f;

                    //是打折的直接添加到折扣总价中
                    saleTotal += sum;

                    //  MyLog.e("折后前价格："+sum);

                    //6号位置设置折后价格
                    s.put(6, sum);
                    break;

                }
            }

            //折扣价格是0，将原价加入到折扣后总价格
            if ((float) s.get(6) == 0f) {

                saleTotal += (float) s.get(4);
            }

        }*/

        //展示享受折扣的列表

        StringBuilder total_sb = new StringBuilder("折后金额：￥");

        View view = getLayoutInflater().inflate(R.layout.view_payactivity_memberdishes_sale_dialog, null);

        TextView t = view.findViewById(R.id.saletotalprice_tv);


        ListView listView = view.findViewById(R.id.memberdisheslist_lv);

       // MemberDishesListAdapter memberDishesListAdapter = new MemberDishesListAdapter(list, this);

        //listView.setAdapter(memberDishesListAdapter);

        t.setText(total_sb.append(saleTotal));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("折扣明细表");
        builder.setView(view);

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        final float finalSaleTotal = saleTotal;

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                IDBManager idbManager = DBFactory.get(DatabaseSource.CouchBase, PayActivity.this);

                Document members = idbManager.getMembers(tel);

                total = finalSaleTotal;

                factTv.setText("实际支付：" + total + "元");

                associatorTv.setText(disrate + "/折");


            }
        });

        builder.show();
    }


    /**
     * 充值卡扣款功能
     *
     * @param data
     */
    private void Rechange(Intent data) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.view_payactivity_memberdishes_rechange_dialog, null);

        final float r = data.getFloatExtra("remainder", 0f);

        final String tel = data.getStringExtra("tel");

        final TextView all = view.findViewById(R.id.useAll);

        TextView remainder_tv = view.findViewById(R.id.remainder_tv);

        remainder_tv.setText(r + "");

        TextView rechangepay_tv = view.findViewById(R.id.rechangepay_tv);

        rechangepay_tv.setText(total + "");


        builder.setTitle("扣款明细表");
        builder.setView(view);
        builder.setPositiveButton("确定扣款", null);

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {




            }
        });

        final AlertDialog alertDialog = builder.show();
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                restPay = total - r;

                alertDialog.dismiss();
            }
        });
        alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (r >= total) {//余额扣款

                    IDBManager idbManager = DBFactory.get(DatabaseSource.CouchBase, PayActivity.this);
                    Document members = idbManager.getMembers(tel);

                    //更新余额
                    members.setFloat("remainder", r - total);

                    try {
                        idbManager.save(members);

                    } catch (CouchbaseLiteException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(PayActivity.this, "扣款成功！", Toast.LENGTH_SHORT).show();

                    //会员卡支付
                    setPayDetail(6);

                    //提交checkorder
                    submitCheckOrder();

                    alertDialog.dismiss();



                } else if(r < total){//余额不足走其他支付方式

                    all.setVisibility(View.VISIBLE);



                }

            }


        });
    }


    /**
     * 提交结账信息
     */
    public void submitCheckOrder() {

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        float needPay = Float.valueOf(totalTv.getText().toString());
        CheckOrderC checkOrder = new CheckOrderC();
        checkOrder.setChannelId(myApplication.getCompany_ID());
        checkOrder.setCheckTime(formatter.format(date));
        checkOrder.setClassName("CheckOrderC");
        checkOrder.setNeedPay(needPay);
        checkOrder.setPay(total);
        checkOrder.setTableNo(myApplication.getTable_sel_obj().getTableNum());

        //营销细节
        PromotionDetailC p = new PromotionDetailC();
        p.setChannelId(myApplication.getCompany_ID());
        p.setClassName("PromotionDetailC");

        //总共优惠
        p.setDisrate((int) (total-needPay));

        //支付方式集合
        p.setPayDetailList(payDetailList);

        //折扣率
        p.setDisrate(disrate);

        CDBHelper.createAndUpdate(getApplicationContext(),p);

        checkOrder.setPromotionDetail(p);


        CDBHelper.createAndUpdate(getApplicationContext(),checkOrder);

        //打印账单
        printOrder();


    }

    /**
     * 打印账单
     */

    private void printOrder() {

        View dialog = getLayoutInflater().inflate(R.layout.view_alipay_dialog, null);
        ImageView imageView = dialog.findViewById(R.id.encode);
        imageView.setImageBitmap(bitmap);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PayActivity.this);
        alertDialog.setView(dialog);
        alertDialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.setNegativeButton("确定支付", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                ProgressBarasyncTask progressBarasyncTask = new ProgressBarasyncTask(PayActivity.this);
              //  progressBarasyncTask.setDate(intent);
                progressBarasyncTask.execute();

            }
        });

        alertDialog.show();

    }

    public void turnMainActivity() {

        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //携带参数返回到MainActivity
        setResult(RESULT_OK, null);

        finish();
    }



    private void Discount() {
        Intent discount = new Intent();
        discount.setClass(PayActivity.this, DiscountActivity.class);
        discount.putExtra("Total", total);
        startActivityForResult(discount, DISTCOUNT);
    }

    /**
     * 字符串生成二维码图片
     *
     * @param str
     * @return
     */

    private Bitmap encodeAsBitmap(String str) {
        Bitmap bitmap = null;
        BitMatrix result = null;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            result = multiFormatWriter.encode(str, BarcodeFormat.QR_CODE, 250, 250);
            // 使用 ZXing Android Embedded
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(result);

        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException iae) {
            return null;
        }
        return bitmap;
    }


    @OnClick({R.id.associator, R.id.discount, R.id.action, R.id.ivalipay, R.id.ivwechat, R.id.cash})
    public void onClick(View view) {
        switch (view.getId()) {

            //会员的支付方式
            case R.id.associator:

                Intent sale = new Intent();
                sale.setClass(PayActivity.this, SaleActivity.class);
                startActivityForResult(sale, SALE);

                break;
            case R.id.discount:

                //抹零

                Discount();

                break;
            case R.id.action:

                //活动
                setAction();

                break;
            case R.id.ivalipay:

                //支付宝支付
                setPayDetail(3);

                submitCheckOrder();

                break;
            case R.id.ivwechat:

                //微信支付
                setPayDetail(4);

                submitCheckOrder();

                break;
            case R.id.cash:


                break;
        }
    }


    /**
     *活动扣款
     */
    private void setAction() {


        final ActionListAdapter a = new ActionListAdapter(promotionCList,PayActivity.this);

        View v = getLayoutInflater().inflate(R.layout.view_payactivity_action_dialog,null);
        ListView l = v.findViewById(R.id.action_lv);
        a.setListView(l);
        l.setAdapter(a);

        AlertDialog.Builder d = new AlertDialog.Builder(this);
        d.setView(v);
        d.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        d.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


               int [] flag = a.getFlag();

                for(int f : flag){

                    MyLog.e(f+"");

                }
            }
        });
        d.show();

    }

    /**
     * 设置支付细节
     * @param type
     */
    private void setPayDetail(int type) {
        //支付细节
        PayDetailC p = new PayDetailC();
        p.setClassName("PayDetailC");
        p.setChannelId(myApplication.getCompany_ID());
        p.setPayTypes(type);

        //支付尾款
        if(restPay != 0){

            p.setSubtotal(restPay);

        }else {//支付全款

            p.setSubtotal(total);

        }

        CDBHelper.createAndUpdate(getApplicationContext(),p);
        payDetailList.add(p);
    }
}

