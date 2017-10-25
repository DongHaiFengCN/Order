package com.zm.order.view;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zm.order.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PayActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.textView7)
    TextView textView7;
    @BindView(R.id.discount)
    LinearLayout discount;
    @BindView(R.id.associator)
    LinearLayout associator;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.fact_tv)
    TextView factTv;
    @BindView(R.id.linearLayout5)
    LinearLayout linearLayout5;
    private AlertDialog.Builder dialog;

    private AlertDialog dg;
    private Intent intent;

    private String tableNumber = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        ButterKnife.bind(this);

        //456
        //789

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setElevation(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(0);
        }

        intent = getIntent();

/*        TextView total_tv = (TextView) findViewById(R.id.total_tv);
        total_tv.setText("消费金额: "+intent.getFloatExtra("total",0)+"元");



        tableNumber_tv = (TextView) findViewById(R.id.tableNumber_tv);



        factPay_tv = (TextView) findViewById(R.id.fact_tv);

        cash_cb = (AppCompatCheckBox) findViewById(R.id.cash_cb);
        alipay_cb = (AppCompatCheckBox) findViewById(R.id.alipay_cb);
        wechatpay_cb = (AppCompatCheckBox) findViewById(R.id.wechatpay_cb);*/


  /*      alipay_cb.setChecked(true);

        cash_cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                flag = CASH;

                if(alipay_cb.isChecked()){
                    alipay_cb.setChecked(false);
                }

                if(wechatpay_cb.isChecked()){
                    wechatpay_cb.setChecked(false);
                }
            }
        });
        alipay_cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                flag = ALIPAY;

                if(cash_cb.isChecked()){
                    cash_cb.setChecked(false);
                }

                if(wechatpay_cb.isChecked()){
                    wechatpay_cb.setChecked(false);
                }
            }
        });
        wechatpay_cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                flag = WECHATPAY;

                if(cash_cb.isChecked()){
                    cash_cb.setChecked(false);
                }

                if(alipay_cb.isChecked()){
                    alipay_cb.setChecked(false);
                }

            }
        });*/
        dialog = new AlertDialog.Builder(PayActivity.this);
        dialog.setView(getLayoutInflater().inflate(R.layout.view_print_dialog, null));


    }




   /* */

    /**
     *
     *
     *//*
    public void onClick(View view){


   *//*     if(tableNumber == null){

            Toast.makeText(this,"请选择桌号!",Toast.LENGTH_LONG).show();

            return;

        }*//*

        if(!cash_cb.isChecked()&&!alipay_cb.isChecked()&&!wechatpay_cb.isChecked()){

            flag = DEFAULT;
        }

        switch(flag){

            case CASH:
                Toast.makeText(this,"现金支付!",Toast.LENGTH_LONG).show();
                break;
            case ALIPAY:
                //Toast.makeText(this,"支付宝支付!",Toast.LENGTH_LONG).show();


                AlertDialog.Builder dialog = new AlertDialog.Builder(PayActivity.this);
                dialog.setView(getLayoutInflater().inflate(R.layout.view_alipay_dialog,null));
                dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.setNegativeButton("确定支付", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                            ProgressBarasyncTask progressBarasyncTask = new ProgressBarasyncTask(PayActivity.this);
                            progressBarasyncTask.setDate(intent);
                            progressBarasyncTask.execute();




                    }
                });

                dialog.show();

                break;
            case WECHATPAY:
                Toast.makeText(this,"微信支付!",Toast.LENGTH_LONG).show();
                break;
            case DEFAULT:
                Toast.makeText(this,"请选择支付方式!",Toast.LENGTH_LONG).show();
                break;
            default:

                break;
        }



    }*/
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

      /*      case R.id.action_sm:

                new IntentIntegrator(this)
                        .setOrientationLocked(false)
                        .setCaptureActivity(ScanActivity.class) // 设置自定义的activity是CustomActivity
                        .initiateScan(); // 初始化扫描

                break;*/
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
// 通过 onActivityResult的方法获取 扫描回来的 值
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

       /* IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);

        if(intentResult != null) {

            if(intentResult.getContents() == null) {

                Toast.makeText(this,"扫描失败，请重新尝试。",Toast.LENGTH_LONG).show();
            } else {

                tableNumber = intentResult.getContents();
                intent.putExtra("tableNumber",tableNumber);

                tableNumber_tv.setText("桌号/ "+tableNumber);


            }
        } else {
            super.onActivityResult(requestCode,resultCode,data);
        }*/


    }

    public void turnMainActivity() {

        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        setResult(RESULT_OK, null);//携带参数返回到MainActivity

        finish();
    }

    @OnClick({R.id.discount, R.id.associator})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.discount:
                break;
            case R.id.associator:
                break;
        }
    }
}

