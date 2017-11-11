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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import application.MyApplication;
import bean.Goods;
import bean.Order;
import bean.kitchenmanage.member.ConsumLogC;
import bean.kitchenmanage.order.CheckOrderC;
import bean.kitchenmanage.order.GoodsC;
import bean.kitchenmanage.order.OrderC;
import bean.kitchenmanage.order.PayDetailC;
import bean.kitchenmanage.order.PromotionDetailC;
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
import untils.Tool;

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
    @BindView(R.id.table_number)
    TextView tableNumber;

    private AlertDialog.Builder dialog;
    private AlertDialog dg;
    private Bitmap bitmap = null;
    private static final int DISTCOUNT = 0;
    private static final int SALE = 1;
    private float total = 0.0f;
    private List<Document> orderList;
    private IDBManager idbManager;
    private MyApplication myApplication;

    //折扣率
    private int disrate;

    private List<Document> promotionCList;
    private TableC tableC;
    List<GoodsC> orderDishesList = new ArrayList<>();

    //List<HashMap> orderDishesList = new ArrayList<>();
    private  CheckOrderC checkOrder = new CheckOrderC();

    //更新总价
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == RESULT_OK) {

                //显示原价
                totalTv.setText(total + "");
                factTv.setText("实际支付：" + total + "元");
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

        StringBuilder stringBuilder = new StringBuilder("实际支付：");

        //显示操作后价格
        factTv.setText(stringBuilder.append(total));
        //获取餐桌编号
        tableC = myApplication.getTable_sel_obj();

        tableNumber.setText("桌/牌:"+tableC.getTableNum()+"号");

    }


    //测试提交数据～～～～～～～～～～～～

    public void show() {

        List<CheckOrderC> l = CDBHelper.getObjByClass(getApplicationContext(), CheckOrderC.class);

        Iterator i = l.iterator();

        CheckOrderC checkOrderC = null;

        while (i.hasNext()) {

            checkOrderC = (CheckOrderC) i.next();

            if (checkOrderC.getTableNo().equals(myApplication.getTable_sel_obj().getTableNum())) {

                //订单提交时间
                MyLog.e("订单提交时间: " + checkOrderC.getCheckTime());
                //桌号
                MyLog.e("桌号: " + checkOrderC.getTableNo());
                //实收
                MyLog.e("实收: " + checkOrderC.getNeedPay());
                //应收
                MyLog.e("应收: " + checkOrderC.getPay());

                //菜
                List<OrderC> f = checkOrderC.getOrderList();

                if(f != null){

                    for(OrderC orderC :f){


                        MyLog.e("&&&&&&&&&&&&&&&&&&&&&&&&&&&&");

                        for(GoodsC goodsC:orderC.getGoodsList()){

                            MyLog.e(goodsC.getDishesName());
                        }


                    }
                }


                //支付详情
                MyLog.e(" PromotionDetailC 支付详情~~~~~~~~~~~ ");
                PromotionDetailC promotionDetail = checkOrderC.getPromotionDetail();
                MyLog.e("优惠金额： " + promotionDetail.getDiscounts());
                MyLog.e("折扣率： " + promotionDetail.getDisrate());

                MyLog.e("PayDetailC 支付细节~~~~~~~~~~~ ");

                List<PayDetailC> payDetailList = promotionDetail.getPayDetailList();

                for (int j = 0; j < payDetailList.size(); j++) {

                    MyLog.e("第 " + j + " 支付方式");

                    PayDetailC p = payDetailList.get(j);

                    MyLog.e("支付类型 " + p.getPayTypes());

                    MyLog.e("支付钱数 " + p.getSubtotal());

                }

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

        //获取包含桌号xx的所有订单
        List<OrderC> orderCList = CDBHelper.getObjByWhere(getApplicationContext(),Expression.property("className").equalTo("OrderC").and(Expression.property("tableNo").equalTo(tableC.getTableNum())).and(Expression.property("orderState").equalTo(1)),null,OrderC.class);

        for(OrderC orderC:orderCList){

            checkOrder.addOrder(orderC);

            total += orderC.getAllPrice();


            //获取当前订单下goods集合下所有的菜品
            for (GoodsC o : orderC.getGoodsList()) {

                orderDishesList.add(o);
            }

        }

        promotionCList = idbManager.getByClassName("PromotionC");

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

                turnMainActivity();

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


            turnDiscount(data);


        } else if (requestCode == SALE && resultCode == RESULT_OK) {//会员账单返回

            int flag = data.getIntExtra("CardTypeFlag", 3);

            //充值
            if (flag == 2) {

                turnRechange(data);

            } else if (flag == 1) {

                turnSale(data);

            } else {

                Toast.makeText(PayActivity.this, "其他 ", Toast.LENGTH_SHORT).show();

            }

        }
    }

    /**
     * 账单减免功能
     *
     * @param data
     */

    private void turnDiscount(Intent data) {


        //处理完成的总价
        total = data.getFloatExtra("Total", 0);

        //展示差额
        discountTv.setText("- " + data.getFloatExtra("Margin", 0) + "元");

        //界面展示实际处理后的价格
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
    private void turnSale(Intent data) {

        //打折时初始化实际支付总价设置为 0

        total = 0f;

        //1 菜品的名称，2当前价格，3折扣价格，4是否折扣

        List<SparseArray> list = new ArrayList<>();


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

        MyLog.e("折扣率：" + disrate);

        //遍历订单中包含的会员菜品

        for (int j = 0; j < orderDishesList.size(); j++) {

            boolean isSale = false;

            SparseArray<Object> s = new SparseArray<>();

            //获取订单下goods的菜品名称
            GoodsC h = orderDishesList.get(j);

            String name = h.getDishesName();
            float sum = Float.valueOf(String.valueOf(h.getAllPrice()));
            //1 设置菜品的名称

            s.put(1, name);

            //2 设置菜品的原价



            s.put(2, sum);
            //   MyLog.e("订单菜名：" + name);

            //遍历所会员菜品找匹配的打折菜品

            for (int i = 0; i < memberDishes.size(); i++) {

                //找到打折的
                if (name.equals(memberDishes.get(i))) {

                    // MyLog.e("订单中包含打折的菜品名称：" + name);

                    //  MyLog.e("折前价格：" + sum);
                    BigDecimal b1 = new BigDecimal(sum);
                    BigDecimal b2 = new BigDecimal(disrate/100f);

                    //   MyLog.e("折后前价格：" + sum);

                    sum = b1.subtract(b2).floatValue();
                    //3 设置菜品的折扣价格

                    s.put(3,sum);
                    //是打折的直接添加到折扣总价中

                    total += sum;

                    isSale = true;

                    //4 设置菜品的打折

                    s.put(4, true);

                    break;
                }
            }

            //不是打折菜品的时候直接将价格加到总价

            if (!isSale) {

                s.put(3, 0f);

                total += sum;

                s.put(4, false);
            }
            list.add(s);

        }

        //获取会员
        final Document members = idbManager.getMembers(tel);

        //展示享受折扣的列表

        StringBuilder total_sb = new StringBuilder("折扣价：￥");

        View view = getLayoutInflater().inflate(R.layout.view_payactivity_memberdishes_sale_dialog, null);

        final TextView balance = view.findViewById(R.id.balance);

        final float remainder = members.getFloat("remainder");

        balance.setText("余额："+remainder);

        //支付价格大于卡内余额，且卡内余额大于零，显示使用卡内全部余额

        final TextView t = view.findViewById(R.id.saletotalprice_tv);

        ListView listView = view.findViewById(R.id.memberdisheslist_lv);

        MemberDishesListAdapter memberDishesListAdapter = new MemberDishesListAdapter(list, this);

        listView.setAdapter(memberDishesListAdapter);

        t.setText(total_sb.append(total));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("折扣明细表");

        builder.setView(view);
        builder.setPositiveButton("确定",null);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //factTv.setText("实际支付：" + total + "元");

            }
        });

        final AlertDialog builder1 = builder.show();

        builder1.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //余额不足状态下

                if(total > remainder&&remainder>0) {

                    //支付价格大于卡内余额，且卡内余额大于零，显示使用卡内全部余额
                    AlertDialog.Builder a = new AlertDialog.Builder(PayActivity.this);
                    a.setTitle("卡内余额不足！");
                    a.setMessage("使用卡内全部余额？");
                    a.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            try {
                                //会员剩余金额清零
                                members.setFloat("remainder", 0f);
                                idbManager.save(members);

                            } catch (CouchbaseLiteException e) {
                                e.printStackTrace();
                            }

                            //会员消费记录
                            setConsumLog(members, remainder);

                            //消费支付细节 6会员消费,计算剩余部分
                            total = total-remainder;

                            setPayDetail(6,remainder);
                            builder1.dismiss();

                           // factTv.setText("实际支付：" + total + "元");
                            turnMainActivity();

                        }
                    });
                    a.setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    a.show();

                }else if(remainder >= total){//余额充足,转跳主界面


                    members.setFloat("remainder", Tool.substrct(remainder,total));
                    try {
                        idbManager.save(members);
                    } catch (CouchbaseLiteException e) {
                        e.printStackTrace();
                    }finally {
                        Toast.makeText(PayActivity.this,"支付成功！",Toast.LENGTH_SHORT);
                    }

                    //会员消费记录
                    setConsumLog(members, total);

                    //消费支付细节 6会员消费
                    setPayDetail(6,total);

                    //结单

                    try {
                        submitCheckOrder();
                    } catch (CouchbaseLiteException e) {
                        e.printStackTrace();
                    }
                    builder1.dismiss();

                    turnMainActivity();
                }
            }
        });
    }

    /**
     * 设置会员消费记录
     * @param members 会员
     * @param consum 消费金额
     */

    private void setConsumLog(Document members, float consum) {

        ConsumLogC consumLogC = new ConsumLogC();
        consumLogC.setClassName("ConsumLogC");
        consumLogC.setChannelId(myApplication.getCompany_ID());
        consumLogC.setMembersId(members.getId());
        consumLogC.setCardNo(members.getString("cardNum"));
        consumLogC.setOrderNo(checkOrder.get_id());
        consumLogC.setCardConsum(consum);
        consumLogC.setTime(new Date());
        CDBHelper.createAndUpdate(myApplication,consumLogC);
    }

    /**
     * 充值卡扣款功能
     *
     * @param data
     */

    private void turnRechange(Intent data) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.view_payactivity_memberdishes_rechange_dialog, null);

        //当前卡中余额的数量
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


        //扣除当前全部余额，
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //当前会员充值卡余额清零

                Document d = idbManager.getMembers(tel);

                d.setFloat("remainder", 0f);
                try {
                    idbManager.save(d);

                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }

                //设置还需支付的钱数

                total -= r;

                alertDialog.dismiss();
            }
        });

        alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (r >= total) {//余额扣款

                    Document members = idbManager.getMembers(tel);

                    //更新余额
                    members.setFloat("remainder", Tool.substrct(r,total));

                    try {
                        idbManager.save(members);

                    } catch (CouchbaseLiteException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(PayActivity.this, "扣款成功！", Toast.LENGTH_SHORT).show();

                    //会员卡支付
                    setPayDetail(6, total);

                    //提交checkorder
                    try {
                        submitCheckOrder();
                    } catch (CouchbaseLiteException e) {
                        e.printStackTrace();
                    }

                    alertDialog.dismiss();


                } else if (r < total) {//余额不足走其他支付方式

                    all.setVisibility(View.VISIBLE);


                }
            }

        });
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

        //携带参数返回到MainActivity
        setResult(RESULT_OK, null);

        finish();
    }


    /**
     * 跳转抹零功能界面
     */
    private void turnDiscount() {

        Intent discount = new Intent();
        discount.setClass(PayActivity.this, DiscountActivity.class);
        discount.putExtra("Total", total);
        startActivityForResult(discount, DISTCOUNT);
    }

    /**
     * 跳转会员折扣界面
     */

    private void turnSale() {

        Intent sale = new Intent();
        sale.setClass(PayActivity.this, SaleActivity.class);
        startActivityForResult(sale, SALE);
    }


    @OnClick({R.id.associator, R.id.discount, R.id.action, R.id.ivalipay, R.id.ivwechat, R.id.cash})
    public void onClick(View view) {
        switch (view.getId()) {

            //会员的支付方式
            case R.id.associator:

                turnSale();

                break;
            case R.id.discount:

                //抹零

                turnDiscount();

                break;
            case R.id.action:

                //活动
                setAction();

                break;
            case R.id.ivalipay:

                //支付宝支付
                setPayDetail(3, total);

                try {
                    submitCheckOrder();
                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.ivwechat:

                //微信支付
                setPayDetail(4, total);

                try {
                    submitCheckOrder();
                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.cash:

                //现金支付
                setPayDetail(1, total);

                try {
                    submitCheckOrder();
                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }




                Intent intent = new Intent(PayActivity.this,DeskActivity.class);
                startActivity(intent);
                finish();

                break;
        }
    }


    /**
     * 活动扣款
     */
    private void setAction() {


        final ActionListAdapter a = new ActionListAdapter(promotionCList, PayActivity.this);
        View v = getLayoutInflater().inflate(R.layout.view_payactivity_action_dialog, null);
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


                int[] flag = a.getFlag();

                for (int f : flag) {

                    MyLog.e(f + "");

                }
            }
        });
        d.show();

    }

    /**
     * 支付细节设置
     *
     * @param type 支付类型
     * @param pay  支付的钱数
     */
    private void setPayDetail(int type, float pay) {

        //支付细节
        PayDetailC p = new PayDetailC();
        p.setClassName("PayDetailC");
        p.setChannelId(myApplication.getCompany_ID());
        p.setPayTypes(type);
        p.setSubtotal(pay);
        CDBHelper.createAndUpdate(getApplicationContext(), p);
        payDetailList.add(p);

    }

    /**
     * 提交结账信息
     * <p>
     * 设置order的状态为买单
     */
    public void submitCheckOrder() throws CouchbaseLiteException {

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        float all = Float.valueOf(totalTv.getText().toString());
        checkOrder.setChannelId(myApplication.getCompany_ID());
        checkOrder.setCheckTime(formatter.format(date));
        checkOrder.setClassName("CheckOrderC");


        MyLog.e("总价：" + all);
        //总共优惠

        checkOrder.setPay(all);

        checkOrder.setNeedPay(total);


        BigDecimal A = new BigDecimal(Float.toString(all));
        BigDecimal T = new BigDecimal(Float.toString(total));

        checkOrder.setTableNo(myApplication.getTable_sel_obj().getTableNum());
        for(OrderC orderC: checkOrder.getOrderList()){

            orderC.setOrderState(0);
            CDBHelper.createAndUpdate(getApplicationContext(),orderC);
        }

        //营销细节
        PromotionDetailC p = new PromotionDetailC();
        p.setChannelId(myApplication.getCompany_ID());
        p.setClassName("PromotionDetailC");


        p.setDiscounts(A.subtract(T).floatValue());

        p.setDisrate(disrate);

        //支付方式集合
        p.setPayDetailList(payDetailList);

        //折扣率
        p.setDisrate(disrate);

        checkOrder.setPromotionDetail(p);

        CDBHelper.createAndUpdate(getApplicationContext(), p);
        CDBHelper.createAndUpdate(getApplicationContext(), checkOrder);

        //打印账单
        // printOrder();


        new Thread(new Runnable() {
            @Override
            public void run() {

                show();

            }
        }).start();
    }

    /**
     * 字符串生成二维码图片
     *
     * @param str 二维码字符串
     * @return Bitmap
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
}

