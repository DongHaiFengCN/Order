package com.zm.order.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.zm.order.R;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import model.ProgressBarasyncTask;
import untils.MyLog;

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
    private AlertDialog.Builder dialog;
    private AlertDialog dg;
    private Intent intent;
    private AlertDialog.Builder alertDialog;
    private Bitmap bitmap = null;
    private static final int DISTCOUNT = 0;
    private float total = 0.0f;

    @Override
    protected void onRestart() {
        super.onRestart();
        MyLog.e(total+"?");
    }

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

        intent = getIntent();


        //创建打印dialog
        dialog = new AlertDialog.Builder(PayActivity.this);
        dialog.setView(getLayoutInflater().inflate(R.layout.view_print_dialog, null)).create();


        String alipayId = "qwhhh";
        bitmap = encodeAsBitmap(alipayId);

        total = 4399;
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

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * onActivityResult的方法获取 扫描回来的 值
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == DISTCOUNT && resultCode == RESULT_OK ){

               total = data.getFloatExtra("Total",0);

                MyLog.e(total+"");

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
                discount.setClass(PayActivity.this,DiscountActivity.class);
                discount.putExtra("Total",total);
                startActivityForResult(discount,DISTCOUNT);

                break;
            case R.id.associator:

                Toast.makeText(PayActivity.this,"associator",Toast.LENGTH_SHORT).show();

                break;
            case R.id.ivalipay:

                View dialog = getLayoutInflater().inflate(R.layout.view_alipay_dialog,null);

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

                Toast.makeText(PayActivity.this,"ivwechat",Toast.LENGTH_SHORT).show();

                break;
            case R.id.cash:

                Toast.makeText(PayActivity.this,"cash",Toast.LENGTH_SHORT).show();

                break;
        }
    }

    /**
     * 字符串生成二维码图片
     * @param str
     * @return
     */

    private Bitmap encodeAsBitmap(String str){
        Bitmap bitmap = null;
        BitMatrix result = null;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            result = multiFormatWriter.encode(str, BarcodeFormat.QR_CODE, 250, 250);

            // 使用 ZXing Android Embedded
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(result);

        } catch (WriterException e){
            e.printStackTrace();
        } catch (IllegalArgumentException iae){
            return null;
        }
        return bitmap;
    }
}

