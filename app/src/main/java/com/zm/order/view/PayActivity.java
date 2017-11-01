package com.zm.order.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Document;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.zm.order.R;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import model.DBFactory;
import model.DatabaseSource;
import model.IDBManager;
import model.ProgressBarasyncTask;
import untils.MyLog;
import untils.PrintUtils;

/**
 * @author 董海峰
 * @date 2017/10/25
 */

public class PayActivity extends AppCompatActivity {

    @BindView(R.id.discount)
    LinearLayout discount;
    @BindView(R.id.associator)
    LinearLayout associator;
    @BindView(R.id.fact_tv)
    TextView factTv;
    @BindView(R.id.ivalipay)
    ImageView ivalipay;
    @BindView(R.id.ivwechat)
    ImageView ivwechat;
    @BindView(R.id.cash)
    ImageView cash;
    @BindView(R.id.discount_tv)
    TextView discountTv;
    @BindView(R.id.total_tv)
    TextView totalTv;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.associator_tv)
    TextView associatorTv;
    private AlertDialog.Builder dialog;
    private AlertDialog dg;
    private Intent intent;
    private AlertDialog.Builder alertDialog;
    private Bitmap bitmap = null;
    private static final int DISTCOUNT = 0;
    private static final int SALE = 1;
    private float total = 0.0f;

    private Intent stashItent;

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

        stashItent = getIntent();

        //创建打印dialog
        dialog = new AlertDialog.Builder(PayActivity.this);
        dialog.setView(getLayoutInflater().inflate(R.layout.view_print_dialog, null)).create();

        //支付宝收款码
        String alipayId = "qwhhh";

        //转化二维码
        bitmap = encodeAsBitmap(alipayId);

        total = stashItent.getFloatExtra("total",0);

        //显示原价
        totalTv.setText(total + "");

        //显示操作后价格
        factTv.setText("实际支付：" + total + "元");

        show();

    }



    public void showDialog() {

        dg = dialog.show();
    }

    public void closeDialog() {

        dg.dismiss();
    }

    private void show(){

        List list = (List) stashItent.getSerializableExtra("Order");

        for (int i = 0; i < list.size(); i++) {


            SparseArray<Object> s = (SparseArray<Object>) list.get(i);

            MyLog.e("订单菜名："+ s.get(0));
            MyLog.e("菜品数量："+s.get(2));
            MyLog.e("当前菜品总价："+s.get(4));
            MyLog.e("打折："+s.get(5));
            MyLog.e("折扣价："+s.get(6));
        }

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


             //   setIntentData();

                MyLog.e("重置后数据"+intent.getFloatExtra("total",0f));
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

            total = data.getFloatExtra("Total", 0);

            discountTv.setText("- " + (Float.valueOf(totalTv.getText().toString()) - total) + "元");

            factTv.setText("实际支付：" + total + "元");

            associator.setEnabled(false);

            associatorTv.setText("减免后不可选");

        }else if(requestCode == SALE && resultCode == RESULT_OK){//会员账单返回

            int flag =  data.getIntExtra("CardTypeFlag",3);

            //充值
            if(flag == 2){


                float r = data.getFloatExtra("remainder",0f);

                MyLog.e("充值卡：余额"+r);

            }else if(flag == 1){//折扣

                float saleTotal = 0.0f;

                IDBManager idbManager = DBFactory.get(DatabaseSource.CouchBase, this);

                List<String> stringList = data.getStringArrayListExtra("DishseList");

                List<String> memberDishes = new ArrayList<>();


                //初始化会员菜名
                for(String id : stringList){

                    Document document = (Document) idbManager.getById(id);
                    memberDishes.add(document.getString("dishesName"));
                }

                int disrate = data.getIntExtra("disrate",3);

                MyLog.e("折扣率："+disrate);

                //订单

                List list = (List) stashItent.getSerializableExtra("Order");

                MyLog.e("长度："+list.size());

                for (int j = 0; j < list.size(); j++) {

                    SparseArray<Object> s = (SparseArray<Object>) list.get(j);

                    String name = (String) s.get(0);

                    MyLog.e("订单菜名："+name);


                    for (int i = 0; i < memberDishes.size(); i++) {

                        MyLog.e("会员菜名："+memberDishes.get(i));

                        if(name.equals(memberDishes.get(i))){

                            //设置折扣菜品  0不打折 1打折
                            s.put(5,1);

                            MyLog.e("订单中包含打折的菜品名称："+name);

                            float sum = (float) s.get(4);

                            MyLog.e("折前价格："+sum);

                            sum = (sum*disrate)/100f;

                            total += sum;

                            MyLog.e("折后前价格："+sum);
                            //设置折后价格
                            s.put(6,sum);
                            break;

                        }
                    }

                    if((float)s.get(6) == 0f){

                        total += (float) s.get(4);
                    }

                }

                //展示享受折扣的列表

                StringBuilder total = new StringBuilder("折后金额：￥");

                View view = getLayoutInflater().inflate(R.layout.view_payactivity_memberdishes_dialog,null);

                TextView t = view.findViewById(R.id.saletotalprice_tv);



                ListView listView = view.findViewById(R.id.memberdisheslist_lv);

                MemberDishesListAdapter memberDishesListAdapter = new MemberDishesListAdapter(list,this);

                listView.setAdapter(memberDishesListAdapter);

                t.setText(total);


                AlertDialog.Builder builder = new AlertDialog.Builder(this);


                builder.setView(view);

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                    }
                });

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                    }
                });

                builder.show();


            }else {

                Toast.makeText(PayActivity.this,"其他 ",Toast.LENGTH_SHORT).show();

            }

        }
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



    @OnClick({R.id.discount, R.id.associator, R.id.fact_tv, R.id.ivalipay, R.id.ivwechat, R.id.cash})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.discount:

                Intent discount = new Intent();
                discount.setClass(PayActivity.this, DiscountActivity.class);
                discount.putExtra("Total", Float.valueOf(totalTv.getText().toString()));
                startActivityForResult(discount, DISTCOUNT);

                break;
            case R.id.associator:

                Intent sale = new Intent();
                sale.setClass(PayActivity.this, SaleActivity.class);
                startActivityForResult(sale, SALE);

                break;
            case R.id.ivalipay:

                View dialog = getLayoutInflater().inflate(R.layout.view_alipay_dialog, null);

                ImageView imageView = dialog.findViewById(R.id.encode);

                imageView.setImageBitmap(bitmap);

                alertDialog = new AlertDialog.Builder(PayActivity.this);
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
                        progressBarasyncTask.setDate(intent);
                        progressBarasyncTask.execute();

                    }
                });
                alertDialog.show();


                break;
            case R.id.ivwechat:

                Toast.makeText(PayActivity.this, "ivwechat", Toast.LENGTH_SHORT).show();

                break;
            case R.id.cash:

                Toast.makeText(PayActivity.this, "cash", Toast.LENGTH_SHORT).show();

                break;
        }
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
}

